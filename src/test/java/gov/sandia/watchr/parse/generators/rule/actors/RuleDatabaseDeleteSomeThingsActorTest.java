package gov.sandia.watchr.parse.generators.rule.actors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
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

public class RuleDatabaseDeleteSomeThingsActorTest {
    
    private TestDatabase db;
    private StringOutputLogger testLogger;
    private IFileReader fileReader;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
        fileReader = new DefaultFileReader(testLogger);
        db = new TestDatabase(testLogger, fileReader);
    }

    @Test
    public void testRuleWorks_NoOp() {
        RuleDatabaseDeleteSomeThingsActor ruleActor = new RuleDatabaseDeleteSomeThingsActor(db, new StringOutputLogger(), new HashMap<>());
        ruleActor.act();
        assertNotNull(db);
    }

    @Test
    public void testRule_SetAndGetData() {
        try {
            RuleDatabaseDeleteSomeThingsActor ruleActor = new RuleDatabaseDeleteSomeThingsActor(db, new StringOutputLogger(), new HashMap<>());

            PlotWindowModel newPlot = new PlotWindowModel("MyTestPlot");
            PlotCanvasModel newCanvas = new PlotCanvasModel(newPlot.getUUID());
            PlotTraceModel newTrace = new PlotTraceModel(newCanvas.getUUID());
            newTrace.add(new PlotTracePoint(1, 1));
            newTrace.add(new PlotTracePoint(2, 4));
            newTrace.add(new PlotTracePoint(3, 9));

            List<PlotTracePoint> pointsToDelete = new ArrayList<>();
            pointsToDelete.add(new PlotTracePoint(2, 4));

            Map<PlotTraceModel, List<PlotTracePoint>> plotsToDelete = new HashMap<>();
            plotsToDelete.put(newTrace, pointsToDelete);

            ruleActor.setDataToProcess(plotsToDelete);
            assertEquals(plotsToDelete, ruleActor.getDataToProcess());
        } catch(ChartreuseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testRuleWorks_DeleteSomeThingsFromDatabase() {
        try {
            PlotWindowModel newPlot = new PlotWindowModel("MyTestPlot");
            PlotCanvasModel newCanvas = new PlotCanvasModel(newPlot.getUUID());
            PlotTraceModel newTrace = new PlotTraceModel(newCanvas.getUUID());
            newTrace.add(new PlotTracePoint(1, 1));
            newTrace.add(new PlotTracePoint(2, 4));
            newTrace.add(new PlotTracePoint(3, 9));

            List<PlotTracePoint> pointsToDelete = new ArrayList<>();
            pointsToDelete.add(new PlotTracePoint(2, 4));

            Map<String, List<PlotTracePoint>> plotsToDelete = new HashMap<>();
            plotsToDelete.put(newTrace.getUUID().toString(), pointsToDelete);

            db.addPlot(newPlot);
            assertEquals(1, db.getAllPlots().size());
            assertEquals(3, newTrace.getPoints().size());

            RuleDatabaseDeleteSomeThingsActor ruleActor = new RuleDatabaseDeleteSomeThingsActor(db, new StringOutputLogger(), new HashMap<>());
            ruleActor.setDataToProcess(plotsToDelete);
            ruleActor.act();

            assertEquals(2, newTrace.getPoints().size());
            assertTrue(newTrace.getPoints().contains(new PlotTracePoint(1,1)));
            assertTrue(newTrace.getPoints().contains(new PlotTracePoint(3,9)));
        } catch(ChartreuseException e) {
            fail(e.getMessage());
        }
    }
}

