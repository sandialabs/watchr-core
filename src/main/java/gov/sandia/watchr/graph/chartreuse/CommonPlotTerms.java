/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse;

public enum CommonPlotTerms {
	ALL_VAR_AND_RESP("All variables and responses"),
	ALL_VARIABLES("All variables"),
	ALL_RESPONSES("All responses"),
	VARIABLE("variable"),
	RESPONSE("response"),
	
	HISTOGRAM_TYPE_COUNT("Count"),
	HISTOGRAM_TYPE_PERCENT("Percent"),
	HISTOGRAM_TYPE_PROBABILITY("Probability"),
	HISTOGRAM_TYPE_DENSITY("Density"),
	HISTOGRAM_TYPE_PROBABILITY_DENSITY("Probability Density"),
	
	SCALE_CONTINUOUS("Continuous"),
	SCALE_DISCRETE("Discrete"),
	
	VALUE_RELATIVE_PERCENTAGES("Relative Percentages"),
	VALUE_DATA_VALUES("Data Values"),
	
	ORIENTATION_HORIZONTAL("Horizontal"),
	ORIENTATION_VERTICAL("Vertical"),
	ORIENTATION_RESP_ALONG_HORIZONTAL("Response-Variable"),
	ORIENTATION_RESP_ALONG_VERTICAL("Variable-Response"),
	ORIENTATION_MIRROR_RESPONSES("Response-Response"),
	ORIENTATION_MIRROR_VARIABLES("Variable-Variable"),
	ORIENTATION_MIRROR_ALL("All-All"),
	
	DEFAULT("Default");
	
	private String label;	
	private CommonPlotTerms(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
}
