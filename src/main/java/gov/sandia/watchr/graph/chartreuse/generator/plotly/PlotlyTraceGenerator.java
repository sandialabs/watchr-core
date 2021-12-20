/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse.generator.plotly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import gov.sandia.watchr.graph.chartreuse.CommonPlotTerms;
import gov.sandia.watchr.graph.chartreuse.Dimension;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.generator.PlotTraceGenerator;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceOptions;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.graph.chartreuse.type.DotShape;
import gov.sandia.watchr.graph.chartreuse.type.DotShapeProvider;
import gov.sandia.watchr.util.ArrayUtil;
import gov.sandia.watchr.util.ListUtil;
import gov.sandia.watchr.util.OsUtil;
import gov.sandia.watchr.util.RGB;
import gov.sandia.watchr.util.RGBA;

public class PlotlyTraceGenerator extends PlotTraceGenerator {
	
	////////////
	// FIELDS //
	////////////
	
	private int traceCounter;
	private DotShapeProvider shapeProvider;
	private PlotlyCanvasGenerator parent;
	
	/////////////////
	// CONSTRUCTOR //
	/////////////////
	
	public PlotlyTraceGenerator() {
		traceCounter = 0;
		shapeProvider = new PlotlyShapeProvider();
		options = new PlotTraceOptions();
		options.sortAlongDimension = Dimension.X;
	}

	/////////////
	// GETTERS //
	/////////////

	@Override
	public PlotTraceOptions getOptions() {
		return options;
	}

	public PlotlyCanvasGenerator getParent() {
		return parent;
	}

	/////////////
	// SETTERS //
	/////////////

	public void setParent(PlotlyCanvasGenerator parent) {
		this.parent = parent;
	}
	
	//////////////
	// OVERRIDE //
	//////////////
	
	@Override
	public String getTemplateFileAsString(PlotType type) {
		if(type == PlotType.SCATTER_PLOT ||
		   type == PlotType.SCATTER_PLOT_CATEGORICAL) {
			return PlotlyHtmlFragmentGenerator.getTraceScatter2D();
		} else if(type == PlotType.SURFACE_3D_PLOT) {
			return PlotlyHtmlFragmentGenerator.getTraceSurface3D();
		} else if(type == PlotType.HEAT_MAP_CATEGORICAL || type == PlotType.HEAT_MAP_3D) {
			return PlotlyHtmlFragmentGenerator.getTraceColoredTable();
		} else if(type == PlotType.CONTOUR_PLOT) {
			return PlotlyHtmlFragmentGenerator.getTraceContour();
		} else if(type == PlotType.TREE_MAP) {
			options.displayRange = -1;
			return PlotlyHtmlFragmentGenerator.getTraceTreemap();
		} else if(type == PlotType.AREA_PLOT) {
			return PlotlyHtmlFragmentGenerator.getTraceAreaPlot();
		}
		return "";
	}
	
	@Override
	public String processPlotName() {
		StringBuilder sb = new StringBuilder();
		PlotTraceModel trace = getTraceModel();

		sb.append("trace");
		if(trace.getUUID() != null) {
			sb.append(Math.abs(trace.getUUID().hashCode()));
		} else {
			sb.append(processIterCount());
		}
		return sb.toString();
	}
	
	@Override
	public String processPlotLabel() {
		PlotTraceModel trace = getTraceModel();
		StringBuilder sb = new StringBuilder();
		sb.append("'");
		sb.append(trace.getName());
		if(trace.getDerivativeLineType() != null) {
			sb.append(" - ");
			sb.append(trace.getDerivativeLineType().get());
		}
		sb.append("'");
		return sb.toString();
	}
	
	@Override
	public String processIterCount() {
		return Integer.toString(++traceCounter);
	}

	@Override
	public String processArrX() {
		List<PlotTracePoint> points = traceModel.getPoints(options);
		List<String> listX = new ArrayList<>();
		for(PlotTracePoint point : points) {
			listX.add(point.x);
		}
		
		if(!ArrayUtil.isDoubleList(listX)) {			
			listX = ArrayUtil.asStringListWithQuotes(listX, '\'');
		}
		
		if(traceModel.getPointType() == PlotType.SURFACE_3D_PLOT) {
			// Plotly requires special logic for surface 3D plots.
			listX = ArrayUtil.sortIntelligent(ListUtil.filterListDuplicates(listX));
		} else if(traceModel.getPointType() == PlotType.HEAT_MAP_3D || traceModel.getPointType() == PlotType.HEAT_MAP_CATEGORICAL) {
			listX = ListUtil.filterListDuplicates(listX);
		}
		return listX.toString();
	}

