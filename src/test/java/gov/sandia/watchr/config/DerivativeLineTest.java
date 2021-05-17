package gov.sandia.watchr.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.TestLogger;
import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.DerivativeLine.DerivativeLineType;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.util.RGB;

public class DerivativeLineTest {

    private TestLogger testLogger;

    @Before
    public void setup() {
        testLogger = new TestLogger();
        WatchrCoreApp.getInstance().setLogger(testLogger);
    }
    
    @Test
    public void testValidate_GoodBoi() {
        DerivativeLine derivativeLine = new DerivativeLine("");
        derivativeLine.setType(DerivativeLineType.AVERAGE);
        derivativeLine.setRollingRange(10);
        derivativeLine.setColor(255, 255, 255);
        derivativeLine.setIgnoreFilteredData(true);
        
        derivativeLine.validate();
        List<WatchrConfigError> errors = testLogger.getErrors();
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidate_NoTypeSet() {
        DerivativeLine derivativeLine = new DerivativeLine("");
        derivativeLine.setRollingRange(10);
        
        derivativeLine.validate();
        List<WatchrConfigError> errors = testLogger.getErrors();
        assertEquals(1, errors.size());
        assertEquals(ErrorLevel.ERROR, errors.get(0).getLevel());
        assertEquals("Derivative line was defined, but no type was specified!", errors.get(0).getMessage());
    }

    @Test
    public void testValidate_NoRollingRange() {
        DerivativeLine derivativeLine = new DerivativeLine("");
        derivativeLine.setType(DerivativeLineType.AVERAGE);
        derivativeLine.setRollingRange(0);
        
        derivativeLine.validate();
        List<WatchrConfigError> errors = testLogger.getErrors();
        assertEquals(1, errors.size());
        assertEquals(ErrorLevel.ERROR, errors.get(0).getLevel());
        assertEquals("The rolling range for a derivative line must use 1 or more points!", errors.get(0).getMessage());
    }

    @Test
    public void testDiffs() {
        DerivativeLine derivativeLine = new DerivativeLine("/my/path/prefix");
        derivativeLine.setColor(255, 255, 255);
        derivativeLine.setIgnoreFilteredData(false);
        derivativeLine.setRollingRange(1);
        derivativeLine.setType(DerivativeLineType.AVERAGE);

        DerivativeLine derivativeLine2 = new DerivativeLine(derivativeLine);
        derivativeLine2.setColor(255, 0, 0);
        derivativeLine2.setIgnoreFilteredData(true);
        derivativeLine2.setRollingRange(10);
        derivativeLine2.setType(DerivativeLineType.STANDARD_DEVIATION_OFFSET);

        List<WatchrDiff<?>> diffs = derivativeLine.diff(derivativeLine2);
        assertEquals(4, diffs.size());

        WatchrDiff<?> diff1 = diffs.get(0);
        assertEquals(DiffCategory.TYPE, diff1.getProperty());
        assertEquals("/my/path/prefix/derivativeLine", diff1.getPath());
        assertEquals(DerivativeLineType.AVERAGE, diff1.getBeforeValue());
        assertEquals(DerivativeLineType.STANDARD_DEVIATION_OFFSET, diff1.getNowValue());

        WatchrDiff<?> diff2 = diffs.get(1);
        assertEquals(DiffCategory.ROLLING_RANGE, diff2.getProperty());
        assertEquals("/my/path/prefix/derivativeLine", diff2.getPath());
        assertEquals(1, diff2.getBeforeValue());
        assertEquals(10, diff2.getNowValue());

        WatchrDiff<?> diff3 = diffs.get(2);
        assertEquals(DiffCategory.DERIVATIVE_LINE_COLOR, diff3.getProperty());
        assertEquals("/my/path/prefix/derivativeLine", diff3.getPath());
        assertEquals(new RGB(255, 255, 255), diff3.getBeforeValue());
        assertEquals(new RGB(255, 0, 0), diff3.getNowValue());

        WatchrDiff<?> diff4 = diffs.get(3);
        assertEquals(DiffCategory.IGNORE_FILTERED_DATA, diff4.getProperty());
        assertEquals("/my/path/prefix/derivativeLine", diff4.getPath());
        assertFalse((Boolean) diff4.getBeforeValue());
        assertTrue((Boolean) diff4.getNowValue());
    }

    @Test
    public void testCopyAndHashCode() {
        DerivativeLine derivativeLine = new DerivativeLine("/my/path/prefix");
        derivativeLine.setColor(255, 255, 255);
        derivativeLine.setIgnoreFilteredData(false);
        derivativeLine.setRollingRange(1);
        derivativeLine.setType(DerivativeLineType.AVERAGE);

        DerivativeLine derivativeLine2 = new DerivativeLine(derivativeLine);
        assertEquals(derivativeLine.hashCode(), derivativeLine2.hashCode());
    }

    @Test
    public void testCopyAndEquals() {
        DerivativeLine derivativeLine = new DerivativeLine("/my/path/prefix");
        derivativeLine.setColor(255, 255, 255);
        derivativeLine.setIgnoreFilteredData(false);
        derivativeLine.setRollingRange(1);
        derivativeLine.setType(DerivativeLineType.AVERAGE);

        DerivativeLine derivativeLine2 = new DerivativeLine(derivativeLine);
        assertEquals(derivativeLine, derivativeLine2);        
    }

    @Test
    public void testCopyAndNotEquals() {
        DerivativeLine derivativeLine = new DerivativeLine("/my/path/prefix");
        derivativeLine.setColor(255, 255, 255);
        derivativeLine.setIgnoreFilteredData(false);
        derivativeLine.setRollingRange(1);
        derivativeLine.setType(DerivativeLineType.AVERAGE);

        DerivativeLine derivativeLine2 = new DerivativeLine(derivativeLine);
        derivativeLine2.setColor(0, 0, 0);
        assertNotEquals(derivativeLine, derivativeLine2);           
    }
}