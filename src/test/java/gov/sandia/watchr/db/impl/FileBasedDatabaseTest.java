package gov.sandia.watchr.db.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;

public class FileBasedDatabaseTest {
    
    ////////////
    // FIELDS //
    ////////////

    private FileBasedDatabase db;
    private File rootDir;

    ///////////
    // SETUP //
    ///////////

    @Before
    public void setup() {
        try {
            rootDir = Files.createTempDirectory(null).toFile();
            db = new FileBasedDatabase(rootDir);
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
        
        String expectedPlotName = "plot_" + plot.getUUID().toString() + ".json";
        File plotFile = new File(rootDir, expectedPlotName);

        PlotWindowModel readPlot = db.readPlot(plotFile);
        assertEquals(plot, readPlot);
    }
}