	@Override
	public String processArrY() {
		List<PlotTracePoint> points = traceModel.getPoints(options);
		List<String> listY = new ArrayList<>();
		for(PlotTracePoint point : points) {
			listY.add(point.y);
		}
		
		if(!ArrayUtil.isDoubleList(listY)) {			
			listY = ArrayUtil.asStringListWithQuotes(listY, '\'');
		}
		
		if(traceModel.getPointType() == PlotType.SURFACE_3D_PLOT) {
			// Plotly requires special logic for surface 3D plots.
			listY = ArrayUtil.sortIntelligent(ListUtil.filterListDuplicates(listY));
		} else if(traceModel.getPointType() == PlotType.HEAT_MAP_3D || traceModel.getPointType() == PlotType.HEAT_MAP_CATEGORICAL) {
			listY = ListUtil.filterListDuplicates(listY);
		} 
		return listY.toString();
	}
	
	@Override
	public String processArrZ() {
		List<String> listX = new ArrayList<>();
		List<String> listY = new ArrayList<>();
		List<String> listZ = new ArrayList<>();
		List<PlotTracePoint> points = traceModel.getPoints(options);
		
		if(traceModel.getPointType() == PlotType.SURFACE_3D_PLOT ||
		   traceModel.getPointType() == PlotType.HEAT_MAP_3D ||
		   traceModel.getPointType() == PlotType.HEAT_MAP_CATEGORICAL) {

			for(PlotTracePoint point : points) {
				listX.add(point.x);
				listY.add(point.y);
				listZ.add(point.z);
			}
			int precision = Integer.parseInt(traceModel.get(PlotToken.TRACE_PRECISION));
			if(precision > -1) {
				listZ = ArrayUtil.applyDecimalPrecision(listZ, precision);
			}
			
			boolean transpose =
				(traceModel.getPointType() == PlotType.SURFACE_3D_PLOT ||
				 traceModel.getPointType() == PlotType.HEAT_MAP_3D);
			
			// Plotly requires special logic for plots with Z data.
			return processZGridAsString(listX, listY, listZ, transpose);
		} else {
			for(PlotTracePoint point : points) {
				listZ.add(point.z);
			}
			if(!ArrayUtil.isDoubleList(listZ)) {
				int precision = Integer.parseInt(traceModel.get(PlotToken.TRACE_PRECISION));
				if(precision > -1) {
					listZ = ArrayUtil.applyDecimalPrecision(listZ, precision);
				}
				listZ = ArrayUtil.asStringListWithQuotes(listZ, '\'');
			}
			return listZ.toString();
		}
	}

	@Override
	public String processArrMetadata() {
		if(traceModel.hasMetadata()) {
			List<PlotTracePoint> points = traceModel.getPoints(options);
			return processAsMetadata(points);
		} else {
			return "''";
		}
	}

	@Override
	public String processHoverTextFormatting() {
		StringBuilder sb = new StringBuilder();
		sb.append("'<b>X</b>: %{x}<br>' +");
		if(traceModel.hasMetadata()) {
			sb.append("'<b>Y</b>: %{y}<br>' +");
			if(traceModel.isThreeDimensional()) {
				sb.append("'<b>Z</b>: %{z}<br>' +");	
			}
			sb.append("'%{text}'");
		} else {
			if(traceModel.isThreeDimensional()) {
				sb.append("'<b>Y</b>: %{y}<br>' +");
				sb.append("'<b>Z</b>: %{z}<br>'");	
			} else {
				sb.append("'<b>Y</b>: %{y}<br>'");
			}
		}
		return sb.toString();
	}		

