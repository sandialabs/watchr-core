package gov.sandia.watchr.parse.extractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class ExtractionResultTest {
    
    private ExtractionResult extractionResult;

    @Test
    public void testExtractionResult_Getters() {
        extractionResult = new ExtractionResult(null, "path", "key", "value");
        assertEquals("path", extractionResult.getPath());
        assertEquals("key", extractionResult.getKey());
        assertEquals("value", extractionResult.getValue());
    }

    @Test
    public void testEquals() {
        extractionResult = new ExtractionResult(null, "path", "key", "value");
        ExtractionResult extractionResult2 = new ExtractionResult(null, "path", "key", "value");
        assertEquals(extractionResult, extractionResult2);
    }

    @Test
    public void testNotEquals() {
        extractionResult = new ExtractionResult(null, "path", "key", "value");
        ExtractionResult extractionResult2 = new ExtractionResult(null, "path2", "key2", "value2");
        assertNotEquals(extractionResult, extractionResult2);
    }

    @Test
    public void testHashCode() {
        extractionResult = new ExtractionResult(null, "path", "key", "value");
        ExtractionResult extractionResult2 = new ExtractionResult(null, "path", "key", "value");
        assertEquals(extractionResult.hashCode(), extractionResult2.hashCode());
    }
    
    @Test
    public void testPrettyPrint() {
        extractionResult = new ExtractionResult(null, "path", "key", "value");
        assertEquals("[path, key=key, value=value]\n[Children:\n]\n", extractionResult.toString());
    } 
}
