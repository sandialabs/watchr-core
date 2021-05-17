/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.rule.actors;

import java.util.Map;

import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.rule.RuleActor;
import gov.sandia.watchr.util.RGB;

public class RuleFailActor extends RuleActor {

    public static final RGB FAIL_COLOR = new RGB(235, 156, 156);

    public RuleFailActor(PlotTraceModel traceModel, Map<String, String> properties) {
        super(traceModel, properties);
    }

    @Override
    public void act() {
        PlotCanvasModel canvasModel = traceModel.getParent();
        PlotWindowModel windowModel = canvasModel.getParent();
        windowModel.setBackgroundColor(FAIL_COLOR);
    }

    @Override
    public void undo() {
        PlotCanvasModel canvasModel = traceModel.getParent();
        PlotWindowModel windowModel = canvasModel.getParent();        
        windowModel.setBackgroundColor(new RGB(255, 255, 255));
    }    
}
