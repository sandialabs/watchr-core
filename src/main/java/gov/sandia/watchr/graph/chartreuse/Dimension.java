/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse;

public enum Dimension {
	NONE(0, "None"),
	X(0, "X"),
	Y(1, "Y"),
	Z(2, "Z");
	
	private int dimensionIndex;
	private String label;
	
	private Dimension(int dimensionIndex, String label) {
		this.dimensionIndex = dimensionIndex;
		this.label = label;
	}
	
	public int getDimensionIndex() {
		return dimensionIndex;
	}
	
	public String getLabel() {
		return label;
	}
	
	public static Dimension getTypeByLabel(String match) {
		switch(match){
			case "X": 		return X;
			case "Y":		return Y;
			case "Z":		return Z;
			case "None":	return NONE;
			default: 		return null;
		}
	}
}
