package gov.sandia.watchr.parse.generators.rule.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.chartreuse.Dimension;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceOptions;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;

public class RulePropertyAgeToDelete implements RuleProperty<IDatabase> {

    @Override
    public Object process(IDatabase db, String value) {
        int ageToDelete = Integer.parseInt(value);
        Map<String, List<PlotTracePoint>> resultMap = new HashMap<>();
        List<PlotWindowModel> plots = db.getAllPlots();
        for(PlotWindowModel windowModel : plots) {
            for(PlotCanvasModel canvasModel : windowModel.getCanvasModels()) {
                for(PlotTraceModel traceModel : canvasModel.getTraceModels()) {
                    PlotTraceOptions options = new PlotTraceOptions();
                    options.sortAlongDimension = Dimension.X;
                    List<PlotTracePoint> points = traceModel.getPoints(options);
                    List<PlotTracePoint> pointsToDelete = new ArrayList<>();

                    int end = Math.max(0, points.size() - ageToDelete);
                    for(int i = 0; i < end; i++) {
                        pointsToDelete.add(points.get(i));
                    }
                    resultMap.put(traceModel.getUUID().toString(), pointsToDelete);
                }
            }
        }
        return resultMap;
    }
}
