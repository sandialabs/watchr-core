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

import gov.sandia.watchr.TestLogger;
import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;

public class FileConfigTest {

    private TestLogger testLogger;

    @Before
    public void setup() {
        testLogger = new TestLogger();
        WatchrCoreApp.getInstance().setLogger(testLogger);
    }
    
    @Test
    public void testValidate_GoodBoi() {
        try {
            File startDir = Files.createTempDirectory(null).toFile();
            FileConfig fileConfig = new FileConfig(startDir, "");
            fileConfig.setFileNamePattern("report");
            fileConfig.setFileExtension("xml");

            fileConfig.validate();
            List<WatchrConfigError> errors = testLogger.getErrors();
            assertEquals(0, errors.size());
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testValidate_MissingDirectory() {
        FileConfig fileConfig = new FileConfig("");
        fileConfig.setFileNamePattern("report");
        fileConfig.setFileExtension("xml");

        fileConfig.validate();
        List<WatchrConfigError> errors = testLogger.getErrors();
        assertEquals(1, errors.size());
        assertEquals(ErrorLevel.ERROR, errors.get(0).getLevel());
        assertEquals("A directory for parseable reports was not provided.", errors.get(0).getMessage());
    } 

    @Test
    public void testValidate_NonexistentDirectory() {
        try {
            File startDir = Files.createTempDirectory(null).toFile();
            FileConfig fileConfig = new FileConfig(startDir, "");
            startDir.delete();
            fileConfig.setFileNamePattern("report");
            fileConfig.setFileExtension("xml");

            fileConfig.validate();
            List<WatchrConfigError> errors = testLogger.getErrors();
            assertEquals(1, errors.size());
            assertEquals(ErrorLevel.ERROR, errors.get(0).getLevel());
            assertTrue(errors.get(0).getMessage().endsWith(" does not exist."));
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testValidate_BlankFileExtension() {
        try {
            File startDir = Files.createTempDirectory(null).toFile();
            FileConfig fileConfig = new FileConfig(startDir, "");
            fileConfig.setFileNamePattern("report");

            fileConfig.validate();
            List<WatchrConfigError> errors = testLogger.getErrors();
            assertEquals(1, errors.size());
            assertEquals(ErrorLevel.WARNING, errors.get(0).getLevel());
            assertEquals("No file type extension was provided.  It is strongly recommend that at least one file extension " +
                         "is specified so Watchr knows how to parse your report files.", errors.get(0).getMessage());
        } catch(IOException e) {
            fail(e.getMessage());
        }
    } 

    @Test
    public void testValidate_BlankFilePattern() {
        try {
            File startDir = Files.createTempDirectory(null).toFile();
            FileConfig fileConfig = new FileConfig(startDir, "");
            fileConfig.setFileExtension("xml");

            fileConfig.validate();
            List<WatchrConfigError> errors = testLogger.getErrors();
            assertEquals(1, errors.size());
            assertEquals(ErrorLevel.ERROR, errors.get(0).getLevel());
            assertEquals("Pattern for finding report files cannot be blank!", errors.get(0).getMessage());
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCopyAndEquals() {
        try {
            File startDir = Files.createTempDirectory(null).toFile();
            FileConfig fileConfig = new FileConfig(startDir, "");
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
            File startDir = Files.createTempDirectory(null).toFile();
            FileConfig fileConfig = new FileConfig(startDir, "");
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
            File startDir = Files.createTempDirectory(null).toFile();
            FileConfig fileConfig = new FileConfig(startDir, "");
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
            File startDir = Files.createTempDirectory(null).toFile();
            FileConfig fileConfig = new FileConfig(startDir, "/my/path/prefix");
            fileConfig.setFileExtension("xml");
            fileConfig.setFileNamePattern("pattern");
            fileConfig.setIgnoreOldFiles(true);
            fileConfig.setRecurseDirectories(true);

            File startDir2 = Files.createTempDirectory(null).toFile();
            FileConfig fileConfig2 = new FileConfig(startDir2, "/my/path/prefix");
            fileConfig2.setFileExtension("json");
            fileConfig2.setFileNamePattern("pattern2");
            fileConfig2.setIgnoreOldFiles(false);
            fileConfig2.setRecurseDirectories(false);
            
            List<WatchrDiff<?>> diffs = fileConfig.diff(fileConfig2);
            assertEquals(5, diffs.size());

            WatchrDiff<?> diff1 = diffs.get(0);
            assertEquals(DiffCategory.START_DIR, diff1.getProperty());
            assertEquals("/my/path/prefix/fileConfig", diff1.getPath());
            assertEquals(startDir, diff1.getBeforeValue());
            assertEquals(startDir2, diff1.getNowValue());

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
