package gov.sandia.watchr.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.log.StringOutputLogger;

public class MetadataConfigTest {

    private StringOutputLogger testLogger;
    private IFileReader fileReader;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
        fileReader = new DefaultFileReader(testLogger);
    }

    @Test
    public void testValidate_HappyPath() {
        FileConfig fileConfig = new FileConfig("", testLogger, fileReader);

        MetadataConfig metadataConfig = new MetadataConfig(fileConfig, "");
        metadataConfig.setName("name");
        metadataConfig.setLink("link");

        metadataConfig.validate();
        List<String> errors = testLogger.getLog();
        assertEquals(0, errors.size());
    }

    @Test
    public void testCopyAndEquals() {
        FileConfig fileConfig = new FileConfig("", testLogger, fileReader);
        MetadataConfig metadataConfig = new MetadataConfig(fileConfig, "");
        metadataConfig.setName("name");
        metadataConfig.setLink("link");

        MetadataConfig copy = new MetadataConfig(metadataConfig);
        assertEquals(metadataConfig, copy);
    }

    @Test
    public void testCopyAndNotEquals() {
        FileConfig fileConfig = new FileConfig("", testLogger, fileReader);
        MetadataConfig metadataConfig = new MetadataConfig(fileConfig, "");
        metadataConfig.setName("name");
        metadataConfig.setLink("link");
        
        MetadataConfig copy = new MetadataConfig(metadataConfig);
        copy.setName("name2");
        assertNotEquals(metadataConfig, copy);
    }    

    @Test
    public void testCopyAndHashCode() {
        FileConfig fileConfig = new FileConfig("", testLogger, fileReader);
        MetadataConfig metadataConfig = new MetadataConfig(fileConfig, "");
        metadataConfig.setName("name");
        metadataConfig.setLink("link");

        MetadataConfig copy = new MetadataConfig(metadataConfig);
        assertEquals(metadataConfig.hashCode(), copy.hashCode());
    }

    @Test
    public void testDiffs() {
        FileConfig fileConfig = new FileConfig("", testLogger, fileReader);
        MetadataConfig metadataConfig = new MetadataConfig(fileConfig, "/my/path/prefix");
        metadataConfig.setName("name");
        metadataConfig.setLink("link");
        
        MetadataConfig metadataConfig2 = new MetadataConfig(metadataConfig);
        metadataConfig2.setName("name2");
        
        List<WatchrDiff<?>> diffs = metadataConfig.diff(metadataConfig2);
        assertEquals(1, diffs.size());

        WatchrDiff<?> diff1 = diffs.get(0);
        assertEquals(DiffCategory.NAME, diff1.getProperty());
        assertEquals("/my/path/prefix/metadataConfig", diff1.getPath());
        assertEquals("name", diff1.getBeforeValue());
        assertEquals("name2", diff1.getNowValue());
    }
}
