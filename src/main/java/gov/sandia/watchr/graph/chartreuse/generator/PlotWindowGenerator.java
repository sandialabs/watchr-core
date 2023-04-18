/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse.generator;

import java.io.IOException;
import java.util.List;

import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.util.OsUtil;
import gov.sandia.watchr.util.TokenStringUtil;

public abstract class PlotWindowGenerator {

	////////////
	// FIELDS //
	////////////

	protected PlotType plotType;
	protected PlotWindowModel windowModel;
	protected PlotCanvasGenerator canvasGenerator;
	protected int displayRange;

	/////////////////
	// CONSTRUCTOR //
	/////////////////
	
	protected PlotWindowGenerator(PlotCanvasGenerator canvasGenerator) {
		this.canvasGenerator = canvasGenerator;
	}

	/////////////
	// SETTERS //
	/////////////
	
	public void setPlotWindowModel(PlotWindowModel windowModel) {
		this.windowModel = windowModel;
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

	protected PlotWindowModel getWindowModel() {
		return windowModel;
	}	

	//////////////
	// GENERATE //
	//////////////

	public String generatePlotWindow(PlotWindowModel windowModel, PlotType plotType) throws IOException {
		return generatePlotWindow(windowModel, plotType, -1);
	}
	
	public String generatePlotWindow(PlotWindowModel windowModel, PlotType plotType, int displayRange) throws IOException {
		setPlotWindowModel(windowModel);
		setPlotType(plotType);
		setDisplayRange(displayRange);
		return replacePlotWindowModelTokens();
	}
	
	protected String replacePlotWindowModelTokens() throws IOException {
		int level = 3;
		String replacedTokensFileString = getTemplateFileAsString(plotType);
		
		List<PlotCanvasModel> canvasModels = windowModel.getCanvasModels();
		
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.WINDOW_CANVASES, level,           () -> { return processCanvases(canvasModels); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.WINDOW_CANVAS_LAYOUTS, level,     () -> { return processCanvasLayouts(canvasModels); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.WINDOW_TRACE_NAME_LIST, level,    () -> { return processTraceNamesList(canvasModels); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.WINDOW_LABEL, level,              () -> { return processWindowLabel(windowModel.getNameOrNickname(), windowModel.getViewWidth()); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.WINDOW_FONT, level,               () -> { return processFont(windowModel.getFont()); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.WINDOW_SHOW_LEGEND, level,        () -> { return processShowLegend(windowModel.getLegendVisible()); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.WINDOW_VIEW_WIDTH, level,         () -> { return processViewWidth(windowModel.getViewWidth()); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.WINDOW_VIEW_HEIGHT, level,        () -> { return processViewHeight(windowModel.getViewHeight()); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.WINDOW_SHOW_NUMBER_LABELS, level, () -> { return processShowNumberLabels(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.WINDOW_DIV_NAME, level,           () -> { return processDivName(); });
		replacedTokensFileString = TokenStringUtil.findAndReplaceToken(replacedTokensFileString, PlotToken.WINDOW_BACKGROUND, level,         () -> { return processBackgroundColor(); });
		return replacedTokensFileString;
	}
	
	public String processCanvases(List<PlotCanvasModel> canvasModels) throws IOException {
		StringBuilder sb = new StringBuilder();
		for(PlotCanvasModel canvasModel : canvasModels) {
			sb.append(canvasGenerator.generatePlotCanvas(canvasModel, PlotType.DEFAULT, displayRange));			
			sb.append(OsUtil.getOSLineBreak());
		}
		return sb.toString();
	}

	//////////////
	// ABSTRACT //
	//////////////
	
	public abstract String processCanvasLayouts(List<PlotCanvasModel> canvasModels) throws IOException;
	
	public abstract String getTemplateFileAsString(PlotType type);
	
	public abstract String processTraceNamesList(List<PlotCanvasModel> plotCanvasModels);
	
	public abstract String processWindowLabel(String plotWindowTitle, int processWindowLabel);

	public abstract String processViewHeight(int viewHeight);
	
	public abstract String processViewWidth(int viewWidth);
	
	public abstract String processFont(String fontName);
	
	public abstract String processShowLegend(boolean showLegend);
	
	public abstract String processShowNumberLabels();

	public abstract String processDivName();

	public abstract String processBackgroundColor();
}
