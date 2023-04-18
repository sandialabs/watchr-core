package gov.sandia.watchr.config.derivative;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.util.RGB;

public class SlopeDerivativeLineTest {

    ////////////
    // FIELDS //
    ////////////
    
    private SlopeDerivativeLine slopeDerivativeLine;
    private StringOutputLogger testLogger;

    ///////////
    // SETUP //
    ///////////

    @Before
    public void setup() {
        this.slopeDerivativeLine = new SlopeDerivativeLine("", testLogger);
        this.testLogger = new StringOutputLogger();
    }

    ///////////
    // TESTS //
    ///////////

    @Test
    public void testConstructor() {
        assertNotNull(slopeDerivativeLine);
    }

    @Test
    public void testGetX() {
        slopeDerivativeLine.setXExpression("100.0");
        assertEquals("100.0", slopeDerivativeLine.getXExpression());
    }

    @Test
    public void testGetY() {
        slopeDerivativeLine.setYExpression("( 50.0 * x ) + 25.0");
        assertEquals("( 50.0 * x ) + 25.0", slopeDerivativeLine.getYExpression());
    }

    @Test
    public void testCalculateYForX() {
        slopeDerivativeLine.setYExpression("( 50.0 * x ) + 25.0");
        double yValue = slopeDerivativeLine.getYForX(0.0);
        assertEquals(25.0, yValue, 1.0e-4);
        yValue = slopeDerivativeLine.getYForX(10.0);
        assertEquals(525.0, yValue, 1.0e-4);
    }

    @Test
    public void testCalculateYForX_Another() {
        slopeDerivativeLine.setYExpression("( 2.0 * x ) + 5.0");
        double yValue = slopeDerivativeLine.getYForX(0.0);
        assertEquals(5.0, yValue, 1.0e-4);
        yValue = slopeDerivativeLine.getYForX(1.0);
        assertEquals(7.0, yValue, 1.0e-4);
        yValue = slopeDerivativeLine.getYForX(2.0);
        assertEquals(9.0, yValue, 1.0e-4);
    }

    @Test
    public void testCalculateXForY() {
        slopeDerivativeLine.setXExpression("( 50.0 * y ) + 25.0");
        double yValue = slopeDerivativeLine.getXForY(0.0);
        assertEquals(25.0, yValue, 1.0e-4);
        yValue = slopeDerivativeLine.getXForY(10.0);
        assertEquals(525.0, yValue, 1.0e-4);
    }

    @Test
    public void testValidate_GoodBoi() {
        SlopeDerivativeLine derivativeLine = new SlopeDerivativeLine("", testLogger);
        derivativeLine.setColor(255, 255, 255);
        
        derivativeLine.validate();
        List<String> errors = testLogger.getLog();
        assertEquals(0, errors.size());
    }

    @Test
    public void testDiffs() {
        SlopeDerivativeLine derivativeLine = new SlopeDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine.setColor(255, 255, 255);
        derivativeLine.setXExpression("y * 50");
        derivativeLine.setYExpression("x * 50");

        SlopeDerivativeLine derivativeLine2 = new SlopeDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine2.setColor(255, 0, 0);
        derivativeLine2.setXExpression("y * 100");
        derivativeLine2.setYExpression("x * 200");

        List<WatchrDiff<?>> diffs = derivativeLine.diff(derivativeLine2);
        assertEquals(3, diffs.size());

        WatchrDiff<?> diff1 = diffs.get(0);
        assertEquals(DiffCategory.DERIVATIVE_LINE_COLOR, diff1.getProperty());
        assertEquals("/my/path/prefix/derivativeLine/slope", diff1.getPath());
        assertEquals(new RGB(255, 255, 255), diff1.getBeforeValue());
        assertEquals(new RGB(255, 0, 0), diff1.getNowValue());

        WatchrDiff<?> diff2 = diffs.get(1);
        assertEquals(DiffCategory.EXPRESSION_X, diff2.getProperty());
        assertEquals("/my/path/prefix/derivativeLine/slope", diff2.getPath());
        assertEquals("y * 50", diff2.getBeforeValue());
        assertEquals("y * 100", diff2.getNowValue());

        WatchrDiff<?> diff3 = diffs.get(2);
        assertEquals(DiffCategory.EXPRESSION_Y, diff3.getProperty());
        assertEquals("/my/path/prefix/derivativeLine/slope", diff3.getPath());
        assertEquals("x * 50", diff3.getBeforeValue());
        assertEquals("x * 200", diff3.getNowValue());
    }

