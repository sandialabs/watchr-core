/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.rule;

import java.util.HashMap;
import java.util.Map;

import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;

public abstract class RuleActor {

    protected final PlotTraceModel traceModel;
    protected final Map<String, String> properties;

    protected RuleActor(PlotTraceModel traceModel, Map<String, String> properties) {
        this.traceModel = traceModel;
        this.properties = new HashMap<>();
        this.properties.putAll(properties);
    }

    public PlotTraceModel getParentTraceModel() {
        return traceModel;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
    
    public abstract void act();

    public abstract void undo();
}
