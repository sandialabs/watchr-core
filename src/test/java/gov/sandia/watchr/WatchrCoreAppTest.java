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

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import gov.sandia.watchr.db.impl.FileBasedDatabase;
import gov.sandia.watchr.parse.WatchrParseException;

/**
 * Unit test for simple App.
 */
public class WatchrCoreAppTest {

    ///////////////
    // API TESTS //
    ///////////////

    @Test
    public void testApp_Basic() {
        WatchrCoreApp app = WatchrCoreApp.getInstance();
        assertNotNull(app);
    }

    @Test
    public void testGetFailedPlotsSize_Basic() {
        WatchrCoreApp app = WatchrCoreApp.getInstance();
        int failedPlotCount = app.getFailedPlotsSize("");
        assertEquals(0, failedPlotCount);
    }

    @Test
    public void testGetFailedPlotsSize_RealData() {
        WatchrCoreApp app = WatchrCoreApp.getInstance();
        try {            
            ClassLoader classLoader = WatchrCoreAppTest.class.getClassLoader();
            File dbDestDir = Files.createTempDirectory(null).toFile();

            URL reportsDirUrl = classLoader.getResource("unit_tests/json/RuleTest");
            File reportsDir = new File(reportsDirUrl.toURI());

            URL sierraConfigJsonUrl = classLoader.getResource("unit_tests/json/RuleTest/rule_test.json");
            File configFile = new File(sierraConfigJsonUrl.toURI());
            String configFileContents = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);

            app.connectDatabase("testGetFailedPlotsSize_RealData", FileBasedDatabase.class, new Object[]{ dbDestDir });
            app.processConfigFile("testGetFailedPlotsSize_RealData", reportsDir, configFileContents);
            int failedPlotCount = app.getFailedPlotsSize("testGetFailedPlotsSize_RealData");
            assertEquals(0, failedPlotCount);
        } catch (WatchrParseException e1) {
            e1.getOriginalException().printStackTrace();
            fail(e1.getOriginalException().getMessage());
        } catch (IOException | URISyntaxException e2) {
            e2.printStackTrace();
            fail(e2.getMessage());
        } 
    } 

    @Test
    public void testGetPlotsSize_Basic() {
        WatchrCoreApp app = WatchrCoreApp.getInstance();
        int plotCount = app.getPlotsSize("");
        assertEquals(0, plotCount);
    }    

    @Test
    public void testGetPlotsSize_RealData() {
        WatchrCoreApp app = WatchrCoreApp.getInstance();
        try {            
            ClassLoader classLoader = WatchrCoreAppTest.class.getClassLoader();
            File dbDestDir = Files.createTempDirectory(null).toFile();

            URL reportsDirUrl = classLoader.getResource("unit_tests/json/RuleTest");
            File reportsDir = new File(reportsDirUrl.toURI());

            URL sierraConfigJsonUrl = classLoader.getResource("unit_tests/json/RuleTest/rule_test.json");
            File configFile = new File(sierraConfigJsonUrl.toURI());
            String configFileContents = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);

            app.connectDatabase("testGetPlotsSize_RealData", FileBasedDatabase.class, new Object[]{ dbDestDir });
            app.processConfigFile("testGetPlotsSize_RealData", reportsDir, configFileContents);
            int plotCount = app.getPlotsSize("testGetPlotsSize_RealData");
            assertEquals(1, plotCount);
        } catch (WatchrParseException e1) {
            e1.getOriginalException().printStackTrace();
            fail(e1.getOriginalException().getMessage());
        } catch (IOException | URISyntaxException e2) {
            e2.printStackTrace();
            fail(e2.getMessage());
        } 
    }

    ///////////////////
    // EXAMPLE TESTS //
    ///////////////////

    @Test
    public void testUnitExample_Xml_HelloWorld() {
        try {            
            File config    = loadTestFile("unit_tests/xml/HelloWorld/config.json");
            File dataFile  = loadTestFile("unit_tests/xml/HelloWorld/performance_day_1.xml");
            File dbDir     = Files.createTempDirectory(null).toFile();
            File exportDir = Files.createTempDirectory(null).toFile();

            WatchrCoreApp.main(new String[]{
                config.getAbsolutePath(),
                dataFile.getAbsolutePath(),
                dbDir.getAbsolutePath(),
                exportDir.getAbsolutePath()
            });

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            assertTrue(exportFileContents.contains("x: ['2021-04-05T22:21:21'],"));
            assertTrue(exportFileContents.contains("y: [1.0],"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }         
    }

    @Test
    public void testUnitExample_Xml_ThreePointsOnOneLine() {
        try {            
            File config    = loadTestFile("unit_tests/xml/ThreePointsOnOneLine/config.json");
            File dataFile  = loadTestFile("unit_tests/xml/ThreePointsOnOneLine/");
            File dbDir     = Files.createTempDirectory(null).toFile();
            File exportDir = Files.createTempDirectory(null).toFile();

            WatchrCoreApp.main(new String[]{
                config.getAbsolutePath(),
                dataFile.getAbsolutePath(),
                dbDir.getAbsolutePath(),
                exportDir.getAbsolutePath()
            });

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            assertTrue(exportFileContents.contains("x: ['2021-04-05T22:21:21', '2021-04-06T12:21:21', '2021-04-07T02:21:21'],"));
            assertTrue(exportFileContents.contains("y: [1.0, 2.0, 3.0],"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }         
    }

    @Test
    public void testUnitExample_Xml_Categories() {
        try {            
            File config    = loadTestFile("unit_tests/xml/Categories/config.json");
            File dataFile  = loadTestFile("unit_tests/xml/Categories/");
            File dbDir     = Files.createTempDirectory(null).toFile();
            File exportDir = Files.createTempDirectory(null).toFile();

            WatchrCoreApp.main(new String[]{
                config.getAbsolutePath(),
                dataFile.getAbsolutePath(),
                dbDir.getAbsolutePath(),
                exportDir.getAbsolutePath()
            });

            assertEquals(3, exportDir.listFiles().length);
            assertEquals("root_max.html", exportDir.listFiles()[0].getName());
            assertEquals("root_mean.html", exportDir.listFiles()[1].getName());
            assertEquals("root_min.html", exportDir.listFiles()[2].getName());

            String maxExportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            assertTrue(maxExportFileContents.contains("x: ['2021-04-05T22:21:21'],"));
            assertTrue(maxExportFileContents.contains("y: [5.0],"));
            assertTrue(maxExportFileContents.contains("name: 'Max Data Line',"));

            String meanExportFileContents = FileUtils.readFileToString(exportDir.listFiles()[1], StandardCharsets.UTF_8);
            assertTrue(meanExportFileContents.contains("x: ['2021-04-05T22:21:21'],"));
            assertTrue(meanExportFileContents.contains("y: [2.5],"));
            assertTrue(meanExportFileContents.contains("name: 'Mean Data Line',"));

            String minExportFileContents = FileUtils.readFileToString(exportDir.listFiles()[2], StandardCharsets.UTF_8);
            assertTrue(minExportFileContents.contains("x: ['2021-04-05T22:21:21'],"));
            assertTrue(minExportFileContents.contains("y: [0.0],"));
            assertTrue(minExportFileContents.contains("name: 'Min Data Line',"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }         
    }      

    @Test
    public void testUnitExample_Json_HelloWorld() {
        try {            
            File config    = loadTestFile("unit_tests/json/HelloWorld/config.json");
            File dataFile  = loadTestFile("unit_tests/json/HelloWorld/performance_day_1.json");
            File dbDir     = Files.createTempDirectory(null).toFile();
            File exportDir = Files.createTempDirectory(null).toFile();

            WatchrCoreApp.main(new String[]{
                config.getAbsolutePath(),
                dataFile.getAbsolutePath(),
                dbDir.getAbsolutePath(),
                exportDir.getAbsolutePath()
            });

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            assertTrue(exportFileContents.contains("x: ['2021-04-05T22:21:21'],"));
            assertTrue(exportFileContents.contains("y: [1.0],"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }         
    }

    @Test
    public void testUnitExample_Json_DerivativeLineTest() {
        try {            
            File config    = loadTestFile("unit_tests/json/DerivativeLineTest/config.json");
            File dataFile  = loadTestFile("unit_tests/json/DerivativeLineTest/performance_rising_data.json");
            File dbDir     = Files.createTempDirectory(null).toFile();
            File exportDir = Files.createTempDirectory(null).toFile();

            WatchrCoreApp.main(new String[]{
                config.getAbsolutePath(),
                dataFile.getAbsolutePath(),
                dbDir.getAbsolutePath(),
                exportDir.getAbsolutePath()
            });

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            assertTrue(exportFileContents.contains("name: 'My First Data Line',"));
            assertTrue(exportFileContents.contains("x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-07T22:21:21', '2021-04-08T22:21:21', '2021-04-09T22:21:21', '2021-04-10T22:21:21'],"));
            assertTrue(exportFileContents.contains("y: [1.0, 2.0, 3.0, 4.0, 1.0, -1.0],"));
            assertTrue(exportFileContents.contains("name: 'Average',"));
            assertTrue(exportFileContents.contains("y: [1.0, 1.5, 2.0, 2.5, 2.5, 1.75],"));
            assertTrue(exportFileContents.contains("name: 'Average + Std. Dev.',"));
            assertTrue(exportFileContents.contains("y: [1.0, 2.0, 2.8164965809277263, 3.618033988749895, 3.618033988749895, 3.670286436967152],"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }         
    }
    
    @Test
    public void testUnitExample_Json_Rules_Success() {
        try {            
            File config    = loadTestFile("unit_tests/json/RuleTest/rule_test.json");
            File dataFile  = loadTestFile("unit_tests/json/RuleTest/performance_rising_data.json");
            File dbDir     = Files.createTempDirectory(null).toFile();
            File exportDir = Files.createTempDirectory(null).toFile();

            WatchrCoreApp.main(new String[]{
                config.getAbsolutePath(),
                dataFile.getAbsolutePath(),
                dbDir.getAbsolutePath(),
                exportDir.getAbsolutePath()
            });

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            assertTrue(exportFileContents.contains("name: 'My First Data Line',"));
            assertTrue(exportFileContents.contains("x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-07T22:21:21', '2021-04-08T22:21:21', '2021-04-09T22:21:21', '2021-04-10T22:21:21'],"));
            assertTrue(exportFileContents.contains("y: [1.0, 2.0, 3.0, 4.0, 1.0, -1.0],"));
            assertTrue(exportFileContents.contains("plot_bgcolor: 'rgb(255, 255, 255)',"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }         
    }

    @Test
    public void testUnitExample_Json_Rules_Fail() {
        try {            
            File config    = loadTestFile("unit_tests/json/RuleTest/rule_test_2.json");
            File dataFile  = loadTestFile("unit_tests/json/RuleTest/performance_rising_data.json");
            File dbDir     = Files.createTempDirectory(null).toFile();
            File exportDir = Files.createTempDirectory(null).toFile();

            WatchrCoreApp.main(new String[]{
                config.getAbsolutePath(),
                dataFile.getAbsolutePath(),
                dbDir.getAbsolutePath(),
                exportDir.getAbsolutePath()
            });

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            assertTrue(exportFileContents.contains("name: 'My First Data Line',"));
            assertTrue(exportFileContents.contains("x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-07T22:21:21', '2021-04-08T22:21:21', '2021-04-09T22:21:21', '2021-04-10T22:21:21'],"));
            assertTrue(exportFileContents.contains("y: [1.0, 2.0, 3.0, 4.0, 1.0, -1.0],"));
            assertTrue(exportFileContents.contains("plot_bgcolor: 'rgb(235, 156, 156)',"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }         
    }

    @Test
    public void testUnitExample_Json_Rules_WarnWithConflictingRules() {
        try {            
            File config    = loadTestFile("unit_tests/json/RuleTest/rule_test_3.json");
            File dataFile  = loadTestFile("unit_tests/json/RuleTest/performance_rising_data_2.json");
            File dbDir     = Files.createTempDirectory(null).toFile();
            File exportDir = Files.createTempDirectory(null).toFile();

            WatchrCoreApp.main(new String[]{
                config.getAbsolutePath(),
                dataFile.getAbsolutePath(),
                dbDir.getAbsolutePath(),
                exportDir.getAbsolutePath()
            });

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            assertTrue(exportFileContents.contains("name: 'My First Data Line',"));
            assertTrue(exportFileContents.contains("x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-07T22:21:21', '2021-04-08T22:21:21', '2021-04-09T22:21:21', '2021-04-10T22:21:21'],"));
            assertTrue(exportFileContents.contains("y: [5.0, 4.0, 3.0, 2.0, 2.5, 3.0],"));
            assertTrue(exportFileContents.contains("plot_bgcolor: 'rgb(239, 228, 176)',"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }         
    }    

    @Test
    public void testUnitExample_Json_FileFilter() {
        try {            
            File config    = loadTestFile("unit_tests/json/FileFilterExample/config.json");
            File dataFile  = loadTestFile("unit_tests/json/FileFilterExample");
            File dbDir     = Files.createTempDirectory(null).toFile();
            File exportDir = Files.createTempDirectory(null).toFile();

            WatchrCoreApp.main(new String[]{
                config.getAbsolutePath(),
                dataFile.getAbsolutePath(),
                dbDir.getAbsolutePath(),
                exportDir.getAbsolutePath()
            });

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            assertTrue(exportFileContents.contains("name: 'Data Line',"));
            assertTrue(exportFileContents.contains("x: ['2021-04-05T22:21:21'"));
            assertTrue(exportFileContents.contains("y: [1.0],"));
            assertFalse(exportFileContents.contains("y: [2.0],"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testUnitExample_Json_Legend() {
        try {            
            File config    = loadTestFile("unit_tests/json/Legend/config.json");
            File dataFile  = loadTestFile("unit_tests/json/Legend/performance_day_1.json");
            File dbDir     = Files.createTempDirectory(null).toFile();
            File exportDir = Files.createTempDirectory(null).toFile();

            WatchrCoreApp.main(new String[]{
                config.getAbsolutePath(),
                dataFile.getAbsolutePath(),
                dbDir.getAbsolutePath(),
                exportDir.getAbsolutePath()
            });

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            assertTrue(exportFileContents.contains("showlegend: true,"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }    

    @Test
    public void testUnitExample_Xml_ChildGraphs() {
        try {            
            File config    = loadTestFile("unit_tests/xml/ChildGraphs/config.json");
            File dataFile  = loadTestFile("unit_tests/xml/ChildGraphs/performance.xml");
            File dbDir     = Files.createTempDirectory(null).toFile();
            File exportDir = Files.createTempDirectory(null).toFile();

            WatchrCoreApp.main(new String[]{
                config.getAbsolutePath(),
                dataFile.getAbsolutePath(),
                dbDir.getAbsolutePath(),
                exportDir.getAbsolutePath()
            });

            assertEquals(3, exportDir.listFiles().length);
            assertEquals("root.html", exportDir.listFiles()[0].getName());
            assertEquals("_nightly_run_2021-04-05_measurement_A.html", exportDir.listFiles()[1].getName());
            assertEquals("_nightly_run_2021-04-05_measurement_A_measurement_A1.html", exportDir.listFiles()[2].getName());

            String file1Contents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            assertTrue(file1Contents.contains("x: ['2021-04-05T22:21:21'],"));
            assertTrue(file1Contents.contains("y: [100.0],"));

            String file2Contents = FileUtils.readFileToString(exportDir.listFiles()[1], StandardCharsets.UTF_8);
            assertTrue(file2Contents.contains("x: ['2021-04-05T22:21:21'],"));
            assertTrue(file2Contents.contains("y: [25.0],"));
            assertTrue(file2Contents.contains("y: [75.0],"));

            String file3Contents = FileUtils.readFileToString(exportDir.listFiles()[2], StandardCharsets.UTF_8);
            assertTrue(file3Contents.contains("x: ['2021-04-05T22:21:21'],"));
            assertTrue(file3Contents.contains("y: [15.0],"));
            assertTrue(file3Contents.contains("y: [10.0],"));
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testUnitExample_Xml_Metadata() {
        try {            
            File config    = loadTestFile("unit_tests/xml/Metadata/config.json");
            File dataFile  = loadTestFile("unit_tests/xml/Metadata/performance_day_1.xml");
            File dbDir     = Files.createTempDirectory(null).toFile();
            File exportDir = Files.createTempDirectory(null).toFile();

            WatchrCoreApp.main(new String[]{
                config.getAbsolutePath(),
                dataFile.getAbsolutePath(),
                dbDir.getAbsolutePath(),
                exportDir.getAbsolutePath()
            });

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            assertTrue(exportFileContents.contains("hovertemplate: '<b>X</b>: %{x}<br>' +'<b>Y</b>: %{y}<br>' +'%{text}',"));
            assertTrue(exportFileContents.contains("text: ['branch: master'],"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testUnitExample_Json_PointFilter() {
        try {            
            File config    = loadTestFile("unit_tests/json/PointFilterExample/config.json");
            File dataFile  = loadTestFile("unit_tests/json/PointFilterExample");
            File dbDir     = Files.createTempDirectory(null).toFile();
            File exportDir = Files.createTempDirectory(null).toFile();

            WatchrCoreApp.main(new String[]{
                config.getAbsolutePath(),
                dataFile.getAbsolutePath(),
                dbDir.getAbsolutePath(),
                exportDir.getAbsolutePath()
            });

            assertEquals(1, exportDir.listFiles().length);
            String exportFileContents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            assertFalse(exportFileContents.contains("x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-07T22:21:21', '2021-04-08T22:21:21', '2021-04-09T22:21:21', '2021-04-10T22:21:21'],"));
            assertTrue(exportFileContents.contains( "x: ['2021-04-05T22:21:21', '2021-04-06T22:21:21', '2021-04-08T22:21:21', '2021-04-09T22:21:21'],"));
            assertFalse(exportFileContents.contains("y: [1.0, 2.0, 3.0, 4.0, 1.0, -1.0],"));
            assertTrue(exportFileContents.contains( "y: [1.0, 2.0, 4.0, 1.0],"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testUnitExample_Xml_TemplateExample() {
        try {            
            File config    = loadTestFile("unit_tests/xml/TemplateExample/config.json");
            File dataFile  = loadTestFile("unit_tests/xml/TemplateExample/performance.xml");
            File dbDir     = Files.createTempDirectory(null).toFile();
            File exportDir = Files.createTempDirectory(null).toFile();

            WatchrCoreApp.main(new String[]{
                config.getAbsolutePath(),
                dataFile.getAbsolutePath(),
                dbDir.getAbsolutePath(),
                exportDir.getAbsolutePath()
            });
            
            List<File> files = Arrays.asList(exportDir.listFiles());
            Collections.sort(files);
            assertEquals(3, files.size());
            
            assertEquals("root_cpu-time-max.html", files.get(0).getName());
            assertEquals("root_cpu-time-min.html", files.get(1).getName());
            assertEquals("root_cpu-time-sum.html", files.get(2).getName());

            String file1Contents = FileUtils.readFileToString(exportDir.listFiles()[0], StandardCharsets.UTF_8);
            assertTrue(file1Contents.contains("x: ['2021-04-05T22:21:21'],"));
            assertTrue(file1Contents.contains("y: [2.0],"));

            String file2Contents = FileUtils.readFileToString(exportDir.listFiles()[1], StandardCharsets.UTF_8);
            assertTrue(file2Contents.contains("x: ['2021-04-05T22:21:21'],"));
            assertTrue(file2Contents.contains("y: [1.0],"));

            String file3Contents = FileUtils.readFileToString(exportDir.listFiles()[2], StandardCharsets.UTF_8);
            assertTrue(file3Contents.contains("x: ['2021-04-05T22:21:21'],"));
            assertTrue(file3Contents.contains("y: [3.0],"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }   

    /////////////
    // PRIVATE //
    /////////////

    private File loadTestFile(String path) {
        try {
            ClassLoader classLoader = WatchrCoreAppTest.class.getClassLoader();
            URL url = classLoader.getResource(path);
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        return null;
    }
}
