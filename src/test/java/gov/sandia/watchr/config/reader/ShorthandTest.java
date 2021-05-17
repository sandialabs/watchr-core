package gov.sandia.watchr.config.reader;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ShorthandTest {
    
    @Test
    public void testShorthand() {
        Shorthand shorthand = new Shorthand("x/path");
        assertEquals("x", shorthand.getAxis());
        assertEquals("path", shorthand.getGroupingField());
    }
}
