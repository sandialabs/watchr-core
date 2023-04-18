package gov.sandia.watchr.parse.generators.rule.actors;

import java.util.HashMap;
import java.util.Map;

import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.generators.rule.RuleActor;

public abstract class RulePlotTraceModelActor implements RuleActor {
    
    ////////////
    // FIELDS //
    ////////////

    protected final PlotTraceModel traceModel;
    protected final ILogger logger;
    protected final Map<String, String> properties;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    protected RulePlotTraceModelActor(PlotTraceModel traceModel, ILogger logger, Map<String, String> properties) {
        this.traceModel = traceModel;
        this.logger = logger;
        this.properties = new HashMap<>();
        this.properties.putAll(properties);
    }

    /////////////
    // GETTERS //
    /////////////

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    public PlotTraceModel getParentTraceModel() {
        return traceModel;
    }
}
