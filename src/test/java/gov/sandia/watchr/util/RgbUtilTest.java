package gov.sandia.watchr.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RgbUtilTest {
  
    @Test
    public void testParseColor_Normal() {
        RGB rgb = RgbUtil.parseColor("255, 77, 1");
        assertNotNull(rgb);
        assertEquals(255, rgb.red);
        assertEquals(77, rgb.green);
        assertEquals(1, rgb.blue);
    }

    @Test
    public void testParseColor_Bad() {
        RGB rgb = RgbUtil.parseColor("255 77 1");
        assertNull(rgb);
    }

    @Test
    public void testParseColor_Random() {
        RGB rgb = RgbUtil.parseColor("random");
        assertNotNull(rgb);
        assertTrue(rgb.red <= 200);
        assertTrue(rgb.green <= 200);
        assertTrue(rgb.blue <= 200);
    }
}
