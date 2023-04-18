package gov.sandia.watchr.log;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.util.OsUtil;

public class StringOutputLoggerTest {
    
    private StringOutputLogger logger;

    @Before
    public void setup() {
        logger = new StringOutputLogger();
    }

    @Test
    public void testLogDebug() {
        logger.setLoggingLevel(ErrorLevel.DEBUG);
        logger.logDebug("TEST", StringOutputLogger.class.getSimpleName());
        assertEquals("TEST" + OsUtil.getOSLineBreak(), logger.getLogAsString());
    }

    @Test
    public void testLog() {
        logger.setLoggingLevel(ErrorLevel.DEBUG);
        logger.log(new WatchrConfigError(ErrorLevel.DEBUG, "TEST", StringOutputLogger.class.getSimpleName()));
        assertTrue(logger.getLogAsString().contains("[DEBUG] : TEST"));
    }

    @Test
    public void testLog_FailWithoutClassName() {
        logger.setLoggingLevel(ErrorLevel.DEBUG);
        logger.log(new WatchrConfigError(ErrorLevel.DEBUG, "TEST"));
        assertFalse(logger.getLogAsString().contains("[DEBUG] : TEST"));
    }

    @Test
    public void testLogError() {
        logger.setLoggingLevel(ErrorLevel.ERROR);
        logger.logError("TEST", new Exception("TestException"));
        assertTrue(logger.getLogAsString().contains("TEST"));
        assertTrue(logger.getLogAsString().contains("TestException"));
    }
}
