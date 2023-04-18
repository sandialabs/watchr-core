package gov.sandia.watchr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

import gov.sandia.watchr.parse.WatchrParseException;


public class WatchrCoreAppTest_JsonTests {

    @Test
    public void testUnitExample_Json_HelloWorld() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/json/HelloWorld/config.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/json/HelloWorld/performance_day_1.json");
            File dbDir     = Files.createTempDirectory("testUnitExample_Json_HelloWorld").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Json_HelloWorld").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21'],");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [1.0],");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }         
    }

    @Test
    public void testUnitExample_Json_DerivativeLineTest() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/json/DerivativeLineTest/config.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/json/DerivativeLineTest/performance_rising_data.json");
            File dbDir     = Files.createTempDirectory("testUnitExample_Json_DerivativeLineTest").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Json_DerivativeLineTest").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-07T22:21:21', '2021-04-08T22:21:21', '2021-04-09T22:21:21', '2021-04-10T22:21:21'],");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [1.0, 2.0, 3.0, 4.0, 1.0, -1.0],");
            TestFileUtils.assertLineEquals(exportFileContents, 59, "opacity: 1,");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_NAME, "name: 'My First Data Line',");

            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_SECOND_PLOT_Y, "y: [1.0, 1.5, 2, 2.5, 2.5, 1.75],");
            TestFileUtils.assertLineEquals(exportFileContents, 88, "opacity: 1,");
            TestFileUtils.assertLineEquals(exportFileContents, 92, "name: 'My First Data Line - Average',");

            TestFileUtils.assertLineEquals(exportFileContents, 100, "y: [1.0, 2, 2.8165, 3.618, 3.618, 3.6703],");
            TestFileUtils.assertLineEquals(exportFileContents, 117, "opacity: 0.5,");
            TestFileUtils.assertLineEquals(exportFileContents, 121, "name: 'My First Data Line - Average + Std. Dev.',");

            TestFileUtils.assertLineEquals(exportFileContents, 129, "y: [1.0, 1, 1.1835, 1.382, 1.382, -0.1703],");
            TestFileUtils.assertLineEquals(exportFileContents, 146, "opacity: 0.5,");
            TestFileUtils.assertLineEquals(exportFileContents, 150, "name: 'My First Data Line - Average - Std. Dev.',");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
    @Test
    public void testUnitExample_Json_Rules_Success() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/json/RuleTest/rule_test.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/json/RuleTest/performance_rising_data.json");
            File dbDir     = Files.createTempDirectory("testUnitExample_Json_Rules_Success").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Json_Rules_Success").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_NAME, "name: 'My First Data Line',");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-07T22:21:21', '2021-04-08T22:21:21', '2021-04-09T22:21:21', '2021-04-10T22:21:21'],");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [1.0, 2.0, 3.0, 4.0, 1.0, -1.0],");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_BACKGROUND_COLOR, "plot_bgcolor: 'rgb(255, 255, 255)',");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }         
    }

    @Test
    public void testUnitExample_Json_Rules_Fail() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/json/RuleTest/rule_test_2.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/json/RuleTest/performance_rising_data.json");
            File dbDir     = Files.createTempDirectory("testUnitExample_Json_Rules_Fail").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Json_Rules_Fail").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_NAME, "name: 'My First Data Line',");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-07T22:21:21', '2021-04-08T22:21:21', '2021-04-09T22:21:21', '2021-04-10T22:21:21'],");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [1.0, 2.0, 3.0, 4.0, 1.0, -1.0],");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_BACKGROUND_COLOR, "plot_bgcolor: 'rgb(235, 156, 156)',");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }         
    }

    @Test
    public void testUnitExample_Json_Rules_WarnWithConflictingRules() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/json/RuleTest/rule_test_3.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/json/RuleTest/performance_rising_data_2.json");
            File dbDir     = Files.createTempDirectory("testUnitExample_Json_Rules_WarnWithConflictingRules").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Json_Rules_WarnWithConflictingRules").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_NAME, "name: 'My First Data Line',");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-07T22:21:21', '2021-04-08T22:21:21', '2021-04-09T22:21:21', '2021-04-10T22:21:21'],");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [5.0, 4.0, 3.0, 2.0, 2.5, 3.0],");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_BACKGROUND_COLOR, "plot_bgcolor: 'rgb(239, 228, 176)',");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }         
    }    

    @Test
    public void testUnitExample_Json_FileFilter() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/json/FileFilterExample/config.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/json/FileFilterExample");
            File dbDir     = Files.createTempDirectory("testUnitExample_Json_FileFilter").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Json_FileFilter").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_NAME, "name: 'Data Line',");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21'],");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [1.0],");
            assertFalse(exportFileContents.contains("y: [2.0],"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testUnitExample_Json_Legend() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/json/Legend/config.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/json/Legend/performance_day_1.json");
            File dbDir     = Files.createTempDirectory("testUnitExample_Json_Legend").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Json_Legend").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(exportFileContents, 79, "showlegend: true,");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }    

    @Test
    public void testUnitExample_Json_FilterPerDataLine() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/json/PointFilterExample/config_lineFilters.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/json/PointFilterExample");
            File dbDir     = Files.createTempDirectory("testUnitExample_Json_FilterPerDataLine").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Json_FilterPerDataLine").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            assertFalse(exportFileContents.contains("x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-07T22:21:21', '2021-04-08T22:21:21', '2021-04-09T22:21:21', '2021-04-10T22:21:21'],"));
            assertFalse(exportFileContents.contains("y: [1.0, 2.0, 3.0, 4.0, 1.0, -1.0],"));
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-08T22:21:21', '2021-04-09T22:21:21'],");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_Y,  "y: [1.0, 2.0, 4.0, 1.0],");
            TestFileUtils.assertLineEquals(exportFileContents, 75,  "title: \"Plot With A Different Filter\",");

            TestFileUtils.assertLineEquals(exportFileContents, 122, "x: ['2021-04-07T22:21:21', '2021-04-10T22:21:21'],");
            TestFileUtils.assertLineEquals(exportFileContents, 123, "y: [3.0, -1.0],");
            TestFileUtils.assertLineEquals(exportFileContents, 156,  "title: \"Plot with One Filter\",");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    } 

    @Test
    public void testUnitExample_Json_FilterPerPlot() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/json/PointFilterExample/config_plotFilters.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/json/PointFilterExample");
            File dbDir     = Files.createTempDirectory("testUnitExample_Json_FilterPerPlot").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Json_FilterPerPlot").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            assertFalse(exportFileContents.contains("x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-07T22:21:21', '2021-04-08T22:21:21', '2021-04-09T22:21:21', '2021-04-10T22:21:21'],"));
            assertFalse(exportFileContents.contains("y: [1.0, 2.0, 3.0, 4.0, 1.0, -1.0],"));
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-08T22:21:21', '2021-04-09T22:21:21'],");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_Y,  "y: [1.0, 2.0, 4.0, 1.0],");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testUnitExample_Json_GlobalPointFilter() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/json/PointFilterExample/config_globalFilters.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/json/PointFilterExample");
            File dbDir     = Files.createTempDirectory("testUnitExample_Json_GlobalPointFilter").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Json_GlobalPointFilter").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            assertFalse(exportFileContents.contains("x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-07T22:21:21', '2021-04-08T22:21:21', '2021-04-09T22:21:21', '2021-04-10T22:21:21'],"));
            assertFalse(exportFileContents.contains("y: [1.0, 2.0, 3.0, 4.0, 1.0, -1.0],"));
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-06T22:21:21', '2021-04-07T22:21:21', '2021-04-08T22:21:21', '2021-04-10T22:21:21'],");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_Y,  "y: [2.0, 3.0, 4.0, -1.0],");
            TestFileUtils.assertLineEquals(exportFileContents, 75,  "title: \"No Filter Plot\",");

            TestFileUtils.assertLineEquals(exportFileContents, 122, "x: ['2021-04-06T22:21:21', '2021-04-08T22:21:21'],");
            TestFileUtils.assertLineEquals(exportFileContents, 123, "y: [2.0, 4.0],");
            TestFileUtils.assertLineEquals(exportFileContents, 156,  "title: \"Plot with Filter\",");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }    

    @Test
    public void testUnitExample_Json_TestAutonameStrategy() {
        try {            
            File config    = TestFileUtils.loadTestFile("system_tests/config/BigJsonAutonameConfig.json");
            File dataFile  = TestFileUtils.loadTestFile("system_tests/reports/json_reports_basic/basic_report_2.json");
            File dbDir     = Files.createTempDirectory("testUnitExample_TestAutonameStrategy").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_TestAutonameStrategy").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");

            assertEquals(1, exportDir.listFiles().length);

            String file1Contents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(file1Contents, 215, "x: ['2019-11-01 22:15:28', '2019-11-01 22:15:29'],");
            TestFileUtils.assertLineEquals(file1Contents, 216, "y: [0.016754639323649404, 0.03536856681926057],");
            TestFileUtils.assertLineEquals(file1Contents, 244, "x: ['2019-11-01 22:17:57'],");
            TestFileUtils.assertLineEquals(file1Contents, 245, "y: [-0.11097154263042563],");
            TestFileUtils.assertLineEquals(file1Contents, 157, "x: ['2019-11-01 22:15:28', '2019-11-01 22:15:29'],");
            TestFileUtils.assertLineEquals(file1Contents, 158, "y: [5.1876956330731954e-11, 1.0944087267160736e-10],");
            TestFileUtils.assertLineEquals(file1Contents, 186, "x: ['2019-11-01 22:17:57'],");
            TestFileUtils.assertLineEquals(file1Contents, 187, "y: [-0.1096240105893509],");
            TestFileUtils.assertLineEquals(file1Contents, 99, "x: ['2019-11-01 22:15:28', '2019-11-01 22:15:29'],");
            TestFileUtils.assertLineEquals(file1Contents, 100, "y: [0.021157282214797696, 0.04451590518611598],");
        } catch (WatchrParseException e) {
            e.getOriginalException().printStackTrace();;
            fail(e.getOriginalException().getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

/*
    @Test
    public void testUnitExample_Json_NumberFormatTest() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/json/NumberFormatTest/config.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/json/NumberFormatTest/performance_day_1.json");
            File dbDir     = Files.createTempDirectory("testUnitExample_NumberFormatTest").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_NumberFormatTest").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");

            assertEquals(1, exportDir.listFiles().length);
            String fileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            assertTrue(fileContents.contains("'2021-05-13T16:39:13'"));
        } catch (WatchrParseException e) {
            e.getOriginalException().printStackTrace();;
            fail(e.getOriginalException().getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testUnitExample_Json_NumberFormatMillisecondsTest() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/json/NumberFormatTest/config_ms.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/json/NumberFormatTest/performance_day_1.json");
            File dbDir     = Files.createTempDirectory("testUnitExample_NumberFormatMillisecondsTest").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_NumberFormatMillisecondsTest").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");

            assertEquals(1, exportDir.listFiles().length);
            String fileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            
            // We now have a completely different date interpretation because we are parsing
            // the number string as milliseconds instead of seconds.
            assertTrue(fileContents.contains("'1970-01-19T11:15:45'"));
        } catch (WatchrParseException e) {
            e.getOriginalException().printStackTrace();;
            fail(e.getOriginalException().getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
*/

    @Test
    public void testUnitExample_Json_SlopeLineTest() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/json/SlopeLine/config.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/json/SlopeLine/performance_data.json");
            File dbDir     = Files.createTempDirectory("testUnitExample_Json_SlopeLineTest").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Json_SlopeLineTest").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");
            
            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);

            assertFalse(exportFileContents.contains("x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-07T22:21:21', '2021-04-08T22:21:21', '2021-04-09T22:21:21', '2021-04-10T22:21:21'],"));
            assertFalse(exportFileContents.contains("y: [1.0, 2.0, 3.0, 4.0, 1.0, -1.0],"));
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: [0.0, 1.0, 2.0],");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_Y,  "y: [1.0, 2.0, 3.0],");

            TestFileUtils.assertLineEquals(exportFileContents, 70, "x: [0.0, 1.0, 2.0],");
            TestFileUtils.assertLineEquals(exportFileContents, 71, "y: [5.0, 7.0, 9.0],");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
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
