package gov.sandia.watchr.db.impl.bc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.TestFileUtils;
import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.GraphDisplayConfig.ExportMode;
import gov.sandia.watchr.config.GraphDisplayConfig.GraphDisplaySort;
import gov.sandia.watchr.config.GraphDisplayConfig.LeafNodeStrategy;
import gov.sandia.watchr.config.derivative.DerivativeLine;
import gov.sandia.watchr.log.StringOutputLogger;

public class GraphDisplayConfigMarshallerTest {
   
    private StringOutputLogger testLogger;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
    }

    @Test
    public void test_GraphDisplayConfig_BackwardsCompatibility_140_to_150() {
        try {
            File version140_ConfigFile = TestFileUtils.getTestFile(
                GraphDisplayConfigMarshallerTest.class, "bc/config_leafNodeStrategy_140.json");

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(DerivativeLine.class, new DerivativeLineMarshaller())
                    .registerTypeAdapter(GraphDisplayConfig.class, new GraphDisplayConfigMarshaller(testLogger))
                    .create();
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(version140_ConfigFile))) {
                WatchrConfig watchrConfig = gson.fromJson(bufferedReader, WatchrConfig.class); 
                GraphDisplayConfig graphDisplayConfig = watchrConfig.getGraphDisplayConfig();
                assertEquals(LeafNodeStrategy.TRAVEL_UP_TO_PARENT, graphDisplayConfig.getLeafNodeStrategy());
            }
        } catch(IOException | URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void test_PlotCanvasModel_140_to_150_TestFields() {
        try {
            File version140_ConfigFile = TestFileUtils.getTestFile(
                GraphDisplayConfigMarshallerTest.class, "bc/config_leafNodeStrategy_140.json");

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(DerivativeLine.class, new DerivativeLineMarshaller())
                    .registerTypeAdapter(GraphDisplayConfig.class, new GraphDisplayConfigMarshaller(testLogger))
                    .create();
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(version140_ConfigFile))) {
                WatchrConfig watchrConfig = gson.fromJson(bufferedReader, WatchrConfig.class); 
                GraphDisplayConfig graphDisplayConfig = watchrConfig.getGraphDisplayConfig();
                
                assertEquals("//graphDisplayConfig/graphDisplayConfig", graphDisplayConfig.getConfigPath());
                assertEquals("cpu-time-max", graphDisplayConfig.getDisplayCategory());
                assertEquals(30, graphDisplayConfig.getDisplayRange());
                assertEquals(3, graphDisplayConfig.getDisplayedDecimalPlaces());
                assertEquals(ExportMode.PER_CATEGORY, graphDisplayConfig.getExportMode());
                assertEquals(450, graphDisplayConfig.getGraphHeight());
                assertEquals(450, graphDisplayConfig.getGraphWidth());
                assertEquals(15, graphDisplayConfig.getGraphsPerPage());
                assertEquals(3, graphDisplayConfig.getGraphsPerRow());
                assertEquals("/3d_omd_fic_s/Test", graphDisplayConfig.getLastPlotDbLocation());
                assertEquals(LeafNodeStrategy.TRAVEL_UP_TO_PARENT, graphDisplayConfig.getLeafNodeStrategy());
                assertEquals("/3d_omd_fic_s/Test", graphDisplayConfig.getNextPlotDbLocation());
                assertEquals(1, graphDisplayConfig.getPage());
                assertEquals(GraphDisplaySort.ASCENDING, graphDisplayConfig.getSort());
            }
        } catch(IOException | URISyntaxException e) {
            fail(e.getMessage());
        }
    }
}