	@Override
	public String processPointMode() {
		String pointMode = traceModel.get(PlotToken.TRACE_POINT_MODE);
		String colorAxis = traceModel.get(PlotToken.TRACE_COLOR_AXIS);
		boolean drawLines = Boolean.parseBoolean(traceModel.get(PlotToken.TRACE_DRAW_LINES));
		boolean showColorScale = Boolean.parseBoolean(traceModel.get(PlotToken.TRACE_DRAW_COLOR_SCALE));
		
		if(traceModel.getPointType() == PlotType.CONTOUR_PLOT) {
			return "'" + pointMode + "'";
		} else if(traceModel.getPointType() == PlotType.HISTOGRAM) {
			return "'" + pointMode.toLowerCase() + "'";
		} else {
			StringBuilder modelBlockBuilder = new StringBuilder();
			if(drawLines) {
			    if(pointMode.equals("No Shape")) {
	                modelBlockBuilder.append("'lines'");
			    } else {
			        modelBlockBuilder.append("'lines+markers'");
			    }
			} else {
				modelBlockBuilder.append("'markers'");
			}
			
			if (!pointMode.equals("No Shape")){
				modelBlockBuilder.append(",\r\n");
				modelBlockBuilder.append("marker: { \r\n");
				
				modelBlockBuilder.append("symbol: \"");
				DotShape shape = shapeProvider.getShapeByDescription(pointMode);
				modelBlockBuilder.append(shape.getImplementedString());
				modelBlockBuilder.append("\", \r\n");
				
				modelBlockBuilder.append("color:");			
				modelBlockBuilder.append(processColor());
				
				if(traceModel.getPointType() == PlotType.SCATTER_3D_PLOT && colorAxis != null) {
				    modelBlockBuilder.append(",\r\n");
				    modelBlockBuilder.append("colorscale:");
				    modelBlockBuilder.append(processColorScale());
				}
				
				if(drawLines) {
				    modelBlockBuilder.append(",\r\n");
					modelBlockBuilder.append("line: { \r\n");
					modelBlockBuilder.append("color:");			
					modelBlockBuilder.append(processColor());
					modelBlockBuilder.append("\r\n");
					modelBlockBuilder.append("}");		
				}
				
				if(traceModel.getPointType() == PlotType.SCATTER_3D_PLOT) {
				    modelBlockBuilder.append(",\r\n");
				    modelBlockBuilder.append("showscale: ").append(showColorScale);
				    
				}
				
				modelBlockBuilder.append("\r\n");
	            modelBlockBuilder.append("}");
	            modelBlockBuilder.append("\r\n");
			} else if(drawLines) {
                modelBlockBuilder.append(",\r\n");
                modelBlockBuilder.append("line: { \r\n");
                modelBlockBuilder.append("color:");         
                modelBlockBuilder.append(processColor());
                modelBlockBuilder.append("\r\n");
                modelBlockBuilder.append("}");      
            }
            
					
			return modelBlockBuilder.toString();
		}
	}

	@Override
	public String processPointType() {
		PlotType plotType = traceModel.getPointType();
		
		if(plotType == PlotType.SCATTER_PLOT ||
		   plotType == PlotType.SCATTER_PLOT_CATEGORICAL ||
		   plotType == PlotType.AREA_PLOT) {
			return "'scatter'";
		} else if(plotType == PlotType.BOX_PLOT) {
			return "'box'";
		} else if(plotType == PlotType.HISTOGRAM) {
			return "'histogram'";
		} else if(plotType == PlotType.SCATTER_3D_PLOT) {
			return "'scatter3d'";
		} else if(plotType == PlotType.SURFACE_3D_PLOT) {
			return "'surface'";
		} else if(plotType == PlotType.HEAT_MAP_CATEGORICAL || plotType == PlotType.HEAT_MAP_3D) {
			return "'heatmap'";
		} else if(plotType == PlotType.CONTOUR_PLOT) {
			return "'contour'";
		} else if(plotType == PlotType.BAR_CHART || plotType == PlotType.BAR_CHART_CATEGORICAL) {
			return "'bar'";
		} else if(plotType == PlotType.PARALLEL_COORD_PLOT) {
			return "'parcoords'";
		} else if(plotType == PlotType.TREE_MAP) {
			return "'treemap'";
		} else {
			return "";
		}
	}

	@Override
	public String processColor() {
	    RGB rgb = traceModel.getPrimaryColor();
    	if(rgb != null) {
    	    return "'rgb(" + rgb.red + ", " + rgb.green + ", " + rgb.blue + ")'";
    	}
    	return "'rgb(0, 0, 0)'";
	}

	@Override
	public String processOpacity() {
	    RGB rgb = traceModel.getPrimaryColor();
    	if(rgb instanceof RGBA) {
    	    return Double.toString(((RGBA)rgb).alpha);
    	}
    	return "1";
	}
	
	@Override
	public String processColorScale() {
		if(traceModel.getColorScaleType().equals(CommonPlotTerms.SCALE_DISCRETE.getLabel())) {
			return processColorScaleDiscrete();
		} else {
			return processColorScaleContinuous();
		}
	}
	
