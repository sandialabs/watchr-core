package gov.sandia.watchr.graph.chartreuse.generator.plotly;

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
import gov.sandia.watchr.graph.chartreuse.CommonPlotTerms;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.graph.chartreuse.type.NodeType;
import gov.sandia.watchr.graph.chartreuse.type.PlotData;
import gov.sandia.watchr.util.RGB;

public class PlotlyTraceGeneratorTest {
	
	////////////
	// FIELDS //
	////////////

	private PlotlyWindowGenerator windowGenerator;
	private PlotlyCanvasGenerator canvasGenerator;
	private PlotlyTraceGenerator traceGenerator;
	
	private PlotWindowModel windowModel;
	private PlotCanvasModel canvasModel;
	private PlotTraceModel traceModel;
	
	private List<Double> colorAnchors;
	private List<RGB> colors;
	
	///////////
	// SETUP //
	///////////
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Before
	public void setup() {
		traceGenerator = new PlotlyTraceGenerator();
		traceGenerator.getOptions().sortAlongDimension = null;
		canvasGenerator = new PlotlyCanvasGenerator(traceGenerator);
		windowGenerator = new PlotlyWindowGenerator(canvasGenerator, false);

		canvasGenerator.setParent(windowGenerator);
        traceGenerator.setParent(canvasGenerator);

		Pair<NodeType,String>[] xHeaders = new Pair[]{
			new ImmutablePair(NodeType.VARIABLE, "x1")
        };
		
		Double[][] data = ChartreuseTestsUtil.getRosenbrockExampleData();
		PlotData tabularData = new PlotData("Run Result 1 - Tabular Data", "", data, xHeaders, null);
		
		colorAnchors = new ArrayList<>();
		colorAnchors.add(0.0);
		colorAnchors.add(500.0);
		colorAnchors.add(1000.0);
		colorAnchors.add(2000.0);
		colorAnchors.add(3000.0);
		colorAnchors.add(4000.0);
		
		colors = new ArrayList<>();
		colors.add(new RGB(255, 0, 0));
		colors.add(new RGB(255, 128, 64));
		colors.add(new RGB(255, 255, 0));
		colors.add(new RGB(0, 255, 0));
		colors.add(new RGB(0, 128, 192));
		colors.add(new RGB(128, 0, 255));
		
		RGB BLACK = new RGB(0, 0, 0);
		
		windowModel = new PlotWindowModel("Rosenbrock Rainbow Curve")
				.setViewWidth(500)
				.setViewHeight(500);
		canvasModel = new PlotCanvasModel(windowModel.getUUID())
				.setDrawGridLines(true)
				.setDrawXAxisLines(true)
					.setDrawYAxisLines(true)
				.setXAxisLabel("x1")
				.setXAxisRGB(BLACK)
				.setYAxisLabel("x2")
				.setYAxisRGB(BLACK)
				.setZAxisLabel("rosen_out")
				.setZAxisRGB(BLACK);
		
		traceModel = new PlotTraceModel(canvasModel.getUUID(), false)
			.setPoints(TestFileUtils.formatAsPoints(tabularData.getRow(0), tabularData.getRow(1), tabularData.getRow(2)))
			.setName("Surface 3D Plot: x1 / x2 / rosen_out")
			.set(PlotToken.TRACE_POINT_TYPE, PlotType.SURFACE_3D_PLOT)
			.set(PlotToken.TRACE_DRAW_COLOR_SCALE, true)
			.setColors(colors)
			.setColorScaleAnchors(colorAnchors);
	}
	
	@Test
	public void testProcessColorScaleContinuous_ForDataValues() {
		try {
			traceModel.set(PlotToken.TRACE_SCALE_COLOR_VALUE_TYPE, CommonPlotTerms.VALUE_DATA_VALUES.getLabel());
			traceModel.setColorScaleType(CommonPlotTerms.SCALE_CONTINUOUS.getLabel());
			
			String result = windowGenerator.generatePlotWindow(windowModel, PlotType.DEFAULT);			
			TestFileUtils.compareStringToTestFile(
				PlotlyTraceGeneratorTest.class,
				result, "plots/generated_from_templates/rosenbrock_3d_curve_rainbow.html"
			);
		} catch(IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testProcessColorScaleDiscrete_ForDataValues() {
		try {
			traceModel.set(PlotToken.TRACE_SCALE_COLOR_VALUE_TYPE, CommonPlotTerms.VALUE_DATA_VALUES.getLabel());
			traceModel.setColorScaleType(CommonPlotTerms.SCALE_DISCRETE.getLabel());
			
			String result = windowGenerator.generatePlotWindow(windowModel, PlotType.DEFAULT);			
			TestFileUtils.compareStringToTestFile(
				PlotlyTraceGeneratorTest.class,
				result, "plots/generated_from_templates/rosenbrock_3d_curve_rainbow_discrete.html"
			);
		} catch(IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testProcessColorScaleContinuous_ForRelativePercentageValues() {
		try {
			traceModel.set(PlotToken.TRACE_SCALE_COLOR_VALUE_TYPE, CommonPlotTerms.VALUE_RELATIVE_PERCENTAGES.getLabel());
			traceModel.setColorScaleType(CommonPlotTerms.SCALE_CONTINUOUS.getLabel());
			
			colorAnchors.clear();
			colorAnchors.add(0.0);
			colorAnchors.add(25.0);
			colorAnchors.add(50.0);
			colorAnchors.add(75.0);
			colorAnchors.add(90.0);
			colorAnchors.add(100.0);
			traceModel.setColorScaleAnchors(colorAnchors);
			
			String result = windowGenerator.generatePlotWindow(windowModel, PlotType.DEFAULT);			
			TestFileUtils.compareStringToTestFile(
				PlotlyTraceGeneratorTest.class,
				result, "plots/generated_from_templates/rosenbrock_3d_curve_rainbow_color_percentages.html"
			);
		} catch(IOException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testProcessColorScaleDiscrete_ForRelativePercentageValues() {
		try {
			traceModel.set(PlotToken.TRACE_SCALE_COLOR_VALUE_TYPE, CommonPlotTerms.VALUE_RELATIVE_PERCENTAGES.getLabel());
			traceModel.setColorScaleType(CommonPlotTerms.SCALE_DISCRETE.getLabel());
			
			colorAnchors.clear();
			colorAnchors.add(0.0);
			colorAnchors.add(25.0);
			colorAnchors.add(50.0);
			colorAnchors.add(75.0);
			colorAnchors.add(90.0);
			colorAnchors.add(100.0);
			traceModel.setColorScaleAnchors(colorAnchors);
			
			String result = windowGenerator.generatePlotWindow(windowModel, PlotType.DEFAULT);			
			TestFileUtils.compareStringToTestFile(
				PlotlyTraceGeneratorTest.class,
				result, "plots/generated_from_templates/rosenbrock_3d_curve_rainbow_discrete_color_percentages.html"
			);
		} catch(IOException e) {
			fail(e.getMessage());
		}
	}
}
