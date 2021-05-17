package gov.sandia.watchr.graph.chartreuse.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.TestFileUtils;
import gov.sandia.watchr.graph.chartreuse.ChartreuseTestsUtil;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.rule.actors.RuleFailActor;

public class PlotWindowModelTest {
	
	////////////
	// FIELDS //
	////////////
	
	private Random randomSeed1;
	
	////////////
	// BEFORE //
	////////////
	
	@Before
	public void setup() {
		// We use fixed random seeds to ensure consistent behavior on repeated unit test execution.
		randomSeed1 = new Random(1337);
	}
	
	///////////
	// TESTS //
	///////////
	
	@Test
	public void testIs3D() {
		PlotWindowModel windowModel = new PlotWindowModel("");
		PlotCanvasModel canvasModel = new PlotCanvasModel(windowModel.getUUID());
		PlotTraceModel traceModel = new PlotTraceModel(canvasModel.getUUID());
		traceModel.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed1),
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed1)
		));
		assertFalse(canvasModel.is3DCanvasModel());
		
		traceModel.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed1),
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed1),
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed1)
		));
		assertTrue(canvasModel.is3DCanvasModel());
	}
	
	@Test
	public void testIs3DRendered() {
		PlotWindowModel windowModel = new PlotWindowModel("");
		PlotCanvasModel canvasModel = new PlotCanvasModel(windowModel.getUUID());
		PlotTraceModel traceModel = new PlotTraceModel(canvasModel.getUUID());
		traceModel.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed1),
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed1)
		));
		assertFalse(canvasModel.is3DRenderedCanvasModel());
		assertFalse(windowModel.is3DWindowModel());
		
		traceModel.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed1),
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed1),
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed1)
		));
		assertFalse(canvasModel.is3DRenderedCanvasModel());
		assertFalse(windowModel.is3DWindowModel());
		
		traceModel.set(PlotToken.TRACE_POINT_TYPE, PlotType.HEAT_MAP_3D);
		assertFalse(canvasModel.is3DRenderedCanvasModel());
		assertFalse(windowModel.is3DWindowModel());
		
		traceModel.set(PlotToken.TRACE_POINT_TYPE, PlotType.SURFACE_3D_PLOT);
		assertTrue(canvasModel.is3DRenderedCanvasModel());
		assertTrue(windowModel.is3DWindowModel());
	}
	
	@Test
	public void testGetPlotHeuristicWidthForSparseTrace() {
		PlotWindowModel windowModel = new PlotWindowModel("");
		PlotCanvasModel canvasModel = new PlotCanvasModel(windowModel.getUUID());
		PlotTraceModel traceModel = new PlotTraceModel(canvasModel.getUUID());
		traceModel.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed1),
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed1)
		));
		
		int heuristicWidth = windowModel.getHeuristicWidth();
		assertEquals(1024, heuristicWidth);
	}

	
	@Test
	public void testGetPlotHeuristicHeightForSparseTrace() {
		PlotWindowModel windowModel = new PlotWindowModel("");
		PlotCanvasModel canvasModel = new PlotCanvasModel(windowModel.getUUID());
		PlotTraceModel traceModel = new PlotTraceModel(canvasModel.getUUID());
		traceModel.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed1),
			ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed1)
		));
		
		int heuristicHeight = windowModel.getHeuristicHeight();
		assertEquals(768, heuristicHeight);
	}
	
	@Test
	public void testGetPlotHeuristicWidthForDenseTrace() {
		PlotWindowModel windowModel = new PlotWindowModel("");
		PlotCanvasModel canvasModel = new PlotCanvasModel(windowModel.getUUID());
		PlotTraceModel traceModel = new PlotTraceModel(canvasModel.getUUID());	
		traceModel.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(2000, randomSeed1),
			ChartreuseTestsUtil.doubleObjArrayOfSize(2000, randomSeed1)
		));
		
		int heuristicWidth = windowModel.getHeuristicWidth();
		assertEquals(2000, heuristicWidth);
	}

	
	@Test
	public void testGetPlotHeuristicHeightForDenseTrace() {
		PlotWindowModel windowModel = new PlotWindowModel("");
		PlotCanvasModel canvasModel = new PlotCanvasModel(windowModel.getUUID());
		PlotTraceModel traceModel = new PlotTraceModel(canvasModel.getUUID());
		traceModel.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(4000, randomSeed1),
			ChartreuseTestsUtil.doubleObjArrayOfSize(4000, randomSeed1)
		));
		
		int heuristicHeight = windowModel.getHeuristicHeight();
		assertEquals(4000, heuristicHeight);
	}
	
	@Test
	public void testGetPlotHeuristicWidthForMultipleSparseCanvases() {
		PlotWindowModel windowModel = new PlotWindowModel("");
		PlotCanvasModel canvasModel1 = new PlotCanvasModel(windowModel.getUUID());
		PlotTraceModel traceModel1 = new PlotTraceModel(canvasModel1.getUUID());
		traceModel1.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(200, randomSeed1),
			ChartreuseTestsUtil.doubleObjArrayOfSize(200, randomSeed1)
		));
		
		PlotCanvasModel canvasModel2 = new PlotCanvasModel(windowModel.getUUID());
		PlotTraceModel traceModel2 = new PlotTraceModel(canvasModel2.getUUID());
		traceModel2.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(200, randomSeed1),
			ChartreuseTestsUtil.doubleObjArrayOfSize(200, randomSeed1)
		));
		
		int heuristicWidth = windowModel.getHeuristicWidth();
		assertEquals(1024, heuristicWidth);
	}
	
	@Test
	public void testGetPlotHeuristicWidthForMultipleDenseCanvases() {
		PlotWindowModel windowModel = new PlotWindowModel("");
		PlotCanvasModel canvasModel1 = new PlotCanvasModel(windowModel.getUUID());
		PlotTraceModel traceModel1 = new PlotTraceModel(canvasModel1.getUUID());
		traceModel1.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(200, randomSeed1),
			ChartreuseTestsUtil.doubleObjArrayOfSize(200, randomSeed1)
		));
		
		PlotCanvasModel canvasModel2 = new PlotCanvasModel(windowModel.getUUID());
		PlotTraceModel traceModel2 = new PlotTraceModel(canvasModel2.getUUID());
		traceModel2.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(200, randomSeed1),
			ChartreuseTestsUtil.doubleObjArrayOfSize(200, randomSeed1)
		));
		
		int heuristicWidth = windowModel.getHeuristicWidth();
		assertEquals(1024, heuristicWidth);
	}
	
	@Test
	public void testGetPlotHeuristicHeightForMultipleSparseCanvases() {
		PlotWindowModel windowModel = new PlotWindowModel("");
		PlotCanvasModel canvasModel1 = new PlotCanvasModel(windowModel.getUUID()).setColPosition(0).setRowPosition(0);
		PlotTraceModel traceModel1 = new PlotTraceModel(canvasModel1.getUUID());
		traceModel1.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(200, randomSeed1),
			ChartreuseTestsUtil.doubleObjArrayOfSize(200, randomSeed1)
		));
		
		PlotCanvasModel canvasModel2 = new PlotCanvasModel(windowModel.getUUID()).setColPosition(0).setRowPosition(1);
		PlotTraceModel traceModel2 = new PlotTraceModel(canvasModel2.getUUID());
		traceModel2.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(200, randomSeed1),
			ChartreuseTestsUtil.doubleObjArrayOfSize(200, randomSeed1)
		));
		
		int heuristicHeight = windowModel.getHeuristicHeight();
		assertEquals(768, heuristicHeight);
	}
	
	@Test
	public void testGetPlotHeuristicHeightForMultipleDenseCanvases() {
		PlotWindowModel windowModel = new PlotWindowModel("");
		PlotCanvasModel canvasModel1 = new PlotCanvasModel(windowModel.getUUID()).setColPosition(0).setRowPosition(0);
		PlotTraceModel traceModel1 = new PlotTraceModel(canvasModel1.getUUID());
		traceModel1.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(2000, randomSeed1),
			ChartreuseTestsUtil.doubleObjArrayOfSize(2000, randomSeed1)
		));
		
		PlotCanvasModel canvasModel2 = new PlotCanvasModel(windowModel.getUUID()).setColPosition(0).setRowPosition(1);
		PlotTraceModel traceModel2 = new PlotTraceModel(canvasModel2.getUUID());
		traceModel2.setPoints(TestFileUtils.formatAsPoints(
			ChartreuseTestsUtil.doubleObjArrayOfSize(2000, randomSeed1),
			ChartreuseTestsUtil.doubleObjArrayOfSize(2000, randomSeed1)
		));
		
		int heuristicHeight = windowModel.getHeuristicHeight();
		assertEquals(4000, heuristicHeight);
	}
	
	@Test
	public void testGetVersion() {
		PlotWindowModel windowModel = new PlotWindowModel("");
		assertEquals("3.0", windowModel.getVersion());
	}

	@Test
	public void testIsFailing() {
		PlotWindowModel windowModel = new PlotWindowModel("");
		assertFalse(windowModel.isFailing());

		windowModel.setBackgroundColor(RuleFailActor.FAIL_COLOR);
		assertTrue(windowModel.isFailing());
	}

	@Test
	public void testEffectiveEquals() {
		PlotWindowModel windowModel1 = new PlotWindowModel("Test Window Model");
		PlotWindowModel windowModel2 = new PlotWindowModel(windowModel1);

		assertTrue(windowModel1.effectiveEquals(windowModel2));
		assertNotEquals(windowModel1.getUUID(), windowModel2.getUUID());
	}
}
