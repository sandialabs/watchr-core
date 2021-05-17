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
import gov.sandia.watchr.config.DerivativeLine;
import gov.sandia.watchr.config.DerivativeLine.DerivativeLineType;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.graph.chartreuse.Dimension;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotModelUtil;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceOptions;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.util.ArrayUtil;

public class DerivativeLineGeneratorTest {

    private List<String> junkXData;
    private List<String> junkYData;

    private List<DerivativeLine> lines;
    private PlotTraceModel mainDataLine;

    @Before
    public void setup() {
        junkXData = new ArrayList<>();
        junkYData = new ArrayList<>();
        Random rand = new Random(1138);

        for(int i = 1; i <= 100; i++) {
            junkXData.add(new Integer(i).toString());
            junkYData.add(new Double(rand.nextDouble()*100.0).toString());
        }

        DerivativeLine averageLine = new DerivativeLine("");
        averageLine.setType(DerivativeLineType.AVERAGE);
        averageLine.setRollingRange(30);
        averageLine.setIgnoreFilteredData(false);
        DerivativeLine stdDevLine = new DerivativeLine("");
        stdDevLine.setType(DerivativeLineType.STANDARD_DEVIATION_OFFSET);
        stdDevLine.setRollingRange(30);
        stdDevLine.setIgnoreFilteredData(false);

        lines = new ArrayList<>();
        lines.add(averageLine);
        lines.add(stdDevLine);

        mainDataLine = new PlotTraceModel(null);
        PlotModelUtil.newPlotCanvas(mainDataLine, "", "", "");

        mainDataLine.setPoints(TestFileUtils.formatAsPoints(
            ArrayUtil.asDoubleObjArrFromStringList(junkXData),
            ArrayUtil.asDoubleObjArrFromStringList(junkYData)
        ));
    }

    @Test
    public void testNewDerivateLines() {
        try {
            DerivativeLineGenerator derivateLineGenerator = new DerivativeLineGenerator(mainDataLine);
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
            DerivativeLineGenerator derivateLineGenerator = new DerivativeLineGenerator(mainDataLine);
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
            assertEquals("50.62852188332792", avgPlotTraceModel.getPoints(options).get(25).y);
            assertEquals("82.10424068213396", stdDevPlotTraceModel.getPoints(options).get(25).y);
            assertEquals("51.40874307909452", avgPlotTraceModel.getPoints(options).get(50).y);
            assertEquals("79.51475833561194", stdDevPlotTraceModel.getPoints(options).get(50).y);
            assertEquals("48.22879534494226", avgPlotTraceModel.getPoints(options).get(75).y);
            assertEquals("74.85223654139949", stdDevPlotTraceModel.getPoints(options).get(75).y);

            List<PlotTracePoint> filteredValues = new ArrayList<>(mainDataLine.getFilterValues());
            filteredValues.add(new PlotTracePoint("20.0", ""));
            filteredValues.add(new PlotTracePoint("21.0", ""));
            filteredValues.add(new PlotTracePoint("22.0", ""));
            filteredValues.add(new PlotTracePoint("47.0", ""));
            filteredValues.add(new PlotTracePoint("48.0", ""));
            filteredValues.add(new PlotTracePoint("49.0", ""));

            mainDataLine.setFilterValues(filteredValues);

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
            assertEquals("50.62852188332792", avgPlotTraceModel.getPoints(options).get(25).y);
            assertEquals("82.10424068213396", stdDevPlotTraceModel.getPoints(options).get(25).y);
            assertEquals("51.40874307909452", avgPlotTraceModel.getPoints(options).get(50).y);
            assertEquals("79.51475833561194", stdDevPlotTraceModel.getPoints(options).get(50).y);
            assertEquals("48.22879534494226", avgPlotTraceModel.getPoints(options).get(75).y);
            assertEquals("74.85223654139949", stdDevPlotTraceModel.getPoints(options).get(75).y);
        } catch(WatchrParseException e) {
            fail(e.getMessage());
        }
    }    
}
