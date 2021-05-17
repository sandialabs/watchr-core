/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse.generator.plotly;

import gov.sandia.watchr.graph.chartreuse.Dimension;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.generator.PlotCanvasGenerator;
import gov.sandia.watchr.graph.chartreuse.generator.PlotTraceGenerator;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.util.RGB;

public class PlotlyCanvasGenerator extends PlotCanvasGenerator {
	
	////////////
	// FIELDS //
	////////////
	
	private static final double BUFFER_BETWEEN_CANVASES_FACTOR = 50;
	private static final double VERTICAL_AXIS_LENGTH = 0.05;

	private PlotlyWindowGenerator parent;
	
	/////////////////
	// CONSTRUCTOR //
	/////////////////
	
	public PlotlyCanvasGenerator(PlotTraceGenerator plotGenerator) {
		super(plotGenerator);
	}

	/////////////
	// GETTERS //
	/////////////

	public PlotlyWindowGenerator getParent() {
		return parent;
	}

	/////////////
	// SETTERS //
	/////////////

	public void setParent(PlotlyWindowGenerator parent) {
		this.parent = parent;
	}
	
	//////////////
	// OVERRIDE //
	//////////////

	@Override
	public String getTemplateFileAsString(PlotType type) {
		if(type == PlotType.DEFAULT) {
			return PlotlyHtmlFragmentGenerator.getCanvasBase();
		} else if(type == PlotType.LAYOUT) {
			return PlotlyHtmlFragmentGenerator.getCanvasLayoutBase();
		} else if(type == PlotType.LAYOUT_3D) {
			return PlotlyHtmlFragmentGenerator.getCanvasLayout3D();
		}
		return "";
	}

	@Override
	public String processCanvasDrawAxisLines() {
		return Boolean.toString(canvasModel.getDrawAxisLines());
	}

	@Override
	public String processCanvasDrawGridLines() {
		return Boolean.toString(canvasModel.getDrawGridLines());
	}
	
	@Override
	public String processCanvasXAutoscale() {
		boolean autoscale = canvasModel.getAutoscale();
		if(Math.abs(canvasModel.getMaximumTraceValue(Dimension.X)) == Double.MAX_VALUE) {
			autoscale = true;
		}		
		return Boolean.toString(autoscale);
	}

	@Override
	public String processCanvasXAxisColor() {
		RGB rgb = canvasModel.getXAxisRGB();
		if(rgb != null) {
			return "'rgb(" + rgb.red + ", " + rgb.green + ", " + rgb.blue + ")'";
		}
		return "";
	}

	@Override
	public String processCanvasXAxisLabel() {
		return "'" + canvasModel.getXAxisLabel() + "'";
	}

	@Override
	public String processCanvasXIndex() {
		return Integer.toString(parent.getCanvasIndexOffset());
	}

	@Override
	public String processCanvasXDisplayedRangeStart() {
		double xRangeStartValue;
		if(canvasModel.getXAxisRangeStart() != Double.NEGATIVE_INFINITY) {
			xRangeStartValue = canvasModel.getXAxisRangeStart();
		} else if(canvasModel.getAutoscale()) {
			xRangeStartValue = canvasModel.getMinimumTraceValue(Dimension.X);
		} else {
			xRangeStartValue = canvasModel.getLocalMinimumTraceValue(Dimension.X);
		}
		return Double.toString(xRangeStartValue);
	}

	@Override
	public String processCanvasXDisplayedRangeEnd() {
		double xRangeEndValue;
		if(canvasModel.getXAxisRangeEnd() != Double.POSITIVE_INFINITY) {
			xRangeEndValue = canvasModel.getXAxisRangeEnd();
		} else if(canvasModel.getAutoscale()) {
			xRangeEndValue = canvasModel.getMaximumTraceValue(Dimension.X);
		} else {
			xRangeEndValue = canvasModel.getLocalMaximumTraceValue(Dimension.X);
		}
		return Double.toString(xRangeEndValue);
	}

	@Override
	public String processCanvasXDomainStart() {
		int xIndex = canvasModel.getColPosition();
		double canvasSize = (1.0 / (1.0 * canvasModel.getParent().getColCount()));
		return Double.toString(calculateDomainStart(xIndex, canvasSize));
	}

	@Override
	public String processCanvasXDomainEnd() {
		int xIndex = canvasModel.getColPosition();
		
		double canvasSize = (1.0 / (1.0 * canvasModel.getParent().getColCount()));
		int numberOfOverlaidCanvases = getCanvasModel().getOverlaidCanvasModels().size();
		
		double domainEnd = calculateDomainEnd(xIndex, canvasSize, false);
		domainEnd -= numberOfOverlaidCanvases * VERTICAL_AXIS_LENGTH;
		
		if(domainEnd < calculateDomainStart(xIndex, canvasSize)) {
			domainEnd = calculateDomainEnd(xIndex, canvasSize, false);
		}
		
		return Double.toString(domainEnd);
	}

