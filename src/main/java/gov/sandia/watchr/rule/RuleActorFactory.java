/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.rule;

import java.util.Map;

import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.rule.actors.RuleFailActor;
import gov.sandia.watchr.rule.actors.RuleWarnActor;

public class RuleActorFactory {
    
    private static RuleActorFactory INSTANCE;
    
    private RuleActorFactory() {}

    public static RuleActorFactory getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new RuleActorFactory();
        }
        return INSTANCE;
    }

    public RuleActor create(RuleAction action, PlotTraceModel traceModel, Map<String, String> properties) {
        if(action == RuleAction.FAIL) {
            return new RuleFailActor(traceModel, properties);
        } else if(action == RuleAction.WARN) {
            return new RuleWarnActor(traceModel, properties);
        }
        return null;
    }

    public RuleAction parseAction(String actionStr) {
        RuleAction action = null;
        if(actionStr.equalsIgnoreCase("fail")) {
            action = RuleAction.FAIL;
        } else if(actionStr.equalsIgnoreCase("warn")) {
            action = RuleAction.WARN;
        }
        return action;
    }   
}