    @Test
    public void testCopyAndHashCode() {
        SlopeDerivativeLine derivativeLine = new SlopeDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine.setColor(255, 255, 255);
        derivativeLine.setXExpression("y * 100");
        derivativeLine.setYExpression("x * 200");

        SlopeDerivativeLine derivativeLine2 = new SlopeDerivativeLine(derivativeLine);
        assertEquals(derivativeLine.hashCode(), derivativeLine2.hashCode());
    }

    @Test
    public void testCopyAndEquals() {
        SlopeDerivativeLine derivativeLine = new SlopeDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine.setColor(255, 255, 255);
        derivativeLine.setXExpression("y * 100");
        derivativeLine.setYExpression("x * 200");

        SlopeDerivativeLine derivativeLine2 = new SlopeDerivativeLine(derivativeLine);
        assertEquals(derivativeLine, derivativeLine2);        
    }

    @Test
    public void testCopyAndNotEquals() {
        SlopeDerivativeLine derivativeLine = new SlopeDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine.setColor(255, 255, 255);
        derivativeLine.setXExpression("y * 100");
        derivativeLine.setYExpression("x * 200");

        SlopeDerivativeLine derivativeLine2 = new SlopeDerivativeLine(derivativeLine);
        derivativeLine2.setColor(0, 0, 0);
        assertNotEquals(derivativeLine, derivativeLine2);           
    }

    @Test
    public void testCalculateSlopeLine_TestYExp() {
        List<PlotTracePoint> originalLine = new ArrayList<>();
        originalLine.add(new PlotTracePoint(0.0, 0.0));
        originalLine.add(new PlotTracePoint(1.0, 1.0));
        originalLine.add(new PlotTracePoint(2.0, 2.0));
        originalLine.add(new PlotTracePoint(3.0, 3.0));

        slopeDerivativeLine.setYExpression("2 * x");

        List<PlotTracePoint> slopeLine = slopeDerivativeLine.calculateSlopeLine(originalLine);
        assertEquals(4, slopeLine.size());
        assertEquals("0.0", slopeLine.get(0).x);
        assertEquals("0.0", slopeLine.get(0).y);

        assertEquals("1.0", slopeLine.get(1).x);
        assertEquals("2.0", slopeLine.get(1).y);

        assertEquals("2.0", slopeLine.get(2).x);
        assertEquals("4.0", slopeLine.get(2).y);

        assertEquals("3.0", slopeLine.get(3).x);
        assertEquals("6.0", slopeLine.get(3).y);
    }

    @Test
    public void testCalculateSlopeLine_TestXExp() {
        List<PlotTracePoint> originalLine = new ArrayList<>();
        originalLine.add(new PlotTracePoint(0.0, 0.0));
        originalLine.add(new PlotTracePoint(1.0, 1.0));
        originalLine.add(new PlotTracePoint(2.0, 2.0));
        originalLine.add(new PlotTracePoint(3.0, 3.0));

        slopeDerivativeLine.setXExpression("0.5 * y");

        List<PlotTracePoint> slopeLine = slopeDerivativeLine.calculateSlopeLine(originalLine);
        assertEquals(4, slopeLine.size());
        assertEquals("0.0", slopeLine.get(0).x);
        assertEquals("0.0", slopeLine.get(0).y);

        assertEquals("0.5", slopeLine.get(1).x);
        assertEquals("1.0", slopeLine.get(1).y);

        assertEquals("1.0", slopeLine.get(2).x);
        assertEquals("2.0", slopeLine.get(2).y);

        assertEquals("1.5", slopeLine.get(3).x);
        assertEquals("3.0", slopeLine.get(3).y);
    }

    @Test
    public void testApplyOverTemplate() {
        SlopeDerivativeLine baseLine = new SlopeDerivativeLine("", testLogger);
        baseLine.setXExpression("x1");
        baseLine.setYExpression("y1");
        SlopeDerivativeLine overwriteLine = new SlopeDerivativeLine("", testLogger);
        overwriteLine.setXExpression("x2");
        overwriteLine.setYExpression("y2");

        SlopeDerivativeLine resultLine = (SlopeDerivativeLine) overwriteLine.applyOverTemplate(baseLine);

        assertEquals("x2", resultLine.getXExpression());
        assertEquals("y2", resultLine.getYExpression());
    }

    @Test
    public void testApplyOverTemplate_TypeMismatch() {
        AverageDerivativeLine baseLine = new AverageDerivativeLine("", testLogger);
        SlopeDerivativeLine overwriteLine = new SlopeDerivativeLine("", testLogger);
        assertNull(overwriteLine.applyOverTemplate(baseLine));
    }
}
