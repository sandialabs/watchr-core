package gov.sandia.watchr.parse.generators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import gov.sandia.watchr.config.FileFilterConfig;

public class PlotsConfigGeneratorTest {

    @Test
    public void testDoesFilePassNameFilter() {
        PlotsConfigGenerator generator = new PlotsConfigGenerator(null, null);
        FileFilterConfig fileFilterConfig = new FileFilterConfig("");

        fileFilterConfig.setNamePattern("*");
        assertTrue(generator.doesFilePassNameFilter("MyFile.json", fileFilterConfig));

        fileFilterConfig.setNamePattern("*blah*");
        assertFalse(generator.doesFilePassNameFilter("MyFile.json", fileFilterConfig));
        assertTrue(generator.doesFilePassNameFilter("MyFile-blah.json", fileFilterConfig));

        fileFilterConfig.setNamePattern("blah");
        assertFalse(generator.doesFilePassNameFilter("MyFile-blah.json", fileFilterConfig));
        assertTrue(generator.doesFilePassNameFilter("blah", fileFilterConfig));
    }
}
