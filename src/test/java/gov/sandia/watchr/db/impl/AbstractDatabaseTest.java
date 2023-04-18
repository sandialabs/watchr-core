package gov.sandia.watchr.db.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.db.PlotDatabaseSearchCriteria;
import gov.sandia.watchr.db.TestDatabase;
import gov.sandia.watchr.graph.chartreuse.ChartreuseException;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.util.CommonConstants;

public class AbstractDatabaseTest {
    
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
    public void testGetGraphDisplayConfig() {
        GraphDisplayConfig graphDisplayConfig = db.getGraphDisplayConfig();
        assertNotNull(graphDisplayConfig);
    }

    @Test
    public void testGetPlotByNameAndCategory() {
        PlotWindowModel newPlot = new PlotWindowModel("MyTestPlot");
        newPlot.setCategory("MyCategory");
        db.addPlot(newPlot);

        PlotWindowModel retrievedPlot = db.searchPlot(new PlotDatabaseSearchCriteria("MyTestPlot", "MyCategory"));
        assertTrue(newPlot.effectiveEquals(retrievedPlot));
    }

    @Test
    public void testGetPlotByName_DoNotReturnIfCategoryDoesntMatch() {
        PlotWindowModel newPlot = new PlotWindowModel("MyTestPlot");
        newPlot.setCategory("MyCategory");
        db.addPlot(newPlot);

        PlotWindowModel retrievedPlot = db.searchPlot(new PlotDatabaseSearchCriteria("MyTestPlot", "MyOtherCategory"));
        assertNull(retrievedPlot);
    }    

    @Test
    public void testGetPlotsByNameAndCategory() {
        PlotWindowModel plot1 = new PlotWindowModel("MyTestPlot1");
        PlotWindowModel plot2 = new PlotWindowModel("MyTestPlot2");
        PlotWindowModel plot3 = new PlotWindowModel("MyTestPlot3");
        plot1.setCategory("MyCategory");
        plot2.setCategory("MyCategory");
        plot3.setCategory("OtherCategory");
        db.addPlot(plot1);
        db.addPlot(plot2);
        db.addPlot(plot3);

        assertNotNull(db.searchPlot(new PlotDatabaseSearchCriteria("MyTestPlot1", "MyCategory")));
        assertNotNull(db.searchPlot(new PlotDatabaseSearchCriteria("MyTestPlot2", "MyCategory")));
        assertNull(db.searchPlot(new PlotDatabaseSearchCriteria("MyTestPlot3", "MyCategory")));
        assertNotNull(db.searchPlot(new PlotDatabaseSearchCriteria("MyTestPlot3", "OtherCategory")));
    }

    @Test
    public void testDeletePlot() {
        PlotWindowModel plot1 = new PlotWindowModel(CommonConstants.ROOT_PATH_ALIAS);
        PlotWindowModel plot2 = new PlotWindowModel("MyChildPlot1");
        PlotWindowModel plot3 = new PlotWindowModel("MyChildPlot2");
        
        db.addPlot(plot1);
        db.addPlot(plot2);
        db.addPlot(plot3);

        List<PlotWindowModel> childPlots = new ArrayList<>();
        childPlots.add(plot2);
        childPlots.add(plot3);
        db.setPlotsAsChildren(plot1, childPlots);

        db.deletePlot(plot1.getUUID().toString());

        List<PlotWindowModel> retrievedPlots = db.getAllPlots();
        assertTrue(retrievedPlots.isEmpty());
    }

    @Test
    public void testGetParent() {
        PlotWindowModel plot1 = new PlotWindowModel(CommonConstants.ROOT_PATH_ALIAS);
        PlotWindowModel plot2 = new PlotWindowModel("MyChildPlot1");
        PlotWindowModel plot3 = new PlotWindowModel("MyChildPlot2");
        
        db.addPlot(plot1);
        db.addPlot(plot2);
        db.addPlot(plot3);

        List<PlotWindowModel> childPlots = new ArrayList<>();
        childPlots.add(plot2);
        childPlots.add(plot3);
        db.setPlotsAsChildren(plot1, childPlots);

        PlotWindowModel parent = db.getParent(new PlotDatabaseSearchCriteria(plot2.getName(), ""));
        assertEquals(parent, plot1);
    }

    @Test
    public void testSetListeners_FireChangeListener() {
        try {
            PlotWindowModel plot = new PlotWindowModel(CommonConstants.ROOT_PATH_ALIAS);       
            PlotCanvasModel canvas = new PlotCanvasModel(plot.getUUID());
            PlotTraceModel trace = new PlotTraceModel(canvas.getUUID());

            db.addPlot(plot);
            db.setListeners(plot);

            trace.fireChangeListeners();
            assertTrue(db.getDirtyPlotUUIDs().contains(plot.getUUID().toString()));
        } catch(ChartreuseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testSetListeners_FirePropertyChangeListener() {
        try {
            PlotWindowModel plot = new PlotWindowModel(CommonConstants.ROOT_PATH_ALIAS);       
            PlotCanvasModel canvas = new PlotCanvasModel(plot.getUUID());
            PlotTraceModel trace = new PlotTraceModel(canvas.getUUID());

            db.addPlot(plot);
            db.setListeners(plot);

            trace.firePropertyChangeListeners(PlotToken.TRACE_POINT_MODE);
            assertTrue(db.getDirtyPlotUUIDs().contains(plot.getUUID().toString()));
        } catch(ChartreuseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetPlot_WithRegexCharactersInTitle() {
        try {
            String regexViolatingPlotName = "Lorem Serial: Ipsum 4 ranks/1) ElementLoop  (Graph)";
            PlotWindowModel plot = new PlotWindowModel(regexViolatingPlotName);       
            PlotCanvasModel canvas = new PlotCanvasModel(plot.getUUID());
            new PlotTraceModel(canvas.getUUID());
            db.addPlot(plot);

            PlotWindowModel returnedPlot = db.searchPlot(new PlotDatabaseSearchCriteria(regexViolatingPlotName, ""));
            assertEquals(plot, returnedPlot);
        } catch(ChartreuseException e) {
            fail(e.getMessage());
        }
    }
}
