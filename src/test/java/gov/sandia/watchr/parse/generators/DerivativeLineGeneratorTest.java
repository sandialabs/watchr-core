package gov.sandia.watchr.parse.generators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.TestFileUtils;
import gov.sandia.watchr.config.derivative.AverageDerivativeLine;
import gov.sandia.watchr.config.derivative.DerivativeLine;
import gov.sandia.watchr.config.derivative.RollingDerivativeLine;
import gov.sandia.watchr.config.derivative.StdDevPositiveOffsetDerivativeLine;
import gov.sandia.watchr.config.derivative.DerivativeLineType;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.filter.DataFilter;
import gov.sandia.watchr.config.filter.FilterExpression;
import gov.sandia.watchr.config.filter.DataFilter.DataFilterPolicy;
import gov.sandia.watchr.config.filter.DataFilter.DataFilterType;
import gov.sandia.watchr.graph.chartreuse.ChartreuseException;
import gov.sandia.watchr.graph.chartreuse.Dimension;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotModelUtil;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceOptions; 
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.util.ArrayUtil;

public class DerivativeLineGeneratorTest {

    private List<String> junkXData;
    private List<String> junkYData;

    private List<DerivativeLine> lines;
    private PlotTraceModel mainDataLine;

    private StringOutputLogger testLogger;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();

        junkXData = new ArrayList<>();
        junkYData = new ArrayList<>();
        Random rand = new Random(1138);

        for(int i = 1; i <= 100; i++) {
            junkXData.add(Integer.valueOf(i).toString());
            junkYData.add(Double.valueOf(rand.nextDouble()*100.0).toString());
        }

        RollingDerivativeLine averageLine = new AverageDerivativeLine("", testLogger);
        averageLine.setRollingRange(30);
        averageLine.setIgnoreFilteredData(false);
        RollingDerivativeLine stdDevLine = new StdDevPositiveOffsetDerivativeLine("", testLogger);
        stdDevLine.setRollingRange(30);
        stdDevLine.setIgnoreFilteredData(false);

        lines = new ArrayList<>();
        lines.add(averageLine);
        lines.add(stdDevLine);

