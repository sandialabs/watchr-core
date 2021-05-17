package gov.sandia.watchr.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringUtilTest {
    
    @Test
    public void testHasIllegalCharacters() {
        assertFalse(StringUtil.hasIllegalCharacters("ABC"));
        assertFalse(StringUtil.hasIllegalCharacters("123"));
        assertFalse(StringUtil.hasIllegalCharacters("A B C"));
        assertTrue(StringUtil.hasIllegalCharacters("!@#$%"));
        assertTrue(StringUtil.hasIllegalCharacters("! @ # $ %"));
        assertFalse(StringUtil.hasIllegalCharacters("A-B-C"));
        assertFalse(StringUtil.hasIllegalCharacters("A_B_C"));
    }
}
