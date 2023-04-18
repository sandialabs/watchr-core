package gov.sandia.watchr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.PlotsConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.config.reader.WatchrConfigReader;
import gov.sandia.watchr.db.PlotDatabaseSearchCriteria;
import gov.sandia.watchr.db.impl.AbstractDatabase;
import gov.sandia.watchr.db.impl.FileBasedDatabase;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.parse.plotypus.Plotypus;

public class WatchrCoreAppTest_BasicTests {

    private TestableWatchrCoreApp app;
    private WatchrConfig config;
    private StringOutputLogger testLogger;
    private IFileReader fileReader;

    @Before
    public void setup() {
        app = new TestableWatchrCoreApp();
        testLogger = new StringOutputLogger();
        fileReader = new DefaultFileReader(testLogger);

        FileConfig fileConfig = new FileConfig("", testLogger, fileReader);
        fileConfig.setFileExtension("xml");
        fileConfig.setFileNamePattern("performance*");
        PlotsConfig plotsConfig = new PlotsConfig("", testLogger, fileReader);
        plotsConfig.setFileConfig(fileConfig);
        config = new WatchrConfig(testLogger, fileReader);
        config.setPlotsConfig(plotsConfig);
    }

    @Test
    public void testIsStartFileValid_Xml() {
        
        try {
            File dummyXml = Files.createTempFile("performance", ".xml").toFile();
            WatchrCoreAppDatabaseSubsystem dbSubsystem = app.getDatabaseSubsystemForTests();
            assertTrue(dbSubsystem.isStartFileValid(config, dummyXml.getAbsolutePath()));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testIsStartFileValid_Json() {
        try {
            File dummyJson = Files.createTempFile("performance", ".json").toFile();
            WatchrCoreAppDatabaseSubsystem dbSubsystem = app.getDatabaseSubsystemForTests();
            assertFalse(dbSubsystem.isStartFileValid(config, dummyJson.getAbsolutePath()));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testIsStartFileValid_BadFile() {
        try {
            File gitFile = Files.createTempFile("", ".git").toFile();
            WatchrCoreAppDatabaseSubsystem dbSubsystem = app.getDatabaseSubsystemForTests();
            assertFalse(dbSubsystem.isStartFileValid(config, gitFile.getAbsolutePath()));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testIsStartFileValid_Directory() {
        try {
            File directory = Files.createTempDirectory("testIsStartFileValid_Directory").toFile();
            WatchrCoreAppDatabaseSubsystem dbSubsystem = app.getDatabaseSubsystemForTests();
            assertTrue(dbSubsystem.isStartFileValid(config, directory.getAbsolutePath()));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }    

    @Test
    public void testApp_Basic() {
        TestableWatchrCoreApp app = new TestableWatchrCoreApp();
        assertNotNull(app);
    }

    @Test
    public void testGetFailedPlotsSize_Basic() {
        TestableWatchrCoreApp app = new TestableWatchrCoreApp();
        WatchrCoreAppDatabaseSubsystem dbSubsystem = app.getDatabaseSubsystemForTests();
        int failedPlotCount = dbSubsystem.getFailedPlotsSize("");
        assertEquals(0, failedPlotCount);
    }

    @Test
    public void testGetFailedPlotsSize_RealData() throws InterruptedException {
        TestableWatchrCoreApp app = new TestableWatchrCoreApp();
        try {            
            ClassLoader classLoader = WatchrCoreAppTest_BasicTests.class.getClassLoader();
            File dbDestDir = Files.createTempDirectory("testGetFailedPlotsSize_RealData").toFile();

            URL reportsDirUrl = classLoader.getResource("unit_tests/json/RuleTest");
            File reportsDir = new File(reportsDirUrl.toURI());

            URL sierraConfigJsonUrl = classLoader.getResource("unit_tests/json/RuleTest/rule_test.json");
            File configFile = new File(sierraConfigJsonUrl.toURI());
            String configFileContents = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);

            app.connectDatabase("testGetFailedPlotsSize_RealData", FileBasedDatabase.class, new Object[]{ dbDestDir });

            Plotypus<WatchrConfig> plotypus = app.createPlotypus(10);
            WatchrConfigReader reader = app.createWatchrConfigReader(reportsDir.getAbsolutePath());
            WatchrConfig watchrConfig = reader.deserialize(configFileContents, FilenameUtils.getExtension(configFile.getName()));
            AbstractDatabase db = app.getDatabaseAndAttachLogger("testGetFailedPlotsSize_RealData");
            List<WatchrDiff<?>> diffs = app.loadDiffs(watchrConfig, reader, db);
            app.addToDatabase(plotypus, watchrConfig, db, diffs, reportsDir.getAbsolutePath());
            plotypus.begin();
            plotypus.waitToFinish();
            app.saveDatabase(plotypus, "testGetFailedPlotsSize_RealData");
            plotypus.kill();
            
            WatchrCoreAppDatabaseSubsystem databaseSubsystem = app.getDatabaseSubsystemForTests();
            int failedPlotCount = databaseSubsystem.getFailedPlotsSize("testGetFailedPlotsSize_RealData");
            assertEquals(0, failedPlotCount);
        } catch (IOException | URISyntaxException e1) {
            e1.printStackTrace();
            fail(e1.getMessage());
        } 
    } 

    @Test
    public void testGetPlotsSize_Basic() {
        TestableWatchrCoreApp app = new TestableWatchrCoreApp();
        WatchrCoreAppDatabaseSubsystem databaseSubsystem = app.getDatabaseSubsystemForTests();
        int plotCount = databaseSubsystem.getPlotsSize("");
        assertEquals(0, plotCount);
    }    

    @Test
    public void testGetPlotsSize_RealData() throws InterruptedException {
        TestableWatchrCoreApp app = new TestableWatchrCoreApp();
        StringOutputLogger logger = new StringOutputLogger();
        try {            
            ClassLoader classLoader = WatchrCoreAppTest.class.getClassLoader();
            File dbDestDir = Files.createTempDirectory("testGetPlotsSize_RealData").toFile();

            URL reportsDirUrl = classLoader.getResource("unit_tests/json/RuleTest");
            File reportsDir = new File(reportsDirUrl.toURI());

            URL sierraConfigJsonUrl = classLoader.getResource("unit_tests/json/RuleTest/rule_test.json");
            File configFile = new File(sierraConfigJsonUrl.toURI());
            String configFileContents = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);

            app.connectDatabase("testGetPlotsSize_RealData", FileBasedDatabase.class, new Object[]{ dbDestDir });
            Plotypus<WatchrConfig> plotypus = app.createPlotypus(10);            
            WatchrConfigReader reader = app.createWatchrConfigReader(reportsDir.getAbsolutePath());
            WatchrConfig watchrConfig = reader.deserialize(configFileContents, FilenameUtils.getExtension(configFile.getName()));
            AbstractDatabase db = app.getDatabaseAndAttachLogger("testGetPlotsSize_RealData");
            List<WatchrDiff<?>> diffs = app.loadDiffs(watchrConfig, reader, db);
            app.addToDatabase(plotypus, watchrConfig, db, diffs, reportsDir.getAbsolutePath());
            plotypus.begin();
            plotypus.waitToFinish();
            app.saveDatabase(plotypus, "testGetPlotsSize_RealData");
            plotypus.kill();

            WatchrCoreAppDatabaseSubsystem dbSubsystem = app.getDatabaseSubsystemForTests();
            int plotCount = dbSubsystem.getPlotsSize("testGetPlotsSize_RealData");
            if(plotCount != 0) {
                assertEquals(1, plotCount);
            } else {
                System.out.println(logger.getLogAsString());
                fail(logger.getLogAsString());
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } 
    }

    @Test
    public void testDeletePlotFromDatabase() throws InterruptedException {
        TestableWatchrCoreApp app = new TestableWatchrCoreApp();
        final String databaseName = "testDeletePlotFromDatabase";
        try {            
            ClassLoader classLoader = WatchrCoreAppTest_BasicTests.class.getClassLoader();
            File dbDestDir = Files.createTempDirectory(databaseName).toFile();

            URL reportsDirUrl = classLoader.getResource("unit_tests/json/RuleTest");
            File reportsDir = new File(reportsDirUrl.toURI());

            URL ruleTestConfigJsonUrl = classLoader.getResource("unit_tests/json/RuleTest/rule_test.json");
            File configFile = new File(ruleTestConfigJsonUrl.toURI());
            String configFileContents = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);

            app.connectDatabase(databaseName, FileBasedDatabase.class, new Object[]{ dbDestDir });

            Plotypus<WatchrConfig> plotypus = app.createPlotypus(10);
            WatchrConfigReader reader = app.createWatchrConfigReader(reportsDir.getAbsolutePath());
            WatchrConfig watchrConfig = reader.deserialize(configFileContents, FilenameUtils.getExtension(configFile.getName()));
            AbstractDatabase db = app.getDatabaseAndAttachLogger(databaseName);
            List<WatchrDiff<?>> diffs = app.loadDiffs(watchrConfig, reader, db);
            app.addToDatabase(plotypus, watchrConfig, db, diffs, reportsDir.getAbsolutePath());
            plotypus.begin();
            plotypus.waitToFinish();
            app.saveDatabase(plotypus, databaseName);
            plotypus.kill();
            
            assertEquals(1, app.getPlotsSizeFromDatabase(databaseName));
            app.deletePlotFromDatabase(databaseName, "My First Plot", "");
            assertEquals(0, app.getPlotsSizeFromDatabase(databaseName));
        } catch (IOException | URISyntaxException e1) {
            e1.printStackTrace();
            fail(e1.getMessage());
        } 
    }

    @Test
    public void testSetNicknameForPlotFromDatabase() throws InterruptedException {
        TestableWatchrCoreApp app = new TestableWatchrCoreApp();
        final String databaseName = "testSetNicknameForPlotFromDatabase";
        try {            
            ClassLoader classLoader = WatchrCoreAppTest_BasicTests.class.getClassLoader();
            File dbDestDir = Files.createTempDirectory(databaseName).toFile();

            URL reportsDirUrl = classLoader.getResource("unit_tests/json/RuleTest");
            File reportsDir = new File(reportsDirUrl.toURI());

            URL ruleTestConfigJsonUrl = classLoader.getResource("unit_tests/json/RuleTest/rule_test.json");
            File configFile = new File(ruleTestConfigJsonUrl.toURI());
            String configFileContents = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);

            app.connectDatabase(databaseName, FileBasedDatabase.class, new Object[]{ dbDestDir });

            Plotypus<WatchrConfig> plotypus = app.createPlotypus(10);
            WatchrConfigReader reader = app.createWatchrConfigReader(reportsDir.getAbsolutePath());
            WatchrConfig watchrConfig = reader.deserialize(configFileContents, FilenameUtils.getExtension(configFile.getName()));
            AbstractDatabase db = app.getDatabaseAndAttachLogger(databaseName);
            List<WatchrDiff<?>> diffs = app.loadDiffs(watchrConfig, reader, db);
            app.addToDatabase(plotypus, watchrConfig, db, diffs, reportsDir.getAbsolutePath());
            plotypus.begin();
            plotypus.waitToFinish();
            app.saveDatabase(plotypus, databaseName);
            plotypus.kill();
            
            PlotDatabaseSearchCriteria p = new PlotDatabaseSearchCriteria("My First Plot", "");

            assertEquals("", app.getDatabasePlot(databaseName, p).getNickname());
            app.setNicknameInDatabase(databaseName, "My First Plot", "", "myFirstNickname");
            assertEquals("myFirstNickname", app.getDatabasePlot(databaseName, p).getNickname());
        } catch (IOException | URISyntaxException e1) {
            e1.printStackTrace();
            fail(e1.getMessage());
        }
    }
    
    @After
    public void teardown() {
        try {
            File watchrRunDirectory = new File(System.getProperty("user.dir") + File.separator + "watchrRun");
            File graphDirectory = new File(System.getProperty("user.dir") + File.separator + "graph");

            FileUtils.deleteDirectory(watchrRunDirectory);
            FileUtils.deleteDirectory(graphDirectory);
        } catch(IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}
