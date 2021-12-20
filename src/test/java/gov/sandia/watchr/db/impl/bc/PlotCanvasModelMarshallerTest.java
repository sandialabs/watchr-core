package gov.sandia.watchr.db.impl.bc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.TestFileUtils;
import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.db.impl.FileBasedDatabase;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.util.RGB;

public class PlotCanvasModelMarshallerTest {
    
    private StringOutputLogger testLogger;
    private IFileReader fileReader;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
        fileReader = new DefaultFileReader(testLogger);
    }

    @Test
    public void test_PlotCanvasModel_BackwardsCompatibility_LoadCanvasesOnce() {
        // If a PlotWindowModel is saved to the database and then later re-loaded,
        // it was previously possible for canvases to be double-loaded onto those
        // window models.  This test guards against that bug.
        try {
            File tempDir = Files.createTempDirectory(null).toFile();
            FileBasedDatabase db = new FileBasedDatabase(tempDir, testLogger, fileReader);
            PlotWindowModel windowModel = new PlotWindowModel("test");
            new PlotCanvasModel(windowModel.getUUID());
            db.addPlot(windowModel);
            db.saveState();

            FileBasedDatabase db2 = new FileBasedDatabase(tempDir, testLogger, fileReader);
            db2.loadState();
            db2.getAllPlots();
            assertEquals(1, windowModel.getCanvasModels().size());
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_PlotCanvasModel_BackwardsCompatibility_130_to_140() {
        try {
            File version130_PlotFile = TestFileUtils.getTestFile(
                PlotCanvasModelMarshallerTest.class, "bc/plot_PlotCanvasModelDrawAxisLines_130.json");

            FileBasedDatabase db = new FileBasedDatabase(
                Files.createTempDirectory(null).toFile(), testLogger, fileReader);
            PlotWindowModel windowModel = db.readPlot(version130_PlotFile);
            PlotCanvasModel canvasModel = windowModel.getCanvasModels().get(0);

            assertTrue(canvasModel.getDrawXAxisLines());
            assertTrue(canvasModel.getDrawYAxisLines());
            assertTrue(canvasModel.getDrawZAxisLines());
        } catch(IOException | URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_PlotCanvasModel_130_to_140_TestFields() {
        try {
            File version130_PlotFile = TestFileUtils.getTestFile(
                PlotCanvasModelMarshallerTest.class, "bc/plot_PlotCanvasModelDrawAxisLines_130.json");

            FileBasedDatabase db = new FileBasedDatabase(
                Files.createTempDirectory(null).toFile(), testLogger, fileReader);
            PlotWindowModel windowModel = db.readPlot(version130_PlotFile);
            PlotCanvasModel canvasModel = windowModel.getCanvasModels().get(0);

            assertEquals("0fef263d-b2f9-41c4-bc59-809885dca2f9", canvasModel.getUUID().toString());
            assertEquals(0, canvasModel.getRowPosition());
            assertEquals(0, canvasModel.getColPosition());
            assertEquals(3, canvasModel.getAxisPrecision());
            assertEquals("timestamp", canvasModel.getXAxisLabel());
            assertEquals("seconds", canvasModel.getYAxisLabel());
            assertEquals(new RGB(0,0,0), canvasModel.getXAxisRGB());
            assertEquals(new RGB(0,0,0), canvasModel.getYAxisRGB());
            assertEquals(new RGB(0,0,0), canvasModel.getZAxisRGB());
            assertTrue(canvasModel.getAutoscale());
            assertFalse(canvasModel.getXLogScale());
            assertFalse(canvasModel.getYLogScale());
            assertFalse(canvasModel.getZLogScale());
            assertFalse(canvasModel.getDrawGridLines());
            assertTrue(canvasModel.getDrawXAxisLines());
            assertTrue(canvasModel.getDrawYAxisLines());
            assertTrue(canvasModel.getDrawZAxisLines());
            assertEquals("00af1a93-b428-4a70-ae90-dfebecd8a94e", canvasModel.getParentWindowModelUUID().toString());
            assertEquals(0, canvasModel.getOverlaidCanvasModels().size());
            assertEquals(3, canvasModel.getTraceModels().size());
        } catch(IOException | URISyntaxException e) {
            fail(e.getMessage());
        }
    }
}