	@Override
	public String processCanvasXPosition() {
		int xIndex = canvasModel.getInvertedRowPosition();
		double yCanvasWithBuffer = (1.0 / (1.0 * canvasModel.getParent().getRowCount()));
		return Double.toString(calculateDomainStart(xIndex, yCanvasWithBuffer));
	}
	
	@Override
	public String processCanvasXType() {
		String scaleType = canvasModel.getXLogScale() ? PlotType.SCALE_LOG.getLabel() : PlotType.SCALE_LINEAR.getLabel();
		
		if(PlotType.getTypeByLabel(scaleType) == PlotType.SCALE_LOG) {
			return "'log'";			
		} else if(canvasModel.getTraceModels().size() == 1 &&
				  canvasModel.getTraceModels().get(0).getPointType() == PlotType.HEAT_MAP_CATEGORICAL ||
				  canvasModel.getTraceModels().get(0).getPointType() == PlotType.BAR_CHART ||
				  canvasModel.getTraceModels().get(0).getPointType() == PlotType.BAR_CHART_CATEGORICAL) {
			return "''";			
		}
		
		return "''";
	}
	
	@Override
	public String processCanvasYAutoscale() {
		boolean autoscale = canvasModel.getAutoscale();
		if(Math.abs(canvasModel.getMaximumTraceValue(Dimension.Y)) == Double.MAX_VALUE) {
			autoscale = true;
		}		
		return Boolean.toString(autoscale);
	}

	@Override
	public String processCanvasYAxisColor() {
		RGB rgb = canvasModel.getYAxisRGB();
		if(rgb != null) {
			return "'rgb(" + rgb.red + ", " + rgb.green + ", " + rgb.blue + ")'";
		}
		return "";
	}

	@Override
	public String processCanvasYAxisLabel() {
		String yAxisLabel = canvasModel.getYAxisLabel();
		return "'" + yAxisLabel + "'";
	}

	@Override
	public String processCanvasYIndex() {
		return Integer.toString(parent.getCanvasIndexOffset());
	}

	@Override
	public String processCanvasYDisplayedRangeStart() {
		double yRangeStartValue;
		if(canvasModel.getYAxisRangeStart() != Double.NEGATIVE_INFINITY) {
			yRangeStartValue = canvasModel.getYAxisRangeStart();
		} else if(canvasModel.getAutoscale()) {
			yRangeStartValue = canvasModel.getMinimumTraceValue(Dimension.Y);
		} else {
			yRangeStartValue = canvasModel.getLocalMinimumTraceValue(Dimension.Y);
		}
		return Double.toString(yRangeStartValue);
	}

	@Override
	public String processCanvasYDisplayedRangeEnd() {
		double yRangeEndValue;
		if(canvasModel.getYAxisRangeEnd() != Double.POSITIVE_INFINITY) {
			yRangeEndValue = canvasModel.getYAxisRangeEnd();
		} else if(canvasModel.getAutoscale()) {
			yRangeEndValue = canvasModel.getMaximumTraceValue(Dimension.Y);
		} else {
			yRangeEndValue = canvasModel.getLocalMaximumTraceValue(Dimension.Y);
		}
		return Double.toString(yRangeEndValue);
	}

	@Override
	public String processCanvasYDomainStart() {
		int yIndex = canvasModel.getInvertedRowPosition();
		double canvasSize = (1.0 / (1.0 * canvasModel.getParent().getRowCount()));
		return Double.toString(calculateDomainStart(yIndex, canvasSize));
	}

	@Override
	public String processCanvasYDomainEnd() {
		int yIndex = canvasModel.getInvertedRowPosition();
		double canvasSize = (1.0 / (1.0 * canvasModel.getParent().getRowCount()));
		return Double.toString(calculateDomainEnd(yIndex, canvasSize, true));
	}

	@Override
	public String processCanvasYPosition() {
		int yIndex = canvasModel.getColPosition();
		double canvasSize = (1.0 / (1.0 * canvasModel.getParent().getColCount()));
		
		PlotCanvasModel baseCanvasModel = getCanvasModel().getBaseCanvasModelIfOverlaid();
		double finalPosition;
		if(baseCanvasModel != null) {
			int myOverlayIndex = baseCanvasModel.getOverlaidCanvasModels().indexOf(getCanvasModel());
			double overlayAxisPosition = myOverlayIndex * VERTICAL_AXIS_LENGTH;
			// Put overlay axes on the right
			finalPosition = calculateDomainEnd(yIndex, canvasSize, false);
			finalPosition -= overlayAxisPosition;
		} else {
			// Put base axes on the left
			finalPosition = calculateDomainStart(yIndex, canvasSize);
		}
		
		return Double.toString(finalPosition);
	}
	
