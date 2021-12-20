package gov.sandia.watchr.graph.library.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.TestFileUtils;
import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.GraphDisplayConfig.ExportMode;
import gov.sandia.watchr.config.GraphDisplayConfig.GraphDisplaySort;
import gov.sandia.watchr.config.GraphDisplayConfig.LeafNodeStrategy;
import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.graph.library.GraphOperationResult;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.util.CommonConstants;

public class PlotlyGraphRendererTest {
    
    private StringOutputLogger testLogger;
    private IFileReader fileReader;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
        fileReader = new DefaultFileReader(testLogger);
    }

    @Test
    public void testGetGraphHtml() {
        try {
            final String dbName = "testDb";
            final File config    = TestFileUtils.loadTestFile("unit_tests/xml/ThreePointsOnOneLine/config_day_1.json");
            final File dataFile  = TestFileUtils.loadTestFile("unit_tests/xml/ThreePointsOnOneLine/performance_day_1.xml");
            final File dbDir     = Files.createTempDirectory("testUnitExample_Xml_ThreeDays_UpdateDatabaseCorrectly").toFile();
            String configFileContents = FileUtils.readFileToString(config, StandardCharsets.UTF_8);

            WatchrCoreApp app = WatchrCoreApp.initWatchrApp(dbName, dbDir);
            app.addToDatabase(dbName, dataFile.getParentFile().getAbsolutePath(), configFileContents);
            app.saveDatabase(dbName);
            
            PlotlyGraphRenderer renderer = new PlotlyGraphRenderer(app.getDatabase(dbName), testLogger, fileReader);
            GraphOperationResult result = renderer.getGraphHtml(getDefaultGraphDisplayConfig(), true);

            assertTrue(StringUtils.isNotBlank(result.getHtml()));
        } catch(IOException | WatchrParseException e) {
            fail(e.getMessage());
        }
    }

    private GraphDisplayConfig getDefaultGraphDisplayConfig() { 
        GraphDisplayConfig graphDisplayConfig = new GraphDisplayConfig("", testLogger);

        graphDisplayConfig.setNextPlotDbLocation(CommonConstants.ROOT_PATH_ALIAS);
        graphDisplayConfig.setLastPlotDbLocation(CommonConstants.ROOT_PATH_ALIAS);
        graphDisplayConfig.setPage(1);
        graphDisplayConfig.setGraphsPerRow(3);
        graphDisplayConfig.setGraphWidth(500);
        graphDisplayConfig.setGraphHeight(500);
        graphDisplayConfig.setGraphsPerPage(15);
        graphDisplayConfig.setDisplayRange(30);
        graphDisplayConfig.setDisplayedDecimalPlaces(3);
        graphDisplayConfig.setDisplayCategory("");
        graphDisplayConfig.setSort(GraphDisplaySort.ASCENDING);
        graphDisplayConfig.setLeafNodeStrategy(LeafNodeStrategy.SHOW_CHILD_ONLY);
        graphDisplayConfig.setExportMode(ExportMode.PER_CATEGORY);

        return graphDisplayConfig;
    }
}
