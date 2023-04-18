package gov.sandia.watchr.parse.generators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.DataFilterConfig;
import gov.sandia.watchr.config.filter.DataFilter;
import gov.sandia.watchr.config.filter.FilterExpression;
import gov.sandia.watchr.config.filter.DataFilter.DataFilterPolicy;
import gov.sandia.watchr.config.filter.DataFilter.DataFilterType;
import gov.sandia.watchr.graph.chartreuse.ChartreuseException;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.log.StringOutputLogger;
import gov.sandia.watchr.parse.WatchrParseException;

public class FilterConfigGeneratorTest {
    
    private StringOutputLogger testLogger;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
    }

    @Test
    public void testGenerate() {
        try {
            PlotTraceModel traceModel = createBasicPlotWindowModel();

            DataFilterConfig filterConfig = new DataFilterConfig("/my/test/path", testLogger);

			DataFilter filter =
            new DataFilter(
                DataFilterType.POINT,
                new FilterExpression("x == 2.0 && y == 20.0"),
                DataFilterPolicy.BLACKLIST);
            filterConfig.getFilters().add(filter);

            FilterConfigGenerator generator = new FilterConfigGenerator(traceModel, true, testLogger);
            generator.generate(filterConfig, new ArrayList<>());

            assertFalse(traceModel.isPointFiltered(new PlotTracePoint(1.0, 10.0)));
            assertTrue(traceModel.isPointFiltered(new PlotTracePoint(2.0, 20.0)));
            assertFalse(traceModel.isPointFiltered(new PlotTracePoint(3.0, 30.0)));
        } catch(WatchrParseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGenerate_WithMultipleFilteredPoints() {
        try {
            PlotTraceModel traceModel = createBasicPlotWindowModel();

            DataFilterConfig filterConfig1 = new DataFilterConfig("/my/test/path", testLogger);
            DataFilter filter1 =
                new DataFilter(
                    DataFilterType.POINT,
                    new FilterExpression("x == 2.0 && y == 20.0"),
                    DataFilterPolicy.BLACKLIST);
            filterConfig1.getFilters().add(filter1);
            DataFilterConfig filterConfig2 = new DataFilterConfig("/my/test/path", testLogger);
            DataFilter filter2 =
                new DataFilter(
                    DataFilterType.POINT,
                    new FilterExpression("x == 3.0 && y == 30.0"),
                    DataFilterPolicy.BLACKLIST);
            filterConfig2.getFilters().add(filter2);

            FilterConfigGenerator filterGenerator1 = new FilterConfigGenerator(traceModel, true, testLogger);
            filterGenerator1.generate(filterConfig1, new ArrayList<>());

            // Note that "clearFilterValuesBeforeApplying" is now false, so the originally
            // filtered point should still apply.
            FilterConfigGenerator filterGenerator2 = new FilterConfigGenerator(traceModel, false, testLogger);
            filterGenerator2.generate(filterConfig2, new ArrayList<>());

            assertFalse(traceModel.isPointFiltered(new PlotTracePoint(1.0, 10.0)));
            assertTrue(traceModel.isPointFiltered(new PlotTracePoint(2.0, 20.0)));
            assertTrue(traceModel.isPointFiltered(new PlotTracePoint(3.0, 30.0)));
        } catch(WatchrParseException e) {
            fail(e.getMessage());
        }
    }

    private PlotTraceModel createBasicPlotWindowModel() {
        try {
            PlotWindowModel windowModel = new PlotWindowModel("Test");
            PlotCanvasModel canvasModel = new PlotCanvasModel(windowModel.getUUID());
            PlotTraceModel traceModel = new PlotTraceModel(canvasModel.getUUID());

            PlotTracePoint point1 = new PlotTracePoint(1.0, 10.0);
            PlotTracePoint point2 = new PlotTracePoint(2.0, 20.0);
            PlotTracePoint point3 = new PlotTracePoint(3.0, 30.0);

            traceModel.add(point1);
            traceModel.add(point2);
            traceModel.add(point3);

            return traceModel;
        } catch(ChartreuseException e) {
            fail(e.getMessage());
        }
        return null;
    }
}