	@Override
	public String processCumulative() {
		StringBuilder sb = new StringBuilder();
		if(traceModel.getPointType() == PlotType.HISTOGRAM) {
			String cumulative = getTraceModel().get(PlotToken.TRACE_CUMULATIVE);
			if(cumulative != null && Boolean.parseBoolean(cumulative)) {
				sb.append("cumulative: {").append("\r\n"); 
				sb.append("enabled:true").append("\r\n"); 
				sb.append("},");
			}
		} 
		return sb.toString();
	}

	@Override
	public String processDrawLines() {
		return ""; // Not used in Plotly.  See processPointMode() for drawing trace lines.
	}
	
	@Override
	public String processDrawColorScale() {
		return Boolean.toString(traceModel.getPropertyAsBoolean(PlotToken.TRACE_DRAW_COLOR_SCALE));
	}
	
	@Override
	public String processArrIndependent() {
		String orientation = traceModel.get(PlotToken.TRACE_ORIENTATION);
		if(orientation.equals(CommonPlotTerms.ORIENTATION_VERTICAL.getLabel())) {
			return processArrX();
		} else {
			return processArrY();
		}
	}

	@Override
	public String processArrDependent() {
		String orientation = traceModel.get(PlotToken.TRACE_ORIENTATION);
		if(orientation.equals(CommonPlotTerms.ORIENTATION_VERTICAL.getLabel())) {
			return processArrY();
		} else {
			return processArrX();
		}
	}

	@Override
	public String processAxisIndependentVar() {
		String orientation = traceModel.get(PlotToken.TRACE_ORIENTATION);
		if(orientation.equals(CommonPlotTerms.ORIENTATION_VERTICAL.getLabel())) {
			return "x";
		} else {
			return "y";
		}
	}
	
	@Override
	public String processAxisDependentVar() {
		String orientation = traceModel.get(PlotToken.TRACE_ORIENTATION);
		if(orientation.equals(CommonPlotTerms.ORIENTATION_VERTICAL.getLabel())) {
			return "y";
		} else {
			return "x";
		}
	}
	
	@Override
	public String processOrientation() {
		String orientation = traceModel.get(PlotToken.TRACE_ORIENTATION);
		if(orientation.equals(CommonPlotTerms.ORIENTATION_VERTICAL.getLabel())) {
			return "'v'";
		} else {
			return "'h'";
		}
	}
	
	@Override
	public String processArrNDimensionalLoop() {
		return "";
	}

	@Override
	public String processParentCanvasX() {
		int canvasX = -1;
		PlotCanvasModel parentCanvas = getTraceModel().getParent();
		int canvasOffsetIndex = getParent().getParent().getCanvasIndexOffset();
		if(parentCanvas.getBaseCanvasModelIfOverlaid() == null) {
			canvasX = PlotlyGeneratorUtil.processParentCanvasGridPosition(parentCanvas, true, canvasOffsetIndex); 
		} else {
			canvasX = PlotlyGeneratorUtil.processParentCanvasGridPosition(parentCanvas, false, canvasOffsetIndex);
		}
		
		return "'x" + canvasX + "'";
	}

	@Override
	public String processParentCanvasY() {
		int canvasOffsetIndex = getParent().getParent().getCanvasIndexOffset();
		return "'y" + PlotlyGeneratorUtil.processParentCanvasGridPosition(getTraceModel().getParent(), true, canvasOffsetIndex) + "'";
	}


	@Override
	public String processLowerBound() {
		return traceModel.get(PlotToken.TRACE_BOUND_LOWER);
	}

	@Override
	public String processUpperBound() {
		return traceModel.get(PlotToken.TRACE_BOUND_UPPER);
	}

	@Override
	public String processPrecision() {
		String precision = traceModel.get(PlotToken.TRACE_PRECISION);
		if(traceModel.getPointType() == PlotType.HISTOGRAM) {
			String orientation = traceModel.get(PlotToken.TRACE_ORIENTATION);
			if(!precision.equals("-1")) {
				if(orientation.equals(CommonPlotTerms.ORIENTATION_HORIZONTAL.getLabel())) {
					return "nbinsy: " + precision;
				} else {
					return "nbinsx: " + precision;
				}
			} else {
				if(orientation.equals(CommonPlotTerms.ORIENTATION_HORIZONTAL.getLabel())) {
					return "nbinsy: 10";
				} else {
					return "nbinsx: 10";
				} 
			}
		} else {
			return precision;
		}
	}

