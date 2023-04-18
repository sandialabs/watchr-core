package gov.sandia.watchr.parse.generators.line.extractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

public class ExtractionResultIndexParserTest {
    
    private ExtractionResultIndexParser rangeParser;

    @Before
    public void setup() {
        rangeParser = new ExtractionResultIndexParser();
    }

    @Test
    public void testIsIndexSyntax() {
        assertFalse(rangeParser.isIndexSyntax("A"));
        assertFalse(rangeParser.isIndexSyntax("{A"));
        assertFalse(rangeParser.isIndexSyntax("A}"));
        assertFalse(rangeParser.isIndexSyntax("{A}"));
        assertTrue(rangeParser.isIndexSyntax("{1}"));
        assertTrue(rangeParser.isIndexSyntax("{100}"));
        assertFalse(rangeParser.isIndexSyntax("{1.0}"));
        assertFalse(rangeParser.isIndexSyntax("{{1}}"));
    }

    @Test
    public void testGetIndexFromIndexSyntax() {
        assertNull(rangeParser.getIndexFromIndexSyntax("{A}"));
        assertEquals(Integer.valueOf(1), rangeParser.getIndexFromIndexSyntax("{1}"));
        assertEquals(Integer.valueOf(100), rangeParser.getIndexFromIndexSyntax("{100}"));
        assertNull(rangeParser.getIndexFromIndexSyntax("{1.0}"));
        assertNull(rangeParser.getIndexFromIndexSyntax("{N}"));
    }

    @Test
    public void testIsIndexRangeSyntax() {
        assertFalse(rangeParser.isIndexRangeSyntax("A"));
        assertFalse(rangeParser.isIndexRangeSyntax("{A"));
        assertFalse(rangeParser.isIndexRangeSyntax("A}"));
        assertFalse(rangeParser.isIndexRangeSyntax("{A}"));
        assertFalse(rangeParser.isIndexRangeSyntax("{1}"));
        assertFalse(rangeParser.isIndexRangeSyntax("{100}"));
        assertFalse(rangeParser.isIndexRangeSyntax("{1.0}"));
        assertFalse(rangeParser.isIndexRangeSyntax("{{1}}"));
        assertFalse(rangeParser.isIndexRangeSyntax("{A-}"));
        assertFalse(rangeParser.isIndexRangeSyntax("{-B}"));
        
        assertTrue(rangeParser.isIndexRangeSyntax("{0-1}"));
        assertTrue(rangeParser.isIndexRangeSyntax("{1-100}"));
        assertTrue(rangeParser.isIndexRangeSyntax("{3-2}"));
        assertTrue(rangeParser.isIndexRangeSyntax("{1-N}"));
        assertTrue(rangeParser.isIndexRangeSyntax("{N-1}"));
    }    

    @Test
    public void testGetRangeFromIndexRangeSyntax() {
        Pair<Integer,Integer> pair = new ImmutablePair<>(0,1);
        assertEquals(pair, rangeParser.getRangeFromIndexRangeSyntax("{0-1}", 3));

        pair = new ImmutablePair<>(1,100);
        assertEquals(pair, rangeParser.getRangeFromIndexRangeSyntax("{1-100}", 101));

        pair = new ImmutablePair<>(0,100);
        assertEquals(pair, rangeParser.getRangeFromIndexRangeSyntax("{0-N}", 100));

        assertNull(rangeParser.getRangeFromIndexRangeSyntax("{3-2}", 4));
        assertNull(rangeParser.getRangeFromIndexRangeSyntax("{N-1}", 4));
    }
}
