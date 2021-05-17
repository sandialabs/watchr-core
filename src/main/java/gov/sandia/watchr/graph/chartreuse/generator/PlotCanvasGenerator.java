/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import gov.sandia.watchr.graph.chartreuse.Dimension;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.util.ArrayUtil;
import gov.sandia.watchr.util.OsUtil;
import gov.sandia.watchr.util.TokenStringUtil;

public abstract class PlotCanvasGenerator {

	////////////
	// FIELDS //
	////////////

	protected PlotType plotType;
	protected PlotCanvasModel canvasModel;
	protected PlotTraceGenerator traceGenerator;
	protected int displayRange;

	/////////////////
	// CONSTRUCTOR //
	/////////////////
	
	protected PlotCanvasGenerator(PlotTraceGenerator traceGenerator) {
		this.traceGenerator = traceGenerator;
	}

	/////////////
	// SETTERS //
	/////////////
	
	public void setPlotCanvasModel(PlotCanvasModel canvasModel) {
		this.canvasModel = canvasModel;
	}
	
	public void setPlotType(PlotType plotType) {
		this.plotType = plotType;
	}

	public void setDisplayRange(int displayRange) {
		this.displayRange = displayRange;
	}

	/////////////
	// GETTERS //
	/////////////
	
	public PlotType getPlotType() {
		return plotType;
	}

	protected PlotCanvasModel getCanvasModel() {
		return canvasModel;
	}

	//////////////
	// GENERATE //
	//////////////
	
	public String generatePlotCanvas(PlotCanvasModel canvasModel, PlotType plotType, int displayRange) throws IOException {
		this.canvasModel = canvasModel;
		this.plotType = plotType;
		this.displayRange = displayRange;
		return replacePlotCanvasModelTokens();
	}
	
