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
import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.PlotsConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.db.impl.FileBasedDatabase;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.parse.WatchrParseException;

public class WatchrCoreAppTest_BasicTests {

    private WatchrCoreApp app;
    private WatchrConfig config;
    private StringOutputLogger testLogger;
    private IFileReader fileReader;

    @Before
    public void setup() {
        app = new WatchrCoreApp();
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
            assertTrue(app.isStartFileValid(config, dummyXml.getAbsolutePath()));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testIsStartFileValid_Json() {
        try {
            File dummyJson = Files.createTempFile("performance", ".json").toFile();
            assertFalse(app.isStartFileValid(config, dummyJson.getAbsolutePath()));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testIsStartFileValid_BadFile() {
        try {
            File gitFile = Files.createTempFile("", ".git").toFile();
            assertFalse(app.isStartFileValid(config, gitFile.getAbsolutePath()));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testIsStartFileValid_Directory() {
        try {
            File directory = Files.createTempDirectory("testIsStartFileValid_Directory").toFile();
            assertTrue(app.isStartFileValid(config, directory.getAbsolutePath()));
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }    

    @Test
    public void testApp_Basic() {
        WatchrCoreApp app = new WatchrCoreApp();
        assertNotNull(app);
    }

    @Test
    public void testGetFailedPlotsSize_Basic() {
        WatchrCoreApp app = new WatchrCoreApp();
        int failedPlotCount = app.getFailedPlotsSize("");
        assertEquals(0, failedPlotCount);
    }

    @Test
    public void testGetFailedPlotsSize_RealData() {
        WatchrCoreApp app = new WatchrCoreApp();
        try {            
            ClassLoader classLoader = WatchrCoreAppTest_BasicTests.class.getClassLoader();
            File dbDestDir = Files.createTempDirectory("testGetFailedPlotsSize_RealData").toFile();

            URL reportsDirUrl = classLoader.getResource("unit_tests/json/RuleTest");
            File reportsDir = new File(reportsDirUrl.toURI());

            URL sierraConfigJsonUrl = classLoader.getResource("unit_tests/json/RuleTest/rule_test.json");
            File configFile = new File(sierraConfigJsonUrl.toURI());
            String configFileContents = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);

            app.connectDatabase("testGetFailedPlotsSize_RealData", FileBasedDatabase.class, new Object[]{ dbDestDir });
            app.addToDatabase("testGetFailedPlotsSize_RealData", reportsDir.getAbsolutePath(), configFileContents);
            app.saveDatabase("testGetFailedPlotsSize_RealData");
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
        WatchrCoreApp app = new WatchrCoreApp();
        int plotCount = app.getPlotsSize("");
        assertEquals(0, plotCount);
    }    

    @Test
    public void testGetPlotsSize_RealData() {
        WatchrCoreApp app = new WatchrCoreApp();
        try {            
            ClassLoader classLoader = WatchrCoreAppTest.class.getClassLoader();
            File dbDestDir = Files.createTempDirectory("testGetPlotsSize_RealData").toFile();

            URL reportsDirUrl = classLoader.getResource("unit_tests/json/RuleTest");
            File reportsDir = new File(reportsDirUrl.toURI());

            URL sierraConfigJsonUrl = classLoader.getResource("unit_tests/json/RuleTest/rule_test.json");
            File configFile = new File(sierraConfigJsonUrl.toURI());
            String configFileContents = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);

            app.connectDatabase("testGetPlotsSize_RealData", FileBasedDatabase.class, new Object[]{ dbDestDir });
            app.addToDatabase("testGetPlotsSize_RealData", reportsDir.getAbsolutePath(), configFileContents);
            app.saveDatabase("testGetPlotsSize_RealData");

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
}
