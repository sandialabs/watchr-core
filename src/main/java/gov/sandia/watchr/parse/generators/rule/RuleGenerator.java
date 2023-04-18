/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators.rule;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.rule.AlwaysRuleConfig;
import gov.sandia.watchr.config.rule.RuleConfig;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.generators.AbstractGenerator;
import gov.sandia.watchr.parse.generators.rule.actors.DataProcessingRuleActor;
import gov.sandia.watchr.parse.generators.rule.properties.RulePropertyAgeToDelete;
import gov.sandia.watchr.util.OsUtil;

public class RuleGenerator extends AbstractGenerator<List<RuleConfig>> {

    //////////
    // ENUM //
    //////////

    public enum RuleTest {
        EQUALS,
        LESS_THAN,
        GREATER_THAN
    }

    ////////////
    // FIELDS //
    ////////////

    private static final String CLASSNAME = RuleGenerator.class.getSimpleName();
    private final RuleApplyable ruleApplyableObject;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public RuleGenerator(RuleApplyable ruleApplyableObject, ILogger logger) {
        super(logger);
        this.ruleApplyableObject = ruleApplyableObject;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void generate(List<RuleConfig> rules, List<WatchrDiff<?>> diffs) throws WatchrParseException {
        for(RuleConfig rule : rules) {
            logger.logDebug("Clear Rule Effect for Rule: " + OsUtil.getOSLineBreak() + rule.toString(), CLASSNAME);
            clearRuleEffect(rule, ruleApplyableObject);
        }

        for(RuleConfig rule : rules) {
            logger.logDebug("Considering rule: " + OsUtil.getOSLineBreak() + rule.toString(), CLASSNAME);
            boolean proceed = rule instanceof AlwaysRuleConfig;
            proceed = proceed || doesRuleApply(rule);
            if(proceed) {
                logger.logDebug("Apply rule: " + OsUtil.getOSLineBreak() + rule.toString(), CLASSNAME);
                applyRuleAction(rule, ruleApplyableObject);
                logger.logDebug("This rule will be applied to: " + OsUtil.getOSLineBreak() + ruleApplyableObject.toString(), CLASSNAME);
            }
        }
    }

    //////////////
    // EVALUATE //
    //////////////
    
    private boolean doesRuleApply(RuleConfig rule) {
        String condition = rule.getCondition();
        String[] lexicalElements = condition.split(" ");
        if(lexicalElements.length == 3) {
            String left   = lexicalElements[0];
            String middle = lexicalElements[1];
            String right  = lexicalElements[2];

            Double leftSide  = processOperand(left);
            Double rightSide = processOperand(right);
            RuleTest test    = processOperator(middle);

            return evaluate(leftSide, test, rightSide);
        } else {
            String message = "Rule \"" + condition + "\" should have three lexical elements in it, separated by spaces.";
            logger.logError(message);
        }
        logger.logDebug("This rule does not apply.", CLASSNAME);
        return false;
    }

    private RuleTest processOperator(String middle) {
        RuleTest test = null;
        if(middle.equals("==") || middle.equals("=")) {
            test = RuleTest.EQUALS;
        } else if(middle.equals(">")) {
            test = RuleTest.GREATER_THAN;
        } else if(middle.equals("<")) {
            test = RuleTest.LESS_THAN;
        } else {
            String message = "Middle part of rule should be =, ==, <, or >, but it was none of these.";
            logger.logError(message);
        }
        return test;
    }

    private Double processOperand(String operand) {
        if(NumberUtils.isCreatable(operand)) {
            return Double.parseDouble(operand);
        } else {   
            RuleTarget target = RuleTarget.getTargetForShortLabel(operand);
            if(target == null) {
                String message = "Operand " + operand + " was not recognized.";
                logger.logError(message);
                return null;
            } else {
                return ruleApplyableObject.getValue(target);
            }
        }
    }

    private boolean evaluate(Double left, RuleTest test, Double right) {
        if(left != null)  logger.logDebug("Left: "  + Double.toString(left), CLASSNAME);
        if(right != null) logger.logDebug("Right: " + Double.toString(right), CLASSNAME);
        if(left != null && right != null && test != null) {
            if(test == RuleTest.EQUALS) {
                return left.equals(right);
            } else if(test == RuleTest.GREATER_THAN) {
                return left > right;
            } else if(test == RuleTest.LESS_THAN) {
                return left < right;
            }
        }
        logger.logDebug("Evaluation of rule condition failed.", CLASSNAME);
        return false;
    }

    ////////////
    // ACTION //
    ////////////

    private RuleActor applyRuleAction(RuleConfig rule, RuleApplyable ruleApplyableObject) {
        RuleActorFactory factory = RuleActorFactory.getInstance();
        RuleAction action = factory.parseAction(rule.getAction());
        RuleActor actor = factory.create(action, ruleApplyableObject, rule.getActionProperties(), logger);
        if(actor != null) {
            logger.logDebug("Rule actor is of class " + actor.getClass().getSimpleName(), CLASSNAME);
            if(actor instanceof DataProcessingRuleActor) {
                Object ruleData = getRuleDataToProcess(rule, ruleApplyableObject);
                ((DataProcessingRuleActor)actor).setDataToProcess(ruleData);
            }
            actor.act();
        }
        return actor;
    }

    private void clearRuleEffect(RuleConfig rule, RuleApplyable ruleApplyableObject) {
        RuleActorFactory factory = RuleActorFactory.getInstance();
        RuleAction action = factory.parseAction(rule.getAction());
        RuleActor actor = factory.create(action, ruleApplyableObject, rule.getActionProperties(), logger);
        if(actor != null) {
            actor.undo();
        }
    }

    /////////////
    // UTILITY //
    /////////////

    private Object getRuleDataToProcess(RuleConfig rule, RuleApplyable ruleApplyableObject) {
        logger.logDebug("RuleGenerator.getRuleDataToProcess()", CLASSNAME);
        Map<String, String> actionProperties = rule.getActionProperties();
        Object result = null;
        if(ruleApplyableObject instanceof IDatabase && actionProperties.containsKey("ageToDelete")) {
            String value = actionProperties.get("ageToDelete");
            RulePropertyAgeToDelete property = new RulePropertyAgeToDelete();
            result = property.process((IDatabase)ruleApplyableObject, value);
            logger.logDebug("Result is " + result.toString(), CLASSNAME);
        }
        return result;
    }
}
