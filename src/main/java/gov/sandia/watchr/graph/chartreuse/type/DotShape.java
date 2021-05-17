/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse.type;

public class DotShape {
	private String description;
	private String implString;
	
	public DotShape(String description, String implString) {
		this.description = description;
		this.implString = implString;
	}
	
	public String getImplementedString() {
		return implString;
	}
	
	public String getDescription() {
		return description;
	}
}
