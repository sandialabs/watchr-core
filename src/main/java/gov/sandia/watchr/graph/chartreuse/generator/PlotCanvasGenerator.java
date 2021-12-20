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
import java.util.Collections;
import java.util.List;

import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
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
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_DRAW_X_AXIS_LABELS, level,        () -> { return processCanvasDrawXAxisLabels(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_DRAW_Y_AXIS_LABELS, level,        () -> { return processCanvasDrawYAxisLabels(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_DRAW_Z_AXIS_LABELS, level,        () -> { return processCanvasDrawZAxisLabels(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_DRAW_X_AXIS_LINES, level,        () -> { return processCanvasDrawXAxisLines(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_DRAW_Y_AXIS_LINES, level,        () -> { return processCanvasDrawYAxisLines(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.CANVAS_DRAW_Z_AXIS_LINES, level,        () -> { return processCanvasDrawZAxisLines(); });
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
		List<PlotTraceModel> sortedList = new ArrayList<>(traceModels);
		StringBuilder sb = new StringBuilder();
		Collections.sort(sortedList, (PlotTraceModel p1, PlotTraceModel p2) -> {
			if(p1.getDerivativeLineType() == null && p2.getDerivativeLineType() == null) {
				return p1.getName().compareTo(p2.getName());
			} else if(p1.getDerivativeLineType() != null && p2.getDerivativeLineType() == null) {
				return 1;
			} else if(p1.getDerivativeLineType() == null && p2.getDerivativeLineType() != null) {
				return -1;
			}
			return 0;
		});

		for(PlotTraceModel traceModel : sortedList) {
			sb.append(traceGenerator.generatePlotTrace(traceModel, traceModel.getPointType(), displayRange));
			sb.append(OsUtil.getOSLineBreak());
		}
		return sb.toString();
	}

	//////////////
	// ABSTRACT //
	//////////////
	
	public abstract String getTemplateFileAsString(PlotType type);

	public abstract String processCanvasDrawXAxisLabels();

	public abstract String processCanvasDrawYAxisLabels();

	public abstract String processCanvasDrawZAxisLabels();

	public abstract String processCanvasDrawXAxisLines();

	public abstract String processCanvasDrawYAxisLines();

	public abstract String processCanvasDrawZAxisLines();
	
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