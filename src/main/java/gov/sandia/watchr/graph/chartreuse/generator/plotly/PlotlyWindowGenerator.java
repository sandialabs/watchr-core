/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse.generator.plotly;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.generator.PlotCanvasGenerator;
import gov.sandia.watchr.graph.chartreuse.generator.PlotWindowGenerator;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.util.OsUtil;
import gov.sandia.watchr.util.RGB;

public class PlotlyWindowGenerator extends PlotWindowGenerator {
	
	////////////
	// FIELDS //
	////////////

	private static final String DEFAULT_FONT = "Segoe UI";
	
	// This adjustment prevents scrollbars from showing up in the browser view if the user
	// just relies on that view's size to determine the plot window size.
	private static final int OVERRIDDEN_SIZE_BUFFER = 30;
	
	private final boolean renderStandaloneHtml;
	private int canvasIndexOffset = 1;

	private int overriddenHeight;
	private int overriddenWidth;

	//////////////////
	// CONSTRUCTORS //
	//////////////////
	
	public PlotlyWindowGenerator(PlotCanvasGenerator canvasGenerator, boolean renderStandaloneHtml) {
		this(canvasGenerator, 100, 100, renderStandaloneHtml);
		if(canvasGenerator instanceof PlotlyCanvasGenerator) {
			((PlotlyCanvasGenerator)canvasGenerator).setParent(this);
		}
	}
	
	public PlotlyWindowGenerator(PlotCanvasGenerator canvasGenerator, int overriddenHeight, int overriddenWidth, boolean renderStandaloneHtml) {
		super(canvasGenerator);
		this.overriddenHeight = overriddenHeight - OVERRIDDEN_SIZE_BUFFER;
		this.overriddenWidth = overriddenWidth - OVERRIDDEN_SIZE_BUFFER;
		this.renderStandaloneHtml = renderStandaloneHtml;
	}

	/////////////
	// GETTERS //
	/////////////

	public int getCanvasIndexOffset() {
		return canvasIndexOffset;
	}

	/////////////
	// SETTERS //
	/////////////

	public void resetCanvasIndexOffset() {
		canvasIndexOffset = 1;
	}
	
	//////////////
	// OVERRIDE //
	//////////////
	
	@Override
	public String getTemplateFileAsString(PlotType type) {
		if(renderStandaloneHtml) {
			if(isTablePlot()) {
				return PlotlyHtmlFragmentGenerator.getPlotlyWindowTable(false);
			} else if(type == PlotType.DEFAULT) {
				return PlotlyHtmlFragmentGenerator.getPlotlyWindowBase(false);
			}
		} else {
			if(isTablePlot()) {
				return PlotlyHtmlFragmentGenerator.getPlotlyWindowTable(true);
			} else if(type == PlotType.DEFAULT) {
				return PlotlyHtmlFragmentGenerator.getPlotlyWindowBase(true);
			}
		}
		return "";
	}
	
	@Override
	public String processCanvasLayouts(List<PlotCanvasModel> canvasModels) throws IOException {		
		StringBuilder sb = new StringBuilder();
		String returnString = "";
		final String canvasModelSeparator = "," + OsUtil.getOSLineBreak();
		
		if(windowModel.is3DWindowModel()) {
			sb.append(canvasGenerator.generatePlotCanvas(windowModel.getCanvasModels().get(0), PlotType.LAYOUT_3D, displayRange));
			returnString = sb.toString();
		} else {	
			for(PlotCanvasModel canvasModel : canvasModels) {
				if(!canvasModel.getTraceModels().isEmpty()) {
					
					if(canvasModel.getBaseCanvasModelIfOverlaid() != null) {
						sb.append(canvasGenerator.generatePlotCanvas(canvasModel, PlotType.OVERLAY, displayRange));
					} else {
						sb.append(canvasGenerator.generatePlotCanvas(canvasModel, PlotType.LAYOUT, displayRange));
					}
					sb.append(canvasModelSeparator);
					
				}
				canvasIndexOffset++;
			}
			returnString = sb.toString();
			if(returnString.endsWith(canvasModelSeparator)) {
				int canvasModelSeparatorLength = canvasModelSeparator.length();
				returnString = returnString.substring(0, returnString.length() - canvasModelSeparatorLength); //Remove last comma
			}
		}
		return returnString;
	}

