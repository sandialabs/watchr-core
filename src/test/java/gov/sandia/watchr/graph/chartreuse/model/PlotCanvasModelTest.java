package gov.sandia.watchr.graph.chartreuse.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import gov.sandia.watchr.TestFileUtils;
import gov.sandia.watchr.graph.chartreuse.ChartreuseTestsUtil;
import gov.sandia.watchr.graph.chartreuse.Dimension;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.util.RGB;

public class PlotCanvasModelTest {
		
	@Test
	public void testCopyConstructor() {
		PlotCanvasModel canvasModel1 = new PlotCanvasModel(null);
		canvasModel1.setName("name");
		canvasModel1.setXAxisLabel("xAxisLabel");
		canvasModel1.setYAxisLabel("yAxisLabel");
		canvasModel1.setZAxisLabel("zAxisLabel");
		canvasModel1.setXAxisRGB(new RGB(255, 0, 0));
		canvasModel1.setYAxisRGB(new RGB(0, 255, 0));
		canvasModel1.setZAxisRGB(new RGB(0, 0, 255));
		canvasModel1.setAxisPrecision(5);
		canvasModel1.setAutoscale(false);
		canvasModel1.setXLogScale(true);
		canvasModel1.setYLogScale(true);
		canvasModel1.setZLogScale(true);
		canvasModel1.setDrawXAxisLines(true);
		canvasModel1.setDrawYAxisLines(true);
		canvasModel1.setDrawGridLines(true);
		canvasModel1.setXAxisRangeStart(-100);
		canvasModel1.setXAxisRangeEnd(100);
		canvasModel1.setYAxisRangeStart(-200);
		canvasModel1.setYAxisRangeEnd(200);
		canvasModel1.setZAxisRangeStart(-300);
		canvasModel1.setZAxisRangeEnd(300);

		PlotWindowModel windowModel = new PlotWindowModel("Test window model");
		PlotCanvasModel canvasModel2 = new PlotCanvasModel(windowModel.getUUID(), canvasModel1);
		assertEquals("name", canvasModel2.getName());
		assertEquals(0, canvasModel2.getRowPosition());
		assertEquals(0, canvasModel2.getColPosition());
		assertEquals("xAxisLabel", canvasModel2.getXAxisLabel());
		assertEquals("yAxisLabel", canvasModel2.getYAxisLabel());
		assertEquals("zAxisLabel", canvasModel2.getZAxisLabel());
		assertEquals(new RGB(255, 0, 0), canvasModel2.getXAxisRGB());
		assertEquals(new RGB(0, 255, 0), canvasModel2.getYAxisRGB());
		assertEquals(new RGB(0, 0, 255), canvasModel2.getZAxisRGB());
		assertEquals(5, canvasModel2.getAxisPrecision());
		assertFalse(canvasModel2.getAutoscale());
		assertTrue(canvasModel2.getXLogScale());
		assertTrue(canvasModel2.getYLogScale());
		assertTrue(canvasModel2.getZLogScale());
		assertTrue(canvasModel2.getDrawXAxisLines());
		assertTrue(canvasModel2.getDrawYAxisLines());
		assertTrue(canvasModel2.getDrawGridLines());
		assertEquals(-100, canvasModel2.getXAxisRangeStart(), 1.0e-4);
		assertEquals(100, canvasModel2.getXAxisRangeEnd(), 1.0e-4);
		assertEquals(-200, canvasModel2.getYAxisRangeStart(), 1.0e-4);
		assertEquals(200, canvasModel2.getYAxisRangeEnd(), 1.0e-4);
		assertEquals(-300, canvasModel2.getZAxisRangeStart(), 1.0e-4);
		assertEquals(300, canvasModel2.getZAxisRangeEnd(), 1.0e-4);
	}

	@Test
	public void testCanvasContainsOnlyBoxPlots() {
		PlotCanvasModel canvasModel = new PlotCanvasModel(null);
		PlotTraceModel traceModel1 = new PlotTraceModel(canvasModel.getUUID());
		PlotTraceModel traceModel2 = new PlotTraceModel(canvasModel.getUUID());
		PlotTraceModel traceModel3 = new PlotTraceModel(canvasModel.getUUID());
		
		traceModel1.set(PlotToken.TRACE_POINT_TYPE, PlotType.BOX_PLOT);
		traceModel2.set(PlotToken.TRACE_POINT_TYPE, PlotType.BOX_PLOT);
		traceModel3.set(PlotToken.TRACE_POINT_TYPE, PlotType.BOX_PLOT);
		assertTrue(canvasModel.canvasContainsOnlyOneType(PlotType.BOX_PLOT));
		
		traceModel3.set(PlotToken.TRACE_POINT_TYPE, PlotType.SCATTER_PLOT);
		assertFalse(canvasModel.canvasContainsOnlyOneType(PlotType.BOX_PLOT));
	}
	
