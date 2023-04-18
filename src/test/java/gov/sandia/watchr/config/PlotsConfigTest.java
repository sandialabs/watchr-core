package gov.sandia.watchr.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.log.StringOutputLogger;

public class PlotsConfigTest {

    private StringOutputLogger testLogger;
    private IFileReader fileReader;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
        fileReader = new DefaultFileReader(testLogger);
    }     

    @Test
    public void testValidate_EmptyFileConfig() {
        CategoryConfiguration categoryConfig = new CategoryConfiguration("", testLogger);
        FileConfig fileConfig = new FileConfig("", testLogger, fileReader);

        PlotsConfig plotsConfig = new PlotsConfig("", testLogger, fileReader);
        plotsConfig.setCategoryConfig(categoryConfig);
        plotsConfig.setFileConfig(fileConfig);

        plotsConfig.validate();
        List<String> errors = testLogger.getLog();
        assertEquals(3, errors.size());

        assertTrue(errors.get(0).contains("A starting path for parseable reports was not provided."));
        assertTrue(errors.get(1).contains("Pattern for finding report files cannot be blank!"));
        assertTrue(errors.get(2).contains("No file type extension was provided.  It is strongly recommend that at least one " +
                     "file extension is specified so Watchr knows how to parse your report files."));
    }

    @Test
    public void testValidate_HappyPath() {
        CategoryConfiguration categoryConfig = new CategoryConfiguration("", testLogger);
        File startFile = new File(".");
        FileConfig fileConfig = new FileConfig(startFile.getAbsolutePath(), "", testLogger, fileReader);
        fileConfig.setFileExtension("xml");
        fileConfig.setFileNamePattern("test");

        PlotsConfig plotsConfig = new PlotsConfig("", testLogger, fileReader);
        plotsConfig.setCategoryConfig(categoryConfig);
        plotsConfig.setFileConfig(fileConfig);

        plotsConfig.validate();
        List<String> errors = testLogger.getLog();
        assertEquals(0, errors.size());
    }    

    @Test
    public void testCopyAndEquals() {
        try {
            File startFile = Files.createTempDirectory("testCopyAndEquals").toFile();
            CategoryConfiguration categoryConfig = new CategoryConfiguration("", testLogger);
            FileConfig fileConfig = new FileConfig(startFile.getAbsolutePath(), "", testLogger, fileReader);
            fileConfig.setFileExtension("xml");
            fileConfig.setFileNamePattern("test");

            PlotsConfig plotsConfig = new PlotsConfig("", testLogger, fileReader);
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
        CategoryConfiguration categoryConfig = new CategoryConfiguration("", testLogger);
        File startFile = new File(".");
        FileConfig fileConfig = new FileConfig(startFile.getAbsolutePath(), "", testLogger, fileReader);
        fileConfig.setFileExtension("xml");
        fileConfig.setFileNamePattern("test");

        PlotsConfig plotsConfig = new PlotsConfig("", testLogger, fileReader);
        plotsConfig.setCategoryConfig(categoryConfig);
        plotsConfig.setFileConfig(fileConfig);

        PlotsConfig plotsConfig2 = new PlotsConfig(plotsConfig);
        plotsConfig2.getFileConfig().setFileExtension("json");
        assertNotEquals(plotsConfig, plotsConfig2);
    } 

    @Test
    public void testCopyAndHashCode() {
        try {
            File startFile = Files.createTempDirectory("testCopyAndHashCode").toFile();

            CategoryConfiguration categoryConfig = new CategoryConfiguration("", testLogger);
            categoryConfig.getCategories().add("category");
            FileConfig fileConfig = new FileConfig(startFile.getAbsolutePath(), "", testLogger, fileReader);
            fileConfig.setFileExtension("xml");
            fileConfig.setFileNamePattern("test");

            PlotsConfig plotsConfig = new PlotsConfig("", testLogger, fileReader);
            plotsConfig.setCategoryConfig(categoryConfig);
            plotsConfig.setFileConfig(fileConfig);

            PlotsConfig plotsConfig2 = new PlotsConfig(plotsConfig);
            assertEquals(plotsConfig.hashCode(), plotsConfig2.hashCode());
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetPointFilterConfig() {
        PlotsConfig plotsConfig = new PlotsConfig("/my/path/prefix/plots", testLogger, fileReader);
        assertNotNull(plotsConfig.getPointFilterConfig());
    }
}