	@Override
	public String processCanvasYType() {
		String scaleType = canvasModel.getYLogScale() ? PlotType.SCALE_LOG.getLabel() : PlotType.SCALE_LINEAR.getLabel();
		
		if(PlotType.getTypeByLabel(scaleType) == PlotType.SCALE_LOG) {
			return "'log'";			
		} else if(canvasModel.getTraceModels().size() == 1 &&
				  canvasModel.getTraceModels().get(0).getPointType() == PlotType.HEAT_MAP_CATEGORICAL ||
				  canvasModel.getTraceModels().get(0).getPointType() == PlotType.BAR_CHART ||
				  canvasModel.getTraceModels().get(0).getPointType() == PlotType.BAR_CHART_CATEGORICAL) {
			return "''";			
		}

		return "''";
	}

	@Override
	public String processCanvasBaseAxisY() {
		PlotCanvasModel basePlotCanvas = canvasModel.getBaseCanvasModelIfOverlaid();
		int canvasIndexOffset = getParent().getCanvasIndexOffset();
		if(basePlotCanvas != null) {
			return "'y" + PlotlyGeneratorUtil.processParentCanvasGridPosition(basePlotCanvas, true, canvasIndexOffset) + "'";
		}
		return "'y0'";
	}

	@Override
	public String processCanvasZAxisLabel() {
		String zAxisLabel = canvasModel.getZAxisLabel();
		return "'" + zAxisLabel + "'";
	}

	@Override
	public String processCanvasZType() {
		String scaleType = canvasModel.getZLogScale() ? PlotType.SCALE_LOG.getLabel() : PlotType.SCALE_LINEAR.getLabel();
		
		if(PlotType.getTypeByLabel(scaleType) == PlotType.SCALE_LOG) {
			return "'log'";			
		} else if(canvasModel.getTraceModels().size() == 1 &&
				  canvasModel.getTraceModels().get(0).getPointType() == PlotType.HEAT_MAP_CATEGORICAL ||
				  canvasModel.getTraceModels().get(0).getPointType() == PlotType.BAR_CHART ||
				  canvasModel.getTraceModels().get(0).getPointType() == PlotType.BAR_CHART_CATEGORICAL) {
			return "''";			
		}
		return "''";
	}

	@Override
	public String processCanvasZAxisColor() {
		RGB rgb = canvasModel.getZAxisRGB();
		if(rgb != null) {
			return "'rgb(" + rgb.red + ", " + rgb.green + ", " + rgb.blue + ")'";
		}
		return "";
	}
	
	@Override
	public String processCanvasAxisPrecision() {
		int precision = canvasModel.getAxisPrecision();
		if(precision < 0) {
			precision = 0;
		}
		StringBuilder tickFormatSb = new StringBuilder();
		if(precision > 0) {
    		tickFormatSb.append("'.");
    		tickFormatSb.append(precision);
    		tickFormatSb.append("g'");
		} else {
		    tickFormatSb.append("''");
		}
		return tickFormatSb.toString();
	}
	
    @Override
    public String processCanvasHoverPrecision() {
        int precision = canvasModel.getAxisPrecision();
        if(precision < 0) {
            precision = 0;
        }
        StringBuilder tickFormatSb = new StringBuilder();
        if(precision > 0) {
            tickFormatSb.append("'.");
            tickFormatSb.append(precision);
            tickFormatSb.append("e'");
        } else {
            tickFormatSb.append("''");
        }
        return tickFormatSb.toString();
    }
	
	/////////////
	// UTILITY //
	/////////////
	
	private double calculateDomainStart(int step, double canvasSize) {
		return ((step) * (canvasSize));
	}
	
	private double calculateDomainEnd(int step, double canvasSize, boolean vertical) {
		PlotWindowModel parentWindow = canvasModel.getParent();
		int viewHeight = parentWindow.getViewHeight();
		int viewWidth  = parentWindow.getViewWidth();
		
		int rowCount = canvasModel.getParent().getRowCount();
		int colCount = canvasModel.getParent().getColCount();
		double normalizedBuffer =
			(vertical ? (BUFFER_BETWEEN_CANVASES_FACTOR / viewHeight) : (BUFFER_BETWEEN_CANVASES_FACTOR / viewWidth));
		if(normalizedBuffer < 0) {
			normalizedBuffer = 0.06; // A resonable default for canvas buffering that works in most cases.
		}
		
		double domainEnd;
		if(step == rowCount - 1 && vertical) {
			// We're on the last canvas, so don't include buffer... go all the way to the edge.
			domainEnd = ((step) * (canvasSize)) + canvasSize;
		} else if(step == colCount - 1 && !vertical) {
			domainEnd = ((step) * (canvasSize)) + canvasSize;
		} else {
			domainEnd = ((step) * (canvasSize)) + canvasSize;
			if(canvasSize > normalizedBuffer) {
				domainEnd -= normalizedBuffer;
			}
		}
		
		return domainEnd;
	}
}

