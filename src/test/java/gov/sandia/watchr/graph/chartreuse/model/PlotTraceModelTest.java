package gov.sandia.watchr.graph.chartreuse.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.TestFileUtils;
import gov.sandia.watchr.graph.chartreuse.ChartreuseTestsUtil;
import gov.sandia.watchr.graph.chartreuse.CommonPlotTerms;
import gov.sandia.watchr.graph.chartreuse.Dimension;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.util.RGB;

public class PlotTraceModelTest {
	
	////////////
	// FIELDS //
	////////////
	
	private Random randomSeed;
	
	////////////
	// BEFORE //
	////////////
	
	@Before
	public void setup() {
		// We use fixed random seeds to ensure consistent behavior on repeated unit test execution.
		randomSeed = new Random(1337);
	}
	
	///////////
	// TESTS //
	///////////

	@Test
	public void testCopyConstructor() {
		PlotTraceModel traceModel1 = new PlotTraceModel(null);
		traceModel1.setName("name");
		traceModel1.setTrimNoDelta(Dimension.X);
		traceModel1.setRelativeAxis(Dimension.Y);
		traceModel1.add(new PlotTracePoint("1", "2", "3"));
		traceModel1.setPrimaryRGB(new RGB(255, 0, 0));
		traceModel1.getColorScaleAnchors().add(100.0);
		traceModel1.setColorScaleType("colorScaleType");
		traceModel1.getProperties().put(PlotToken.TRACE_BOUND_LOWER, Integer.toString(Integer.MIN_VALUE));
		traceModel1.getProperties().put(PlotToken.TRACE_BOUND_UPPER, Integer.toString(Integer.MAX_VALUE));
		traceModel1.getProperties().put(PlotToken.TRACE_PRECISION, Integer.toString(-1));
		traceModel1.getProperties().put(PlotToken.TRACE_DRAW_NUMBER_LABELS, Boolean.TRUE.toString());
		traceModel1.getProperties().put(PlotToken.TRACE_POINT_MODE, "Circle");
		traceModel1.getProperties().put(PlotToken.TRACE_ORIENTATION, CommonPlotTerms.ORIENTATION_HORIZONTAL.getLabel());
		traceModel1.addFilterValue(new PlotTracePoint("4", "5", "6"));
		
		PlotCanvasModel canvasModel = new PlotCanvasModel(null);
		PlotTraceModel traceModel2 = new PlotTraceModel(canvasModel.getUUID(), traceModel1);
		assertEquals("name", traceModel2.getName());
		assertEquals(Dimension.X, traceModel2.getTrimNoDelta());
		assertEquals(Dimension.Y, traceModel2.getRelativeAxis());
		assertEquals("1", traceModel2.getPoints().get(0).x);
		assertEquals("2", traceModel2.getPoints().get(0).y);
		assertEquals("3", traceModel2.getPoints().get(0).z);
		assertEquals(new RGB(255, 0, 0), traceModel2.getPrimaryColor());
		assertEquals(100.0, traceModel2.getColorScaleAnchors().get(2), 1.0e-4);
		assertEquals("colorScaleType", traceModel2.getColorScaleType());
		assertEquals(new PlotTracePoint("4", "5", "6"), traceModel2.getFilterValues().iterator().next());

		assertEquals(Integer.toString(Integer.MIN_VALUE), traceModel2.getProperties().get(PlotToken.TRACE_BOUND_LOWER));
		assertEquals(Integer.toString(Integer.MAX_VALUE), traceModel2.getProperties().get(PlotToken.TRACE_BOUND_UPPER));
		assertEquals(Integer.toString(-1), traceModel2.getProperties().get(PlotToken.TRACE_PRECISION));
		assertEquals(Boolean.TRUE.toString(), traceModel2.getProperties().get(PlotToken.TRACE_DRAW_NUMBER_LABELS));
		assertEquals("Circle", traceModel2.getProperties().get(PlotToken.TRACE_POINT_MODE));
		assertEquals(CommonPlotTerms.ORIENTATION_HORIZONTAL.getLabel(), traceModel2.getProperties().get(PlotToken.TRACE_ORIENTATION));
	}
	
