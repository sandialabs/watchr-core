/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse;

public enum PlotToken {
	TRACE_ARR_DEPENDENT,
	TRACE_ARR_INDEPENDENT,
	TRACE_ARR_METADATA,
	TRACE_ARR_N_DIMENSIONAL_LOOP,
	TRACE_ARR_X,
	TRACE_ARR_Y,
	TRACE_ARR_Z,
	TRACE_AXIS_DEPENDENT_VAR,
	TRACE_AXIS_INDEPENDENT_VAR,
	TRACE_BOUND_LOWER,
	TRACE_BOUND_UPPER,
	TRACE_COLOR,
	TRACE_COLOR_AXIS,
	TRACE_CUMULATIVE,
	TRACE_DRAW_NUMBER_LABELS,
	TRACE_DRAW_LINES,
	TRACE_DRAW_LINEAR_REGRESSION_LINE,
	TRACE_DRAW_COLOR_SCALE,
	TRACE_ERROR_ARR_X_UPPER,
	TRACE_ERROR_ARR_X_LOWER,
	TRACE_ERROR_ARR_X_VISIBLE,
	TRACE_ERROR_ARR_Y_UPPER,
	TRACE_ERROR_ARR_Y_LOWER,
	TRACE_ERROR_ARR_Y_VISIBLE,
	TRACE_ERROR_ARR_Z_UPPER,
	TRACE_ERROR_ARR_Z_LOWER,
	TRACE_ERROR_ARR_Z_VISIBLE,
	TRACE_GRID_DATA,
	TRACE_HOVER_TEXT,
	TRACE_LABEL,
	TRACE_NAME,
	TRACE_OPACITY,
	TRACE_ORIENTATION,
	TRACE_PARENT_CANVAS_X,
	TRACE_PARENT_CANVAS_Y,
	TRACE_PRECISION,
	TRACE_POINT_MODE,
	TRACE_POINT_TYPE,
	TRACE_SCALE_COLOR,
	TRACE_SCALE_COLOR_VALUE_TYPE,
	TRACE_VARIABLE_RESPONSE_ORIENTATION,
	
	CANVAS_AXIS_DECIMAL_PRECISION,
	CANVAS_BASE_AXIS_Y,
	CANVAS_DRAW_X_AXIS_LABELS,
	CANVAS_DRAW_Y_AXIS_LABELS,
	CANVAS_DRAW_Z_AXIS_LABELS,
	CANVAS_DRAW_X_AXIS_LINES,
	CANVAS_DRAW_Y_AXIS_LINES,
	CANVAS_DRAW_Z_AXIS_LINES,
	CANVAS_DRAW_GRID_LINES,
	CANVAS_HOVER_DECIMAL_PRECISION,
	CANVAS_TRACES,	
	CANVAS_X_AUTOSCALE,
	CANVAS_X_AXIS_COLOR,
	CANVAS_X_AXIS_LABEL,
	CANVAS_X_DISPLAYED_RANGE_END,
	CANVAS_X_DISPLAYED_RANGE_START,
	CANVAS_X_DOMAIN_END,
	CANVAS_X_DOMAIN_START,
	CANVAS_X_INDEX,
	CANVAS_X_POSITION,
	CANVAS_X_TYPE,
	CANVAS_Y_AUTOSCALE,
	CANVAS_Y_AXIS_COLOR,
	CANVAS_Y_AXIS_LABEL,	
	CANVAS_Y_DISPLAYED_RANGE_END,
	CANVAS_Y_DISPLAYED_RANGE_START,
	CANVAS_Y_DOMAIN_END,
	CANVAS_Y_DOMAIN_START,
	CANVAS_Y_INDEX,
	CANVAS_Y_POSITION,
	CANVAS_Y_TYPE,
	CANVAS_Z_AXIS_LABEL,
	CANVAS_Z_AXIS_COLOR,
	CANVAS_Z_TYPE,
	
	WINDOW_BACKGROUND,
	WINDOW_CANVASES,
	WINDOW_CANVAS_LAYOUTS,
	WINDOW_DIV_NAME,
	WINDOW_FONT,
	WINDOW_LABEL,
	WINDOW_SHOW_LEGEND,
	WINDOW_SHOW_NUMBER_LABELS,
	WINDOW_TRACE_NAME_LIST,
	WINDOW_VIEW_HEIGHT,
	WINDOW_VIEW_WIDTH,
	
	SOURCE_HTML_FILE,
	TARGET_SCREENSHOT_FILE,
	TARGET_SCREENSHOT_EXTENSION;
}
