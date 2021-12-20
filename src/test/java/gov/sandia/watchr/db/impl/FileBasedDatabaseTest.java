package gov.sandia.watchr.db.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.util.CommonConstants;

public class FileBasedDatabaseTest {
    
    ////////////
    // FIELDS //
    ////////////

    private FileBasedDatabase db;
    private File rootDir;
    private StringOutputLogger testLogger;
    private IFileReader fileReader;

    ///////////
    // SETUP //
    ///////////

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
        fileReader = new DefaultFileReader(testLogger);

        try {
            rootDir = Files.createTempDirectory("FileBasedDatabaseTest").toFile();
            db = new FileBasedDatabase(rootDir, testLogger, fileReader);
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }

    //////////
    // TEST //
    //////////

    @Test
    public void testReadPlot() {
        PlotWindowModel plot = new PlotWindowModel("MyTestPlot1");
        plot.setCategory("MyCategory");
        db.addPlot(plot);
        db.saveState();
        db.clearPlotCache();
        
        String expectedPlotName = "plot_" + plot.getUUID().toString() + ".json";
        File plotFile = new File(rootDir, expectedPlotName);

        PlotWindowModel readPlot = db.readPlot(plotFile);
        assertEquals(plot, readPlot);
    }

    @Test
    public void testGetPlotByUUID() {
        PlotWindowModel plot = new PlotWindowModel("MyTestPlot1");
        plot.setCategory("MyCategory");
        db.addPlot(plot);
        db.saveState();
        db.clearPlotCache();
        
        PlotWindowModel readPlot = db.getPlotByUUID(plot.getUUID().toString());
        assertEquals(plot, readPlot);
    }

    @Test
    public void testDeletePlot() {
        PlotWindowModel plot = new PlotWindowModel(CommonConstants.ROOT_PATH_ALIAS);
        db.addPlot(plot);
        db.saveState();
        assertEquals(1, db.getAllPlots().size());

        db.deletePlot(plot);
        db.saveState();
        assertEquals(0, db.getAllPlots().size());
    }

    @Test
    public void testReadPlots() {
        PlotWindowModel plot1 = new PlotWindowModel("Plot1");
        PlotWindowModel plot2 = new PlotWindowModel("Plot2");
        PlotWindowModel plot3 = new PlotWindowModel("Plot3");
        db.addPlot(plot1);
        db.addPlot(plot2);
        db.addPlot(plot3);
        db.saveState();

        assertEquals(3, db.plots.size());

        db.clearPlotCache();
        assertEquals(0, db.plots.size());

        db.readPlots();
        assertEquals(3, db.plots.size());
    }

    @Test
    public void testLoadRootPlot() {
        PlotWindowModel plot = new PlotWindowModel("MyTestPlot1");
        db.addPlot(plot);
        PlotWindowModel rootPlot = new PlotWindowModel(CommonConstants.ROOT_PATH_ALIAS);
        db.addPlot(rootPlot);
        db.saveState();
        db.clearPlotCache();
        
        PlotWindowModel readPlot = db.loadRootPlot();
        assertEquals(rootPlot, readPlot);
        assertEquals(1, db.plots.size());
    }

    @Test
    public void testLoadChildPlots() {
        PlotWindowModel plotWindow1 = new PlotWindowModel("MyTestPlot1");
        PlotWindowModel plotWindow2 = new PlotWindowModel("MyTestPlot2");
        PlotWindowModel plotWindow3 = new PlotWindowModel("MyTestPlot3");

        List<PlotWindowModel> childPlots = new ArrayList<>();
        childPlots.add(plotWindow2);
        childPlots.add(plotWindow3);

        db.addPlot(plotWindow1);
        db.addPlot(plotWindow2);
        db.addPlot(plotWindow3);
        db.setPlotsAsChildren(plotWindow1, childPlots);

        db.saveState();
        db.clearPlotCache();
        
        PlotWindowModel readPlot = db.loadPlotUsingUUID(plotWindow1.getUUID().toString());
        assertEquals(plotWindow1, readPlot);
        assertEquals(1, db.plots.size());

        db.getChildren("MyTestPlot1", "");
        assertEquals(3, db.plots.size());
    }    

    @Test
    public void testReadParentChildRelationships() {
        PlotWindowModel plot = new PlotWindowModel("MyTestPlot1");
        db.addPlot(plot);
        PlotWindowModel plot2 = new PlotWindowModel("MyTestPlot2");
        PlotWindowModel plot3 = new PlotWindowModel("MyTestPlot3");
        List<PlotWindowModel> childPlots = new ArrayList<>();
        childPlots.add(plot2);
        childPlots.add(plot3);
        db.setPlotsAsChildren(plot, childPlots);
        db.saveState();
        db.clearPlotCache();
        
        FileBasedDatabase newDb = new FileBasedDatabase(rootDir, testLogger, fileReader);
        newDb.readParentChildPlotRelationships();
        assertEquals(1, newDb.parentChildPlots.size());
        Set<String> expectedKeys = newDb.parentChildPlots.keySet();
        assertEquals(1, expectedKeys.size());
        assertTrue(expectedKeys.contains(plot.getUUID().toString()));

        List<String> expectedValues = new ArrayList<>(newDb.parentChildPlots.values().iterator().next());
        assertEquals(2, expectedValues.size());
        assertTrue(expectedValues.contains(plot2.getUUID().toString()));
        assertTrue(expectedValues.contains(plot3.getUUID().toString()));
    }

    
    @Test
    public void testReadFileCache() {
        db.addFileToCache("FilePath1");
        db.addFileToCache("FilePath2");
        
        db.saveState();

        FileBasedDatabase newDb = new FileBasedDatabase(rootDir, testLogger, fileReader);
        assertFalse(newDb.hasSeenFile("FilePath1"));
        assertFalse(newDb.hasSeenFile("FilePath2"));
        newDb.readFileCache();
        assertTrue(newDb.hasSeenFile("FilePath1"));
        assertTrue(newDb.hasSeenFile("FilePath2"));
    }

    @Test
    public void testReadMetadata() {
        db.getMetadata().setPlotCount(100);
        db.getMetadata().setFailedPlotCount(25);
        
        db.saveState();

        FileBasedDatabase newDb = new FileBasedDatabase(rootDir, testLogger, fileReader);
        assertEquals(0, newDb.getMetadata().getPlotCount());
        assertEquals(0, newDb.getMetadata().getFailedPlotCount());
        newDb.readMetadata();
        assertEquals(100, newDb.getMetadata().getPlotCount());
        assertEquals(25, newDb.getMetadata().getFailedPlotCount());
    }

    @Test
    public void testReadLastConfiguration() {
        GraphDisplayConfig graphDisplayConfig = new GraphDisplayConfig("", testLogger);
        graphDisplayConfig.setGraphWidth(1920);
        graphDisplayConfig.setGraphHeight(1080);

        WatchrConfig config = db.getLastConfig();
        config.setGraphDisplayConfig(graphDisplayConfig);

        db.saveState();

        FileBasedDatabase newDb = new FileBasedDatabase(rootDir, testLogger, fileReader);
        assertEquals(500, newDb.getLastConfig().getGraphDisplayConfig().getGraphWidth());
        assertEquals(500, newDb.getLastConfig().getGraphDisplayConfig().getGraphHeight());

        newDb.readLastConfiguration();
        assertEquals(1920, newDb.getLastConfig().getGraphDisplayConfig().getGraphWidth());
        assertEquals(1080, newDb.getLastConfig().getGraphDisplayConfig().getGraphHeight());
    }
}