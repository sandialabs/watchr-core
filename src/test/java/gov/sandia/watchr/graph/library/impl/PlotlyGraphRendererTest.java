package gov.sandia.watchr.graph.library.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.TestFileUtils;
import gov.sandia.watchr.TestableWatchrCoreApp;
import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.WatchrCoreAppGraphSubsystem;
import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.GraphDisplayConfig.ExportMode;
import gov.sandia.watchr.config.GraphDisplayConfig.GraphDisplaySort;
import gov.sandia.watchr.config.GraphDisplayConfig.LeafNodeStrategy;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.config.reader.WatchrConfigReader;
import gov.sandia.watchr.db.impl.AbstractDatabase;
import gov.sandia.watchr.db.impl.FileBasedDatabase;
import gov.sandia.watchr.graph.library.GraphOperationResult;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.parse.plotypus.Plotypus;
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
    public void testGetGraphHtml() throws InterruptedException {
        try {
            final String dbName = "testDb";
            final File config    = TestFileUtils.loadTestFile("unit_tests/xml/ThreePointsOnOneLine/config_day_1.json");
            final File dataFile  = TestFileUtils.loadTestFile("unit_tests/xml/ThreePointsOnOneLine/performance_day_1.xml");
            final File dbDir     = Files.createTempDirectory("testUnitExample_Xml_ThreeDays_UpdateDatabaseCorrectly").toFile();
            String configFileContents = FileUtils.readFileToString(config, StandardCharsets.UTF_8);

            WatchrCoreApp app = TestableWatchrCoreApp.initWatchrAppForTests();
            app.connectDatabase(dbName, FileBasedDatabase.class, new Object[] { dbDir });
            Plotypus<WatchrConfig> plotypus = app.createPlotypus(10);
            WatchrConfigReader reader = app.createWatchrConfigReader(dataFile.getParentFile().getAbsolutePath());
            WatchrConfig watchrConfig = reader.deserialize(configFileContents, FilenameUtils.getExtension(config.getName()));
            AbstractDatabase db = app.getDatabaseAndAttachLogger(dbName);
            List<WatchrDiff<?>> diffs = app.loadDiffs(watchrConfig, reader, db);
            app.addToDatabase(plotypus, watchrConfig, db, diffs, dataFile.getParentFile().getAbsolutePath());
            plotypus.begin();
            plotypus.waitToFinish();
            app.saveDatabase(plotypus, dbName);
            plotypus.kill();
            
            WatchrCoreAppGraphSubsystem graphSubsystem = new WatchrCoreAppGraphSubsystem(app, testLogger, fileReader);
            PlotlyGraphRenderer renderer = new PlotlyGraphRenderer(graphSubsystem, testLogger, fileReader, dbName);
            GraphOperationResult result = renderer.getGraphHtml(getDefaultGraphDisplayConfig(), true);

            assertTrue(StringUtils.isNotBlank(result.getHtml()));
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testFilterPlotsWithSearchCriteria() throws InterruptedException {
        try {
            final String dbName = "testDb";
            final File config    = TestFileUtils.loadTestFile("unit_tests/xml/Categories/config.json");
            final File dataFile  = TestFileUtils.loadTestFile("unit_tests/xml/Categories/performance_day_1.xml");
            final File dbDir     = Files.createTempDirectory("testUnitExample_Xml_Categories_SearchDatabaseForPlot").toFile();
            String configFileContents = FileUtils.readFileToString(config, StandardCharsets.UTF_8);

            WatchrCoreApp app = TestableWatchrCoreApp.initWatchrAppForTests();
            app.connectDatabase(dbName, FileBasedDatabase.class, new Object[] { dbDir });
            Plotypus<WatchrConfig> plotypus = app.createPlotypus(10);
            WatchrConfigReader reader = app.createWatchrConfigReader(dataFile.getParentFile().getAbsolutePath());
            WatchrConfig watchrConfig = reader.deserialize(configFileContents, FilenameUtils.getExtension(config.getName()));
            AbstractDatabase db = app.getDatabaseAndAttachLogger(dbName);
            List<WatchrDiff<?>> diffs = app.loadDiffs(watchrConfig, reader, db);
            app.addToDatabase(plotypus, watchrConfig, db, diffs, dataFile.getParentFile().getAbsolutePath());
            plotypus.begin();
            plotypus.waitToFinish();
            app.saveDatabase(plotypus, dbName);
            plotypus.kill();
            
            WatchrCoreAppGraphSubsystem graphSubsystem = new WatchrCoreAppGraphSubsystem(app, testLogger, fileReader);
            PlotlyGraphRenderer renderer = new PlotlyGraphRenderer(graphSubsystem, testLogger, fileReader, dbName);

            GraphDisplayConfig graphDisplayConfig = getDefaultGraphDisplayConfig();
            graphDisplayConfig.setSearchQuery("Min Plot");

            GraphOperationResult result = renderer.getGraphHtml(graphDisplayConfig, true);

            assertTrue(result.getHtml().contains("title: \"Min Plot\""));
            assertFalse(result.getHtml().contains("title: \"Max Plot\""));
            assertFalse(result.getHtml().contains("title: \"Mean Plot\""));
        } catch(IOException e) {
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