	@Override
	public String processTraceNamesList(List<PlotCanvasModel> canvasModels) {
		StringBuilder sb = new StringBuilder();
		int traceCounter = 0;
		for(PlotCanvasModel canvasModel : canvasModels) {
			for(int i = 0; i < canvasModel.getTraceModels().size(); i++) {
				PlotTraceModel traceModel = canvasModel.getTraceModels().get(i);
				sb.append("trace");
				if(traceModel.getUUID() != null) {
					sb.append(Math.abs(traceModel.getUUID().hashCode()));
				} else {
					sb.append(Integer.toString(++traceCounter));
				}
				sb.append(", ");
			}
		}
		String returnedString = sb.toString();
		if(returnedString.length() > 2) {
			returnedString = returnedString.substring(0, returnedString.length() - 2); //Remove last comma
		}
		return returnedString;
	}

	@Override
	public String processWindowLabel(String plotWindowTitle, int viewWidth) {
		if(StringUtils.isNotBlank(plotWindowTitle)) {
			int stringLength = plotWindowTitle.length();

			final int plotlyWindowFontRatio = 9; // 9 pixels per character
			int graphWidth = (viewWidth == -1 ? overriddenWidth : viewWidth);
			int maximumStringLength = graphWidth / plotlyWindowFontRatio;
			if(plotlyWindowFontRatio * stringLength > graphWidth) {
				int beginIndex = plotWindowTitle.length() - maximumStringLength;
				if(beginIndex < 0) {
					beginIndex = 0;
				}
				int endIndex = plotWindowTitle.length();
				plotWindowTitle = "..." + plotWindowTitle.substring(beginIndex, endIndex);
			}
		}

		return "\"" + plotWindowTitle + "\"";
	}
	
	@Override
	public String processViewHeight(int viewHeight) {
		return viewHeight == -1 ? Integer.toString(overriddenHeight) : Integer.toString(viewHeight);
	}

	@Override
	public String processViewWidth(int viewWidth) {
		return viewWidth == -1 ? Integer.toString(overriddenWidth) : Integer.toString(viewWidth);
	}

	@Override
	public String processFont(String font) {
		if(StringUtils.isBlank(font)) {
			return "'" + DEFAULT_FONT + "'";
		}
		return "'" + font.toLowerCase() + "'";
	}

	@Override
	public String processShowLegend(boolean showLegend) {
		return Boolean.toString(showLegend);
	}
	
	@Override
	public String processShowNumberLabels() {
		if(isTablePlot()) {
			PlotTraceModel trace = windowModel.getCanvasModels().get(0).getTraceModels().get(0);
			if(trace.getPropertyAsBoolean(PlotToken.TRACE_DRAW_NUMBER_LABELS)) {
				return "zValues[i][j]";
			} else {
				return "''";
			}
		}
		return "";
	}
	
	/////////////
	// UTILITY //
	/////////////
	
	private boolean isTablePlot() {
		if(windowModel != null && windowModel.isSingleCanvas()) {
			PlotCanvasModel canvas = windowModel.getCanvasModels().get(0);
			if(canvas.isSingleTrace()) {
				PlotTraceModel trace = canvas.getTraceModels().get(0);
				if(trace.getPointType() == PlotType.HEAT_MAP_CATEGORICAL || trace.getPointType() == PlotType.HEAT_MAP_3D) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String processDivName() {
		StringBuilder sb = new StringBuilder();
		sb.append("'");
		sb.append(windowModel.getDivName());
		sb.append("'");
		return sb.toString();
	}

	@Override
	public String processBackgroundColor() {
		RGB rgb = windowModel.getBackgroundColor();
    	if(rgb != null) {
    	    return "'rgb(" + rgb.red + ", " + rgb.green + ", " + rgb.blue + ")'";
    	}
    	return "'rgb(255, 255, 255)'";
	}
}

