package gov.sandia.watchr.config;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.log.StringOutputLogger;

public class LogConfigTest {
    
    private LogConfig logConfig;
    private StringOutputLogger testLogger;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
        logConfig = new LogConfig("", testLogger);
    }

    @Test
    public void testGetLoggingLevel() {
        assertEquals(ErrorLevel.INFO, logConfig.getLoggingLevel());
    }

    @Test
    public void testSetLoggingLevel() {
        logConfig.setLoggingLevel(ErrorLevel.DEBUG.toString());
        assertEquals(ErrorLevel.DEBUG, logConfig.getLoggingLevel());
    }

    @Test
    public void testDiff() {
        LogConfig otherLogConfig = new LogConfig("", new StringOutputLogger());
        otherLogConfig.setLoggingLevel(ErrorLevel.ERROR.toString());

        List<WatchrDiff<?>> diffs = logConfig.diff(otherLogConfig);
        assertEquals(1, diffs.size());

        WatchrDiff<?> diff1 = diffs.get(0);
        assertEquals(DiffCategory.LOGGING_LEVEL, diff1.getProperty());
        assertEquals("/logConfig", diff1.getPath());
        assertEquals(ErrorLevel.INFO.toString(), diff1.getBeforeValue());
        assertEquals(ErrorLevel.ERROR.toString(), diff1.getNowValue());
    }
}
