package gov.sandia.watchr.config.derivative;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.util.RGB;

public class StdDevPositiveOffsetDerivativeLineTest {

    private StringOutputLogger testLogger;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
    }
    
    @Test
    public void testValidate_GoodBoi() {
        StdDevPositiveOffsetDerivativeLine derivativeLine = new StdDevPositiveOffsetDerivativeLine("", testLogger);
        derivativeLine.setRollingRange(10);
        derivativeLine.setColor(255, 255, 255);
        derivativeLine.setIgnoreFilteredData(true);
        
        derivativeLine.validate();
        List<String> errors = testLogger.getLog();
        assertEquals(0, errors.size());
    }

    @Test
    public void testValidate_NoRollingRange() {
        StdDevPositiveOffsetDerivativeLine derivativeLine = new StdDevPositiveOffsetDerivativeLine("", testLogger);
        derivativeLine.setRollingRange(0);
        
        derivativeLine.validate();
        List<String> errors = testLogger.getLog();
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("ERROR"));
        assertTrue(errors.get(0).contains("The rolling range for a derivative line must use 1 or more points!"));
    }

    @Test
    public void testDiffs() {
        StdDevPositiveOffsetDerivativeLine derivativeLine = new StdDevPositiveOffsetDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine.setColor(255, 255, 255);
        derivativeLine.setIgnoreFilteredData(false);
        derivativeLine.setRollingRange(1);

        StdDevPositiveOffsetDerivativeLine derivativeLine2 = new StdDevPositiveOffsetDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine2.setColor(255, 0, 0);
        derivativeLine2.setIgnoreFilteredData(true);
        derivativeLine2.setRollingRange(10);

        List<WatchrDiff<?>> diffs = derivativeLine.diff(derivativeLine2);
        assertEquals(3, diffs.size());

        WatchrDiff<?> diff1 = diffs.get(0);
        assertEquals(DiffCategory.DERIVATIVE_LINE_COLOR, diff1.getProperty());
        assertEquals("/my/path/prefix/derivativeLine/stdDevPositiveOffset", diff1.getPath());
        assertEquals(new RGB(255, 255, 255), diff1.getBeforeValue());
        assertEquals(new RGB(255, 0, 0), diff1.getNowValue());

        WatchrDiff<?> diff2 = diffs.get(1);
        assertEquals(DiffCategory.ROLLING_RANGE, diff2.getProperty());
        assertEquals("/my/path/prefix/derivativeLine/stdDevPositiveOffset", diff2.getPath());
        assertEquals(1, diff2.getBeforeValue());
        assertEquals(10, diff2.getNowValue());

        WatchrDiff<?> diff3 = diffs.get(2);
        assertEquals(DiffCategory.IGNORE_FILTERED_DATA, diff3.getProperty());
        assertEquals("/my/path/prefix/derivativeLine/stdDevPositiveOffset", diff3.getPath());
        assertFalse((Boolean) diff3.getBeforeValue());
        assertTrue((Boolean) diff3.getNowValue());
    }

    @Test
    public void testCopyAndHashCode() {
        StdDevPositiveOffsetDerivativeLine derivativeLine = new StdDevPositiveOffsetDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine.setColor(255, 255, 255);
        derivativeLine.setIgnoreFilteredData(false);
        derivativeLine.setRollingRange(1);

        StdDevPositiveOffsetDerivativeLine derivativeLine2 = new StdDevPositiveOffsetDerivativeLine(derivativeLine);
        assertEquals(derivativeLine.hashCode(), derivativeLine2.hashCode());
    }

    @Test
    public void testCopyAndEquals() {
        StdDevPositiveOffsetDerivativeLine derivativeLine = new StdDevPositiveOffsetDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine.setColor(255, 255, 255);
        derivativeLine.setIgnoreFilteredData(false);
        derivativeLine.setRollingRange(1);

        StdDevPositiveOffsetDerivativeLine derivativeLine2 = new StdDevPositiveOffsetDerivativeLine(derivativeLine);
        assertEquals(derivativeLine, derivativeLine2);        
    }

    @Test
    public void testCopyAndNotEquals() {
        StdDevPositiveOffsetDerivativeLine derivativeLine = new StdDevPositiveOffsetDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine.setColor(255, 255, 255);
        derivativeLine.setIgnoreFilteredData(false);
        derivativeLine.setRollingRange(1);

        StdDevPositiveOffsetDerivativeLine derivativeLine2 = new StdDevPositiveOffsetDerivativeLine(derivativeLine);
        derivativeLine2.setColor(0, 0, 0);
        assertNotEquals(derivativeLine, derivativeLine2);           
    }

    @Test
    public void testApplyOverTemplate() {
        StdDevPositiveOffsetDerivativeLine baseLine = new StdDevPositiveOffsetDerivativeLine("", testLogger);
        baseLine.setRollingRange(20);
        baseLine.setNumberFormat("#.#");
        StdDevPositiveOffsetDerivativeLine overwriteLine = new StdDevPositiveOffsetDerivativeLine("", testLogger);
        overwriteLine.setRollingRange(10);
        overwriteLine.setNumberFormat("#.####");

        StdDevPositiveOffsetDerivativeLine resultLine = (StdDevPositiveOffsetDerivativeLine) overwriteLine.applyOverTemplate(baseLine);

        assertEquals(10, resultLine.getRollingRange());
        assertEquals("#.####", resultLine.getNumberFormat());
    }

    @Test
    public void testApplyOverTemplate_TypeMismatch() {
        SlopeDerivativeLine baseLine = new SlopeDerivativeLine("", testLogger);
        StdDevPositiveOffsetDerivativeLine overwriteLine = new StdDevPositiveOffsetDerivativeLine("", testLogger);
        assertNull(overwriteLine.applyOverTemplate(baseLine));
    }
}