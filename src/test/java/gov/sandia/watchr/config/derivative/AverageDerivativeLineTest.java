package gov.sandia.watchr.config.derivative;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.util.RGB;

public class AverageDerivativeLineTest {

    private StringOutputLogger testLogger;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
    }
    
    @Test
    public void testValidate_GoodBoi() {
        AverageDerivativeLine derivativeLine = new AverageDerivativeLine("", testLogger);
        derivativeLine.setRollingRange(10);
        derivativeLine.setColor(255, 255, 255);
        derivativeLine.setIgnoreFilteredData(true);
        
        derivativeLine.validate();
        String logContents = testLogger.getLogAsString();
        assertEquals(0, logContents.length());
    }

    @Test
    public void testValidate_NoRollingRange() {
        AverageDerivativeLine derivativeLine = new AverageDerivativeLine("", testLogger);
        derivativeLine.setRollingRange(0);
        
        derivativeLine.validate();
        String logContents = testLogger.getLogAsString();
        assertTrue(logContents.contains("The rolling range for a derivative line must use 1 or more points!"));
    }

    @Test
    public void testDiffs() {
        AverageDerivativeLine derivativeLine = new AverageDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine.setColor(255, 255, 255);
        derivativeLine.setIgnoreFilteredData(false);
        derivativeLine.setRollingRange(1);

        AverageDerivativeLine derivativeLine2 =
            new AverageDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine2.setColor(255, 0, 0);
        derivativeLine2.setIgnoreFilteredData(true);
        derivativeLine2.setNumberFormat("##.##");
        derivativeLine2.setRollingRange(10);

        List<WatchrDiff<?>> diffs = derivativeLine.diff(derivativeLine2);
        assertEquals(4, diffs.size());

        WatchrDiff<?> diff1 = diffs.get(0);
        assertEquals(DiffCategory.DERIVATIVE_LINE_COLOR, diff1.getProperty());
        assertEquals("/my/path/prefix/derivativeLine/average", diff1.getPath());
        assertEquals(new RGB(255, 255, 255), diff1.getBeforeValue());
        assertEquals(new RGB(255, 0, 0), diff1.getNowValue());

        WatchrDiff<?> diff2 = diffs.get(1);
        assertEquals(DiffCategory.ROLLING_RANGE, diff2.getProperty());
        assertEquals("/my/path/prefix/derivativeLine/average", diff2.getPath());
        assertEquals(1, diff2.getBeforeValue());
        assertEquals(10, diff2.getNowValue());

        WatchrDiff<?> diff3 = diffs.get(2);
        assertEquals(DiffCategory.IGNORE_FILTERED_DATA, diff3.getProperty());
        assertEquals("/my/path/prefix/derivativeLine/average", diff3.getPath());
        assertFalse((Boolean) diff3.getBeforeValue());
        assertTrue((Boolean) diff3.getNowValue());

        WatchrDiff<?> diff4 = diffs.get(3);
        assertEquals(DiffCategory.NUMBER_FORMAT, diff4.getProperty());
        assertEquals("/my/path/prefix/derivativeLine/average", diff4.getPath());
        assertEquals("", diff4.getBeforeValue());
        assertEquals("##.##",diff4.getNowValue());
    }

    @Test
    public void testCopyAndHashCode() {
        AverageDerivativeLine derivativeLine = new AverageDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine.setColor(255, 255, 255);
        derivativeLine.setIgnoreFilteredData(false);
        derivativeLine.setRollingRange(1);

        AverageDerivativeLine derivativeLine2 = new AverageDerivativeLine(derivativeLine);
        assertEquals(derivativeLine.hashCode(), derivativeLine2.hashCode());
    }

    @Test
    public void testCopyAndEquals() {
        AverageDerivativeLine derivativeLine = new AverageDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine.setColor(255, 255, 255);
        derivativeLine.setIgnoreFilteredData(false);
        derivativeLine.setRollingRange(1);

        RollingDerivativeLine derivativeLine2 = new AverageDerivativeLine(derivativeLine);
        assertEquals(derivativeLine, derivativeLine2);        
    }

    @Test
    public void testCopyAndNotEquals() {
        AverageDerivativeLine derivativeLine = new AverageDerivativeLine("/my/path/prefix", testLogger);
        derivativeLine.setColor(255, 255, 255);
        derivativeLine.setIgnoreFilteredData(false);
        derivativeLine.setRollingRange(1);

        AverageDerivativeLine derivativeLine2 = new AverageDerivativeLine(derivativeLine);
        derivativeLine2.setColor(0, 0, 0);
        assertNotEquals(derivativeLine, derivativeLine2);           
    }

    @Test
    public void testAvg_ManyDigitsOfPrecision() {
        final List<PlotTracePoint> points = new ArrayList<>();
        points.add(new PlotTracePoint("'2018-10-02T09:28:28'", "0.001"));
        points.add(new PlotTracePoint("'2018-10-02T21:35:35'", "0.001"));
        points.add(new PlotTracePoint("'2018-10-03T22:05:05'", "0.001"));
        points.add(new PlotTracePoint("'2018-10-04T08:16:16'", "0.001"));
        points.add(new PlotTracePoint("'2018-10-04T22:24:24'", "0.001"));
        points.add(new PlotTracePoint("'2018-10-05T21:35:35'", "0.001"));
        points.add(new PlotTracePoint("'2018-10-06T21:37:37'", "0.001"));
        points.add(new PlotTracePoint("'2018-10-07T21:33:33'", "0.001"));
        points.add(new PlotTracePoint("'2018-10-08T21:07:07',", "0.001"));
        points.add(new PlotTracePoint("'2018-10-09T21:54:54',", "0.001"));
        points.add(new PlotTracePoint("'2018-10-10T21:09:09',", "0.001"));
        points.add(new PlotTracePoint("'2018-10-11T21:14:14',", "0.001"));
        points.add(new PlotTracePoint("'2018-10-12T21:42:42',", "0.001"));
        points.add(new PlotTracePoint("'2018-10-13T21:57:57',", "0.001"));
        points.add(new PlotTracePoint("'2018-10-14T21:07:07',", "0.001"));
        points.add(new PlotTracePoint("'2018-10-15T21:15:15',", "0.001"));
        points.add(new PlotTracePoint("'2018-10-17T21:39:39',", "0.001"));
        points.add(new PlotTracePoint("'2018-10-18T11:03:03',", "0.001"));
        points.add(new PlotTracePoint("'2018-10-18T21:45:45',", "0.001"));
        points.add(new PlotTracePoint("'2018-10-19T22:33:33',", "0.001"));
        points.add(new PlotTracePoint("'2018-10-20T21:27:27',", "0.001"));
        points.add(new PlotTracePoint("'2018-10-22T21:12:12',", "0.001"));
        points.add(new PlotTracePoint("'2018-10-23T20:13:13',", "0.001"));

        // Average should remain the same over time, since there is no
        // variation in the values.
        AverageDerivativeLine derivativeLine = new AverageDerivativeLine("", testLogger);
        derivativeLine.setRollingRange(10);
        derivativeLine.setNumberFormat("#.####");
        List<PlotTracePoint> averagePoints = derivativeLine.calculateRollingLine(points);

        for(PlotTracePoint averagePoint : averagePoints) {
            assertEquals(0.001, Double.parseDouble(averagePoint.y), 1.0e-20);
        }
    }

    @Test
    public void testApplyOverTemplate() {
        AverageDerivativeLine baseLine = new AverageDerivativeLine("", testLogger);
        baseLine.setRollingRange(20);
        baseLine.setNumberFormat("#.#");
        AverageDerivativeLine overwriteLine = new AverageDerivativeLine("", testLogger);
        overwriteLine.setRollingRange(10);
        overwriteLine.setNumberFormat("#.####");

        AverageDerivativeLine resultLine = (AverageDerivativeLine) overwriteLine.applyOverTemplate(baseLine);

        assertEquals(10, resultLine.getRollingRange());
        assertEquals("#.####", resultLine.getNumberFormat());
    }

    @Test
    public void testApplyOverTemplate_TypeMismatch() {
        SlopeDerivativeLine baseLine = new SlopeDerivativeLine("", testLogger);
        AverageDerivativeLine overwriteLine = new AverageDerivativeLine("", testLogger);
        assertNull(overwriteLine.applyOverTemplate(baseLine));
    }
}