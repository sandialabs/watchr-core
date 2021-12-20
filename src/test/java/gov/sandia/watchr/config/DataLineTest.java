package gov.sandia.watchr.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.derivative.AverageDerivativeLine;
import gov.sandia.watchr.config.derivative.DerivativeLine;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.util.RGB;

public class DataLineTest {
    
    private ILogger logger;
    private IFileReader fileReader;

    @Before
    public void setup() {
        logger = new StringOutputLogger();
        fileReader = new DefaultFileReader(logger);
    }

    @Test
    public void testDiffs() {
        FileConfig fileConfig = new FileConfig("/my/path/prefix", logger, fileReader);
        DataLine dataLine = new DataLine(fileConfig, "/my/path/prefix");

        dataLine.setColor(255, 255, 255);
        dataLine.setName("A");
        dataLine.setXExtractor(new HierarchicalExtractor(fileConfig, "/my/path/prefix"));
        dataLine.setYExtractor(new HierarchicalExtractor(fileConfig, "/my/path/prefix"));

        DataLine dataLine2 = new DataLine(dataLine);
        dataLine2.setColor(255, 0, 0);
        dataLine2.setName("B");
        DerivativeLine derivativeLine = new AverageDerivativeLine(dataLine.getConfigPath(), logger);
        derivativeLine.setColor(0, 255, 0);
        dataLine2.getDerivativeLines().add(derivativeLine);
        MetadataConfig metadataConfig = new MetadataConfig(fileConfig, "/my/path/prefix");
        metadataConfig.setName("metadata");
        dataLine2.getMetadata().add(metadataConfig);

        List<WatchrDiff<?>> diffs = dataLine.diff(dataLine2);
        assertEquals(4, diffs.size());

        WatchrDiff<?> diff1 = diffs.get(0);
        assertEquals(DiffCategory.NAME, diff1.getProperty());
        assertEquals("/my/path/prefix/dataLine", diff1.getPath());
        assertEquals("A", diff1.getBeforeValue());
        assertEquals("B", diff1.getNowValue());

        WatchrDiff<?> diff2 = diffs.get(1);
        assertEquals(DiffCategory.LINE_COLOR, diff2.getProperty());
        assertEquals("/my/path/prefix/dataLine", diff2.getPath());
        assertEquals(new RGB(255, 255, 255), diff2.getBeforeValue());
        assertEquals(new RGB(255, 0, 0), diff2.getNowValue());

        WatchrDiff<?> diff3 = diffs.get(2);
        assertEquals(DiffCategory.NAME, diff3.getProperty());
        assertEquals("/my/path/prefix/dataLine/metadataConfig", diff3.getPath());
        assertEquals("", diff3.getBeforeValue());
        assertEquals("metadata", diff3.getNowValue());

        WatchrDiff<?> diff4 = diffs.get(3);
        assertEquals(DiffCategory.DERIVATIVE_LINE_COLOR, diff4.getProperty());
        assertEquals("/my/path/prefix/dataLine/derivativeLine/average", diff4.getPath());
        assertNull(diff4.getBeforeValue());
        assertEquals(new RGB(0, 255, 0), diff4.getNowValue());
    }

    @Test
    public void testDiffs_Color_TwoNulls() {
        FileConfig fileConfig = new FileConfig("/my/path/prefix", logger, fileReader);
        DataLine dataLine = new DataLine(fileConfig, "/my/path/prefix");
        dataLine.setColor(null);

        DataLine dataLine2 = new DataLine(dataLine);
        dataLine2.setColor(null);

        List<WatchrDiff<?>> diffs = dataLine.diff(dataLine2);
        assertEquals(0, diffs.size());
    }

