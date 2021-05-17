package gov.sandia.watchr.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RGBTest {
    
    @Test
    public void testToString() {
        RGB color = new RGB(255, 128, 0);
        assertEquals("(255,128,0)", color.toString());
    }
}