        try {
            mainDataLine = new PlotTraceModel(null);
            PlotModelUtil.newPlotCanvas(mainDataLine, "", "", "");

            mainDataLine.setPoints(TestFileUtils.formatAsPoints(
                ArrayUtil.asDoubleObjArrFromStringList(junkXData),
                ArrayUtil.asDoubleObjArrFromStringList(junkYData)
            ));
        } catch(ChartreuseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testNewDerivateLines() {
        try {
            DerivativeLineGenerator derivateLineGenerator = new DerivativeLineGenerator(mainDataLine, testLogger);
            List<WatchrDiff<?>> diffs = new ArrayList<>();
            derivateLineGenerator.generate(lines, diffs);

            PlotCanvasModel canvasModel = mainDataLine.getParent();
            PlotTraceModel avgPlotTraceModel = canvasModel.findDerivativeLine(DerivativeLineType.AVERAGE);
            PlotTraceModel stdDevPlotTraceModel = canvasModel.findDerivativeLine(DerivativeLineType.STANDARD_DEVIATION_OFFSET);
            assertNotNull(avgPlotTraceModel);
            assertNotNull(stdDevPlotTraceModel);

            assertEquals(100, avgPlotTraceModel.getPoints().size());
            assertEquals(100, stdDevPlotTraceModel.getPoints().size());
        } catch(WatchrParseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testUpdateDerivateLinesAfterFilter() {
        try {
            DerivativeLineGenerator derivateLineGenerator = new DerivativeLineGenerator(mainDataLine, testLogger);
            List<WatchrDiff<?>> diffs = new ArrayList<>();
            derivateLineGenerator.generate(lines, diffs);

            PlotCanvasModel canvasModel = mainDataLine.getParent();
            PlotTraceModel avgPlotTraceModel = canvasModel.findDerivativeLine(DerivativeLineType.AVERAGE);
            PlotTraceModel stdDevPlotTraceModel = canvasModel.findDerivativeLine(DerivativeLineType.STANDARD_DEVIATION_OFFSET);
            assertNotNull(avgPlotTraceModel);
            assertNotNull(stdDevPlotTraceModel);

            // Verify the values of the average and standard deviation lines here
            // (the actual values don't matter much - we just want to observe that
            // they change appropriately after the filter is applied).
            PlotTraceOptions options = new PlotTraceOptions();
            options.sortAlongDimension = Dimension.X;
            assertEquals("63.22491512439361", avgPlotTraceModel.getPoints(options).get(0).y);
            assertEquals("63.22491512439361", stdDevPlotTraceModel.getPoints(options).get(0).y);
            assertEquals(50.6285, Double.parseDouble(avgPlotTraceModel.getPoints(options).get(25).y), 1.0e-4);
            assertEquals(82.1042, Double.parseDouble(stdDevPlotTraceModel.getPoints(options).get(25).y), 1.0e-4);
            assertEquals(51.4087, Double.parseDouble(avgPlotTraceModel.getPoints(options).get(50).y), 1.0e-4);
            assertEquals(79.5148, Double.parseDouble(stdDevPlotTraceModel.getPoints(options).get(50).y), 1.0e-4);
            assertEquals(48.2288, Double.parseDouble(avgPlotTraceModel.getPoints(options).get(75).y), 1.0e-4);
            assertEquals(74.8522, Double.parseDouble(stdDevPlotTraceModel.getPoints(options).get(75).y), 1.0e-4);

            List<DataFilter> filters = new ArrayList<>(mainDataLine.getFilters());
            filters.add(new DataFilter(DataFilterType.POINT, new FilterExpression("x == 20.0"), DataFilterPolicy.BLACKLIST));
            filters.add(new DataFilter(DataFilterType.POINT, new FilterExpression("x == 21.0"), DataFilterPolicy.BLACKLIST));
            filters.add(new DataFilter(DataFilterType.POINT, new FilterExpression("x == 22.0"), DataFilterPolicy.BLACKLIST));
            filters.add(new DataFilter(DataFilterType.POINT, new FilterExpression("x == 47.0"), DataFilterPolicy.BLACKLIST));
            filters.add(new DataFilter(DataFilterType.POINT, new FilterExpression("x == 48.0"), DataFilterPolicy.BLACKLIST));
            filters.add(new DataFilter(DataFilterType.POINT, new FilterExpression("x == 49.0"), DataFilterPolicy.BLACKLIST));

            mainDataLine.setFilterValues(filters);

            // Regenerate the derivative lines and verify that they have changed.
            derivateLineGenerator.generate(lines, diffs);

            canvasModel = mainDataLine.getParent();
            avgPlotTraceModel = canvasModel.findDerivativeLine(DerivativeLineType.AVERAGE);
            stdDevPlotTraceModel = canvasModel.findDerivativeLine(DerivativeLineType.STANDARD_DEVIATION_OFFSET);
            assertNotNull(avgPlotTraceModel);
            assertNotNull(stdDevPlotTraceModel);

            options.filterPoints = true;

            assertEquals("63.22491512439361", avgPlotTraceModel.getPoints(options).get(0).y);
            assertEquals("63.22491512439361", stdDevPlotTraceModel.getPoints(options).get(0).y);
            assertEquals(50.6285, Double.parseDouble(avgPlotTraceModel.getPoints(options).get(25).y), 1.0e-4);
            assertEquals(82.1042, Double.parseDouble(stdDevPlotTraceModel.getPoints(options).get(25).y), 1.0e-4);
            assertEquals(51.4087, Double.parseDouble(avgPlotTraceModel.getPoints(options).get(50).y), 1.0e-4);
            assertEquals(79.5148, Double.parseDouble(stdDevPlotTraceModel.getPoints(options).get(50).y), 1.0e-4);
            assertEquals(48.2288, Double.parseDouble(avgPlotTraceModel.getPoints(options).get(75).y), 1.0e-4);
            assertEquals(74.8522, Double.parseDouble(stdDevPlotTraceModel.getPoints(options).get(75).y), 1.0e-4);
        } catch(WatchrParseException e) {
            fail(e.getMessage());
        }
    }    
}