    @Test
    public void testDiffs_Color_OneNull() {
        FileConfig fileConfig = new FileConfig("/my/path/prefix", logger, fileReader);
        DataLine dataLine = new DataLine(fileConfig, "/my/path/prefix");
        dataLine.setColor(null);

        DataLine dataLine2 = new DataLine(dataLine);
        dataLine2.setColor(new RGB(255, 255, 255));

        List<WatchrDiff<?>> diffs = dataLine.diff(dataLine2);
        assertEquals(1, diffs.size());

        WatchrDiff<?> diff1 = diffs.get(0);
        assertEquals(DiffCategory.LINE_COLOR, diff1.getProperty());
        assertEquals("/my/path/prefix/dataLine", diff1.getPath());
        assertNull(diff1.getBeforeValue());
        assertEquals(new RGB(255, 255, 255), diff1.getNowValue());
    }

    @Test
    public void testDiffs_Color_OtherNull() {
        FileConfig fileConfig = new FileConfig("/my/path/prefix", logger, fileReader);
        DataLine dataLine = new DataLine(fileConfig, "/my/path/prefix");
        dataLine.setColor(new RGB(255, 255, 255));

        DataLine dataLine2 = new DataLine(dataLine);
        dataLine2.setColor(null);

        List<WatchrDiff<?>> diffs = dataLine.diff(dataLine2);
        assertEquals(1, diffs.size());

        WatchrDiff<?> diff1 = diffs.get(0);
        assertEquals(DiffCategory.LINE_COLOR, diff1.getProperty());
        assertEquals("/my/path/prefix/dataLine", diff1.getPath());
        assertEquals(new RGB(255, 255, 255), diff1.getBeforeValue());
        assertNull(diff1.getNowValue());
    }

    @Test
    public void testDiffs_Color_SameColors() {
        FileConfig fileConfig = new FileConfig("/my/path/prefix", logger, fileReader);
        DataLine dataLine = new DataLine(fileConfig, "/my/path/prefix");
        dataLine.setColor(new RGB(255, 255, 255));

        DataLine dataLine2 = new DataLine(dataLine);
        dataLine2.setColor(new RGB(255, 255, 255));

        List<WatchrDiff<?>> diffs = dataLine.diff(dataLine2);
        assertEquals(0, diffs.size());
    }  

    @Test
    public void testCopyAndHashCode() {
        FileConfig fileConfig = new FileConfig("/my/path/prefix", logger, fileReader);
        DataLine dataLine = new DataLine(fileConfig, "/my/path/prefix");

        dataLine.setColor(255, 255, 255);
        dataLine.setName("A");
        dataLine.setXExtractor(new HierarchicalExtractor(fileConfig, "/my/path/prefix"));
        dataLine.setYExtractor(new HierarchicalExtractor(fileConfig, "/my/path/prefix"));

        DataLine dataLine2 = new DataLine(dataLine);
        assertEquals(dataLine.hashCode(), dataLine2.hashCode());
    }

    @Test
    public void testCopyAndEquals() {
        FileConfig fileConfig = new FileConfig("/my/path/prefix", logger, fileReader);
        DataLine dataLine = new DataLine(fileConfig, "/my/path/prefix");

        dataLine.setColor(255, 255, 255);
        dataLine.setName("A");
        dataLine.setXExtractor(new HierarchicalExtractor(fileConfig, "/my/path/prefix"));
        dataLine.setYExtractor(new HierarchicalExtractor(fileConfig, "/my/path/prefix"));

        DataLine dataLine2 = new DataLine(dataLine);
        assertEquals(dataLine, dataLine2);       
    }

    @Test
    public void testCopyAndNotEquals() {
        FileConfig fileConfig = new FileConfig("/my/path/prefix", logger, fileReader);
        DataLine dataLine = new DataLine(fileConfig, "/my/path/prefix");

        dataLine.setColor(255, 255, 255);
        dataLine.setName("A");
        dataLine.setXExtractor(new HierarchicalExtractor(fileConfig, "/my/path/prefix"));
        dataLine.setYExtractor(new HierarchicalExtractor(fileConfig, "/my/path/prefix"));

        DataLine dataLine2 = new DataLine(dataLine);
        dataLine2.setName("B");
        assertNotEquals(dataLine, dataLine2);         
    }
}
