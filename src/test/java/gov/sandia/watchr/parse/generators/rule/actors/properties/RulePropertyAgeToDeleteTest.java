package gov.sandia.watchr.parse.generators.rule.actors.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.db.TestDatabase;
import gov.sandia.watchr.graph.chartreuse.ChartreuseException;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.parse.generators.rule.properties.RulePropertyAgeToDelete;

public class RulePropertyAgeToDeleteTest {
    
    private TestDatabase db;
    private StringOutputLogger testLogger;
    private IFileReader fileReader;
    private RulePropertyAgeToDelete propertyProcessor;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
        fileReader = new DefaultFileReader(testLogger);
        db = new TestDatabase(testLogger, fileReader);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProcessDataFromDatabase_DeleteFivePoints() {
        try {
            PlotWindowModel newPlot = new PlotWindowModel("MyTestPlot");
            PlotCanvasModel newCanvas = new PlotCanvasModel(newPlot.getUUID());
            PlotTraceModel newTrace = new PlotTraceModel(newCanvas.getUUID());
            newTrace.add(new PlotTracePoint(1, 1));
            newTrace.add(new PlotTracePoint(2, 4));
            newTrace.add(new PlotTracePoint(3, 9));
            newTrace.add(new PlotTracePoint(4, 16));
            newTrace.add(new PlotTracePoint(5, 25));
            newTrace.add(new PlotTracePoint(6, 36));
            newTrace.add(new PlotTracePoint(7, 49));
            newTrace.add(new PlotTracePoint(8, 64));

            db.addPlot(newPlot);
            
            propertyProcessor = new RulePropertyAgeToDelete();
            Object result = propertyProcessor.process(db, "5");

            assertTrue(result instanceof Map<?,?>);
            Map<PlotTraceModel, List<PlotTracePoint>> pointsToDelete =
                (Map<PlotTraceModel, List<PlotTracePoint>>) result;

            assertEquals(1, pointsToDelete.values().size());
            List<PlotTracePoint> points = pointsToDelete.values().iterator().next();
            assertEquals(3, points.size());
            assertEquals(new PlotTracePoint(1,1), points.get(0));
            assertEquals(new PlotTracePoint(2,4), points.get(1));
            assertEquals(new PlotTracePoint(3,9), points.get(2));
        } catch(ChartreuseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProcessDataFromDatabase_DeleteLessThanFivePoints() {
        try {
            PlotWindowModel newPlot = new PlotWindowModel("MyTestPlot");
            PlotCanvasModel newCanvas = new PlotCanvasModel(newPlot.getUUID());
            PlotTraceModel newTrace = new PlotTraceModel(newCanvas.getUUID());
            newTrace.add(new PlotTracePoint(1, 1));
            newTrace.add(new PlotTracePoint(2, 4));
            newTrace.add(new PlotTracePoint(3, 9));

            db.addPlot(newPlot);
            
            propertyProcessor = new RulePropertyAgeToDelete();
            Object result = propertyProcessor.process(db, "5");

            assertTrue(result instanceof Map<?,?>);
            Map<String, List<PlotTracePoint>> pointsToDelete = (Map<String, List<PlotTracePoint>>) result;

            assertEquals(1, pointsToDelete.values().size());
            List<PlotTracePoint> points = pointsToDelete.values().iterator().next();
            assertEquals(0, points.size());
        } catch(ChartreuseException e) {
            fail(e.getMessage());
        }
    }
}