	@Test
	public void testIsThreeDimensional() {
		PlotWindowModel windowModel = new PlotWindowModel("");
		PlotCanvasModel canvasModel = new PlotCanvasModel(windowModel.getUUID());
		PlotTraceModel traceModel = new PlotTraceModel(canvasModel.getUUID(), false);
		traceModel.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed),
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed)
		));
		assertFalse(traceModel.isThreeDimensional());
		
		traceModel.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed),
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed),
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed)
		));
		assertTrue(traceModel.isThreeDimensional());
	}
	
	@Test
	public void testIsThreeDimensionalRendered() {
		PlotWindowModel windowModel = new PlotWindowModel("");
		PlotCanvasModel canvasModel = new PlotCanvasModel(windowModel.getUUID());
		PlotTraceModel traceModel = new PlotTraceModel(canvasModel.getUUID(), false);
		traceModel.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed),
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed)
		));
		assertFalse(traceModel.isThreeDimensionalRendered());
		
		// PlotTraceModels can have third-dimensional data and still not be considered a "3D rendered" plot.		
		traceModel.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed),
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed),
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed)
		));
		assertFalse(traceModel.isThreeDimensionalRendered());
		
		// The only true test is to see if the PlotType is of a certain type.
		traceModel.set(PlotToken.TRACE_POINT_TYPE, PlotType.SCATTER_3D_PLOT);
		assertTrue(traceModel.isThreeDimensionalRendered());
		traceModel.set(PlotToken.TRACE_POINT_TYPE, PlotType.SURFACE_3D_PLOT);
		assertTrue(traceModel.isThreeDimensionalRendered());
	}
	
	@Test
	public void testAreColorAnchorValuesOutOfOrder_Good() {
		PlotTraceModel traceModel = new PlotTraceModel(null, false);
		traceModel.getColorScaleAnchors().add(0.0);
		traceModel.getColorScaleAnchors().add(1.0);
		traceModel.getColorScaleAnchors().add(2.0);
		assertFalse(traceModel.areColorAnchorValuesOutOfOrder());
	}
	
	@Test
	public void testAreColorAnchorValuesOutOfOrder_Bad() {
		PlotTraceModel traceModel = new PlotTraceModel(null, false);
		traceModel.getColorScaleAnchors().add(2.0);
		traceModel.getColorScaleAnchors().add(1.0);
		traceModel.getColorScaleAnchors().add(0.0);
		assertTrue(traceModel.areColorAnchorValuesOutOfOrder());
	}
	
	@Test
	public void testAreColorAnchorValuesOutsideRange_Good() {
		PlotTraceModel traceModel = new PlotTraceModel(null, false);
		traceModel.getColorScaleAnchors().add(0.0);
		traceModel.getColorScaleAnchors().add(50.0);
		traceModel.getColorScaleAnchors().add(100.0);
		assertFalse(traceModel.areColorAnchorValuesOutsideRange(0.0, 100.0));
	}
	
	@Test
	public void testAreColorAnchorValuesOutsideRange_Bad() {
		PlotTraceModel traceModel = new PlotTraceModel(null, false);
		traceModel.getColorScaleAnchors().add(-100.0);
		traceModel.getColorScaleAnchors().add(0.0);
		traceModel.getColorScaleAnchors().add(100.0);
		assertTrue(traceModel.areColorAnchorValuesOutsideRange(0.0, 100.0));
		
		traceModel.getColorScaleAnchors().clear();
		traceModel.getColorScaleAnchors().add(0.0);
		traceModel.getColorScaleAnchors().add(50.0);
		traceModel.getColorScaleAnchors().add(150.0);
		assertTrue(traceModel.areColorAnchorValuesOutsideRange(0.0, 100.0));
	}
	
	@Test
	public void testIsOnlyOneColorAnchorValue() {
		PlotTraceModel traceModel = new PlotTraceModel(null, false);
		assertTrue(traceModel.isOnlyOneColorAnchorValue());
		
		traceModel.getColorScaleAnchors().add(100.0);
		assertFalse(traceModel.isOnlyOneColorAnchorValue());
	}
	
	@Test
	public void testGetVersion() {
		PlotTraceModel traceModel = new PlotTraceModel(null, false);
		assertEquals("3.0", traceModel.getVersion());
	}
	
	@Test
	public void testGetDefaultProperties() {
		PlotTraceModel traceModel = new PlotTraceModel(null);
		assertEquals(traceModel.get(PlotToken.TRACE_BOUND_LOWER), Integer.toString(Integer.MIN_VALUE));
		assertEquals(traceModel.get(PlotToken.TRACE_BOUND_UPPER), Integer.toString(Integer.MAX_VALUE));
		assertEquals(traceModel.get(PlotToken.TRACE_PRECISION), Integer.toString(-1));
		assertEquals(traceModel.get(PlotToken.TRACE_DRAW_NUMBER_LABELS), Boolean.TRUE.toString());
	}
}

