/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse.type;

import java.util.ArrayList;
import java.util.List;

public abstract class DotShapeProvider {
	protected List<DotShape> allShapes = new ArrayList<>();
	
	public List<DotShape> getAllShapes() {
		return allShapes;
	}

	public List<String> getAllShapeStrings() {
		List<String> allShapeStrings = new ArrayList<>();
		for(DotShape shape : allShapes) {
			allShapeStrings.add(shape.getImplementedString());
		}
		return allShapeStrings;
	}

	public List<String> getAllShapeDescriptions() {
		List<String> allShapeDescriptions = new ArrayList<>();
		for(DotShape shape : allShapes) {
			allShapeDescriptions.add(shape.getDescription());
		}
		return allShapeDescriptions;
	}

	public DotShape getShapeByDescription(String match) {
		for(DotShape shape : allShapes) {
			if(shape.getDescription().equals(match)) {
				return shape;
			}
		}
		return null;
	}

	public DotShape getShapeByImplString(String match) {
		for(DotShape shape : allShapes) {
			if(shape.getImplementedString().equals(match)) {
				return shape;
			}
		}
		return null;
	}
}

