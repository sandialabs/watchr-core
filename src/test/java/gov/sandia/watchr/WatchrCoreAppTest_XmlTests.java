package gov.sandia.watchr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.strategy.WatchrRunStrategy;

public class WatchrCoreAppTest_XmlTests {

    @Test
    public void testUnitExample_Xml_HelloWorld() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/xml/HelloWorld/config.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/xml/HelloWorld/performance_day_1.xml");
            File dbDir     = Files.createTempDirectory("testUnitExample_Xml_HelloWorld").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Xml_HelloWorld").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");
            WatchrCoreApp.cmd("stop");

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
    public void testUnitExample_Xml_ThreePointsOnOneLine() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/xml/ThreePointsOnOneLine/config.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/xml/ThreePointsOnOneLine/");
            File dbDir     = Files.createTempDirectory("testUnitExample_Xml_ThreePointsOnOneLine").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Xml_ThreePointsOnOneLine").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");
            WatchrCoreApp.cmd("stop");

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21', '2021-04-06T12:21:21', '2021-04-07T02:21:21'],");
            TestFileUtils.assertLineEquals(exportFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [1.0, 2.0, 3.0],");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }         
    }

    @Test
    public void testUnitExample_Xml_Categories() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/xml/Categories/config.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/xml/Categories/");
            File dbDir     = Files.createTempDirectory("testUnitExample_Xml_Categories").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Xml_Categories").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");
            WatchrCoreApp.cmd("stop");

            List<File> files = Arrays.asList(exportDir.listFiles());
            Collections.sort(files);

            assertEquals(4, files.size());
            assertEquals("root_all-categories.html", files.get(0).getName());
            assertEquals("root_max.html", files.get(1).getName());
            assertEquals("root_mean.html", files.get(2).getName());
            assertEquals("root_min.html", files.get(3).getName());

            String maxExportFileContents = FileUtils.readFileToString(files.get(1), StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(maxExportFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21'],");
            TestFileUtils.assertLineEquals(maxExportFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [5.0],");
            TestFileUtils.assertLineEquals(maxExportFileContents, TestFileUtils.LINE_FIRST_PLOT_NAME, "name: 'Max Data Line',");

            String meanExportFileContents = FileUtils.readFileToString(files.get(2), StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(meanExportFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21'],");
            TestFileUtils.assertLineEquals(meanExportFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [2.5],");
            TestFileUtils.assertLineEquals(meanExportFileContents, TestFileUtils.LINE_FIRST_PLOT_NAME, "name: 'Mean Data Line',");

            String minExportFileContents = FileUtils.readFileToString(files.get(3), StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(minExportFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21'],");
            TestFileUtils.assertLineEquals(minExportFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [0.0],");
            TestFileUtils.assertLineEquals(minExportFileContents, TestFileUtils.LINE_FIRST_PLOT_NAME, "name: 'Min Data Line',");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }         
    }      

    @Test
    public void testUnitExample_Xml_ChildGraphs() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/xml/ChildGraphs/config.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/xml/ChildGraphs/performance.xml");
            File dbDir     = Files.createTempDirectory("testUnitExample_Xml_ChildGraphs").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Xml_ChildGraphs").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");
            WatchrCoreApp.cmd("stop");

            List<File> files = Arrays.asList(exportDir.listFiles());
            Collections.sort(files);

            assertEquals(3, files.size());
            assertEquals("_nightly_run_2021-04-05_measurement_A.html", files.get(0).getName());
            assertEquals("_nightly_run_2021-04-05_measurement_A_measurement_A1.html", files.get(1).getName());
            assertEquals("root.html", files.get(2).getName());

            String file1Contents = FileUtils.readFileToString(files.get(2), StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(file1Contents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21'],");
            TestFileUtils.assertLineEquals(file1Contents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [100.0],");
            
            String file2Contents = FileUtils.readFileToString(files.get(0), StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(file2Contents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21'],");
            TestFileUtils.assertLineEquals(file2Contents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [25.0],");
            TestFileUtils.assertLineEquals(file2Contents, 123, "y: [75.0],");

            String file3Contents = FileUtils.readFileToString(files.get(1), StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(file3Contents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21'],");
            TestFileUtils.assertLineEquals(file3Contents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [15.0],");
            TestFileUtils.assertLineEquals(file3Contents, 123, "y: [10.0],");
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testUnitExample_Xml_Metadata() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/xml/Metadata/config.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/xml/Metadata/performance_day_1.xml");
            File dbDir     = Files.createTempDirectory("testUnitExample_Xml_Metadata").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Xml_Metadata").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");
            WatchrCoreApp.cmd("stop");

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(exportFileContents, 60, "hovertemplate: '<b>X</b>: %{x}<br>' +'<b>Y</b>: %{y}<br>' +'%{text}',");
            TestFileUtils.assertLineEquals(exportFileContents, 61, "text: ['branch: master'],");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testUnitExample_Xml_TemplateExample() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/xml/TemplateExample/config.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/xml/TemplateExample/performance.xml");
            File dbDir     = Files.createTempDirectory("testUnitExample_Xml_TemplateExample").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Xml_TemplateExample").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");
            WatchrCoreApp.cmd("stop");

            List<File> files = Arrays.asList(exportDir.listFiles());
            Collections.sort(files);

            assertEquals(4, files.size());
            assertEquals("root_all-categories.html", files.get(0).getName());
            assertEquals("root_cpu-time-max.html", files.get(1).getName());
            assertEquals("root_cpu-time-min.html", files.get(2).getName());
            assertEquals("root_cpu-time-sum.html", files.get(3).getName());

            String file1Contents = FileUtils.readFileToString(files.get(1), StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(file1Contents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21'],");
            TestFileUtils.assertLineEquals(file1Contents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [2.0],");

            String file2Contents = FileUtils.readFileToString(files.get(2), StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(file2Contents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21'],");
            TestFileUtils.assertLineEquals(file2Contents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [1.0],");

            String file3Contents = FileUtils.readFileToString(files.get(3), StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(file3Contents, TestFileUtils.LINE_FIRST_PLOT_X, "x: ['2021-04-05T22:21:21'],");
            TestFileUtils.assertLineEquals(file3Contents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [3.0],");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testUnitExample_Xml_Treemap() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/xml/Treemap/config.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/xml/Treemap/performance.xml");
            File dbDir     = Files.createTempDirectory("testUnitExample_Xml_Treemap").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Xml_Treemap").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");
            WatchrCoreApp.cmd("stop");

            assertEquals(1, exportDir.listFiles().length);

            String file1Contents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(file1Contents, 43, "labels: ['/nightly_run_2021-04-05/A', 'B', 'C', 'D', 'E', 'F', 'G'],");
            TestFileUtils.assertLineEquals(file1Contents, 44, "parents: ['', '/nightly_run_2021-04-05/A', '/nightly_run_2021-04-05/A', 'B', 'B', 'C', 'C'],");
            TestFileUtils.assertLineEquals(file1Contents, 45, "values: [100.0, 75.0, 25.0, 25.0, 50.0, 15.0, 10.0],");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testUnitExample_Xml_AreaPlot() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/xml/AreaPlot/config.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/xml/AreaPlot");
            File dbDir     = Files.createTempDirectory("testUnitExample_Xml_AreaPlot").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Xml_AreaPlot").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");
            WatchrCoreApp.cmd("stop");

            List<File> files = Arrays.asList(exportDir.listFiles());
            Collections.sort(files);

            assertEquals(4, files.size());
            assertEquals("_A.html", files.get(0).getName());
            assertEquals("_A_B.html", files.get(1).getName());
            assertEquals("_A_C.html", files.get(2).getName());
            assertEquals("root.html", files.get(3).getName());

            String aFileContents = FileUtils.readFileToString(files.get(0), StandardCharsets.UTF_8);
            String abFileContents = FileUtils.readFileToString(files.get(1), StandardCharsets.UTF_8);
            String acFileContents = FileUtils.readFileToString(files.get(2), StandardCharsets.UTF_8);
            String rootFileContents = FileUtils.readFileToString(files.get(3), StandardCharsets.UTF_8);

            assertTrue(aFileContents.contains("x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-07T22:21:21'],"));
            assertTrue(abFileContents.contains("x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-07T22:21:21'],"));
            assertTrue(acFileContents.contains("x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-07T22:21:21'],"));
            assertTrue(rootFileContents.contains("x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-07T22:21:21'],"));

            assertTrue(aFileContents.contains("y: [75.0, 80.0, 20.0],"));
            assertTrue(aFileContents.contains("y: [25.0, 30.0, 5.0],"));
            assertTrue(aFileContents.contains("y: [50.0, 50.0, 15.0],"));
            
            assertTrue(rootFileContents.contains("stackgroup: 'one'"));
            assertTrue(aFileContents.contains("stackgroup: 'one'"));
            assertFalse(abFileContents.contains("stackgroup: 'one'"));
            assertFalse(acFileContents.contains("stackgroup: 'one'"));

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testUnitExample_Xml_SlopeExample() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/xml/SlopeLines/config.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/xml/SlopeLines");
            File dbDir     = Files.createTempDirectory("testUnitExample_Xml_SlopeLines").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Xml_SlopeLines").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");
            WatchrCoreApp.cmd("stop");

            List<File> files = Arrays.asList(exportDir.listFiles());
            Collections.sort(files);

            assertEquals(1, files.size());
            assertEquals("root.html", files.get(0).getName());

            String rootFileContents = FileUtils.readFileToString(files.get(0), StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(rootFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: [100.0, 101.0, 102.0],");
            TestFileUtils.assertLineEquals(rootFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [1.0, 2.0, 3.0],");
            TestFileUtils.assertLineEquals(rootFileContents, TestFileUtils.LINE_FIRST_PLOT_NAME, "name: 'Data Line',");
            TestFileUtils.assertLineEquals(rootFileContents, TestFileUtils.LINE_SECOND_PLOT_X, "x: [100.0, 101.0, 102.0],");
            TestFileUtils.assertLineEquals(rootFileContents, TestFileUtils.LINE_SECOND_PLOT_Y, "y: [125.0, 126.0, 127.0],");
            TestFileUtils.assertLineEquals(rootFileContents, TestFileUtils.LINE_SECOND_PLOT_Y, "y: [125.0, 126.0, 127.0],");
            TestFileUtils.assertLineEquals(rootFileContents, TestFileUtils.LINE_SECOND_PLOT_NAME, "name: 'Data Line - Slope',");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testUnitExample_Xml_MetadataFiltering() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/xml/MetadataFiltering/config.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/xml/MetadataFiltering");
            File dbDir     = Files.createTempDirectory("testUnitExample_Xml_MetadataFiltering").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Xml_MetadataFiltering").toFile();

            WatchrCoreApp.cmd("start");
            WatchrCoreApp.cmd("config", config.getAbsolutePath());
            WatchrCoreApp.cmd("put", "db", dbDir.getAbsolutePath());
            WatchrCoreApp.cmd("put", "plots", exportDir.getAbsolutePath());
            WatchrCoreApp.cmd("add", dataFile.getAbsolutePath());
            WatchrCoreApp.cmd("run");
            WatchrCoreApp.cmd("stop");

            // System.out.println(exportDir.getAbsolutePath().toString());

            List<File> files = Arrays.asList(exportDir.listFiles());
            Collections.sort(files);

            assertEquals(7, files.size());
            assertEquals("root_all-categories.html", files.get(0).getName());
            assertEquals("root_cpu-time-max.html", files.get(1).getName());
            assertEquals("root_cpu-time-min.html", files.get(2).getName());
            assertEquals("root_cpu-time-sum.html", files.get(3).getName());
            assertEquals("root_wall-time-max.html", files.get(4).getName());
            assertEquals("root_wall-time-min.html", files.get(5).getName());
            assertEquals("root_wall-time-sum.html", files.get(6).getName());

            String cpuMaxFileContents = FileUtils.readFileToString(files.get(1), StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(cpuMaxFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: [],");
            TestFileUtils.assertLineEquals(cpuMaxFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [],");
            TestFileUtils.assertLineEquals(cpuMaxFileContents, TestFileUtils.LINE_FIRST_PLOT_NAME, "name: 'Data Line',");
            TestFileUtils.assertLineEquals(cpuMaxFileContents, 122, "x: ['2018-01-17T04:16:16'],");
            TestFileUtils.assertLineEquals(cpuMaxFileContents, 123, "y: [125.799],");
            TestFileUtils.assertLineEquals(cpuMaxFileContents, 144, "name: 'Data Line',");

            String cpuMinFileContents = FileUtils.readFileToString(files.get(2), StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(cpuMinFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: [],");
            TestFileUtils.assertLineEquals(cpuMinFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [],");
            TestFileUtils.assertLineEquals(cpuMinFileContents, TestFileUtils.LINE_FIRST_PLOT_NAME, "name: 'Data Line',");
            TestFileUtils.assertLineEquals(cpuMinFileContents, 122, "x: ['2018-01-17T04:16:16'],");
            TestFileUtils.assertLineEquals(cpuMinFileContents, 123, "y: [124.765],");
            TestFileUtils.assertLineEquals(cpuMinFileContents, 144, "name: 'Data Line',");

            String cpuSumFileContents = FileUtils.readFileToString(files.get(3), StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(cpuSumFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: [],");
            TestFileUtils.assertLineEquals(cpuSumFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [],");
            TestFileUtils.assertLineEquals(cpuSumFileContents, TestFileUtils.LINE_FIRST_PLOT_NAME, "name: 'Data Line',");
            TestFileUtils.assertLineEquals(cpuSumFileContents, 122, "x: ['2018-01-17T04:16:16'],");
            TestFileUtils.assertLineEquals(cpuSumFileContents, 123, "y: [16038.958],");
            TestFileUtils.assertLineEquals(cpuSumFileContents, 144, "name: 'Data Line',");

            String wallMaxFileContents = FileUtils.readFileToString(files.get(4), StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(wallMaxFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: [],");
            TestFileUtils.assertLineEquals(wallMaxFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [],");
            TestFileUtils.assertLineEquals(wallMaxFileContents, TestFileUtils.LINE_FIRST_PLOT_NAME, "name: 'Data Line',");
            TestFileUtils.assertLineEquals(wallMaxFileContents, 122, "x: ['2018-01-17T04:16:16'],");
            TestFileUtils.assertLineEquals(wallMaxFileContents, 123, "y: [126.394],");
            TestFileUtils.assertLineEquals(wallMaxFileContents, 144, "name: 'Data Line',");

            String wallMinFileContents = FileUtils.readFileToString(files.get(5), StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(wallMinFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: [],");
            TestFileUtils.assertLineEquals(wallMinFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [],");
            TestFileUtils.assertLineEquals(wallMinFileContents, TestFileUtils.LINE_FIRST_PLOT_NAME, "name: 'Data Line',");
            TestFileUtils.assertLineEquals(wallMinFileContents, 122, "x: ['2018-01-17T04:16:16'],");
            TestFileUtils.assertLineEquals(wallMinFileContents, 123, "y: [126.277],");
            TestFileUtils.assertLineEquals(wallMinFileContents, 144, "name: 'Data Line',");

            String wallSumFileContents = FileUtils.readFileToString(files.get(6), StandardCharsets.UTF_8);
            TestFileUtils.assertLineEquals(wallSumFileContents, TestFileUtils.LINE_FIRST_PLOT_X, "x: [],");
            TestFileUtils.assertLineEquals(wallSumFileContents, TestFileUtils.LINE_FIRST_PLOT_Y, "y: [],");
            TestFileUtils.assertLineEquals(wallSumFileContents, TestFileUtils.LINE_FIRST_PLOT_NAME, "name: 'Data Line',");
            TestFileUtils.assertLineEquals(wallSumFileContents, 122, "x: ['2018-01-17T04:16:16'],");
            TestFileUtils.assertLineEquals(wallSumFileContents, 123, "y: [16175.207],");
            TestFileUtils.assertLineEquals(wallSumFileContents, 144, "name: 'Data Line',");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }    

    @Test
    public void testUnitExample_Xml_ExportByPlot() {
        try {            
            File config    = TestFileUtils.loadTestFile("unit_tests/xml/ExportByPlot/config.json");
            File dataFile  = TestFileUtils.loadTestFile("unit_tests/xml/ExportByPlot");
            File dbDir     = Files.createTempDirectory("testUnitExample_Xml_ExportByPlot").toFile();
            File exportDir = Files.createTempDirectory("testUnitExample_Xml_ExportByPlot").toFile();

            WatchrCoreApp app = new WatchrCoreApp();
            StringOutputLogger logger = new StringOutputLogger();
            app.setLogger(logger);
            new WatchrRunStrategy()
                .setDatabaseDir(dbDir)
                .setDataFile(dataFile)
                .setConfigFile(config)
                .setExportDir(exportDir)
                .run(app);

            List<File> files = Arrays.asList(exportDir.listFiles());
            Collections.sort(files);

            if(files.size() == 3) {
                assertEquals("My_Plot_A.html", files.get(0).getName());
                assertEquals("My_Plot_B.html", files.get(1).getName());
                assertEquals("My_Plot_C.html", files.get(2).getName());
            } else {
                System.out.println("Number of exported plots: " + files.size());
                fail(logger.getLogAsString());
            }
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
