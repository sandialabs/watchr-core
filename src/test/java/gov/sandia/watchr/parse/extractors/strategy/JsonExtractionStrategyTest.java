package gov.sandia.watchr.parse.extractors.strategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.extractors.ExtractionResult;

public class JsonExtractionStrategyTest {
    
    ////////////
    // FIELDS //
    ////////////

    private JsonExtractionStrategy jsonExtractor;
    private File jsonReportFile;
    private StringOutputLogger testLogger;
    private IFileReader fileReader;

    ///////////
    // SETUP //
    ///////////

    @Before
    public void setup() {
        try {            
            ClassLoader classLoader = JsonExtractionStrategyTest.class.getClassLoader();
            URL jsonReportUrl = classLoader.getResource("system_tests/reports/json_reports_basic/basic_report.json");
            jsonReportFile = new File(jsonReportUrl.toURI());

            testLogger = new StringOutputLogger();
            fileReader = new DefaultFileReader(testLogger);
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
            jsonExtractor = new JsonExtractionStrategy(properties, strategy, testLogger, fileReader);

            List<ExtractionResult> results = jsonExtractor.extract(jsonReportFile.getAbsolutePath());
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
            jsonExtractor = new JsonExtractionStrategy(properties, strategy, testLogger, fileReader);

            List<ExtractionResult> results = jsonExtractor.extract(jsonReportFile.getAbsolutePath());
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
            jsonExtractor = new JsonExtractionStrategy(properties, strategy, testLogger, fileReader);

            List<ExtractionResult> results = jsonExtractor.extract(jsonReportFile.getAbsolutePath());
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
            jsonExtractor = new JsonExtractionStrategy(properties, strategy, testLogger, fileReader);

            List<ExtractionResult> results = jsonExtractor.extract(jsonReportFile.getAbsolutePath());
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
            jsonExtractor = new JsonExtractionStrategy(properties, strategy, testLogger, fileReader);

            List<ExtractionResult> results = jsonExtractor.extract(jsonReportFile.getAbsolutePath());
            assertEquals(2, results.size());
            assertEquals("overRanksVirtualMemory", results.get(0).getValue());
            assertEquals("overRanksResidentMemory", results.get(1).getValue());
        } catch (WatchrParseException e1) {
            e1.getOriginalException().printStackTrace();
            fail(e1.getOriginalException().getMessage());
        } 
    }

    @Test
    public void testDataWithHoles_SkipFailedRetrievals() {
        File bigJsonFile = null;
        try {            
            ClassLoader classLoader = JsonExtractionStrategyTest.class.getClassLoader();
            URL bigJsonUrl = classLoader.getResource("system_tests/reports/json_reports_basic/basic_report_2.json");
            bigJsonFile = new File(bigJsonUrl.toURI());
        } catch(URISyntaxException e) {
            fail(e.getMessage());
        } 

        if(bigJsonFile != null) {
            try {
                AmbiguityStrategy strategy = new AmbiguityStrategy("");
                strategy.setShouldGetFirstMatchOnly(false);

                Map<String, String> properties = new HashMap<>();
                properties.put(Keywords.GET_PATH, "*/metrics/{1}");
                properties.put(Keywords.GET_KEY, "value");
                jsonExtractor = new JsonExtractionStrategy(properties, strategy, testLogger, fileReader);

                List<ExtractionResult> results = jsonExtractor.extract(bigJsonFile.getAbsolutePath());
                assertEquals(5, results.size());
                assertEquals("0.03536856681926057", results.get(0).getValue());
                assertEquals("-0.11097154263042563", results.get(1).getValue());
                assertEquals("0.016754639323649404", results.get(2).getValue());
                assertEquals("0.20721664135315865", results.get(3).getValue());
                assertEquals("0.12458985040458417", results.get(4).getValue());
            } catch (WatchrParseException e1) {
                e1.getOriginalException().printStackTrace();
                fail(e1.getOriginalException().getMessage());
            }         
        } else {
            fail("File was not loaded.");
        }
    }

