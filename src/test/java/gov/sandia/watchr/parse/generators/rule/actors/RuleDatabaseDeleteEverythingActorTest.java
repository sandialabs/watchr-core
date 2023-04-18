package gov.sandia.watchr.parse.generators.rule.actors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;

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

public class RuleDatabaseDeleteEverythingActorTest {
    
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
        RuleDatabaseDeleteEverythingActor ruleActor = new RuleDatabaseDeleteEverythingActor(db, new StringOutputLogger(), new HashMap<>());
        ruleActor.act();
        assertNotNull(db);
    }

    @Test
    public void testRuleWorks_DeleteEverythingFromDatabase() {
        try {
            PlotWindowModel newPlot = new PlotWindowModel("MyTestPlot");
            PlotCanvasModel newCanvas = new PlotCanvasModel(newPlot.getUUID());
            PlotTraceModel newTrace = new PlotTraceModel(newCanvas.getUUID());
            newTrace.add(new PlotTracePoint(1, 1));

            db.addPlot(newPlot);
            assertEquals(1, db.getAllPlots().size());

            RuleDatabaseDeleteEverythingActor ruleActor = new RuleDatabaseDeleteEverythingActor(db, new StringOutputLogger(), new HashMap<>());
            ruleActor.act();

            assertEquals(0, db.getAllPlots().size());
        } catch(ChartreuseException e) {
            fail(e.getMessage());
        }
    }
}
