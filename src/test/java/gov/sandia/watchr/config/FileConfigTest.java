package gov.sandia.watchr.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.log.StringOutputLogger;

public class FileConfigTest {

    private StringOutputLogger logger;
    private IFileReader fileReader;

    @Before
    public void setup() {
        logger = new StringOutputLogger();
        fileReader = new DefaultFileReader(logger);
    }
    
    @Test
    public void testValidate_GoodBoi() {
        try {
            File startDir = Files.createTempDirectory("testValidate_GoodBoi").toFile();
            FileConfig fileConfig = new FileConfig(startDir.getAbsolutePath(), "", logger, fileReader);
            fileConfig.setFileNamePattern("report");
            fileConfig.setFileExtension("xml");

            fileConfig.validate();
            List<String> errors = logger.getLog();
            assertEquals(0, errors.size());
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testValidate_MissingDirectory() {
        FileConfig fileConfig = new FileConfig("", logger, fileReader);
        fileConfig.setFileNamePattern("report");
        fileConfig.setFileExtension("xml");

        fileConfig.validate();
        List<String> errors = logger.getLog();
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("ERROR"));
        assertTrue(errors.get(0).contains("A starting path for parseable reports was not provided."));
    } 

    @Test
    public void testValidate_NonexistentDirectory() {
        try {
            File startDir = Files.createTempDirectory("testValidate_NonexistentDirectory").toFile();
            FileConfig fileConfig = new FileConfig(startDir.getAbsolutePath(), "", logger, fileReader);
            startDir.delete();
            fileConfig.setFileNamePattern("report");
            fileConfig.setFileExtension("xml");

            fileConfig.validate();
            List<String> errors = logger.getLog();
            assertEquals(1, errors.size());
            assertTrue(errors.get(0).contains("ERROR"));
            assertTrue(errors.get(0).endsWith(" does not exist."));
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testValidate_BlankFileExtension() {
        try {
            File startDir = Files.createTempDirectory("testValidate_BlankFileExtension").toFile();
            FileConfig fileConfig = new FileConfig(startDir.getAbsolutePath(), "", logger, fileReader);
            fileConfig.setFileNamePattern("report");

            fileConfig.validate();
            List<String> errors = logger.getLog();
            assertEquals(1, errors.size());
            assertTrue(errors.get(0).contains("WARNING"));
            assertTrue(errors.get(0).contains("No file type extension was provided.  It is strongly recommend that at least one file extension " +
                         "is specified so Watchr knows how to parse your report files."));
        } catch(IOException e) {
            fail(e.getMessage());
        }
    } 

    @Test
    public void testValidate_BlankFilePattern() {
        try {
            File startDir = Files.createTempDirectory("testValidate_BlankFileExtension").toFile();
            FileConfig fileConfig = new FileConfig(startDir.getAbsolutePath(), "", logger, fileReader);
            fileConfig.setFileExtension("xml");

            fileConfig.validate();
            List<String> errors = logger.getLog();
            assertEquals(1, errors.size());
            assertTrue(errors.get(0).contains("ERROR"));
            assertTrue(errors.get(0).contains("Pattern for finding report files cannot be blank!"));
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCopyAndEquals() {
        try {
            File startDir = Files.createTempDirectory("testCopyAndEquals").toFile();
            FileConfig fileConfig = new FileConfig(startDir.getAbsolutePath(), "", logger, fileReader);
            fileConfig.setFileExtension("xml");
            fileConfig.setFileNamePattern("pattern");
            fileConfig.setIgnoreOldFiles(true);
            fileConfig.setRecurseDirectories(true);

            FileConfig copy = new FileConfig(fileConfig);
            assertEquals(fileConfig, copy);
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCopyAndNotEquals() {
        try {
            File startDir = Files.createTempDirectory("testCopyAndNotEquals").toFile();
            FileConfig fileConfig = new FileConfig(startDir.getAbsolutePath(), "", logger, fileReader);
            fileConfig.setFileExtension("xml");
            fileConfig.setFileNamePattern("pattern");
            fileConfig.setIgnoreOldFiles(true);
            fileConfig.setRecurseDirectories(true);

            FileConfig copy = new FileConfig(fileConfig);
            copy.setFileExtension("json");
            assertNotEquals(fileConfig, copy);
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }    

    @Test
    public void testCopyAndHashCode() {
        try {
            File startDir = Files.createTempDirectory("testCopyAndHashCode").toFile();
            FileConfig fileConfig = new FileConfig(startDir.getAbsolutePath(), "", logger, fileReader);
            fileConfig.setFileExtension("xml");
            fileConfig.setFileNamePattern("pattern");
            fileConfig.setIgnoreOldFiles(true);
            fileConfig.setRecurseDirectories(true);

            FileConfig copy = new FileConfig(fileConfig);
            assertEquals(fileConfig.hashCode(), copy.hashCode());
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDiffs() {
        try {
            File startDir = Files.createTempDirectory("testDiffs1").toFile();
            FileConfig fileConfig = new FileConfig(startDir.getAbsolutePath(), "/my/path/prefix", logger, fileReader);
            fileConfig.setFileExtension("xml");
            fileConfig.setFileNamePattern("pattern");
            fileConfig.setIgnoreOldFiles(true);
            fileConfig.setRecurseDirectories(true);

            File startDir2 = Files.createTempDirectory("testDiffs2").toFile();
            FileConfig fileConfig2 = new FileConfig(startDir2.getAbsolutePath(), "/my/path/prefix", logger, fileReader);
            fileConfig2.setFileExtension("json");
            fileConfig2.setFileNamePattern("pattern2");
            fileConfig2.setIgnoreOldFiles(false);
            fileConfig2.setRecurseDirectories(false);
            
            List<WatchrDiff<?>> diffs = fileConfig.diff(fileConfig2);
            assertEquals(5, diffs.size());

            WatchrDiff<?> diff1 = diffs.get(0);
            assertEquals(DiffCategory.START_DIR, diff1.getProperty());
            assertEquals("/my/path/prefix/fileConfig", diff1.getPath());
            assertEquals(startDir.getAbsolutePath(), diff1.getBeforeValue());
            assertEquals(startDir2.getAbsolutePath(), diff1.getNowValue());

            WatchrDiff<?> diff2 = diffs.get(1);
            assertEquals(DiffCategory.FILENAME_PATTERN, diff2.getProperty());
            assertEquals("/my/path/prefix/fileConfig", diff2.getPath());
            assertEquals("pattern", diff2.getBeforeValue());
            assertEquals("pattern2", diff2.getNowValue());

            WatchrDiff<?> diff3 = diffs.get(2);
            assertEquals(DiffCategory.FILE_EXTENSION, diff3.getProperty());
            assertEquals("/my/path/prefix/fileConfig", diff3.getPath());
            assertEquals("xml", diff3.getBeforeValue());
            assertEquals("json", diff3.getNowValue());

            WatchrDiff<?> diff4 = diffs.get(3);
            assertEquals(DiffCategory.IGNORE_OLD_FILES, diff4.getProperty());
            assertEquals("/my/path/prefix/fileConfig", diff4.getPath());
            assertTrue((Boolean) diff4.getBeforeValue());
            assertFalse((Boolean) diff4.getNowValue());

            WatchrDiff<?> diff5 = diffs.get(4);
            assertEquals(DiffCategory.RECURSE_DIRECTORIES, diff5.getProperty());
            assertEquals("/my/path/prefix/fileConfig", diff5.getPath());
            assertTrue((Boolean) diff5.getBeforeValue());
            assertFalse((Boolean) diff5.getNowValue());
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }      
}
