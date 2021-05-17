/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.RuleConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.DerivativeLine.DerivativeLineType;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.graph.chartreuse.Dimension;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceOptions;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.rule.RuleAction;
import gov.sandia.watchr.rule.RuleActor;
import gov.sandia.watchr.rule.RuleActorFactory;

public class RuleGenerator extends AbstractGenerator<List<RuleConfig>> {

    //////////
    // ENUM //
    //////////

    public enum RuleTest {
        EQUALS,
        LESS_THAN,
        GREATER_THAN
    }

    public enum RuleTarget {
        LAST_POINT_ON_DATA_LINE,
        LAST_POINT_ON_AVERAGE_LINE,
        LAST_POINT_ON_STD_DEV_LINE,
        LAST_POINT_ON_STD_DEV_OFFSET_LINE
    }

    ////////////
    // FIELDS //
    ////////////

    private final PlotTraceModel traceModel;
    private List<RuleActor> ruleActors;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public RuleGenerator(PlotTraceModel traceModel) {
        this.traceModel = traceModel;
        this.ruleActors = new ArrayList<>();
    }

    /////////////
    // GETTERS //
    /////////////

    public List<RuleActor> getRuleActors() {
        return ruleActors;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void generate(List<RuleConfig> rules, List<WatchrDiff<?>> diffs) throws WatchrParseException {
        ruleActors.clear();
        for(RuleConfig rule : rules) {
            clearRuleEffect(rule, traceModel);
        }

        for(RuleConfig rule : rules) {
            if(doesRuleApply(rule, traceModel)) {
                RuleActor actor = applyRuleAction(rule, traceModel);
                ruleActors.add(actor);
            }
        }
    }

    //////////////
    // EVALUATE //
    //////////////
    
    private boolean doesRuleApply(RuleConfig rule, PlotTraceModel traceModel) {
        String condition = rule.getCondition();
        String[] lexicalElements = condition.split(" ");
        if(lexicalElements.length == 3) {
            String left   = lexicalElements[0];
            String middle = lexicalElements[1];
            String right  = lexicalElements[2];

            RuleTarget leftSide  = processOperand(left);
            RuleTarget rightSide = processOperand(right);
            RuleTest test        = processOperator(middle);

            Double leftValue  = getValue(leftSide, traceModel);
            Double rightValue = getValue(rightSide, traceModel);

            return evaluate(leftValue, test, rightValue);
        } else {
            String message = "Rule \"" + condition + "\" should have three lexical elements in it, separated by spaces.";
            ILogger logger = WatchrCoreApp.getInstance().getLogger();
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, message));
        }
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
            ILogger logger = WatchrCoreApp.getInstance().getLogger();
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, message));
        }
        return test;
    }

    private RuleTarget processOperand(String operand) {
        RuleTarget target = null;
        if(operand.equals("dataLine")) {
            target = RuleTarget.LAST_POINT_ON_DATA_LINE;
        } else if(operand.equals("average")) {
            target = RuleTarget.LAST_POINT_ON_AVERAGE_LINE;
        } else if(operand.equals("standardDeviation")) {
            target = RuleTarget.LAST_POINT_ON_STD_DEV_LINE;
        } else if(operand.equals("standardDeviationOffset")) {
            target = RuleTarget.LAST_POINT_ON_STD_DEV_OFFSET_LINE;
        } else {
            String message = "Left part of rule must refer to \"dataLine\", \"average\", \"standardDeviation\", or \"standardDeviationOffset\".";
            ILogger logger = WatchrCoreApp.getInstance().getLogger();
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, message));
        }
        return target;
    }

    private Double getValue(RuleTarget target, PlotTraceModel traceModel) {
        PlotTraceModel actualTraceModel = getTraceModelForTarget(target, traceModel);

        if(actualTraceModel != null) {
            PlotTraceOptions options = new PlotTraceOptions();
            options.sortAlongDimension = Dimension.X;
            List<PlotTracePoint> points = actualTraceModel.getPoints(options);
            if(!points.isEmpty()) {
                PlotTracePoint lastPoint = points.get(points.size() - 1);
                String stringValue = lastPoint.y;
                if(NumberUtils.isCreatable(stringValue)) {
                    return Double.parseDouble(stringValue);
                }
            }
        }
        return null;
    }

    private PlotTraceModel getTraceModelForTarget(RuleTarget target, PlotTraceModel traceModel) {
        PlotCanvasModel parent = traceModel.getParent();

        if(target == RuleTarget.LAST_POINT_ON_DATA_LINE) {
            return traceModel;
        } else if(target == RuleTarget.LAST_POINT_ON_AVERAGE_LINE) {
            return parent.findDerivativeLine(traceModel.getName(), DerivativeLineType.AVERAGE);
        } else if(target == RuleTarget.LAST_POINT_ON_STD_DEV_LINE) {
            return parent.findDerivativeLine(traceModel.getName(), DerivativeLineType.STANDARD_DEVIATION);
        } else if(target == RuleTarget.LAST_POINT_ON_STD_DEV_OFFSET_LINE) {
            return parent.findDerivativeLine(traceModel.getName(), DerivativeLineType.STANDARD_DEVIATION_OFFSET);
        }
        return null;
    }

    private boolean evaluate(Double left, RuleTest test, Double right) {
        if(left != null && right != null && test != null) {
            if(test == RuleTest.EQUALS) {
                return left.equals(right);
            } else if(test == RuleTest.GREATER_THAN) {
                return left > right;
            } else if(test == RuleTest.LESS_THAN) {
                return left < right;
            }
        }
        return false;
    }

    ////////////
    // ACTION //
    ////////////

    private RuleActor applyRuleAction(RuleConfig rule, PlotTraceModel traceModel) {
        RuleActorFactory factory = RuleActorFactory.getInstance();
        RuleAction action = factory.parseAction(rule.getAction());
        RuleActor actor = factory.create(action, traceModel, rule.getActionProperties());
        if(actor != null) {
            actor.act();
        }
        return actor;
    }

    private void clearRuleEffect(RuleConfig rule, PlotTraceModel traceModel) {
        RuleActorFactory factory = RuleActorFactory.getInstance();
        RuleAction action = factory.parseAction(rule.getAction());
        RuleActor actor = factory.create(action, traceModel, rule.getActionProperties());
        if(actor != null) {
            actor.undo();
        }
    }
}
