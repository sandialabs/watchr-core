package gov.sandia.watchr.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.TestLogger;
import gov.sandia.watchr.WatchrCoreApp;

public class PlotsConfigTest {

    private TestLogger testLogger;

    @Before
    public void setup() {
        testLogger = new TestLogger();
        WatchrCoreApp.getInstance().setLogger(testLogger);
    }     

    @Test
    public void testValidate_EmptyFileConfig() {
        CategoryConfiguration categoryConfig = new CategoryConfiguration("");
        FileConfig fileConfig = new FileConfig("");

        PlotsConfig plotsConfig = new PlotsConfig("");
        plotsConfig.setCategoryConfig(categoryConfig);
        plotsConfig.setFileConfig(fileConfig);

        plotsConfig.validate();
        List<WatchrConfigError> errors = testLogger.getErrors();
        assertEquals(3, errors.size());

        assertEquals("A directory for parseable reports was not provided.", errors.get(0).getMessage());
        assertEquals("Pattern for finding report files cannot be blank!", errors.get(1).getMessage());
        assertEquals("No file type extension was provided.  It is strongly recommend that at least one " +
                     "file extension is specified so Watchr knows how to parse your report files.", errors.get(2).getMessage());
    }

    @Test
    public void testValidate_HappyPath() {
        CategoryConfiguration categoryConfig = new CategoryConfiguration("");
        FileConfig fileConfig = new FileConfig(new File("."), "");
        fileConfig.setFileExtension("xml");
        fileConfig.setFileNamePattern("test");

        PlotsConfig plotsConfig = new PlotsConfig("");
        plotsConfig.setCategoryConfig(categoryConfig);
        plotsConfig.setFileConfig(fileConfig);

        plotsConfig.validate();
        List<WatchrConfigError> errors = testLogger.getErrors();
        assertEquals(0, errors.size());
    }    

    @Test
    public void testCopyAndEquals() {
        try {
            File startFile = Files.createTempDirectory(null).toFile();
            CategoryConfiguration categoryConfig = new CategoryConfiguration("");
            FileConfig fileConfig = new FileConfig(startFile, "");
            fileConfig.setFileExtension("xml");
            fileConfig.setFileNamePattern("test");

            PlotsConfig plotsConfig = new PlotsConfig("");
            plotsConfig.setCategoryConfig(categoryConfig);
            plotsConfig.setFileConfig(fileConfig);

            PlotsConfig plotsConfig2 = new PlotsConfig(plotsConfig);
            assertEquals(plotsConfig, plotsConfig2);
        } catch(IOException e) {
            fail(e.getMessage());
        }
    } 

    @Test
    public void testCopyAndNotEquals() {
        CategoryConfiguration categoryConfig = new CategoryConfiguration("");
        FileConfig fileConfig = new FileConfig(new File("."), "");
        fileConfig.setFileExtension("xml");
        fileConfig.setFileNamePattern("test");

        PlotsConfig plotsConfig = new PlotsConfig("");
        plotsConfig.setCategoryConfig(categoryConfig);
        plotsConfig.setFileConfig(fileConfig);

        PlotsConfig plotsConfig2 = new PlotsConfig(plotsConfig);
        plotsConfig2.getFileConfig().setFileExtension("json");
        assertNotEquals(plotsConfig, plotsConfig2);
    } 

    @Test
    public void testCopyAndHashCode() {
        try {
            File startFile = Files.createTempDirectory(null).toFile();

            CategoryConfiguration categoryConfig = new CategoryConfiguration("");
            categoryConfig.getCategories().add("category");
            FileConfig fileConfig = new FileConfig(startFile, "");
            fileConfig.setFileExtension("xml");
            fileConfig.setFileNamePattern("test");

            PlotsConfig plotsConfig = new PlotsConfig("");
            plotsConfig.setCategoryConfig(categoryConfig);
            plotsConfig.setFileConfig(fileConfig);

            PlotsConfig plotsConfig2 = new PlotsConfig(plotsConfig);
            assertEquals(plotsConfig.hashCode(), plotsConfig2.hashCode());
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }     
}
