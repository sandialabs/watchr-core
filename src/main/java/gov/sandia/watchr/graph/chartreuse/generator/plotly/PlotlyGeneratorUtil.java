/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse.generator.plotly;

import java.util.List;

import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;

public class PlotlyGeneratorUtil {
    
	public static int processParentCanvasGridPosition(PlotCanvasModel canvasModel, boolean countOverlay, int offset) {
		// Plotly has a very weird way of calculating canvas index.  Here's essentially how it works:
		//
		//  - If all of your canvases are nicely laid out, then you can use monotonically increasing
		//    values to represent your canvas axes.  So the first canvas has axes of "x1, y1", the second
		//    canvas has axes of "x2, y2" and so on.  These values don't have to necessarily correspond
		//    to grid position in any way, they just have to uniquely represent a canvas.  So far so good...
		//
		//  - However, if you have overlaid canvases, the y value for canvas axis needs to continue increasing
		//    for each overlaid canvas.  So if "x1" and "y1" are the axis labels for your base canvas, and you
		//    have two overlaid canvases, those two overlaid canvases will have "y2" and "y3" axes, but
		//    they'll share the "x1" axis with the base canvas.  THEN, after you have finished counting
		//    overlaid canvases and are ready for the next base canvas, the next set of x and y values needs
		//    to jump ahead, taking into account both base and canvas counts so far.  So in the previous example,
		//    the highest overlaid axis value we had was "y3", so the next pair of axes need to be one up from
		//    that, i.e. "x4, y4".  "x2" and "x3" are dead to us, because of overlapping canvases.
		//
		// The following method figures out the correct count for axis labels based on the above description.
		
		PlotWindowModel windowModel = canvasModel.getParent();	
		List<List<PlotCanvasModel>> canvasTable = windowModel.getChildCanvasesAsTable();
		
		int baseCount = offset;
		int overlayCount = 0;
		boolean foundCanvas = false;
		
		for(List<PlotCanvasModel> canvasRow : canvasTable) {
			if(foundCanvas) {
				break;
			}
			for(PlotCanvasModel canvas : canvasRow) {
				if(foundCanvas) {
					break;
				}				
				
				if(canvas.equals(canvasModel)) {
					foundCanvas = true;
					break;
				}
				
				if(!canvas.getOverlaidCanvasModels().isEmpty()) {
					// Count overlaid canvases separately;
					for(PlotCanvasModel overlayCanvas : canvas.getOverlaidCanvasModels()) {
						overlayCount ++;
						if(overlayCanvas.equals(canvasModel)) {
							foundCanvas = true;
							break;
						}
					}
					if(!foundCanvas) {
						// Roll up overlay count so far and reset overlayCount.
						baseCount = baseCount + overlayCount;
						overlayCount = 0;
					}
				}
				baseCount ++;
			}			
		}
		
		if(countOverlay) {
			return baseCount + overlayCount;
		} else {
			return baseCount;
		}
	}
}

