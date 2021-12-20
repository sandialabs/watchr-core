package gov.sandia.watchr.graph.chartreuse.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.TestFileUtils;
import gov.sandia.watchr.graph.chartreuse.ChartreuseTestsUtil;
import gov.sandia.watchr.graph.chartreuse.type.NodeType;
import gov.sandia.watchr.util.ArrayUtil;
import gov.sandia.watchr.util.RgbUtil;

public class PlotModelUtilTest {
	
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
	public void testShapes() {
		assertEquals("Circle", PlotModelUtil.circleShape());
		assertEquals("Square", PlotModelUtil.squareShape());
	}
	
	@Test
	public void testNewPlotWindowModelFromCanvas() {
		PlotCanvasModel canvasModel = new PlotCanvasModel(null);
		canvasModel.setRowPosition(1);
		canvasModel.setColPosition(1);
		PlotWindowModel windowModel = new PlotWindowModel(canvasModel);
		
		assertEquals(0, canvasModel.getRowPosition());
		assertEquals(0, canvasModel.getColPosition());
		assertEquals(windowModel, canvasModel.getParent());
	}
	
	@Test
	public void testNewPlotCanvasModelFromTrace() {
		PlotTraceModel traceModel = new PlotTraceModel(null);
		traceModel.setPoints(
			TestFileUtils.formatAsPoints(
				ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed1),
				ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed1),
				ChartreuseTestsUtil.doubleObjArrayOfSize(3, randomSeed1)
			)
		);
		
		PlotCanvasModel canvasModel = PlotModelUtil.newPlotCanvas(traceModel, "X", "Y", "Z");
		
		assertEquals(canvasModel, traceModel.getParent());
		assertEquals("X", canvasModel.getXAxisLabel());
		assertEquals("Y", canvasModel.getYAxisLabel());
		assertEquals("Z", canvasModel.getZAxisLabel());
		assertTrue(canvasModel.getDrawXAxisLines());
		assertTrue(canvasModel.getDrawYAxisLines());
		assertTrue(canvasModel.getDrawGridLines());
		assertEquals(RgbUtil.blackRGB(), canvasModel.getXAxisRGB());
		assertEquals(RgbUtil.blackRGB(), canvasModel.getYAxisRGB());
		assertEquals(RgbUtil.blackRGB(), canvasModel.getZAxisRGB());
	}
	
	@Test
	public void testGetZValue() {
		List<String> xValues = ArrayUtil.asStringList(ChartreuseTestsUtil.doubleArrayOfSize(10, randomSeed1));
		List<String> yValues = ArrayUtil.asStringList(ChartreuseTestsUtil.doubleArrayOfSize(10, randomSeed1));
		List<String> zValues = ArrayUtil.asStringList(ChartreuseTestsUtil.doubleArrayOfSize(10, randomSeed1));
		
		assertEquals("0.44130694778942414", PlotModelUtil.getZValue(xValues, yValues, zValues, "0.6599297847448217", "0.19570252190931603"));
		assertEquals("0.6572159774089178", PlotModelUtil.getZValue(xValues, yValues, zValues, "0.6892426740281012", "0.49526296435984674"));
		assertEquals("0.961775957472761", PlotModelUtil.getZValue(xValues, yValues, zValues, "0.8832726771624211", "0.07871964753598903"));
		assertNull(PlotModelUtil.getZValue(xValues, yValues, zValues, "1", "2"));
	}
	
	@Test
	public void testTrimVariableComboText() {
		assertEquals("", PlotModelUtil.trimVariableComboText(""));
		assertEquals(NodeType.TIMESTEP.toString(), PlotModelUtil.trimVariableComboText("[Time Step]"));
		assertEquals("A", PlotModelUtil.trimVariableComboText("A [Variable]"));
		assertEquals("B", PlotModelUtil.trimVariableComboText("B [Response]"));
	}
	
	@Test
	public void testConstructAxisRegex() {
		assertEquals("^.*\\[" + NodeType.TIMESTEP.toString() + "\\]$", PlotModelUtil.constructAxisRegex("Time Step"));
		assertEquals("^A\\s\\[(Variable|Response)\\]$", PlotModelUtil.constructAxisRegex("A"));
	}
}

