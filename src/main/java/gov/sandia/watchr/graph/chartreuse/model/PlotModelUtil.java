/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse.model;

import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.graph.chartreuse.generator.plotly.PlotlyShapeProvider;
import gov.sandia.watchr.graph.chartreuse.type.DotShapeProvider;
import gov.sandia.watchr.graph.chartreuse.type.NodeType;
import gov.sandia.watchr.util.RgbUtil;

/**
 * Various utility methods for {@link PlotWindowModel}, {@link PlotCanvasModel}
 * and {@link PlotTraceModel} objects.
 * 
 * @author Elliott Ridgway
 *
 */
public class PlotModelUtil {
	
	private static final Random rand = new Random();

	private PlotModelUtil() {}
	
	///////////////
	// SHORTCUTS //
	///////////////

	/**
	 * @return A random shape described by a {@link String}.  See {@link DotShapeProvider}
	 * for more information.
	 */
	public static String randomShape() {
		DotShapeProvider shapeProvider = new PlotlyShapeProvider();
		List<String> allShapeDescriptions = shapeProvider.getAllShapeDescriptions();
		int index = rand.nextInt(allShapeDescriptions.size());
		return allShapeDescriptions.get(index);
	}
	
	/**
	 * @return The {@link String} for circle shapes.
	 */
	public static String circleShape() {
		return "Circle";
	}

	/**
	 * @return The {@link String} for square shapes.
	 */
	public static String squareShape() {
		return "Square";
	}
	
	/////////
	// NEW //
	/////////
	
	/**
	 * Attaches an existing {@link PlotTraceModel} to a new parent
	 * {@link PlotWindowModel} that has default settings configured
	 * automatically.  The PlotTraceModel will also receive a new
	 * parent {@link PlotCanvasModel} that has default settings
	 * configured.
	 * 
	 * @param traceModel The PlotTraceModel to give a new parent
	 * PlotWindowModel and parent PlotCanvasModel.
	 * @param xLabel
	 * @param yLabel
	 * @param zLabel
	 * @return The new PlotWindowModel.
	 */
	public static PlotWindowModel newPlotWindow(PlotTraceModel traceModel, String xLabel, String yLabel, String zLabel) {
		PlotWindowModel windowModel = new PlotWindowModel(newPlotCanvas(traceModel, xLabel, yLabel, zLabel));
		windowModel.setName(traceModel.getName());
		return windowModel;
	}
	
	/**
	 * Attaches an existing {@link PlotTraceModel} to a new parent
	 * {@link PlotCanvasModel} that has default settings configured
	 * automatically.
	 * 
	 * @param traceModel The PlotTraceModel to give a new parent
	 * PlotCanvasModel.
	 * @param xLabel
	 * @param yLabel
	 * @param zLabel
	 * @return The new PlotCanvasModel.
	 */
	public static PlotCanvasModel newPlotCanvas(PlotTraceModel traceModel, String xLabel, String yLabel, String zLabel) {
		final PlotCanvasModel canvasModel = new PlotCanvasModel(null);
		
		canvasModel
			.setName(traceModel.getName())
			.setRowPosition(0)
			.setColPosition(0)
			.setXAxisLabel(xLabel)
			.setYAxisLabel(yLabel)
			.setXAxisRGB(RgbUtil.blackRGB())
			.setYAxisRGB(RgbUtil.blackRGB())
			.setAxisPrecision(PlotCanvasModel.DEFAULT_CANVAS_DECIMAL_PRECISION)
			.setDrawAxisLines(true)
			.setDrawGridLines(true);
		
		if(traceModel.isThreeDimensional()) {
			canvasModel
				.setZAxisLabel(zLabel)
				.setZAxisRGB(RgbUtil.blackRGB());
		}
		
		canvasModel.addTraceModel(traceModel);
		
		return canvasModel;
	}
	
	//////////
	// TRIM //
	//////////
	
	/**
	 * Trims out the variable/response value from the formatted text displayed in trace composites,
	 * typically in combo boxes.
	 * 
	 * @param str The formatted text to trim.
	 * @return The key variable/response value.
	 */
	public static String trimVariableComboText(String str) {
		if(StringUtils.isNotBlank(str)) {
			if(str.equals("[" + NodeType.TIMESTEP.toString() + "]")) {
				return NodeType.TIMESTEP.toString();
			} else {
				return StringUtils.trim(str.split("\\[")[0]);
			}
		}
		return "";
	}
	
	/**
	 * Constructs a regex for identifying formatted text displayed in trace composites,
	 * typically in combo boxes.
	 * @param axisLabel The label to construct a regex around.
	 * @return The regex.
	 */
	public static String constructAxisRegex(String axisLabel) {
		if(axisLabel.equals(NodeType.TIMESTEP.toString())) {
			return "^.*\\[" + NodeType.TIMESTEP.toString() + "\\]$";
		} else {
			return "^" + axisLabel + "\\s\\[(Variable|Response)\\]$";
		}
	}
	
	/////////////
	// GETTERS //
	/////////////
	
	/**
	 * Given three {@link List}s of {@link String}s representing three-dimensional data (i.e. an X list of values,
	 * a Y list of values, and a Z list of values), locate the corresponding Z value for a provided X value and
	 * a provided Y value.
	 * 
	 * @param listX The list of X values.
	 * @param listY The list of Y values.
	 * @param listZ The list of Z values.
	 * @param xValue The X value to find the corresponding Z value for.
	 * @param yValue The Y value to find the corresponding Z value for.
	 * @return The Z value if found for the provided X and Y values, or null if no value could be found.
	 */
	public static String getZValue(List<String> listX, List<String> listY, List<String> listZ, String xValue, String yValue) {
		for(int i = 0; i < listX.size() && i < listY.size(); i++) {
			if(listX.get(i).equals(xValue) &&
			   listY.get(i).equals(yValue)) {
				return listZ.get(i);
			}
		}
		return null;
	}
}

