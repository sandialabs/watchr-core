package gov.sandia.watchr.graph.chartreuse.model;

import java.util.Random;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.TestFileUtils;
import gov.sandia.watchr.config.filter.DataFilter;
import gov.sandia.watchr.config.filter.FilterExpression;
import gov.sandia.watchr.config.filter.DataFilter.DataFilterPolicy;
import gov.sandia.watchr.config.filter.DataFilter.DataFilterType;
import gov.sandia.watchr.graph.chartreuse.ChartreuseException;
import gov.sandia.watchr.graph.chartreuse.ChartreuseTestsUtil;
import gov.sandia.watchr.graph.chartreuse.CommonPlotTerms;
import gov.sandia.watchr.graph.chartreuse.Dimension;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.util.RGB;

public class PlotTraceModelBasicTests {
    
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
		try {
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

			DataFilter filter =
            new DataFilter(
                DataFilterType.POINT,
                new FilterExpression("x == 4.0 && y == 5.0 && z == 6.0"),
                DataFilterPolicy.BLACKLIST);

			traceModel1.addFilterValue(filter);
			
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
			assertEquals(filter, traceModel2.getFilters().iterator().next());

			assertEquals(Integer.toString(Integer.MIN_VALUE), traceModel2.getProperties().get(PlotToken.TRACE_BOUND_LOWER));
			assertEquals(Integer.toString(Integer.MAX_VALUE), traceModel2.getProperties().get(PlotToken.TRACE_BOUND_UPPER));
			assertEquals(Integer.toString(-1), traceModel2.getProperties().get(PlotToken.TRACE_PRECISION));
			assertEquals(Boolean.TRUE.toString(), traceModel2.getProperties().get(PlotToken.TRACE_DRAW_NUMBER_LABELS));
			assertEquals("Circle", traceModel2.getProperties().get(PlotToken.TRACE_POINT_MODE));
			assertEquals(CommonPlotTerms.ORIENTATION_HORIZONTAL.getLabel(), traceModel2.getProperties().get(PlotToken.TRACE_ORIENTATION));
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}
	
	@Test
	public void testIsThreeDimensional() {
		try {
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
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}
	
	@Test
	public void testIsThreeDimensionalRendered() {
		try {
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
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}
	
	@Test
	public void testAreColorAnchorValuesOutOfOrder_Good() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null, false);
			traceModel.getColorScaleAnchors().add(0.0);
			traceModel.getColorScaleAnchors().add(1.0);
			traceModel.getColorScaleAnchors().add(2.0);
			assertFalse(traceModel.areColorAnchorValuesOutOfOrder());
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}
	
	@Test
	public void testAreColorAnchorValuesOutOfOrder_Bad() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null, false);
			traceModel.getColorScaleAnchors().add(2.0);
			traceModel.getColorScaleAnchors().add(1.0);
			traceModel.getColorScaleAnchors().add(0.0);
			assertTrue(traceModel.areColorAnchorValuesOutOfOrder());
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}
	
	@Test
	public void testAreColorAnchorValuesOutsideRange_Good() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null, false);
			traceModel.getColorScaleAnchors().add(0.0);
			traceModel.getColorScaleAnchors().add(50.0);
			traceModel.getColorScaleAnchors().add(100.0);
			assertFalse(traceModel.areColorAnchorValuesOutsideRange(0.0, 100.0));
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}
	
	@Test
	public void testAreColorAnchorValuesOutsideRange_Bad() {
		try {
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
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}
	
	@Test
	public void testIsOnlyOneColorAnchorValue() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null, false);
			assertTrue(traceModel.isOnlyOneColorAnchorValue());	
			traceModel.getColorScaleAnchors().add(100.0);
			assertFalse(traceModel.isOnlyOneColorAnchorValue());
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}
	
	@Test
	public void testGetVersion() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null, false);
			assertEquals("3.0", traceModel.getVersion());
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}
	
	@Test
	public void testGetDefaultProperties() {
		try {
			PlotTraceModel traceModel = new PlotTraceModel(null);
			assertEquals(traceModel.get(PlotToken.TRACE_BOUND_LOWER), Integer.toString(Integer.MIN_VALUE));
			assertEquals(traceModel.get(PlotToken.TRACE_BOUND_UPPER), Integer.toString(Integer.MAX_VALUE));
			assertEquals(traceModel.get(PlotToken.TRACE_PRECISION), Integer.toString(-1));
			assertEquals(traceModel.get(PlotToken.TRACE_DRAW_NUMBER_LABELS), Boolean.TRUE.toString());
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}

	@Test
	public void testEquals() {
		try {
			// Make a trace model.
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

			DataFilter filter =
            new DataFilter(
                DataFilterType.POINT,
                new FilterExpression("x == 4.0 && y == 5.0 && z == 6.0"),
                DataFilterPolicy.BLACKLIST);

			traceModel1.addFilterValue(filter);
			
			// Make a copy of the trace model.
			PlotCanvasModel canvasModel = new PlotCanvasModel(null);
			PlotTraceModel traceModel2 = new PlotTraceModel(canvasModel.getUUID(), traceModel1);

			// Assert that they are initially equal.
			assertEquals(traceModel1, traceModel2);

			// Change things one at a time and verify inequality.
			traceModel2.setName("name2");
			assertNotEquals(traceModel1, traceModel2);
			traceModel2.setName("name");
			assertEquals(traceModel1, traceModel2);

			traceModel2.setTrimNoDelta(Dimension.Y);
			assertNotEquals(traceModel1, traceModel2);
			traceModel2.setTrimNoDelta(Dimension.X);
			assertEquals(traceModel1, traceModel2);

			traceModel2.setRelativeAxis(Dimension.X);
			assertNotEquals(traceModel1, traceModel2);
			traceModel2.setRelativeAxis(Dimension.Y);
			assertEquals(traceModel1, traceModel2);

			PlotTracePoint anotherPoint = new PlotTracePoint("4", "5", "6");
			traceModel2.add(anotherPoint);
			assertNotEquals(traceModel1, traceModel2);
			traceModel2.remove(anotherPoint);
			assertEquals(traceModel1, traceModel2);
		} catch(ChartreuseException e) {
            fail(e.getMessage());
        }
	}
}