	@Test
	public void testGetVersion() {
		PlotCanvasModel canvasModel = new PlotCanvasModel(null);
		assertEquals("3.0", canvasModel.getVersion());
	}
	
	@Test
	public void testGetDescriptiveAxesName() {
		PlotCanvasModel canvasModel = new PlotCanvasModel(null);
		assertEquals("<No axis text>", canvasModel.getDescriptiveAxesName());
		canvasModel.setXAxisLabel("X");
		assertEquals("X", canvasModel.getDescriptiveAxesName());
		canvasModel.setYAxisLabel("Y");
		assertEquals("X/Y", canvasModel.getDescriptiveAxesName());
		canvasModel.setZAxisLabel("Z");
		assertEquals("X/Y", canvasModel.getDescriptiveAxesName()); // Just setting the Z axis text doesn't make it 3D.
		
		PlotTraceModel traceModel = new PlotTraceModel(canvasModel.getUUID());
		
		traceModel.setPoints(
			TestFileUtils.formatAsPoints(
				ChartreuseTestsUtil.doubleObjArrayOfSize(3, new Random(1337)),
				ChartreuseTestsUtil.doubleObjArrayOfSize(3, new Random(1337)),
				ChartreuseTestsUtil.doubleObjArrayOfSize(3, new Random(1337))
			)
		);

		assertEquals("X/Y/Z", canvasModel.getDescriptiveAxesName());
	}

	@Test
	public void testToString() {
		PlotCanvasModel canvasModel1 = new PlotCanvasModel(null);
		canvasModel1.setName("name");
		canvasModel1.setXAxisLabel("xAxisLabel");
		canvasModel1.setYAxisLabel("yAxisLabel");
		assertEquals("PlotCanvasModel: [xAxisLabel/yAxisLabel]", canvasModel1.toString());
	}

	@Test
	public void testGetSmallestValueAcrossTraces_ForNumberValues() {
		PlotCanvasModel canvasModel1 = new PlotCanvasModel(null);
		PlotTraceModel traceModel1 = new PlotTraceModel(canvasModel1.getUUID());
		PlotTraceModel traceModel2 = new PlotTraceModel(canvasModel1.getUUID());

		traceModel1.add(new PlotTracePoint("1.0", "100.0"));
		traceModel1.add(new PlotTracePoint("-100.0", "-200.0"));
		traceModel2.add(new PlotTracePoint("2.0", "100.0"));
		traceModel2.add(new PlotTracePoint("-99.0", "200.0"));

		assertEquals("-100.0", canvasModel1.getSmallestValueAcrossTraces(Dimension.X));
	}

	@Test
	public void testGetLargestValueAcrossTraces_ForNumberValues() {
		PlotCanvasModel canvasModel1 = new PlotCanvasModel(null);
		PlotTraceModel traceModel1 = new PlotTraceModel(canvasModel1.getUUID());
		PlotTraceModel traceModel2 = new PlotTraceModel(canvasModel1.getUUID());

		traceModel1.add(new PlotTracePoint("1.0", "100.0"));
		traceModel1.add(new PlotTracePoint("-100.0", "-200.0"));
		traceModel2.add(new PlotTracePoint("2.0", "100.0"));
		traceModel2.add(new PlotTracePoint("-99.0", "200.0"));

		assertEquals("200.0", canvasModel1.getLargestValueAcrossTraces(Dimension.Y));
	}

	@Test
	public void testGetSmallestValueAcrossTraces_ForDateValues() {
		PlotCanvasModel canvasModel1 = new PlotCanvasModel(null);
		PlotTraceModel traceModel1 = new PlotTraceModel(canvasModel1.getUUID());
		PlotTraceModel traceModel2 = new PlotTraceModel(canvasModel1.getUUID());

		traceModel1.add(new PlotTracePoint("2021-04-16", "100.0"));
		traceModel1.add(new PlotTracePoint("2020-03-09", "-200.0"));
		traceModel2.add(new PlotTracePoint("2020-01-01", "100.0"));
		traceModel2.add(new PlotTracePoint("2021-12-31", "200.0"));

		assertEquals("2020-01-01", canvasModel1.getSmallestValueAcrossTraces(Dimension.X));
	}

	@Test
	public void testGetLargestValueAcrossTraces_ForDateValues() {
		PlotCanvasModel canvasModel1 = new PlotCanvasModel(null);
		PlotTraceModel traceModel1 = new PlotTraceModel(canvasModel1.getUUID());
		PlotTraceModel traceModel2 = new PlotTraceModel(canvasModel1.getUUID());

		traceModel1.add(new PlotTracePoint("2021-04-16", "100.0"));
		traceModel1.add(new PlotTracePoint("2020-03-09", "-200.0"));
		traceModel2.add(new PlotTracePoint("2020-01-01", "100.0"));
		traceModel2.add(new PlotTracePoint("2021-12-31", "200.0"));

		assertEquals("2021-12-31", canvasModel1.getLargestValueAcrossTraces(Dimension.X));
	}
}

