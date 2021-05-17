package gov.sandia.watchr.parse.extractors.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.extractors.ExtractionResult;

public class JsonExtractionStrategyTest {
    
    ////////////
    // FIELDS //
    ////////////

    private JsonExtractionStrategy jsonExtractor;
    private File jsonReportFile;

    ///////////
    // SETUP //
    ///////////

    @Before
    public void setup() {
        try {            
            ClassLoader classLoader = JsonExtractionStrategyTest.class.getClassLoader();
            URL jsonReportUrl = classLoader.getResource("system_tests/reports/json_reports_basic/basic_report.json");
            jsonReportFile = new File(jsonReportUrl.toURI());
        } catch(URISyntaxException e) {
            fail(e.getMessage());
        } 
    }

    ///////////
    // TESTS //
    ///////////

    @Test
    public void testGetPropertyValue_OneLevel() {
        try {
            AmbiguityStrategy strategy = new AmbiguityStrategy("");
            Map<String, String> properties = new HashMap<>();
            properties.put(Keywords.GET_PATH, "performanceReport");
            properties.put(Keywords.GET_KEY, "name");
            jsonExtractor = new JsonExtractionStrategy(properties, strategy);

            List<ExtractionResult> results = jsonExtractor.extract(jsonReportFile);
            assertEquals(1, results.size());
            assertEquals("my_test_performance_report", results.get(0).getValue());
        } catch (WatchrParseException e1) {
            e1.getOriginalException().printStackTrace();
            fail(e1.getOriginalException().getMessage());
        } 
    }

    @Test
    public void testGetPropertyValue_TwoLevels() {
        try {
            AmbiguityStrategy strategy = new AmbiguityStrategy("");
            Map<String, String> properties = new HashMap<>();
            properties.put(Keywords.GET_PATH, "performanceReport/metadata");
            properties.put(Keywords.GET_KEY, "abc");
            jsonExtractor = new JsonExtractionStrategy(properties, strategy);

            List<ExtractionResult> results = jsonExtractor.extract(jsonReportFile);
            assertEquals(1, results.size());
            assertEquals("def", results.get(0).getValue());
        } catch (WatchrParseException e1) {
            e1.getOriginalException().printStackTrace();
            fail(e1.getOriginalException().getMessage());
        } 
    }

    @Test
    public void testGetPropertyValue_ArrayElement() {
        try {
            AmbiguityStrategy strategy = new AmbiguityStrategy("");
            Map<String, String> properties = new HashMap<>();
            properties.put(Keywords.GET_PATH, "performanceReport/timings/{0}");
            properties.put(Keywords.GET_KEY, "name");
            jsonExtractor = new JsonExtractionStrategy(properties, strategy);

            List<ExtractionResult> results = jsonExtractor.extract(jsonReportFile);
            assertEquals(1, results.size());
            assertEquals("obliquesphereimpact", results.get(0).getValue());
        } catch (WatchrParseException e1) {
            e1.getOriginalException().printStackTrace();
            fail(e1.getOriginalException().getMessage());
        } 
    }

    @Test
    public void testGetPropertyValue_ArrayElement_Nested() {
        try {
            AmbiguityStrategy strategy = new AmbiguityStrategy("");
            Map<String, String> properties = new HashMap<>();
            properties.put(Keywords.GET_PATH, "performanceReport/timings/{0}/metrics/{1}");
            properties.put(Keywords.GET_KEY, "units");
            jsonExtractor = new JsonExtractionStrategy(properties, strategy);

            List<ExtractionResult> results = jsonExtractor.extract(jsonReportFile);
            assertEquals(1, results.size());
            assertEquals("MB", results.get(0).getValue());
        } catch (WatchrParseException e1) {
            e1.getOriginalException().printStackTrace();
            fail(e1.getOriginalException().getMessage());
        } 
    }

    @Test
    public void testGetAllArrayPropertyValues() {
        try {
            AmbiguityStrategy strategy = new AmbiguityStrategy("");
            strategy.setShouldGetFirstMatchOnly(false);

            Map<String, String> properties = new HashMap<>();
            properties.put(Keywords.GET_PATH, "performanceReport/timings/{0}/metrics/*");
            properties.put(Keywords.GET_KEY, "name");
            jsonExtractor = new JsonExtractionStrategy(properties, strategy);

            List<ExtractionResult> results = jsonExtractor.extract(jsonReportFile);
            assertEquals(2, results.size());
            assertEquals("overRanksVirtualMemory", results.get(0).getValue());
            assertEquals("overRanksResidentMemory", results.get(1).getValue());
        } catch (WatchrParseException e1) {
            e1.getOriginalException().printStackTrace();
            fail(e1.getOriginalException().getMessage());
        } 
    } 
}
