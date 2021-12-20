package gov.sandia.watchr.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.extractors.ExtractionResult;
import gov.sandia.watchr.parse.extractors.strategy.AmbiguityStrategy;

public class HierarchicalExtractorTest {
    
    private ILogger logger;
    private IFileReader fileReader;

    @Before
    public void setup() {
        logger = new StringOutputLogger();
        fileReader = new DefaultFileReader(logger);
    }

    @Test
    public void testExtract_TopLevel() {
        try {
            ClassLoader classLoader = HierarchicalExtractorTest.class.getClassLoader();
            URL reportXmlDirUrl = classLoader.getResource("system_tests/reports/xml_reports_basic");
            File reportXmlDir = new File(reportXmlDirUrl.toURI());
            FileConfig fileConfig = new FileConfig(reportXmlDir.getAbsolutePath(), "", logger, fileReader);
            fileConfig.setFileNamePattern("basic_report_2");
            fileConfig.setFileExtension("xml");
            HierarchicalExtractor extractor = new HierarchicalExtractor(fileConfig, "");

            extractor.setPath("nightly_run_2018_01_08");
            extractor.setProperty(Keywords.GET_PATH_ATTRIBUTE, "name");
            extractor.setProperty(Keywords.GET_ELEMENT, "performance-report");
            extractor.setKey("date");

            File basicReport2 = new File(reportXmlDir, "basic_report_2.xml");
            List<ExtractionResult> results = extractor.extract(basicReport2.getAbsolutePath());
            ExtractionResult result = results.get(0);
            assertEquals("/nightly_run_2018_01_08", result.getPath());
            assertEquals("date", result.getKey());
            assertEquals("2018-01-08T21:59:59", result.getValue());

        } catch(WatchrParseException e) {
            e.getOriginalException().printStackTrace();
            fail(e.getOriginalException().getMessage());
        } catch(URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testExtract_SingleNumberElement() {
        try {
            ClassLoader classLoader = HierarchicalExtractorTest.class.getClassLoader();
            URL reportXmlDirUrl = classLoader.getResource("system_tests/reports/xml_reports_basic");
            File reportXmlDir = new File(reportXmlDirUrl.toURI());
            FileConfig fileConfig = new FileConfig(reportXmlDir.getAbsolutePath(), "", logger, fileReader);
            fileConfig.setFileNamePattern("basic_report_2");
            fileConfig.setFileExtension("xml");
            HierarchicalExtractor extractor = new HierarchicalExtractor(fileConfig, "");

            extractor.setPath("nightly_run_2018_01_08/TestTiming/Test");
            extractor.setProperty(Keywords.GET_PATH_ATTRIBUTE, "name");
            extractor.setProperty(Keywords.GET_ELEMENT, "performance-report|timing");
            extractor.setKey("cpu-time-max");

            AmbiguityStrategy strategy = new AmbiguityStrategy("");
            strategy.setShouldGetFirstMatchOnly(false);
            extractor.setAmbiguityStrategy(strategy);

            File basicReport2 = new File(reportXmlDir, "basic_report_2.xml");
            List<ExtractionResult> results = extractor.extract(basicReport2.getAbsolutePath());
            ExtractionResult result = results.get(0);
            assertEquals("/nightly_run_2018_01_08/TestTiming/Test", result.getPath());
            assertEquals("cpu-time-max", result.getKey());
            assertEquals("100.286", result.getValue());

        } catch(WatchrParseException e) {
            fail(e.getOriginalException().getMessage());
        } catch(URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testExtract_WithWildcardInPath() {
        try {
            ClassLoader classLoader = HierarchicalExtractorTest.class.getClassLoader();
            URL reportXmlDirUrl = classLoader.getResource("system_tests/reports/xml_reports_basic");
            File reportXmlDir = new File(reportXmlDirUrl.toURI());
            FileConfig fileConfig = new FileConfig(reportXmlDir.getAbsolutePath(), "", logger, fileReader);
            fileConfig.setFileNamePattern("basic_report_2");
            fileConfig.setFileExtension("xml");
            HierarchicalExtractor extractor = new HierarchicalExtractor(fileConfig, "");

            extractor.setPath("nightly_run_2018_01_08/*/Test");
            extractor.setProperty(Keywords.GET_PATH_ATTRIBUTE, "name");
            extractor.setProperty(Keywords.GET_ELEMENT, "performance-report|timing");
            extractor.setKey("cpu-time-max");

            AmbiguityStrategy strategy = new AmbiguityStrategy("");
            strategy.setShouldGetFirstMatchOnly(false);
            extractor.setAmbiguityStrategy(strategy);

            File basicReport2 = new File(reportXmlDir, "basic_report_2.xml");
            List<ExtractionResult> results = extractor.extract(basicReport2.getAbsolutePath());
            ExtractionResult result = results.get(0);
            assertEquals("/nightly_run_2018_01_08/TestTiming/Test", result.getPath());
            assertEquals("cpu-time-max", result.getKey());
            assertEquals("100.286", result.getValue());

        } catch(WatchrParseException e) {
            fail(e.getOriginalException().getMessage());
        } catch(URISyntaxException e) {
            fail(e.getMessage());
        }
    } 

    @Test
    public void testExtract_SingleStringElement() {
        try {
            ClassLoader classLoader = HierarchicalExtractorTest.class.getClassLoader();
            URL reportXmlDirUrl = classLoader.getResource("system_tests/reports/xml_reports_basic");
            File reportXmlDir = new File(reportXmlDirUrl.toURI());
            FileConfig fileConfig = new FileConfig(reportXmlDir.getAbsolutePath(), "", logger, fileReader);
            fileConfig.setFileNamePattern("basic_report_2");
            fileConfig.setFileExtension("xml");
            HierarchicalExtractor extractor = new HierarchicalExtractor(fileConfig, "");

            extractor.setPath("nightly_run_2018_01_08/TestTiming/*");
            extractor.setProperty(Keywords.GET_PATH_ATTRIBUTE, "name|key");
            extractor.setProperty(Keywords.GET_ELEMENT, "performance-report|timing|metadata");
            extractor.setKey("value");

            File basicReport2 = new File(reportXmlDir, "basic_report_2.xml");
            List<ExtractionResult> results = extractor.extract(basicReport2.getAbsolutePath());
            ExtractionResult result = results.get(0);
            assertEquals("/nightly_run_2018_01_08/TestTiming/codeBase", result.getPath());
            assertEquals("value", result.getKey());
            assertEquals("Aria", result.getValue());
        } catch(WatchrParseException e) {
            fail(e.getOriginalException().getMessage());
        } catch(URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testExtract_GoToBottomAndGetFirstOne() {
        try {
            ClassLoader classLoader = HierarchicalExtractorTest.class.getClassLoader();
            URL reportXmlDirUrl = classLoader.getResource("system_tests/reports/xml_reports_basic");
            File reportXmlDir = new File(reportXmlDirUrl.toURI());
            FileConfig fileConfig = new FileConfig(reportXmlDir.getAbsolutePath(), "", logger, fileReader);
            fileConfig.setFileNamePattern("basic_report_2");
            fileConfig.setFileExtension("xml");
            HierarchicalExtractor extractor = new HierarchicalExtractor(fileConfig, "");

            extractor.setPath("nightly_run*/*/*/*/*/Mesh output");
            extractor.setProperty(Keywords.GET_PATH_ATTRIBUTE, "name");
            extractor.setProperty(Keywords.GET_ELEMENT, "performance-report|timing");
            extractor.setKey("cpu-time-max");

            AmbiguityStrategy strategy = new AmbiguityStrategy("");
            strategy.setShouldGetFirstMatchOnly(false);
            extractor.setAmbiguityStrategy(strategy);

            File basicReport2 = new File(reportXmlDir, "basic_report_2.xml");
            List<ExtractionResult> results = extractor.extract(basicReport2.getAbsolutePath());
            ExtractionResult result = results.get(0);
            assertEquals("/nightly_run_2018_01_08/TestTiming/Test/Domain/Procedure myProcedure/Mesh output", result.getPath());
            assertEquals("cpu-time-max", result.getKey());
            assertEquals("1.15", result.getValue());
        } catch(WatchrParseException e) {
            fail(e.getOriginalException().getMessage());
        } catch(URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testExtract_GoToBottomAndGetSpecificOne() {
        try {
            ClassLoader classLoader = HierarchicalExtractorTest.class.getClassLoader();
            URL reportXmlDirUrl = classLoader.getResource("system_tests/reports/xml_reports_basic");
            File reportXmlDir = new File(reportXmlDirUrl.toURI());
            FileConfig fileConfig = new FileConfig(reportXmlDir.getAbsolutePath(), "", logger, fileReader);
            fileConfig.setFileNamePattern("basic_report_2");
            fileConfig.setFileExtension("xml");
            HierarchicalExtractor extractor = new HierarchicalExtractor(fileConfig, "");

            extractor.setPath("nightly_run*/*/*/*/*/Initialize");
            extractor.setProperty(Keywords.GET_PATH_ATTRIBUTE, "name");
            extractor.setProperty(Keywords.GET_ELEMENT, "performance-report|timing");
            extractor.setKey("cpu-time-max");

            AmbiguityStrategy strategy = new AmbiguityStrategy("");
            strategy.setShouldGetFirstMatchOnly(false);
            extractor.setAmbiguityStrategy(strategy);

            File basicReport2 = new File(reportXmlDir, "basic_report_2.xml");
            List<ExtractionResult> results = extractor.extract(basicReport2.getAbsolutePath());
            ExtractionResult result = results.get(0);
            assertEquals("/nightly_run_2018_01_08/TestTiming/Test/Domain/Procedure myProcedure/Initialize", result.getPath());
            assertEquals("cpu-time-max", result.getKey());
            assertEquals("0.429", result.getValue());
        } catch(WatchrParseException e) {
            fail(e.getOriginalException().getMessage());
        } catch(URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testExtract_GetEntireHierarchyRecursively() {
        try {
            ClassLoader classLoader = HierarchicalExtractorTest.class.getClassLoader();
            URL reportXmlDirUrl = classLoader.getResource("system_tests/reports/xml_reports_basic");
            File reportXmlDir = new File(reportXmlDirUrl.toURI());
            FileConfig fileConfig = new FileConfig(reportXmlDir.getAbsolutePath(), "", logger, fileReader);
            fileConfig.setFileNamePattern("basic_report_2");
            fileConfig.setFileExtension("xml");
            HierarchicalExtractor extractor = new HierarchicalExtractor(fileConfig, "");

            extractor.setPath("nightly_run*");
            extractor.setProperty(Keywords.GET_PATH_ATTRIBUTE, "name");
            extractor.setProperty(Keywords.GET_ELEMENT, "performance-report|timing");
            extractor.setKey("cpu-time-max");

            AmbiguityStrategy strategy = new AmbiguityStrategy("");
            strategy.setShouldGetFirstMatchOnly(false);
            strategy.setShouldRecurseToChildGraphs(true);
            extractor.setAmbiguityStrategy(strategy);

            File basicReport2 = new File(reportXmlDir, "basic_report_2.xml");
            List<ExtractionResult> results = extractor.extract(basicReport2.getAbsolutePath());
            ExtractionResult result = results.get(0);

            assertEquals("/nightly_run_2018_01_08", result.getPath());
            assertEquals("cpu-time-max", result.getKey());
            assertEquals("", result.getValue());
            assertEquals(1, result.getChildren().size());

            result = result.getChildren().get(0);
            assertEquals("/nightly_run_2018_01_08/TestTiming", result.getPath());
            assertEquals("cpu-time-max", result.getKey());
            assertEquals("", result.getValue());
            assertEquals(1, result.getChildren().size());

            result = result.getChildren().get(0);
            assertEquals("/nightly_run_2018_01_08/TestTiming/Test", result.getPath());
            assertEquals("cpu-time-max", result.getKey());
            assertEquals("100.286", result.getValue());
            assertEquals(1, result.getChildren().size());

            result = result.getChildren().get(0);
            assertEquals("/nightly_run_2018_01_08/TestTiming/Test/Domain", result.getPath());
            assertEquals("cpu-time-max", result.getKey());
            assertEquals("", result.getValue());
            assertEquals(1, result.getChildren().size());

            result = result.getChildren().get(0);
            assertEquals("/nightly_run_2018_01_08/TestTiming/Test/Domain/Procedure myProcedure", result.getPath());
            assertEquals("cpu-time-max", result.getKey());
            assertEquals("", result.getValue());
            assertEquals(4, result.getChildren().size());            

            ExtractionResult initializeResult = result.getChildren().get(0);
            ExtractionResult executeResult = result.getChildren().get(1);
            ExtractionResult meshInputResult = result.getChildren().get(2);
            ExtractionResult meshOutputResult = result.getChildren().get(3);

            assertEquals("/nightly_run_2018_01_08/TestTiming/Test/Domain/Procedure myProcedure/Initialize", initializeResult.getPath());
            assertEquals("cpu-time-max", initializeResult.getKey());
            assertEquals("0.429", initializeResult.getValue());
            assertEquals(0, initializeResult.getChildren().size()); 

            assertEquals("/nightly_run_2018_01_08/TestTiming/Test/Domain/Procedure myProcedure/Execute", executeResult.getPath());
            assertEquals("cpu-time-max", executeResult.getKey());
            assertEquals("97.084", executeResult.getValue());
            assertEquals(0, executeResult.getChildren().size());    

            assertEquals("/nightly_run_2018_01_08/TestTiming/Test/Domain/Procedure myProcedure/Mesh input", meshInputResult.getPath());
            assertEquals("cpu-time-max", meshInputResult.getKey());
            assertEquals("0.425", meshInputResult.getValue());
            assertEquals(0, meshInputResult.getChildren().size());

            assertEquals("/nightly_run_2018_01_08/TestTiming/Test/Domain/Procedure myProcedure/Mesh output", meshOutputResult.getPath());
            assertEquals("cpu-time-max", meshOutputResult.getKey());
            assertEquals("1.15", meshOutputResult.getValue());
            assertEquals(0, meshOutputResult.getChildren().size());    
        } catch(WatchrParseException e) {
            fail(e.getOriginalException().getMessage());
        } catch(URISyntaxException e) {
            fail(e.getMessage());
        }
    }    
}