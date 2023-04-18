/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse.generator;

import java.io.IOException;

import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceOptions;
import gov.sandia.watchr.util.OsUtil;
import gov.sandia.watchr.util.TokenStringUtil;

public abstract class PlotTraceGenerator {
	
	////////////
	// FIELDS //
	////////////
	
	protected PlotType plotType;
	protected PlotTraceModel traceModel;
	protected PlotTraceOptions options;
	
	/////////////
	// SETTERS //
	/////////////
	
	public void setPlotTraceModel(PlotTraceModel traceModel) {
		this.traceModel = traceModel;
	}
	
	public void setPlotType(PlotType plotType) {
		this.plotType = plotType;
	}
	
	/////////////
	// GETTERS //
	/////////////
	
	public PlotType getPlotType() {
		return plotType;
	}
	
	protected PlotTraceModel getTraceModel() {
		return traceModel;
	}

	public PlotTraceOptions getOptions() {
		return options;
	}
	
	//////////////
	// GENERATE //
	//////////////
	
	public String generatePlotTrace(PlotTraceModel traceModel, PlotType plotType, int displayRange) throws IOException {
		setPlotTraceModel(traceModel);
		setPlotType(plotType);
		options.displayRange = displayRange;
		options.filterPoints = true;
		
		StringBuilder sb = new StringBuilder();
		sb.append(replacePlotModelTokens());
		sb.append(OsUtil.getOSLineBreak());
		
		return sb.toString();
	}
	
	protected String replacePlotModelTokens() throws IOException {
		int level = 1;		
		String replacedTokensFileString = getTemplateFileAsString(plotType);

		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_ARR_DEPENDENT, level,            () -> {return processArrDependent(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_ARR_INDEPENDENT, level,	      () -> {return processArrIndependent(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_ARR_N_DIMENSIONAL_LOOP, level,   () -> {return processArrNDimensionalLoop(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_ARR_X, level,                    () -> {return processArrX(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_ARR_Y, level,			          () -> {return processArrY(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_ARR_Z, level,                    () -> {return processArrZ(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_ARR_METADATA, level,             () -> {return processArrMetadata(); });		
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_AXIS_DEPENDENT_VAR, level,       () -> {return processAxisDependentVar(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_AXIS_INDEPENDENT_VAR, level,     () -> {return processAxisIndependentVar(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_BOUND_LOWER, level,              () -> {return processLowerBound(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_BOUND_UPPER, level,              () -> {return processUpperBound(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_COLOR, level,                    () -> {return processColor(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_CUMULATIVE, level,               () -> {return processCumulative(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_DRAW_LINES, level,               () -> {return processDrawLines(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_DRAW_COLOR_SCALE, level,         () -> {return processDrawColorScale(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_ERROR_ARR_X_LOWER, level,        () -> {return processErrorArrXLower(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_ERROR_ARR_X_UPPER, level,        () -> {return processErrorArrXUpper(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_ERROR_ARR_X_VISIBLE, level,      () -> {return processErrorArrXVisible(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_ERROR_ARR_Y_LOWER, level,        () -> {return processErrorArrYLower(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_ERROR_ARR_Y_UPPER, level,        () -> {return processErrorArrYUpper(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_ERROR_ARR_Y_VISIBLE, level,      () -> {return processErrorArrYVisible(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_ERROR_ARR_Z_LOWER, level,        () -> {return processErrorArrZLower(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_ERROR_ARR_Z_UPPER, level,        () -> {return processErrorArrZUpper(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_ERROR_ARR_Z_VISIBLE, level,      () -> {return processErrorArrZVisible(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_HOVER_TEXT, level,               () -> {return processHoverTextFormatting(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_NAME,  level,                    () -> {return processPlotName(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_LABEL, level,                    () -> {return processPlotLabel(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_OPACITY, level,                  () -> {return processOpacity(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_ORIENTATION, level,              () -> {return processOrientation(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_PARENT_CANVAS_X, level,          () -> {return processParentCanvasX(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_PARENT_CANVAS_Y, level,          () -> {return processParentCanvasY(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_POINT_MODE, level,               () -> {return processPointMode(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_POINT_TYPE, level,               () -> {return processPointType(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_PRECISION, level,                () -> {return processPrecision(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.TRACE_SCALE_COLOR, level,              () -> {return processColorScale(); });
		return replacedTokensFileString;
	}
	
	//////////////
	// ABSTRACT //
	//////////////
	
	public abstract String getTemplateFileAsString(PlotType type);
	
	public abstract String processAxisDependentVar();
	public abstract String processAxisIndependentVar();
	public abstract String processArrDependent();
	public abstract String processArrIndependent();
	public abstract String processArrNDimensionalLoop();
	public abstract String processArrMetadata();
	public abstract String processArrX();
	public abstract String processArrY();
	public abstract String processArrZ();
	public abstract String processColor();
	public abstract String processColorScale();
	public abstract String processCumulative();
	public abstract String processDrawColorScale();
	public abstract String processDrawLines();
	public abstract String processErrorArrXUpper();
	public abstract String processErrorArrXLower();
	public abstract String processErrorArrXVisible();
	public abstract String processErrorArrYUpper();
	public abstract String processErrorArrYLower();
	public abstract String processErrorArrYVisible();
	public abstract String processErrorArrZUpper();
	public abstract String processErrorArrZLower();
	public abstract String processErrorArrZVisible();
	public abstract String processHoverTextFormatting();
	public abstract String processIterCount();
	public abstract String processOpacity();
	public abstract String processOrientation();
	public abstract String processParentCanvasX();
	public abstract String processParentCanvasY();
	public abstract String processPlotName();
	public abstract String processPlotLabel();
	public abstract String processPointMode();
	public abstract String processPointType();
	public abstract String processPrecision();
	public abstract String processLowerBound();
	public abstract String processUpperBound();
}

