/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse.type;

/**
 * An enum that declares the type of a {@link Node} object.
 * This enum's values are common labels for different
 * types of data storage.
 * 
 * @author Elliott Ridgway
 *
 */
public enum NodeType {
	BLANK(""),                                                      //$NON-NLS-1$
	TIMESTEP("Time Step"),  //$NON-NLS-1$
	VARIABLE("Variable"),  //$NON-NLS-1$
	RESPONSE("Response"),  //$NON-NLS-1$
	GROUP("Group");        //$NON-NLS-1$
	
	private String label;
	private NodeType(String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return label;
	}
}

