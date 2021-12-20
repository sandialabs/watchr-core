package gov.sandia.watchr.graph.chartreuse.generator.plotly;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.TestFileUtils;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.graph.chartreuse.type.NodeType;
import gov.sandia.watchr.graph.chartreuse.type.PlotData;
import gov.sandia.watchr.util.RGB;

public class ContourPlotCompositeTest {

	private PlotlyWindowGenerator windowGenerator;
	private PlotlyCanvasGenerator canvasGenerator;
	private PlotlyTraceGenerator traceGenerator;

	private PlotData tabularData;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	public void setup() {
		try {
			traceGenerator = new PlotlyTraceGenerator();
			traceGenerator.getOptions().sortAlongDimension = null;
			canvasGenerator = new PlotlyCanvasGenerator(traceGenerator);
			windowGenerator = new PlotlyWindowGenerator(canvasGenerator, false);

			Pair<NodeType,String>[] xHeaders = new Pair[]{
				new ImmutablePair(NodeType.VARIABLE, "x1")
	        };
			
			Double[][] data = new Double[][]{
				{ -2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0,
				  -2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0,
				  -2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0,
				  -2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0,
				  -2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0,
				  -2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0,
				  -2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0,
				  -2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0,
				  -2.0, -1.5, -1.0, -0.5, 0.0, 0.5, 1.0, 1.5, 2.0 },
				{ -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0,
				  -1.5, -1.5, -1.5, -1.5, -1.5, -1.5, -1.5, -1.5, -1.5,
				  -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0,
				  -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5, -0.5,
				   0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,
				   0.5,  0.5,  0.5,  0.5,  0.5,  0.5,  0.5,  0.5,  0.5,
				   1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,
				   1.5,  1.5,  1.5,  1.5,  1.5,  1.5,  1.5,  1.5,  1.5,
				   2.0,  2.0,  2.0,  2.0,  2.0,  2.0,  2.0,  2.0,  2.0 },
				{ 3609.0, 1812.5, 904.0, 508.5, 401.0, 506.5, 900.0, 1806.5, 3601.0, 3034.0, 1412.5, 629.0, 308.5, 226.0, 306.5, 625.0, 1406.5, 3026.0, 2509.0, 1062.5, 404.0, 158.5, 101.0, 156.5, 400.0, 1056.5, 2501.0, 2034.0, 762.5, 229.0, 58.5, 26.0, 56.5, 225.0, 756.5, 2026.0, 1609.0, 512.5, 104.0, 8.5, 1.0, 6.5, 100.0, 506.5, 1601.0, 1234.0, 312.5, 29.0, 8.5, 26.0, 6.5, 25.0, 306.5, 1226.0, 909.0, 162.5, 4.0, 58.5, 101.0, 56.5, 0.0, 156.5, 901.0, 634.0, 62.5, 29.0, 158.5, 226.0, 156.5, 25.0, 56.5, 626.0, 409.0, 12.5, 104.0, 308.5, 401.0, 306.5, 100.0, 6.5, 401.0}
			};
			
			tabularData = new PlotData("Contour Plot - Tabular Data Set 1", "", data, xHeaders, null);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void testContourPlot() {
		try {
			RGB BLACK = new RGB(0, 0, 0);
			
			List<Double> colorAnchors = new ArrayList<>();
			colorAnchors.add(0.0);
			colorAnchors.add(1000.0);
			
			List<RGB> colors = new ArrayList<>();
			colors.add(BLACK);
			colors.add(BLACK);
			
			PlotWindowModel windowModel = new PlotWindowModel("Contour Plot - Tabular Data Set 1")
					.setViewWidth(500)
					.setViewHeight(500);
			PlotCanvasModel canvasModel = new PlotCanvasModel(windowModel.getUUID())
					.setDrawGridLines(true)
					.setDrawXAxisLines(true)
					.setDrawYAxisLines(true)
					.setXAxisLabel("x1")
					.setXAxisRGB(BLACK)
					.setYAxisLabel("x2")
					.setYAxisRGB(BLACK)
					.setZAxisLabel("response_fn_1")
					.setZAxisRGB(BLACK);
			
			new PlotTraceModel(canvasModel.getUUID(), false)
				.setPoints(TestFileUtils.formatAsPoints(tabularData.getRow(0), tabularData.getRow(1), tabularData.getRow(2)))
				.setName("Contour Plot: x1 / x2 / response_fn_1")
				.set(PlotToken.TRACE_POINT_TYPE, PlotType.CONTOUR_PLOT)
				.set(PlotToken.TRACE_BOUND_LOWER, 0)
				.set(PlotToken.TRACE_BOUND_UPPER, 500)
				.set(PlotToken.TRACE_PRECISION, 100)
				.set(PlotToken.TRACE_POINT_MODE, "heatmap")
				.set(PlotToken.TRACE_DRAW_COLOR_SCALE, false)
				.setColors(colors)
				.setColorScaleAnchors(colorAnchors);
			
			String result = windowGenerator.generatePlotWindow(windowModel, PlotType.DEFAULT);			
			TestFileUtils.compareStringToTestFile(
				ContourPlotCompositeTest.class,
				result, "plots/generated_from_templates/rosenbrock_contour_plot.html"
			);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}
}