	@Override
	public String processErrorArrXUpper() {
		String errorXUpperAsString = traceModel.get(PlotToken.TRACE_ERROR_ARR_X_UPPER);
		if(StringUtils.isNotBlank(errorXUpperAsString)) {
			return "[" + errorXUpperAsString + "]";
		}
		return "[]";
	}

	@Override
	public String processErrorArrXLower() {
		String errorXLowerAsString = traceModel.get(PlotToken.TRACE_ERROR_ARR_X_LOWER);
		if(StringUtils.isNotBlank(errorXLowerAsString)) {
			return "[" + errorXLowerAsString + "]";
		}
		return "[]";
	}

	@Override
	public String processErrorArrXVisible() {
		String value = traceModel.get(PlotToken.TRACE_ERROR_ARR_X_VISIBLE);
		if(StringUtils.isBlank(value)) {
			return "false";
		}
		return value;
	}

	@Override
	public String processErrorArrYUpper() {
		String errorYUpperAsString = traceModel.get(PlotToken.TRACE_ERROR_ARR_Y_UPPER);
		if(StringUtils.isNotBlank(errorYUpperAsString)) {
			return "[" + errorYUpperAsString + "]";
		}
		return "[]";
	}

	@Override
	public String processErrorArrYLower() {
		String errorYLowerAsString = traceModel.get(PlotToken.TRACE_ERROR_ARR_Y_LOWER);
		if(StringUtils.isNotBlank(errorYLowerAsString)) {
			return "[" + errorYLowerAsString + "]";
		}
		return "[]";
	}

	@Override
	public String processErrorArrYVisible() {
		String value = traceModel.get(PlotToken.TRACE_ERROR_ARR_Y_VISIBLE);
		if(StringUtils.isBlank(value)) {
			return "false";
		}
		return value;
	}

	@Override
	public String processErrorArrZUpper() {
		String errorZUpperAsString = traceModel.get(PlotToken.TRACE_ERROR_ARR_Z_UPPER);
		if(StringUtils.isNotBlank(errorZUpperAsString)) {
			return "[" + errorZUpperAsString + "]";
		}
		return "[]";
	}

	@Override
	public String processErrorArrZLower() {
		String errorZLowerAsString = traceModel.get(PlotToken.TRACE_ERROR_ARR_Z_LOWER);
		if(StringUtils.isNotBlank(errorZLowerAsString)) {
			return "[" + errorZLowerAsString + "]";
		}
		return "[]";
	}

	@Override
	public String processErrorArrZVisible() {
		String value = traceModel.get(PlotToken.TRACE_ERROR_ARR_Z_VISIBLE);
		if(StringUtils.isBlank(value)) {
			return "false";
		}
		return value;
	}	
	
	/////////////
	// PRIVATE //
	/////////////
	
	private String processZGridAsString(List<String> listX, List<String> listY, List<String> listZ, boolean transpose) {
		// For Plotly, Z-data is represented as a grid of z-values, where the row and column
		// indices correspond to the x and y values of the associated x- and y-value arrays.
		
		List<String> dupsRemovedX = ListUtil.filterListDuplicates(listX);
		List<String> dupsRemovedY = ListUtil.filterListDuplicates(listY);
		StringBuilder grid = new StringBuilder();
		
		grid.append("[");
		for(int i = 0; i < dupsRemovedY.size(); i++) {			
			grid.append("[");			
			for(int j = 0; j < dupsRemovedX.size(); j++) {
				int linearZPosition = transpose ? ((i * dupsRemovedX.size()) + j) : ((j * dupsRemovedY.size()) + i);
				if(linearZPosition < listZ.size()) {
					String zValue = listZ.get(linearZPosition);
					if(StringUtils.isBlank(zValue) || !NumberUtils.isCreatable(zValue)) {
						grid.append("\"").append(zValue).append("\"");
					} else {
						grid.append(zValue);
					}
				}
					
				if(j < dupsRemovedX.size() - 1){
					grid.append(", ");
				}
			}
			grid.append("]");
			
			if(i < dupsRemovedY.size() - 1){
				grid.append(", ");
			}
			grid.append(OsUtil.getOSLineBreak());
		}
		grid.append("]");
		
		return grid.toString();
	}
	
	private double processMinValueFromDimension(Dimension dim) {
		return ArrayUtil.getMinFromStringList(traceModel.getDimensionValues(dim));
	}
	