    @Test
    public void testDataWithHoles_PreserveFullIterationList() {
        File bigJsonFile = null;
        try {            
            ClassLoader classLoader = JsonExtractionStrategyTest.class.getClassLoader();
            URL bigJsonUrl = classLoader.getResource("system_tests/reports/json_reports_basic/basic_report_2.json");
            bigJsonFile = new File(bigJsonUrl.toURI());
        } catch(URISyntaxException e) {
            fail(e.getMessage());
        } 

        if(bigJsonFile != null) {
            try {
                AmbiguityStrategy strategy = new AmbiguityStrategy("");
                strategy.setShouldGetFirstMatchOnly(false);
                strategy.setIterateWithOtherExtractor("x");

                Map<String, String> properties = new HashMap<>();
                properties.put(Keywords.GET_PATH, "*/metrics/{1}");
                properties.put(Keywords.GET_KEY, "value");
                jsonExtractor = new JsonExtractionStrategy(properties, strategy, testLogger, fileReader);

                List<ExtractionResult> results = jsonExtractor.extract(bigJsonFile.getAbsolutePath());
                assertEquals(7, results.size());
                assertEquals("0.03536856681926057", results.get(0).getValue());
                assertEquals("-0.11097154263042563", results.get(1).getValue());
                assertEquals("0.016754639323649404", results.get(2).getValue());
                assertNull(results.get(3));
                assertNull(results.get(4));
                assertEquals("0.20721664135315865", results.get(5).getValue());
                assertEquals("0.12458985040458417", results.get(6).getValue());
            } catch (WatchrParseException e1) {
                e1.getOriginalException().printStackTrace();
                fail(e1.getOriginalException().getMessage());
            }         
        } else {
            fail("File was not loaded.");
        }
    }

    @Test
    public void testRangeSyntax() {
        File bigJsonFile = null;
        try {            
            ClassLoader classLoader = JsonExtractionStrategyTest.class.getClassLoader();
            URL bigJsonUrl = classLoader.getResource("system_tests/reports/json_reports_basic/basic_report_2.json");
            bigJsonFile = new File(bigJsonUrl.toURI());
        } catch(URISyntaxException e) {
            fail(e.getMessage());
        } 

        if(bigJsonFile != null) {
            try {
                AmbiguityStrategy strategy = new AmbiguityStrategy("");
                strategy.setShouldGetFirstMatchOnly(true);

                Map<String, String> properties = new HashMap<>();
                properties.put(Keywords.GET_PATH, "*/metrics/{1-4}");
                properties.put(Keywords.GET_KEY, "value");
                jsonExtractor = new JsonExtractionStrategy(properties, strategy, testLogger, fileReader);

                List<ExtractionResult> results = jsonExtractor.extract(bigJsonFile.getAbsolutePath());
                assertEquals(4, results.size());
                assertEquals("0.03536856681926057", results.get(0).getValue());
                assertEquals("1.0944087267160736e-10", results.get(1).getValue());
                assertEquals("0.04451590518611598", results.get(2).getValue());
                assertEquals("1.3681275329881638e-10", results.get(3).getValue());
            } catch (WatchrParseException e1) {
                e1.getOriginalException().printStackTrace();
                fail(e1.getOriginalException().getMessage());
            }         
        } else {
            fail("File was not loaded.");
        }
    }

    @Test
    public void testRangeNSyntax() {
        File bigJsonFile = null;
        try {            
            ClassLoader classLoader = JsonExtractionStrategyTest.class.getClassLoader();
            URL bigJsonUrl = classLoader.getResource("system_tests/reports/json_reports_basic/basic_report_2.json");
            bigJsonFile = new File(bigJsonUrl.toURI());
        } catch(URISyntaxException e) {
            fail(e.getMessage());
        } 

        if(bigJsonFile != null) {
            try {
                AmbiguityStrategy strategy = new AmbiguityStrategy("");
                strategy.setShouldGetFirstMatchOnly(true);

                Map<String, String> properties = new HashMap<>();
                properties.put(Keywords.GET_PATH, "*/metrics/{1-N}");
                properties.put(Keywords.GET_KEY, "value");
                jsonExtractor = new JsonExtractionStrategy(properties, strategy, testLogger, fileReader);

                List<ExtractionResult> results = jsonExtractor.extract(bigJsonFile.getAbsolutePath());
                assertEquals(4, results.size());
                assertEquals("0.03536856681926057", results.get(0).getValue());
                assertEquals("1.0944087267160736e-10", results.get(1).getValue());
                assertEquals("0.04451590518611598", results.get(2).getValue());
                assertEquals("1.3681275329881638e-10", results.get(3).getValue());
            } catch (WatchrParseException e1) {
                e1.getOriginalException().printStackTrace();
                fail(e1.getOriginalException().getMessage());
            }         
        } else {
            fail("File was not loaded.");
        }
    }    
}
