/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse;

public enum PlotType {
	
	/////////////////
	// General Use //
	/////////////////
	
	DEFAULT(""),
	LAYOUT("Layout"),
	OVERLAY("Layout for Overlays"),
	LAYOUT_3D("Layout for 3D Plot"),
	CATEGORICAL("Categorical"),
	CATEGORICAL_X("Categorical X Axis"),
	CATEGORICAL_Y("Categorical Y Axis"),
	
	/////////////////////
	// Axis Scale Type //
	/////////////////////
	
	SCALE_LINEAR("Linear Scale"),
	SCALE_LOG("Log Scale"),
	
	///////////////
	// 2D Traces //
	///////////////
	
	BOX_PLOT("Box Plot"),
	BAR_CHART("Bar Chart"),	
	BAR_CHART_CATEGORICAL("Bar Chart (Categorical Data)"),
	HEAT_MAP_CATEGORICAL("Heat Map (Categorical Axes)"),
	HISTOGRAM("Histogram"),
	PARALLEL_COORD_PLOT("Parallel Coordinates Plot"),
	PIE_CHART("Pie Chart"),
	SCATTER_PLOT("Scatter Plot"),
	SCATTER_PLOT_CATEGORICAL("Scatter Plot (Categorical Axes)"),
	
	///////////////
	// 3D Traces //
	///////////////
	
	CONTOUR_PLOT("Contour Plot"),
	HEAT_MAP_3D("Heat Map"),
	SCATTER_3D_PLOT("Scatter 3D Plot"),
	SURFACE_3D_PLOT("Surface 3D Plot");

	private String label;
	
	private PlotType(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
	public static PlotType getTypeByLabel(String match) {
		switch(match){
			case "Box Plot": 		                return BOX_PLOT;
			case "Bar Chart": 		                return BAR_CHART;
			case "Bar Chart (Categorical Data)":    return BAR_CHART_CATEGORICAL;
			case "Contour Plot":				    return CONTOUR_PLOT;
			case "Heat Map":		                return HEAT_MAP_3D;
			case "Heat Map (Categorical Axes)":     return HEAT_MAP_CATEGORICAL;
			case "Histogram":		                return HISTOGRAM;
			case "Parallel Coordinates Plot":       return PARALLEL_COORD_PLOT;
			case "Pie Chart":		                return PIE_CHART;
			case "Scatter Plot":	                return SCATTER_PLOT;
			case "Scatter Plot (Categorical Axes)": return SCATTER_PLOT_CATEGORICAL;
			case "Scatter 3D Plot":                 return SCATTER_3D_PLOT;
			case "Surface 3D Plot":	                return SURFACE_3D_PLOT;
			
			case "Linear Scale":                    return SCALE_LINEAR;
			case "Log Scale":                       return SCALE_LOG;
			default: 				                return DEFAULT;
		}
	}
}