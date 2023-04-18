package gov.sandia.watchr.parse.generators.rule.actors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.generators.rule.RuleAction;

public class RuleDatabaseDeleteSomeThingsActor extends RuleDatabaseActor implements DataProcessingRuleActor {

    private static final String CLASSNAME = "RuleDatabaseDeleteSomeThingsActor";
    private Map<String, List<PlotTracePoint>> plotsToDelete;

    public RuleDatabaseDeleteSomeThingsActor(IDatabase db, ILogger logger, Map<String, String> properties) {
        super(db, logger, properties);
        this.plotsToDelete = new HashMap<>();
    }

    @Override
    public void act() {
        logger.logInfo("Acting upon rule " + RuleAction.DELETE_SOME.toString() + "...");
        List<PlotWindowModel> plots = db.getAllPlots();
        for(PlotWindowModel windowModel : plots) {
            boolean dataChanged = false;
            for(PlotCanvasModel canvasModel : windowModel.getCanvasModels()) {
                for(PlotTraceModel traceModel : canvasModel.getTraceModels()) {
                    if(plotsToDelete.containsKey(traceModel.getUUID().toString())) {
                        List<PlotTracePoint> pointsToDelete = plotsToDelete.get(traceModel.getUUID().toString());
                        if(!pointsToDelete.isEmpty()) {
                            logger.logDebug("Deleting some data from main trace model of plot " + windowModel.getName(), CLASSNAME);
                        }
                        for(PlotTracePoint point : pointsToDelete) {
                            traceModel.remove(point);
                            dataChanged = true;
                        }
                    }
                }
            }
            if(dataChanged) {
                db.updatePlot(windowModel, false);
            }
        }
    }

    @Override
    public void undo() {
        // Not implemented - You can't undo a delete.
    }

    @Override
    public Object getDataToProcess() {
        return plotsToDelete;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setDataToProcess(Object data) {
        if(data instanceof Map<?,?>) {
            plotsToDelete = (Map<String, List<PlotTracePoint>>) data;
        }
    }
    
}
