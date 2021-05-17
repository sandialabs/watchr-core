package gov.sandia.watchr.util;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Elliott Ridgway
 */
public class PathUtilTest {
    @Test
    public void testRemoveLeadingSegments() {
        String result = PathUtil.removeLeadingSegments("/A/B/C/D", "/", 1);
        assertEquals("A/B/C/D", result);
        result = PathUtil.removeLeadingSegments("/A/B/C/D", "/", 2);
        assertEquals("B/C/D", result);
        result = PathUtil.removeLeadingSegments("/A/B/C/D", "/", 3);
        assertEquals("C/D", result);
        result = PathUtil.removeLeadingSegments("/A/B/C/D", "/", 4);
        assertEquals("D", result);
    }
}
