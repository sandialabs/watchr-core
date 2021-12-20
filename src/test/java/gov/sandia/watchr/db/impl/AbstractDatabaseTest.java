package gov.sandia.watchr.db.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.log.ILogger;
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

        PlotWindowModel retrievedPlot = db.searchPlot("MyTestPlot", "MyCategory");
        assertTrue(newPlot.effectiveEquals(retrievedPlot));
    }

    @Test
    public void testGetPlotByName_DoNotReturnIfCategoryDoesntMatch() {
        PlotWindowModel newPlot = new PlotWindowModel("MyTestPlot");
        newPlot.setCategory("MyCategory");
        db.addPlot(newPlot);

        PlotWindowModel retrievedPlot = db.searchPlot("MyTestPlot", "MyOtherCategory");
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

        assertNotNull(db.searchPlot("MyTestPlot1", "MyCategory"));
        assertNotNull(db.searchPlot("MyTestPlot2", "MyCategory"));
        assertNull(db.searchPlot("MyTestPlot3", "MyCategory"));
        assertNotNull(db.searchPlot("MyTestPlot3", "OtherCategory"));
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

        db.deletePlot(plot1);

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

        PlotWindowModel parent = db.getParent(plot2.getName(), "MyChildPlot1");
        assertEquals(parent, plot1);
    }

    @Test
    public void testSetListeners_FireChangeListener() {
        PlotWindowModel plot = new PlotWindowModel(CommonConstants.ROOT_PATH_ALIAS);       
        PlotCanvasModel canvas = new PlotCanvasModel(plot.getUUID());
        PlotTraceModel trace = new PlotTraceModel(canvas.getUUID());

        db.addPlot(plot);
        db.setListeners(plot);

        trace.fireChangeListeners();
        assertTrue(db.getDirtyPlotUUIDs().contains(plot.getUUID().toString()));
    }

    @Test
    public void testSetListeners_FirePropertyChangeListener() {
        PlotWindowModel plot = new PlotWindowModel(CommonConstants.ROOT_PATH_ALIAS);       
        PlotCanvasModel canvas = new PlotCanvasModel(plot.getUUID());
        PlotTraceModel trace = new PlotTraceModel(canvas.getUUID());

        db.addPlot(plot);
        db.setListeners(plot);

        trace.firePropertyChangeListeners(PlotToken.TRACE_POINT_MODE);
        assertTrue(db.getDirtyPlotUUIDs().contains(plot.getUUID().toString()));
    }

    @Test
    public void testGetPlot_WithRegexCharactersInTitle() {
        String regexViolatingPlotName = "Lorem Serial: Ipsum 4 ranks/1) ElementLoop  (Graph)";
        PlotWindowModel plot = new PlotWindowModel(regexViolatingPlotName);       
        PlotCanvasModel canvas = new PlotCanvasModel(plot.getUUID());
        new PlotTraceModel(canvas.getUUID());
        db.addPlot(plot);

        PlotWindowModel returnedPlot = db.searchPlot(regexViolatingPlotName, "");
        assertEquals(plot, returnedPlot);
    }
}

class TestDatabase extends AbstractDatabase {

    protected TestDatabase(ILogger logger, IFileReader fileReader) {
        super(logger, fileReader);
    }

    @Override
    public void loadState() {
        // Do nothing
    }

    @Override
    public void saveState() {
        // Do nothing
    }

    public void setListeners(PlotWindowModel windowModel) {
        super.setListeners(windowModel);
    }

    public Set<String> getDirtyPlotUUIDs() {
        return dirtyPlotUUIDs;
    }

    @Override
    public PlotWindowModel loadPlotUsingUUID(String uuid) {
        return null;
    }

    @Override
    public PlotWindowModel loadRootPlot() {
        return null;
    }

    @Override
    public void updateMetadata() {
        // Do nothing
    }

    @Override
    public PlotWindowModel loadPlotUsingInnerFields(String name, String category) {
        for(PlotWindowModel plot : plots) {
            if(plot.getName().equals(name) && plot.getCategory().equals(category)) {
                return plot;
            }
        }
        return null;
    }
}
