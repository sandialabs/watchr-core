/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators.rule.actors;

import java.util.Map;

import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.generators.rule.RuleAction;
import gov.sandia.watchr.util.RGB;

public class RulePlotTraceModelWarnActor extends RulePlotTraceModelActor {

    public static final RGB WARN_COLOR = new RGB(239, 228, 176);
    public static final RGB NORMAL_COLOR = new RGB(255, 255, 255);
    private static final String CLASSNAME = RulePlotTraceModelWarnActor.class.getSimpleName();

    public RulePlotTraceModelWarnActor(PlotTraceModel traceModel, ILogger logger, Map<String, String> properties) {
        super(traceModel, logger, properties);
    }

    @Override
    public void act() {
        logger.logDebug("Acting upon rule " + RuleAction.WARN_PLOT.toString() + "...", CLASSNAME);
        PlotCanvasModel canvasModel = traceModel.getParent();
        PlotWindowModel windowModel = canvasModel.getParent();
        windowModel.setBackgroundColor(WARN_COLOR);
    }
    
    @Override
    public void undo() {
        PlotCanvasModel canvasModel = traceModel.getParent();
        PlotWindowModel windowModel = canvasModel.getParent();        
        windowModel.setBackgroundColor(NORMAL_COLOR);
    }    
}