	private double processMaxValueFromDimension(Dimension dim) {
		return ArrayUtil.getMaxFromStringList(traceModel.getDimensionValues(dim));
	}
	
	private String processColorScaleContinuous() {
		Dimension targetColorScaleDimension = null;
		if(traceModel.getPointType() == PlotType.PARALLEL_COORD_PLOT) {
			targetColorScaleDimension = Dimension.X;
		} else {
			targetColorScaleDimension = Dimension.Z;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if(!traceModel.getColorScaleAnchors().isEmpty()) {

			double min = processMinValueFromDimension(targetColorScaleDimension);
			double max = processMaxValueFromDimension(targetColorScaleDimension);
			double range = max - min;
			
			String colorValueType = traceModel.get(PlotToken.TRACE_SCALE_COLOR_VALUE_TYPE);
			boolean usingRelativePercentages =
				StringUtils.isNotBlank(colorValueType) &&
				colorValueType.equals(CommonPlotTerms.VALUE_RELATIVE_PERCENTAGES.getLabel());
			
			// Use the trace model's color scale information to form pairs of double and RGB objects.
			List<Pair<Double, RGB>> colorScale = new ArrayList<>();
			for(int i = 0; i < traceModel.getColorScaleAnchors().size() && i < traceModel.getRGBs().size(); i++) {
				Double colorValue = traceModel.getColorScaleAnchors().get(i);
				RGB color         = traceModel.getRGBs().get(i);
				
				if(usingRelativePercentages) {
					colorValue = min + (range * colorValue * .01);
				}
				colorScale.add(new ImmutablePair<>(colorValue, color));
			}
			Collections.sort(colorScale, (Pair<Double, RGB> p1, Pair<Double, RGB> p2) -> {
				return p1.getLeft().compareTo(p2.getLeft());
			});
			
			// The list of actual color scale anchor points we'll use.
			List<Double> colorScaleAnchorPoints = new ArrayList<>();
			
			// Include min and max array values as part of the color scale so that it normalizes the way we want.
			colorScaleAnchorPoints.add(min);
			colorScaleAnchorPoints.add(max);
			
			for(Pair<Double, RGB> colorAnchor : colorScale) {
				Double colorAnchorValue = colorAnchor.getKey();
				
				// Any color scale anchor above the max value or below the min value is thrown out.
				if(colorAnchorValue >= min && colorAnchorValue <= max) {
					colorScaleAnchorPoints.add(colorAnchorValue);
				}
			}
			Collections.sort(colorScaleAnchorPoints);
			
			// Normalize the color scale (because Plotly assumes a normalized color scale).
			double[] colorScaleNormalized = ArrayUtil.normalizeArray(ArrayUtil.asDoubleArrFromDoubleList(colorScaleAnchorPoints));
			
			// Determine what colors to use.  We have to do some fancy math because the user
			// may have specified min or max color scale anchors that don't correspond to the
			// actual min and max of the data being plotted.
			List<Pair<Double, RGB>> normalizedColorScale = new ArrayList<>();
			for(int i = 0; i < colorScaleAnchorPoints.size(); i++) {
				RGB scaledColor = getScaledColor(colorScale, colorScaleAnchorPoints.get(i));
				normalizedColorScale.add(
					new ImmutablePair<>(colorScaleNormalized[i], scaledColor)
				);
			}
			
			// Finally, print the color scale in Plotly format.
			for(Pair<Double, RGB> colorScaleValue : normalizedColorScale) {
				sb.append("[").append(colorScaleValue.getLeft()).append(", ");
				RGB rgb = colorScaleValue.getRight();
				if(rgb != null) {
					sb.append("'rgb(" + rgb.red + ", " + rgb.green + ", " + rgb.blue + ")'");
					sb.append("]");
					sb.append(",");
				}
			}
		} else {
			sb.append(processColorScaleDefault());
		}
		sb.append("]");
		return sb.toString();
	}
	
	private String processColorScaleDiscrete() {
		final double DISCRETE_ADVANCE_VALUE = 0.0001;		
		
		Dimension targetColorScaleDimension = null;
		if(traceModel.getPointType() == PlotType.PARALLEL_COORD_PLOT) {
			targetColorScaleDimension = Dimension.X;
		} else {
			targetColorScaleDimension = Dimension.Z;
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if(!traceModel.getColorScaleAnchors().isEmpty()) {
			
		    String colorValueType = traceModel.get(PlotToken.TRACE_SCALE_COLOR_VALUE_TYPE);
            boolean usingRelativePercentages =
                StringUtils.isNotBlank(colorValueType) &&
                colorValueType.equals(CommonPlotTerms.VALUE_RELATIVE_PERCENTAGES.getLabel());
		    
			double min = processMinValueFromDimension(targetColorScaleDimension);
			double max = processMaxValueFromDimension(targetColorScaleDimension);
			if(usingRelativePercentages) {
			    min = 0.0;
			    max = 100.0;
			}
			double range = max - min;
			
			// Use the trace model's color scale information to form pairs of double and RGB objects.
			List<Pair<Double, RGB>> colorScale = new ArrayList<>();
			for(int i = 0; i < traceModel.getColorScaleAnchors().size() && i < traceModel.getRGBs().size(); i++) {
				Double colorValue = traceModel.getColorScaleAnchors().get(i);
				RGB color         = traceModel.getRGBs().get(i);
				
				if(usingRelativePercentages) {
					colorValue = min + (range * colorValue * .01);
				}
				colorScale.add(new ImmutablePair<>(colorValue, color));
			}
			Collections.sort(colorScale, (Pair<Double, RGB> p1, Pair<Double, RGB> p2) -> {
				return p1.getLeft().compareTo(p2.getLeft());
			});
			
			// The list of actual color scale anchor points we'll use.
			List<Double> colorScaleAnchorPoints = new ArrayList<>();
			
			// Include min and max array values as part of the color scale so that it normalizes the way we want.
			if(usingRelativePercentages) {
			    colorScaleAnchorPoints.add(0.0);
			    colorScaleAnchorPoints.add(100.0);
			    colorScaleAnchorPoints.add(100.0 - DISCRETE_ADVANCE_VALUE);
			} else {
			    colorScaleAnchorPoints.add(min);
                colorScaleAnchorPoints.add(max);
                colorScaleAnchorPoints.add(max - DISCRETE_ADVANCE_VALUE);
			}
			
			// Any color scale anchor above the max value or below the min value is thrown out.			
			for(Double colorAnchor : traceModel.getColorScaleAnchors()) {
				if(colorAnchor > min && colorAnchor < max) {
					colorScaleAnchorPoints.add(colorAnchor);
					colorScaleAnchorPoints.add(colorAnchor - DISCRETE_ADVANCE_VALUE);
				}
			}
			Collections.sort(colorScaleAnchorPoints);
			
			// Normalize the color scale (because Plotly assumes a normalized color scale).
			double[] colorScaleNormalized = ArrayUtil.normalizeArray(ArrayUtil.asDoubleArrFromDoubleList(colorScaleAnchorPoints));
			
			// Determine what colors to use.  We have to do some fancy math because the user
			// may have specified min or max color scale anchors that don't correspond to the
			// actual min and max of the data being plotted.
			List<Pair<Double, RGB>> normalizedColorScale = new ArrayList<>();
			for(int i = 0; i < colorScaleAnchorPoints.size(); i += 2) {
				RGB nearestColor = getNearestDiscreteColor(colorScale, colorScaleAnchorPoints.get(i));
				normalizedColorScale.add(
					new ImmutablePair<>(colorScaleNormalized[i], nearestColor)
				);
				if(i + 1 < colorScaleAnchorPoints.size()) {
					normalizedColorScale.add(
						new ImmutablePair<>(colorScaleNormalized[i+1], nearestColor)
					);	
				}
			}
			Collections.sort(normalizedColorScale, (Pair<Double, RGB> p1, Pair<Double, RGB> p2) -> {
				return p1.getLeft().compareTo(p2.getLeft());
			});
			
			// Finally, print the color scale in Plotly format.
			for(Pair<Double, RGB> colorScaleValue : normalizedColorScale) {
				sb.append("[").append(colorScaleValue.getLeft()).append(", ");
				RGB rgb = colorScaleValue.getRight();
				sb.append("'rgb(" + rgb.red + ", " + rgb.green + ", " + rgb.blue + ")'");
				sb.append("]");
				sb.append(",");
			}
		} else {
			sb.append(processColorScaleDefault());
		}
		sb.append("]");
		return sb.toString();
	}
	
	private String processColorScaleDefault() {
		// Worst case scenario, we can still make a reasonable color scale with only color values.
		StringBuilder sb = new StringBuilder();
		List<RGB> colors = traceModel.getRGBs();
		
		for(RGB rgb : colors) {
			sb.append("[");
			double positionValue = (colors.indexOf(rgb))*1.0 / ((colors.size()-1)*1.0);
			sb.append("'").append(Double.toString(positionValue)).append("', ");			
			sb.append("'rgb(" + rgb.red + ", " + rgb.green + ", " + rgb.blue + ")'");
			sb.append("]");
			
			if(colors.indexOf(rgb) < colors.size()-1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	private RGB getScaledColor(List<Pair<Double, RGB>> scale, double positionOnColorScale) {
		Pair<Double, RGB> lowerBound = scale.get(0);
		Pair<Double, RGB> upperBound = scale.get(scale.size() - 1);
		
		if(positionOnColorScale <= lowerBound.getLeft()) {
			return lowerBound.getRight();
		} else if(positionOnColorScale >= upperBound.getLeft()) {
			return upperBound.getRight();
		}
		
		for(int i = 1; i < scale.size(); i++) {
			int lowerSide = i - 1;
			int upperSide = i;
			
			if(positionOnColorScale >= scale.get(lowerSide).getLeft() && positionOnColorScale <= scale.get(upperSide).getLeft()) {
				// Get the RGB values on either side.
				RGB lowerRGB = scale.get(lowerSide).getRight();
				RGB upperRGB = scale.get(upperSide).getRight();
				
				// How much RGB distance is there between the two sides?
				int redDistance   = upperRGB.red   - lowerRGB.red;
				int greenDistance = upperRGB.green - lowerRGB.green;
				int blueDistance  = upperRGB.blue  - lowerRGB.blue;
				
				// Find out how far away our position is from the left side.
				// (We could've also used the right side here.)
				double percentageGreater =
					Math.abs((positionOnColorScale - scale.get(lowerSide).getLeft()) / (scale.get(upperSide).getLeft() - scale.get(lowerSide).getLeft()
				));
				
				// Create the new color value.
				int newRed   = (int) (lowerRGB.red   + redDistance * percentageGreater);
				int newGreen = (int) (lowerRGB.green + greenDistance * percentageGreater);
				int newBlue  = (int) (lowerRGB.blue  + blueDistance * percentageGreater);
				
				return new RGB(newRed, newGreen, newBlue);
			}
		}
		return null;
	}
	
	private RGB getNearestDiscreteColor(List<Pair<Double, RGB>> scale, double positionOnColorScale) {
		Pair<Double, RGB> lowerBound = scale.get(0);
		Pair<Double, RGB> upperBound = scale.get(scale.size() - 1);
		
		if(positionOnColorScale <= lowerBound.getLeft()) {
			return lowerBound.getRight();
		} else if(positionOnColorScale >= upperBound.getLeft()) {
			return upperBound.getRight();
		}
		
		for(int i = 1; i < scale.size(); i++) {
			int lowerSide = i - 1;
			int upperSide = i;
			
			if(positionOnColorScale >= scale.get(lowerSide).getLeft() && positionOnColorScale <= scale.get(upperSide).getLeft()) {
				// Get the RGB values on either side.
				RGB lowerRGB = scale.get(lowerSide).getRight();
				RGB upperRGB = scale.get(upperSide).getRight();
				
				// Find out how far away our position is from the left side.
				// (We could've also used the right side here.)
				double percentageGreater =
					Math.abs(
					    (positionOnColorScale - scale.get(lowerSide).getLeft()) /
					    (scale.get(upperSide).getLeft() - scale.get(lowerSide).getLeft())
					);
				
				if(percentageGreater > .5) {
					return upperRGB;
				} else {
					return lowerRGB;
				}
			}
		}
		return null;
	}

	private String processAsMetadata(List<PlotTracePoint> points) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i = 0; i < points.size(); i++) {
			PlotTracePoint point = points.get(i);
			sb.append("'");

			Map<String, String> metadataForThisPoint = point.metadata;
			List<Entry<String, String>> entries = new ArrayList<>(metadataForThisPoint.entrySet());
			for(int j = 0; j < entries.size(); j++) {
				Entry<String, String> entry = entries.get(j);
				sb.append(entry.getKey()).append(": ").append(entry.getValue());
				if(j < entries.size() - 1) {
					sb.append("<br>");
				}
			}
			sb.append("'");
			if(i < points.size() - 1) {
				sb.append(",");
			}
		}
		sb.append("]");
		return sb.toString();
	}
}

