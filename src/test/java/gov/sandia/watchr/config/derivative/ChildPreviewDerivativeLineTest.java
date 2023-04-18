package gov.sandia.watchr.config.derivative;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.util.RGB;

public class ChildPreviewDerivativeLineTest {

    private StringOutputLogger testLogger;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
    }
    
    @Test
    public void testValidate_GoodBoi() {
        ChildPreviewDerivativeLine derivativeLine = new ChildPreviewDerivativeLine("", testLogger);
        derivativeLine.setColor(255, 255, 255);
        
        derivativeLine.validate();
        List<String> errors = testLogger.getLog();
        assertEquals(0, errors.size());
    }

    @Test
    public void testDiffs() {
        ChildPreviewDerivativeLine derivativeLine = new ChildPreviewDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine.setColor(255, 255, 255);

        ChildPreviewDerivativeLine derivativeLine2 = new ChildPreviewDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine2.setColor(255, 0, 0);

        List<WatchrDiff<?>> diffs = derivativeLine.diff(derivativeLine2);
        assertEquals(1, diffs.size());

        WatchrDiff<?> diff1 = diffs.get(0);
        assertEquals(DiffCategory.DERIVATIVE_LINE_COLOR, diff1.getProperty());
        assertEquals("/my/path/prefix/derivativeLine/child", diff1.getPath());
        assertEquals(new RGB(255, 255, 255), diff1.getBeforeValue());
        assertEquals(new RGB(255, 0, 0), diff1.getNowValue());
    }

    @Test
    public void testCopyAndHashCode() {
        ChildPreviewDerivativeLine derivativeLine = new ChildPreviewDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine.setColor(255, 255, 255);

        ChildPreviewDerivativeLine derivativeLine2 = new ChildPreviewDerivativeLine(derivativeLine);
        assertEquals(derivativeLine.hashCode(), derivativeLine2.hashCode());
    }

    @Test
    public void testCopyAndEquals() {
        ChildPreviewDerivativeLine derivativeLine = new ChildPreviewDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine.setColor(255, 255, 255);

        ChildPreviewDerivativeLine derivativeLine2 = new ChildPreviewDerivativeLine(derivativeLine);
        assertEquals(derivativeLine, derivativeLine2);        
    }

    @Test
    public void testCopyAndNotEquals() {
        ChildPreviewDerivativeLine derivativeLine = new ChildPreviewDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine.setColor(255, 255, 255);

        ChildPreviewDerivativeLine derivativeLine2 = new ChildPreviewDerivativeLine(derivativeLine);
        derivativeLine2.setColor(0, 0, 0);
        assertNotEquals(derivativeLine, derivativeLine2);           
    }

    @Test
    public void testApplyOverTemplate() {
        ChildPreviewDerivativeLine baseLine = new ChildPreviewDerivativeLine("", testLogger);
        baseLine.setName("A");
        ChildPreviewDerivativeLine overwriteLine = new ChildPreviewDerivativeLine("", testLogger);
        overwriteLine.setName("B");

        ChildPreviewDerivativeLine resultLine = (ChildPreviewDerivativeLine) overwriteLine.applyOverTemplate(baseLine);

        assertEquals("B", resultLine.getName());
    }

    @Test
    public void testApplyOverTemplate_TypeMismatch() {
        SlopeDerivativeLine baseLine = new SlopeDerivativeLine("", testLogger);
        ChildPreviewDerivativeLine overwriteLine = new ChildPreviewDerivativeLine("", testLogger);
        assertNull(overwriteLine.applyOverTemplate(baseLine));
    }
}