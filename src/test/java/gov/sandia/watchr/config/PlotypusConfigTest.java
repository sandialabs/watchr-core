package gov.sandia.watchr.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.log.StringOutputLogger;

public class PlotypusConfigTest {
    
    private PlotypusConfig plotypusConfig;
    private StringOutputLogger testLogger;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
        plotypusConfig = new PlotypusConfig("", testLogger);
    }

    @Test
    public void testGetNumberOfTentacles() {
        assertEquals(10, plotypusConfig.getNumberOfTentacles());
    }

    @Test
    public void testDiff_Tentacles() {
        PlotypusConfig otherPlotypusConfig = new PlotypusConfig("", new StringOutputLogger());
        otherPlotypusConfig.setNumberOfTentacles(100);

        List<WatchrDiff<?>> diffs = plotypusConfig.diff(otherPlotypusConfig);
        assertEquals(1, diffs.size());

        WatchrDiff<?> diff1 = diffs.get(0);
        assertEquals(DiffCategory.TENTACLES, diff1.getProperty());
        assertEquals("/plotypusConfig", diff1.getPath());
        assertEquals(10, diff1.getBeforeValue());
        assertEquals(100, diff1.getNowValue());
    }

    @Test
    public void testDiff_Timeout() {
        PlotypusConfig otherPlotypusConfig = new PlotypusConfig("", new StringOutputLogger());
        otherPlotypusConfig.setPayloadTimeout(1000);

        List<WatchrDiff<?>> diffs = plotypusConfig.diff(otherPlotypusConfig);
        assertEquals(1, diffs.size());

        WatchrDiff<?> diff1 = diffs.get(0);
        assertEquals(DiffCategory.TIMEOUT, diff1.getProperty());
        assertEquals("/plotypusConfig", diff1.getPath());
        assertEquals(30000, diff1.getBeforeValue());
        assertEquals(1000, diff1.getNowValue());
    }

    @Test
    public void testDiff_TimeWarning() {
        PlotypusConfig otherPlotypusConfig = new PlotypusConfig("", new StringOutputLogger());
        otherPlotypusConfig.setPayloadTimeWarning(500);

        List<WatchrDiff<?>> diffs = plotypusConfig.diff(otherPlotypusConfig);
        assertEquals(1, diffs.size());

        WatchrDiff<?> diff1 = diffs.get(0);
        assertEquals(DiffCategory.TIME_WARNING, diff1.getProperty());
        assertEquals("/plotypusConfig", diff1.getPath());
        assertEquals(5000, diff1.getBeforeValue());
        assertEquals(500, diff1.getNowValue());
    }

    @Test
    public void testValidate_NoProblems() {
        plotypusConfig.validate();
        assertEquals("", ((StringOutputLogger)plotypusConfig.getLogger()).getLogAsString());
    }

    @Test
    public void testValidate_BadTentacleValue() {
        plotypusConfig.setNumberOfTentacles(-100);
        plotypusConfig.validate();
        String logContents = ((StringOutputLogger)plotypusConfig.getLogger()).getLogAsString();
        assertTrue(logContents.contains("Plotypus needs at least one tentacle."));
    }

    @Test
    public void testValidate_TimeWarningGreaterThanTimeout() {
        plotypusConfig.setPayloadTimeWarning(1000);
        plotypusConfig.setPayloadTimeout(1);
        plotypusConfig.validate();
        String logContents = ((StringOutputLogger)plotypusConfig.getLogger()).getLogAsString();
        assertTrue(logContents.contains("Warning time is longer than timeout time."));
    }
}