	protected String replacePlotCanvasModelTokens() throws IOException {
		int level = 2;
		String replacedTokensFileString = getTemplateFileAsString(plotType);
		
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_TRACES, level,                   () -> { return processTraces(canvasModel.getTraceModels()); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_DRAW_AXIS_LINES, level,          () -> { return processCanvasDrawAxisLines(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_DRAW_GRID_LINES, level,          () -> { return processCanvasDrawGridLines(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_X_AUTOSCALE, level,              () -> { return processCanvasXAutoscale(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_X_AXIS_COLOR, level,             () -> { return processCanvasXAxisColor(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_X_AXIS_LABEL, level,             () -> { return processCanvasXAxisLabel(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_X_DISPLAYED_RANGE_START, level,  () -> { return processCanvasXDisplayedRangeStart(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_X_DISPLAYED_RANGE_END, level,    () -> { return processCanvasXDisplayedRangeEnd(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_X_DOMAIN_START, level,           () -> { return processCanvasXDomainStart(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_X_DOMAIN_END, level,             () -> { return processCanvasXDomainEnd(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_X_INDEX, level,                  () -> { return processCanvasXIndex(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_X_POSITION, level,               () -> { return processCanvasXPosition(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_X_TYPE, level,                   () -> { return processCanvasXType(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_Y_AUTOSCALE, level,              () -> { return processCanvasYAutoscale(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_Y_AXIS_COLOR, level,             () -> { return processCanvasYAxisColor(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_Y_AXIS_LABEL, level,             () -> { return processCanvasYAxisLabel(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_Y_DISPLAYED_RANGE_START, level,  () -> { return processCanvasYDisplayedRangeStart(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_Y_DISPLAYED_RANGE_END, level,    () -> { return processCanvasYDisplayedRangeEnd(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_Y_DOMAIN_START, level,           () -> { return processCanvasYDomainStart(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_Y_DOMAIN_END, level,             () -> { return processCanvasYDomainEnd(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_Y_INDEX, level,                  () -> { return processCanvasYIndex(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_Y_POSITION, level,               () -> { return processCanvasYPosition(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_Y_TYPE, level,                   () -> { return processCanvasYType(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_BASE_AXIS_Y, level,              () -> { return processCanvasBaseAxisY(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_Z_AXIS_LABEL, level,             () -> { return processCanvasZAxisLabel(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_Z_AXIS_COLOR, level,             () -> { return processCanvasZAxisColor(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_Z_TYPE, level,                   () -> { return processCanvasZType(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_AXIS_DECIMAL_PRECISION, level,   () -> { return processCanvasAxisPrecision(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_HOVER_DECIMAL_PRECISION, level,  () -> { return processCanvasHoverPrecision(); });
		return replacedTokensFileString;
	}
	
	public String processTraces(List<PlotTraceModel> traceModels) throws IOException {
		StringBuilder sb = new StringBuilder();
		for(PlotTraceModel traceModel : new ArrayList<>(traceModels)) {
			sb.append(traceGenerator.generatePlotTrace(traceModel, traceModel.getPointType(), displayRange));
			sb.append(OsUtil.getOSLineBreak());
			
			// Linear regression lines
			if(traceModel.getPropertyAsBoolean(PlotToken.TRACE_DRAW_LINEAR_REGRESSION_LINE)) {
				sb.append(processLinRegTraceModel(traceModel));
			}
		}
		return sb.toString();
	}
	
	private String processLinRegTraceModel(PlotTraceModel parentTraceModel) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		double[] xData = ArrayUtil.asDoubleArrFromStringList(new ArrayList<>(parentTraceModel.getDimensionValues(Dimension.X)));
		double[] yData = ArrayUtil.asDoubleArrFromStringList(new ArrayList<>(parentTraceModel.getDimensionValues(Dimension.Y)));

		Pair<Double, Double> linearRegression = ArrayUtil.getLinearRegression(xData, yData);
				 
		double minX = ArrayUtil.getMinFromStringList(new ArrayList<>(parentTraceModel.getDimensionValues(Dimension.X)));
		double maxX = ArrayUtil.getMaxFromStringList(new ArrayList<>(parentTraceModel.getDimensionValues(Dimension.X)));
		
		double slope = linearRegression.getLeft();
		double intercept = linearRegression.getRight();
		
		double minY = slope * minX + intercept;
		double maxY = slope * maxX + intercept;
		 
		List<PlotTracePoint> linRegData = new ArrayList<>();
		linRegData.add(new PlotTracePoint(minX, minY));
		linRegData.add(new PlotTracePoint(maxX, maxY));
		 
		PlotTraceModel linRegTraceModel = (new PlotTraceModel(parentTraceModel.getParentUUID(), parentTraceModel))
			 .setPoints(linRegData)
			 .setName("Linear Regression: " + parentTraceModel.getName())
			 .set(PlotToken.TRACE_DRAW_LINES, true);
		
		parentTraceModel.getParent().addTraceModel(linRegTraceModel);
		 
		sb.append(traceGenerator.generatePlotTrace(linRegTraceModel, PlotType.DEFAULT, displayRange));
	    sb.append(OsUtil.getOSLineBreak());
	    
	    // Remove the new trace model from the parent canvas after we're done with it.
	    parentTraceModel.getParent().removeTraceModel(linRegTraceModel);
	    
	    return sb.toString();
	}

	//////////////
	// ABSTRACT //
	//////////////
	
	public abstract String getTemplateFileAsString(PlotType type);
		
	public abstract String processCanvasDrawAxisLines();
	
	public abstract String processCanvasDrawGridLines();
		
	public abstract String processCanvasXAutoscale();
	
	public abstract String processCanvasXAxisColor();
	
	public abstract String processCanvasXAxisLabel();
	
	public abstract String processCanvasXIndex();	
	
	public abstract String processCanvasXDisplayedRangeStart();
	
	public abstract String processCanvasXDisplayedRangeEnd();
	
	public abstract String processCanvasXDomainStart();
	
	public abstract String processCanvasXDomainEnd();
	
	public abstract String processCanvasXPosition();
	
	public abstract String processCanvasXType();
	
	public abstract String processCanvasYAutoscale();
	
	public abstract String processCanvasYAxisColor();
	
	public abstract String processCanvasYAxisLabel();
	
	public abstract String processCanvasYIndex();	
	
	public abstract String processCanvasYDisplayedRangeStart();
	
	public abstract String processCanvasYDisplayedRangeEnd();
	
	public abstract String processCanvasYDomainStart();
	
	public abstract String processCanvasYDomainEnd();
	
	public abstract String processCanvasYPosition();
	
	public abstract String processCanvasYType();
	
	public abstract String processCanvasBaseAxisY();
	
	public abstract String processCanvasZType();
	
	public abstract String processCanvasZAxisColor();
	
	public abstract String processCanvasZAxisLabel();
	
	public abstract String processCanvasAxisPrecision();
	
	public abstract String processCanvasHoverPrecision();
}