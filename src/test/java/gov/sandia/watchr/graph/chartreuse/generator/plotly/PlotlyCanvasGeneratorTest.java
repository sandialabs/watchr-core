package gov.sandia.watchr.graph.chartreuse.generator.plotly;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.TestFileUtils;
import gov.sandia.watchr.graph.chartreuse.ChartreuseTestsUtil;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.graph.chartreuse.type.NodeType;
import gov.sandia.watchr.graph.chartreuse.type.PlotData;

public class PlotlyCanvasGeneratorTest {

	////////////
	// FIELDS //
	////////////

	private PlotlyWindowGenerator windowGenerator;
	private PlotlyCanvasGenerator canvasGenerator;
	private PlotlyTraceGenerator traceGenerator;
	
	private PlotWindowModel windowModel;
	private PlotCanvasModel canvasModel;
	private PlotTraceModel  traceModel;
	
	///////////
	// SETUP //
	///////////
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	public void setup() {
		traceGenerator = new PlotlyTraceGenerator();
		traceGenerator.getOptions().sortAlongDimension = null;
		traceGenerator.getOptions().filterPoints = false;
		canvasGenerator = new PlotlyCanvasGenerator(traceGenerator);
		windowGenerator = new PlotlyWindowGenerator(canvasGenerator, false);

		canvasGenerator.setParent(windowGenerator);
        traceGenerator.setParent(canvasGenerator);

		Pair<NodeType,String>[] xHeaders = new Pair[]{
			new ImmutablePair(NodeType.VARIABLE, "x1")
        };
		
		Double[][] data = ChartreuseTestsUtil.getRosenbrockExampleData();
		PlotData tabularData = new PlotData("Run Result 1 - Tabular Data", "", data, xHeaders, null);
		
		windowModel = new PlotWindowModel("Rosenbrock Rainbow Curve")
				.setViewHeight(500)
				.setViewWidth(500);
		canvasModel = new PlotCanvasModel(windowModel.getUUID())
				.setDrawGridLines(true)
				.setDrawAxisLines(true)
				.setXAxisLabel("x1")
				.setYAxisLabel("rosen_out");
		
		traceModel = new PlotTraceModel(canvasModel.getUUID(), false)
			.setPoints(TestFileUtils.formatAsPoints(tabularData.getRow(0), tabularData.getRow(2)))
			.setName("Scatter Plot")
			.set(PlotToken.TRACE_POINT_TYPE, PlotType.SCATTER_PLOT)
			.set(PlotToken.TRACE_POINT_MODE, "Circle");
	}
	
	@Test
	public void testProcessCanvasAxisPrecision_DefaultPrecision() {
		try {
			String result = windowGenerator.generatePlotWindow(windowModel, PlotType.DEFAULT);			
			TestFileUtils.compareStringToTestFile(
				PlotlyCanvasGeneratorTest.class,
				result, "plots/generated_from_templates/rosenbrock_2d_canvas_precision_default.html"
			);
		} catch(IOException e) {
			fail(e.getMessage());
		}	
	}
	
	@Test
	public void testProcessCanvasAxisPrecision_OneDecimalPrecision() {
		try {
			canvasModel.setAxisPrecision(1);
			String result = windowGenerator.generatePlotWindow(windowModel, PlotType.DEFAULT);			
			TestFileUtils.compareStringToTestFile(
				PlotlyCanvasGeneratorTest.class,
				result, "plots/generated_from_templates/rosenbrock_2d_canvas_precision_one.html"
			);
		} catch(IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testProcessCanvasAxisPrecision_NegativeDecimalPrecision() {
		try {
			canvasModel.setAxisPrecision(-1);
			String result = windowGenerator.generatePlotWindow(windowModel, PlotType.DEFAULT);			
			TestFileUtils.compareStringToTestFile(
				PlotlyCanvasGeneratorTest.class,
				result, "plots/generated_from_templates/rosenbrock_2d_canvas_precision_negative.html"
			);
		} catch(IOException e) {
			fail(e.getMessage());
		}	
	}

	@Test
	public void testProcessCanvasYDisplayedRangeEnd_ForZeroLine() {
		PlotTracePoint point1 = new PlotTracePoint("2018-10-02T10:19:19", "0.0");
		PlotTracePoint point2 = new PlotTracePoint("2018-10-02T22:24:24", "0.0");
		PlotTracePoint point3 = new PlotTracePoint("2018-10-04T00:55:55", "0.0");
		List<PlotTracePoint> zeroPoints = new ArrayList<>();
		zeroPoints.add(point1);
		zeroPoints.add(point2);
		zeroPoints.add(point3);

		canvasModel.setXAxisRangeStart(Double.NEGATIVE_INFINITY);
		canvasModel.setXAxisRangeEnd(Double.POSITIVE_INFINITY);

		traceModel.setPoints(zeroPoints);
		canvasGenerator.setPlotCanvasModel(canvasModel);
		String rangeEnd = canvasGenerator.processCanvasYDisplayedRangeEnd();
		assertEquals("0.0", rangeEnd);
	}
}
