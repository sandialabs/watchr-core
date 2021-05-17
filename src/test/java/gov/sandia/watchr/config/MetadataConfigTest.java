package gov.sandia.watchr.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.TestLogger;
import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;

public class MetadataConfigTest {

    private TestLogger testLogger;

    @Before
    public void setup() {
        testLogger = new TestLogger();
        WatchrCoreApp.getInstance().setLogger(testLogger);
    }    

    @Test
    public void testValidate_HappyPath() {
        FileConfig fileConfig = new FileConfig("");

        MetadataConfig metadataConfig = new MetadataConfig(fileConfig, "");
        metadataConfig.setName("name");
        metadataConfig.setLink("link");

        metadataConfig.validate();
        List<WatchrConfigError> errors = testLogger.getErrors();
        assertEquals(0, errors.size());
    }

    @Test
    public void testCopyAndEquals() {
        FileConfig fileConfig = new FileConfig("");
        MetadataConfig metadataConfig = new MetadataConfig(fileConfig, "");
        metadataConfig.setName("name");
        metadataConfig.setLink("link");

        MetadataConfig copy = new MetadataConfig(metadataConfig);
        assertEquals(metadataConfig, copy);
    }

    @Test
    public void testCopyAndNotEquals() {
        FileConfig fileConfig = new FileConfig("");
        MetadataConfig metadataConfig = new MetadataConfig(fileConfig, "");
        metadataConfig.setName("name");
        metadataConfig.setLink("link");
        
        MetadataConfig copy = new MetadataConfig(metadataConfig);
        copy.setName("name2");
        assertNotEquals(metadataConfig, copy);
    }    

    @Test
    public void testCopyAndHashCode() {
        FileConfig fileConfig = new FileConfig("");
        MetadataConfig metadataConfig = new MetadataConfig(fileConfig, "");
        metadataConfig.setName("name");
        metadataConfig.setLink("link");

        MetadataConfig copy = new MetadataConfig(metadataConfig);
        assertEquals(metadataConfig.hashCode(), copy.hashCode());
    }

    @Test
    public void testDiffs() {
        FileConfig fileConfig = new FileConfig("");
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
