/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators.rule;

import java.util.Map;

import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.generators.rule.actors.RuleDatabaseDeleteEverythingActor;
import gov.sandia.watchr.parse.generators.rule.actors.RuleDatabaseDeleteSomeThingsActor;
import gov.sandia.watchr.parse.generators.rule.actors.RuleDatabaseFailActor;
import gov.sandia.watchr.parse.generators.rule.actors.RulePlotTraceModelFailActor;
import gov.sandia.watchr.parse.generators.rule.actors.RulePlotTraceModelWarnActor;

public class RuleActorFactory {
    
    private static RuleActorFactory INSTANCE;
    
    private RuleActorFactory() {}

    public static RuleActorFactory getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new RuleActorFactory();
        }
        return INSTANCE;
    }

    public RuleActor create(RuleAction action, RuleApplyable ruleApplyableObject, Map<String, String> properties, ILogger logger) {
        if(ruleApplyableObject instanceof PlotTraceModel) {
            PlotTraceModel traceModel = (PlotTraceModel) ruleApplyableObject;
            if(action == RuleAction.FAIL_PLOT) {
                return new RulePlotTraceModelFailActor(traceModel, logger, properties);
            } else if(action == RuleAction.WARN_PLOT) {
                return new RulePlotTraceModelWarnActor(traceModel, logger, properties);
            }
        } else if(ruleApplyableObject instanceof IDatabase) {
            IDatabase db = (IDatabase) ruleApplyableObject;
            if(action == RuleAction.DELETE_EVERYTHING) {
                return new RuleDatabaseDeleteEverythingActor(db, logger, properties);
            } else if(action == RuleAction.DELETE_SOME) {
                return new RuleDatabaseDeleteSomeThingsActor(db, logger, properties);
            } else if(action == RuleAction.FAIL_DATABASE) {
                return new RuleDatabaseFailActor(db, logger, properties);
            }
        }
        return null;
    }

    public RuleAction parseAction(String actionStr) {
        RuleAction action = null;
        if(actionStr.equalsIgnoreCase("fail")) {
            action = RuleAction.FAIL_PLOT;
        } else if(actionStr.equalsIgnoreCase("warn")) {
            action = RuleAction.WARN_PLOT;
        } else if(actionStr.equalsIgnoreCase("deleteAll")) {
            action = RuleAction.DELETE_EVERYTHING;
        } else if(actionStr.equalsIgnoreCase("deleteSome")) {
            action = RuleAction.DELETE_SOME;
        } else if(actionStr.equalsIgnoreCase("failDatabase")) {
            action = RuleAction.FAIL_DATABASE;
        }
        return action;
    }   
}
