package gov.sandia.watchr.util;

import static org.junit.Assert.assertEquals;
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

    @Test
    public void testConvertToRegex_SimpleAsterisks() {
        assertEquals("a", StringUtil.convertToRegex("a"));
        assertEquals(".*a", StringUtil.convertToRegex("*a"));
        assertEquals("a.*", StringUtil.convertToRegex("a*"));
        assertEquals(".*a.*", StringUtil.convertToRegex("*a*"));
    }

    @Test
    public void testConvertToRegex_Malformed() {
        assertEquals("^a", StringUtil.convertToRegex("^a"));
        assertEquals("a$", StringUtil.convertToRegex("a$"));
    }

    @Test
    public void testConvertToRegex_Exact() {
        assertEquals("^a.*b[0-9]$", StringUtil.convertToRegex("R$^a.*b[0-9]$"));
    }

    @Test
    public void testEscapeRegexCharacters() {
        assertEquals("HelloWorld", StringUtil.escapeRegexCharacters("HelloWorld"));
        assertEquals("\\(HelloWorld\\)", StringUtil.escapeRegexCharacters("(HelloWorld)"));
        assertEquals("\\s\\(HelloWorld\\)\\s", StringUtil.escapeRegexCharacters(" (HelloWorld) "));
        assertEquals("\\^\\{HelloWorld\\}\\^", StringUtil.escapeRegexCharacters("^{HelloWorld}^"));
    }
}
