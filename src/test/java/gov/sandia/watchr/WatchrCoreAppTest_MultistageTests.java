package gov.sandia.watchr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class WatchrCoreAppTest_MultistageTests {

    ///////////
    // TESTS //
    ///////////

    /**
     * This test demonstrates Watchr's ability to store multiple "days" of data. One
     * set of data is provided to an instance of WatchrCoreApp; then, in a separate
     * call (i.e. a "day" later), we provide more data to the same plots. We then
     * verify that the data was correctly added to the existing plots from the
     * previous day. We add a third day's worth of data as a last step, for good
     * measure.
     */
    @Test
    public void testUnitExample_Xml_ThreeDays_UpdateDatabaseCorrectly() {
        try {       
            File dbDir = Files.createTempDirectory("testUnitExample_Xml_ThreeDays_UpdateDatabaseCorrectly").toFile();
            WatchrCoreApp app =
                testUnitExample_Xml_ThreeDays_UpdateDatabaseCorrectly_Day1("MyDatabase", dbDir);
            testUnitExample_Xml_ThreeDays_UpdateDatabaseCorrectly_Day2(app, "MyDatabase", dbDir);
            testUnitExample_Xml_ThreeDays_UpdateDatabaseCorrectly_Day3(app, "MyDatabase", dbDir);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }         
    }

    /**
     * This test demonstrates Watchr's ability to store multiple "days" of data. One
     * set of data is provided to an instance of WatchrCoreApp; then, we start a new
     * WatchrCoreApp (to simulate a restart of Watchr), and then we provide more
     * data to the same plots. We then verify that the data was correctly added to
     * the existing plots from the previous day.
     */
    @Test
    public void testUnitExample_Xml_TwoDays_UpdateDatabaseCorrectly_WithAppShutdown() {
        try {       
            File dbDir = Files.createTempDirectory("testUnitExample_Xml_ThreeDays_UpdateDatabaseCorrectly").toFile();
            testUnitExample_Xml_ThreeDays_UpdateDatabaseCorrectly_Day1("MyDatabase", dbDir);
            testUnitExample_Xml_ThreeDays_UpdateDatabaseCorrectly_Day2_NewApp("MyDatabase", dbDir);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }         
    }

    /////////////
    // PRIVATE //
    /////////////

    private WatchrCoreApp testUnitExample_Xml_ThreeDays_UpdateDatabaseCorrectly_Day1(String dbName, File dbDir) throws Exception {
        File config    = TestFileUtils.loadTestFile("unit_tests/xml/ThreePointsOnOneLine/config_day_1.json");
        File dataFile  = TestFileUtils.loadTestFile("unit_tests/xml/ThreePointsOnOneLine/performance_day_1.xml");
        File exportDir = Files.createTempDirectory("testUnitExample_Xml_ThreeDays_UpdateDatabaseCorrectly_Day1").toFile();
        String configFileContents = FileUtils.readFileToString(config, StandardCharsets.UTF_8);

        WatchrCoreApp app = WatchrCoreApp.initWatchrApp(dbName, dbDir);
        app.addToDatabase(dbName, dataFile.getParentFile().getAbsolutePath(), configFileContents);
        app.saveDatabase(dbName);
        app.exportAllGraphHtml(dbName, dataFile, config, exportDir);

        assertEquals(1, exportDir.listFiles().length);
        String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
        TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21'],");
        TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [1.0],");

        assertEquals(6, dbDir.listFiles().length);

        List<File> files = Arrays.asList(dbDir.listFiles());
        Collections.sort(files);
        assertEquals("fileCache.json", files.get(0).getName());
        assertEquals("lastConfig.json", files.get(1).getName());
        assertEquals("metadata.json", files.get(2).getName());
        assertEquals("parentChildPlots.json", files.get(3).getName());
        assertTrue(files.get(4).getName().startsWith("plot_"));
        assertTrue(files.get(5).getName().startsWith("plot_"));

        return app;
    }

    private void testUnitExample_Xml_ThreeDays_UpdateDatabaseCorrectly_Day2(
            WatchrCoreApp app, String dbName, File dbDir) throws Exception {
        File config    = TestFileUtils.loadTestFile("unit_tests/xml/ThreePointsOnOneLine/config_day_2.json");
        File dataFile  = TestFileUtils.loadTestFile("unit_tests/xml/ThreePointsOnOneLine/performance_day_2.xml");
        File exportDir = Files.createTempDirectory("testUnitExample_Xml_ThreeDays_UpdateDatabaseCorrectly_Day2").toFile();
        String configFileContents = FileUtils.readFileToString(config, StandardCharsets.UTF_8);

        app.addToDatabase(dbName, dataFile.getParentFile().getAbsolutePath(), configFileContents);
        app.saveDatabase(dbName);
        app.exportAllGraphHtml(dbName, dataFile, config, exportDir);

        assertEquals(1, exportDir.listFiles().length);
        String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
        TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21', '2021-04-06T12:21:21'],");
        TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [1.0, 2.0],");

        assertEquals(6, dbDir.listFiles().length);

        List<File> files = Arrays.asList(dbDir.listFiles());
        Collections.sort(files);
        assertEquals("fileCache.json", files.get(0).getName());
        assertEquals("lastConfig.json", files.get(1).getName());
        assertEquals("metadata.json", files.get(2).getName());
        assertEquals("parentChildPlots.json", files.get(3).getName());
        assertTrue(files.get(4).getName().startsWith("plot_"));
        assertTrue(files.get(5).getName().startsWith("plot_"));
    }

    private void testUnitExample_Xml_ThreeDays_UpdateDatabaseCorrectly_Day2_NewApp(String dbName, File dbDir) throws Exception {
        File config    = TestFileUtils.loadTestFile("unit_tests/xml/ThreePointsOnOneLine/config_day_2.json");
        File dataFile  = TestFileUtils.loadTestFile("unit_tests/xml/ThreePointsOnOneLine/performance_day_2.xml");
        File exportDir = Files.createTempDirectory("testUnitExample_Xml_ThreeDays_UpdateDatabaseCorrectly_Day2").toFile();
        String configFileContents = FileUtils.readFileToString(config, StandardCharsets.UTF_8);

        WatchrCoreApp app = WatchrCoreApp.initWatchrApp(dbName, dbDir);
        app.addToDatabase(dbName, dataFile.getParentFile().getAbsolutePath(), configFileContents);
        app.saveDatabase(dbName);
        app.exportAllGraphHtml(dbName, dataFile, config, exportDir);

        assertEquals(1, exportDir.listFiles().length);
        String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
        TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21', '2021-04-06T12:21:21'],");
        TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [1.0, 2.0],");

        assertEquals(6, dbDir.listFiles().length);

        List<File> files = Arrays.asList(dbDir.listFiles());
        Collections.sort(files);
        assertEquals("fileCache.json", files.get(0).getName());
        assertEquals("lastConfig.json", files.get(1).getName());
        assertEquals("metadata.json", files.get(2).getName());
        assertEquals("parentChildPlots.json", files.get(3).getName());
        assertTrue(files.get(4).getName().startsWith("plot_"));
        assertTrue(files.get(5).getName().startsWith("plot_"));
    }

    private void testUnitExample_Xml_ThreeDays_UpdateDatabaseCorrectly_Day3(
            WatchrCoreApp app, String dbName, File dbDir) throws Exception {
        File config    = TestFileUtils.loadTestFile("unit_tests/xml/ThreePointsOnOneLine/config_day_3.json");
        File dataFile  = TestFileUtils.loadTestFile("unit_tests/xml/ThreePointsOnOneLine/performance_day_3.xml");
        File exportDir = Files.createTempDirectory("testUnitExample_Xml_ThreeDays_UpdateDatabaseCorrectly_Day3").toFile();
        String configFileContents = FileUtils.readFileToString(config, StandardCharsets.UTF_8);

        app.addToDatabase(dbName, dataFile.getParentFile().getAbsolutePath(), configFileContents);
        app.saveDatabase(dbName);
        app.exportAllGraphHtml(dbName, dataFile, config, exportDir);

        assertEquals(1, exportDir.listFiles().length);
        String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
        TestFileUtils.assertLineEquals(
            exportFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21', '2021-04-06T12:21:21', '2021-04-07T02:21:21'],");
        TestFileUtils.assertLineEquals(
            exportFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [1.0, 2.0, 3.0],");

        assertEquals(6, dbDir.listFiles().length);

        List<File> files = Arrays.asList(dbDir.listFiles());
        Collections.sort(files);
        assertEquals("fileCache.json", files.get(0).getName());
        assertEquals("lastConfig.json", files.get(1).getName());
        assertEquals("metadata.json", files.get(2).getName());
        assertEquals("parentChildPlots.json", files.get(3).getName());
        assertTrue(files.get(4).getName().startsWith("plot_"));
        assertTrue(files.get(5).getName().startsWith("plot_"));
    }
}
