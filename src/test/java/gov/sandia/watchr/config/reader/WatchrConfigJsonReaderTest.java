package gov.sandia.watchr.config.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.DataLine;
import gov.sandia.watchr.config.DerivativeLine;
import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.HierarchicalExtractor;
import gov.sandia.watchr.config.MetadataConfig;
import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.config.PlotsConfig;
import gov.sandia.watchr.config.RuleConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.DerivativeLine.DerivativeLineType;
import gov.sandia.watchr.config.schema.Keywords;

public class WatchrConfigJsonReaderTest {
    
    private File reportsDir;
    private File configDir;
    private WatchrConfigJsonReader reader;

    private static final String CONFIG = "BigXmlBasedConfig.json";

    @Before
    public void setup() {
        try {
            ClassLoader classLoader = WatchrConfigJsonReaderTest.class.getClassLoader();
            URL reportsDirUrl = classLoader.getResource("unit_tests/xml/HelloWorld");
            reportsDir = new File(reportsDirUrl.toURI());
            reader = new WatchrConfigJsonReader(reportsDir);

            URL configDirUrl = classLoader.getResource("system_tests/config");
            configDir = new File(configDirUrl.toURI());
        } catch(URISyntaxException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDeserialize_TopLevel() {
        try {
            File configFile = new File(configDir, CONFIG);
            WatchrConfig watchrConfig = reader.deserialize(configFile);
            assertNotNull(watchrConfig.getPlotsConfig());
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDeserialize_FileConfig() {
        try {
            File configFile = new File(configDir, CONFIG);
            WatchrConfig watchrConfig = reader.deserialize(configFile);
            PlotsConfig plotConfig = watchrConfig.getPlotsConfig();

            FileConfig fileConfig = plotConfig.getFileConfig();
            assertEquals("xml", fileConfig.getFileExtension());
            assertEquals("performance_.*", fileConfig.getFileNamePattern());
            assertTrue(fileConfig.getStartDir().getAbsolutePath().contains("HelloWorld"));
            assertTrue(fileConfig.getStartDir().getAbsolutePath().contains("xml"));
            assertTrue(fileConfig.shouldIgnoreOldFiles());
            assertFalse(fileConfig.shouldRecurseDirectories());
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDeserialize_Categories() {
        try {
            File configFile = new File(configDir, CONFIG);
            WatchrConfig watchrConfig = reader.deserialize(configFile);

            List<String> categories = new ArrayList<>(watchrConfig.getPlotsConfig().getCategoryConfig().getCategories());
            assertEquals("cpu-time-max", categories.get(0));
            assertEquals("cpu-time-min", categories.get(1));
            assertEquals("cpu-time-sum", categories.get(2));
            assertEquals("wall-time-max", categories.get(3));
            assertEquals("wall-time-min", categories.get(4));
            assertEquals("wall-time-sum", categories.get(5));
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDeserialize_Rules() {
        try {
            File configFile = new File(configDir, CONFIG);
            WatchrConfig watchrConfig = reader.deserialize(configFile);
            PlotsConfig plotsConfig = watchrConfig.getPlotsConfig();
            assertFalse(plotsConfig.getPlotConfigs().isEmpty());
            
            PlotConfig plotConfig = plotsConfig.getPlotConfigs().get(0);
            List<RuleConfig> plotRules = plotConfig.getPlotRules();
            RuleConfig rule1 = plotRules.get(0);
            assertEquals("dataLine > average", rule1.getCondition());
            assertEquals("fail", rule1.getAction());
            RuleConfig rule2 = plotRules.get(1);
            assertEquals("dataLine > standardDeviationOffset", rule2.getCondition());
            assertEquals("fail", rule2.getAction());
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDeserialize_Metadata() {
        try {
            File configFile = new File(configDir, CONFIG);
            WatchrConfig watchrConfig = reader.deserialize(configFile);
            PlotsConfig plotsConfig = watchrConfig.getPlotsConfig();
            
            List<MetadataConfig> metadatas = plotsConfig.getPlotConfigs().get(0).getDataLines().get(0).getMetadata();
            assertEquals(2, metadatas.size());

            MetadataConfig metadata1 = metadatas.get(0);
            assertEquals("codeBase", metadata1.getName());
            HierarchicalExtractor metadataExtractor = metadata1.getMetadataExtractor();
            assertEquals("nightly_run_*/*/codeBase", metadataExtractor.getProperty(Keywords.GET_PATH));
            assertEquals("name|key", metadataExtractor.getProperty(Keywords.GET_PATH_ATTRIBUTE));
            assertEquals("performance-report|timing|metadata", metadataExtractor.getProperty(Keywords.GET_ELEMENT));
            assertEquals("value", metadataExtractor.getProperty(Keywords.GET_KEY));

            MetadataConfig metadata2 = metadatas.get(1);
            assertEquals("branch", metadata2.getName());
            metadataExtractor = metadata2.getMetadataExtractor();
            assertEquals("nightly_run_*/*/branch", metadataExtractor.getProperty(Keywords.GET_PATH));
            assertEquals("name|key", metadataExtractor.getProperty(Keywords.GET_PATH_ATTRIBUTE));
            assertEquals("performance-report|timing|metadata", metadataExtractor.getProperty(Keywords.GET_ELEMENT));
            assertEquals("value", metadataExtractor.getProperty(Keywords.GET_KEY));
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }   

    @Test
    public void testDeserialize_DataLines() {
        try {
            File configFile = new File(configDir, CONFIG);
            WatchrConfig watchrConfig = reader.deserialize(configFile);
            PlotsConfig plotsConfig = watchrConfig.getPlotsConfig();
            assertFalse(plotsConfig.getPlotConfigs().isEmpty());

            PlotConfig plotConfig = plotsConfig.getPlotConfigs().get(0);
            List<DataLine> lines = plotConfig.getDataLines();
            assertEquals(1, lines.size());

            DataLine line = lines.get(0);
            assertEquals("y/path", plotsConfig.getPlotConfigs().get(0).getNameConfig().getNameUseProperty());
            assertNotNull(line.getXExtractor());
            assertNotNull(line.getYExtractor());
            
            HierarchicalExtractor xExtractor = line.getXExtractor();
            HierarchicalExtractor yExtractor = line.getYExtractor();

            assertEquals("performance-report", xExtractor.getProperty(Keywords.GET_ELEMENT));
            assertEquals("date", xExtractor.getProperty(Keywords.GET_KEY));
            assertEquals("timestamp", xExtractor.getProperty(Keywords.UNIT));

            assertEquals("nightly_run_*/*/*", yExtractor.getProperty(Keywords.GET_PATH));
            assertEquals("name", yExtractor.getProperty(Keywords.GET_PATH_ATTRIBUTE));
            assertEquals("performance-report|timing", yExtractor.getProperty(Keywords.GET_ELEMENT));
            assertEquals("cpu-time-max", yExtractor.getProperty(Keywords.GET_KEY));
            assertEquals("seconds", yExtractor.getProperty(Keywords.UNIT));     
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDeserialize_DerivativeLines() {
        try {
            File configFile = new File(configDir, CONFIG);
            WatchrConfig watchrConfig = reader.deserialize(configFile);
            PlotsConfig plotsConfig = watchrConfig.getPlotsConfig();
            assertFalse(plotsConfig.getPlotConfigs().isEmpty());

            PlotConfig plotConfig = plotsConfig.getPlotConfigs().get(0);
            List<DataLine> lines = plotConfig.getDataLines();
            DataLine line = lines.get(0);
            List<DerivativeLine> derivativeLines = line.getDerivativeLines();
            assertEquals(2, derivativeLines.size());

            DerivativeLine avgLine = derivativeLines.get(0);
            assertEquals(DerivativeLineType.AVERAGE, avgLine.getType());
            assertEquals(20, avgLine.getRollingRange());
            assertEquals(202, avgLine.getColor().red);
            assertEquals(77, avgLine.getColor().green);
            assertEquals(77, avgLine.getColor().blue);

            DerivativeLine stdDevLine = derivativeLines.get(1);
            assertEquals(DerivativeLineType.STANDARD_DEVIATION_OFFSET, stdDevLine.getType());
            assertEquals(20, stdDevLine.getRollingRange());
            assertEquals(77, stdDevLine.getColor().red);
            assertEquals(202, stdDevLine.getColor().green);
            assertEquals(77, stdDevLine.getColor().blue);
        } catch(IOException e) {
            fail(e.getMessage());
        }
    }
}