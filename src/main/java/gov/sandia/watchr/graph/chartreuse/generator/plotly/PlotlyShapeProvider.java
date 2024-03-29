/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.chartreuse.generator.plotly;

import gov.sandia.watchr.graph.chartreuse.type.DotShape;
import gov.sandia.watchr.graph.chartreuse.type.DotShapeProvider;

public class PlotlyShapeProvider extends DotShapeProvider {
	// These are all the dot shapes that Plotly can render according to their online documentation.
	// We have opted to comment out certain shapes because either they are hard to see after rendering,
	// or Plotly isn't rendering them for some reason.
	
	private static final DotShape CIRCLE          				= new DotShape("Circle", "circle");
	private static final DotShape CIRCLE_OPEN     				= new DotShape("Circle Open", "circle-open");
	private static final DotShape CIRCLE_DOT      				= new DotShape("Circle Dot", "circle-dot");
	private static final DotShape CIRCLE_OPEN_DOT 				= new DotShape("Circle Open Dot", "circle-open-dot");
	private static final DotShape SQUARE          				= new DotShape("Square", "square");
	private static final DotShape SQUARE_OPEN     				= new DotShape("Square Open", "square-open");
	private static final DotShape SQUARE_DOT      				= new DotShape("Square Dot", "square-dot");
	private static final DotShape SQUARE_OPEN_DOT				= new DotShape("Square Open Dot", "square-open-dot");
	private static final DotShape DIAMOND         				= new DotShape("Diamond", "diamond");
	private static final DotShape DIAMOND_OPEN    				= new DotShape("Diamond Open", "diamond-open");
	private static final DotShape DIAMOND_DOT     				= new DotShape("Diamond Dot", "diamond-dot");
	private static final DotShape DIAMOND_OPEN_DOT				= new DotShape("Diamond Open Dot", "diamond-open-dot");
	private static final DotShape CROSS           				= new DotShape("Cross", "cross");
	private static final DotShape CROSS_OPEN      				= new DotShape("Cross Open", "cross-open");
	private static final DotShape CROSS_DOT       				= new DotShape("Cross Dot", "cross-dot");
	private static final DotShape CROSS_OPEN_DOT  				= new DotShape("Cross Open Dot", "cross-open-dot");
	private static final DotShape X_OPEN          				= new DotShape("X Open", "x-open" );
	private static final DotShape X_DOT           				= new DotShape("X Dot", "x-dot");
	private static final DotShape X_OPEN_DOT      				= new DotShape("X Open Dot", "x-open-dot");
	private static final DotShape TRIANGLE_UP     				= new DotShape("Triangle Up", "triangle-up");
	private static final DotShape TRIANGLE_OPEN   				= new DotShape("Triangle Up Open", "triangle-up-open");
	private static final DotShape TRIANGLE_UP_DOT 				= new DotShape("Triangle Up Dot", "triangle-up-dot");
	private static final DotShape TRIANGLE_UP_OPEN_DOT 		    = new DotShape("Triangle Up Open Dot", "triangle-up-open-dot");
	private static final DotShape TRIANGLE_DOWN 				= new DotShape("Triangle Down", "triangle-down");
	private static final DotShape TRIANGLE_DOWN_OPEN 			= new DotShape("Triangle Down Open", "triangle-down-open");
	private static final DotShape TRIANGLE_DOWN_DOT 			= new DotShape("Triangle Down Dot", "triangle-down-dot");
	private static final DotShape TRIANGLE_DOWN_OPEN_DOT 		= new DotShape("Triangle Down Open Dot", "triangle-down-open-dot");
	private static final DotShape TRIANGLE_LEFT 				= new DotShape("Triangle Left", "triangle-left");
	private static final DotShape TRIANGLE_LEFT_OPEN 			= new DotShape("Triangle Left Open", "triangle-left-open");
	private static final DotShape TRIANGLE_LEFT_DOT 			= new DotShape("Triangle Left Dot", "triangle-left-dot");
	private static final DotShape TRIANGLE_LEFT_OPEN_DOT		= new DotShape("Triangle Left Open Dot", "triangle-left-open-dot");
	private static final DotShape TRIANGLE_RIGHT 				= new DotShape("Triangle Right", "triangle-right");
	private static final DotShape TRIANGLE_RIGHT_OPEN 			= new DotShape("Triangle Right Open", "triangle-right-open");
	private static final DotShape TRIANGLE_RIGHT_DOT 			= new DotShape("Triangle Right Dot", "triangle-right-dot");
	private static final DotShape TRIANGLE_RIGHT_OPEN_DOT 		= new DotShape("Triangle Right Open Dot", "triangle-right-open-dot");
	private static final DotShape TRIANGLE_NE 					= new DotShape("Triangle Northeast", "triangle-ne");
	private static final DotShape TRIANGLE_NE_OPEN 			    = new DotShape("Triangle Northeast Open", "triangle-ne-open");
	private static final DotShape TRIANGLE_NE_DOT 				= new DotShape("Triangle Northeast Dot", "triangle-ne-dot");
	private static final DotShape TRIANGLE_NE_OPEN_DOT 		    = new DotShape("Triangle Northeast Open Dot", "triangle-ne-open-dot");
	private static final DotShape TRIANGLE_SE 					= new DotShape("Triangle Southeast", "triangle-se");
	private static final DotShape TRIANGLE_SE_OPEN 			    = new DotShape("Triangle Southeast Open", "triangle-se-open");
	private static final DotShape TRIANGLE_SE_DOT 				= new DotShape("Triangle Southeast Dot", "triangle-se-dot");
	private static final DotShape TRIANGLE_SE_OPEN_DOT 		    = new DotShape("Triangle Southeast Open Dot", "triangle-se-open-dot");
	private static final DotShape TRIANGLE_SW 					= new DotShape("Triangle Southwest", "triangle-sw");
	private static final DotShape TRIANGLE_SW_OPEN 			    = new DotShape("Triangle Southwest Open", "triangle-sw-open");
	private static final DotShape TRIANGLE_SW_DOT 				= new DotShape("Triangle Southwest Dot", "triangle-sw-dot");
	private static final DotShape TRIANGLE_SW_OPEN_DOT 		    = new DotShape("Triangle Southwest Open Dot", "triangle-sw-open-dot");
	private static final DotShape TRIANGLE_NW 					= new DotShape("Triangle Northwest", "triangle-nw");
	private static final DotShape TRIANGLE_NW_OPEN 			    = new DotShape("Triangle Northwest Open", "triangle-nw-open");
	private static final DotShape TRIANGLE_NW_DOT 				= new DotShape("Triangle Northwest Dot", "triangle-nw-dot");
	private static final DotShape TRIANGLE_NW_OPEN_DOT 		    = new DotShape("Triangle Northwest Open Dot", "triangle-nw-open-dot");
	private static final DotShape PENTAGON 					    = new DotShape("Pentagon", "pentagon");
	private static final DotShape PENTAGON_OPEN 				= new DotShape("Pentagon Open", "pentagon-open");
	private static final DotShape PENTAGON_DOT 				    = new DotShape("Pentagon Dot", "pentagon-dot");
	private static final DotShape PENTAGON_OPEN_DOT 			= new DotShape("Pentagon Open Dot", "pentagon-open-dot");
	private static final DotShape HEXAGON 						= new DotShape("Hexagon", "hexagon");
	private static final DotShape HEXAGON_OPEN 				    = new DotShape("Hexagon Open", "hexagon-open");
	private static final DotShape HEXAGON_DOT 					= new DotShape("Hexagon Dot", "hexagon-dot");
	private static final DotShape HEXAGON_OPEN_DOT 			    = new DotShape("Hexagon Open Dot", "hexagon-open-dot");
	private static final DotShape OCTAGON 						= new DotShape("Octagon", "octagon");
	private static final DotShape OCTAGON_OPEN 				    = new DotShape("Octagon Open", "octagon-open");
	private static final DotShape OCTAGON_DOT 					= new DotShape("Octagon Dot", "octagon-dot");
	private static final DotShape OCTAGON_OPEN_DOT 			    = new DotShape("Octagon Open Dot", "octagon-open-dot");
	private static final DotShape STAR 						    = new DotShape("Star", "star");
	private static final DotShape STAR_OPEN 					= new DotShape("Star Open", "star-open");
	private static final DotShape STAR_DOT 					    = new DotShape("Star Dot", "star-dot");
	private static final DotShape STAR_OPEN_DOT 				= new DotShape("Star Open Dot", "star-open-dot");
	private static final DotShape HEXAGRAM 					    = new DotShape("Hexagram", "hexagram");
	private static final DotShape HEXAGRAM_OPEN 				= new DotShape("Hexagram Open", "hexagram-open");
	private static final DotShape HEXAGRAM_DOT 				    = new DotShape("Hexagram Dot", "hexagram-dot");
	private static final DotShape HEXAGRAM_OPEN_DOT 			= new DotShape("Hexagram Open Dot", "hexagram-open-dot");
	private static final DotShape STAR_TRIANGLE_UP 			    = new DotShape("Star Triangle Up", "star-triangle-up");
	private static final DotShape STAR_TRIANGLE_UP_OPEN 		= new DotShape("Star Triangle Up Open", "star-triangle-up-open");
	private static final DotShape STAR_TRIANGLE_UP_DOT 		    = new DotShape("Star Triangle Up Dot", "star-triangle-up-dot");
	private static final DotShape STAR_TRIANGLE_UP_OPEN_DOT 	= new DotShape("Star Triangle Up Open Dot", "star-triangle-up-open-dot");
	private static final DotShape STAR_TRIANGLE_DOWN 			= new DotShape("Star Triangle Down", "star-triangle-down");
	private static final DotShape STAR_TRIANGLE_DOWN_OPEN 		= new DotShape("Star Triangle Down Open", "star-triangle-down-open");
	private static final DotShape STAR_TRIANGLE_DOWN_DOT 		= new DotShape("Star Triangle Down Dot", "star-triangle-down-dot");
	private static final DotShape STAR_TRIANGLE_DOWN_OPEN_DOT 	= new DotShape("Star Triangle Down Open Dot", "star-triangle-down-open-dot");
	private static final DotShape STAR_SQUARE					= new DotShape("Star Square", "star-square");
	private static final DotShape STAR_SQUARE_OPEN 			    = new DotShape("Star Square Open", "star-square-open");
	private static final DotShape STAR_SQUARE_DOT 				= new DotShape("Star Square Dot", "star-square-dot");
	private static final DotShape STAR_SQUARE_OPEN_DOT 		    = new DotShape("Star Square Open Dot", "star-square-open-dot");
	private static final DotShape STAR_DIAMOND 				    = new DotShape("Star Diamond", "star-diamond");
	private static final DotShape STAR_DIAMOND_OPEN 			= new DotShape("Star Diamond Open", "star-diamond-open");
	private static final DotShape STAR_DIAMOND_DOT 			    = new DotShape("Star Diamond Dot", "star-diamond-dot");
	private static final DotShape STAR_DIAMOND_OPEN_DOT 		= new DotShape("Star Diamond Open Dot", "star-diamond-open-dot");
	private static final DotShape DIAMOND_TALL 				    = new DotShape("Diamond Tall", "diamond-tall");
	private static final DotShape DIAMOND_TALL_OPEN 			= new DotShape("Diamond Tall Open", "diamond-tall-open");
	private static final DotShape DIAMOND_TALL_DOT 			    = new DotShape("Diamond Tall Dot", "diamond-tall-dot");
	private static final DotShape DIAMOND_TALL_OPEN_DOT 		= new DotShape("Diamond Tall Open Dot", "diamond-tall-open-dot");
	private static final DotShape DIAMOND_WIDE 				    = new DotShape("Diamond Wide", "diamond-wide");
	private static final DotShape DIAMOND_WIDE_OPEN 			= new DotShape("Diamond Wide Open", "diamond-wide-open");
	private static final DotShape DIAMOND_WIDE_DOT 			    = new DotShape("Diamond Wide Dot", "diamond-wide-dot");
	private static final DotShape DIAMOND_WIDE_OPEN_DOT 		= new DotShape("Diamond Wide Open Dot", "diamond-wide-open-dot");
	private static final DotShape HOURGLASS 					= new DotShape("Hourglass", "hourglass");
	private static final DotShape HOURGLASS_OPEN 				= new DotShape("Hourglass Open", "hourglass-open");
	private static final DotShape BOWTIE 						= new DotShape("Bowtie", "bowtie");
	private static final DotShape BOWTIE_OPEN 					= new DotShape("Bowtie Open", "bowtie-open");
	private static final DotShape CIRCLE_CROSS 				    = new DotShape("Circle Cross", "circle-cross");
	private static final DotShape CIRCLE_CROSS_OPEN 			= new DotShape("Circle Cross Open", "circle-cross-open");
	private static final DotShape CIRCLE_X 					    = new DotShape("Circle X", "circle-x");
	private static final DotShape CIRCLE_X_OPEN 				= new DotShape("Circle X Open", "circle-x-open");
	private static final DotShape SQUARE_CROSS 				    = new DotShape("Square Cross", "square-cross");
	private static final DotShape SQUARE_CROSS_OPEN 			= new DotShape("Square Cross Open", "square-cross-open");
	private static final DotShape SQUARE_X 					    = new DotShape("Square X", "square-x");
	private static final DotShape SQUARE_X_OPEN 				= new DotShape("Square X Open", "square-x-open");
	private static final DotShape DIAMOND_CROSS 				= new DotShape("Diamond Cross", "diamond-cross");
	private static final DotShape DIAMOND_CROSS_OPEN 			= new DotShape("Diamond Cross Open", "diamond-cross-open");
	private static final DotShape DIAMOND_X 					= new DotShape("Diamond X", "diamond-x");
	private static final DotShape DIAMOND_X_OPEN 				= new DotShape("Diamond X Open", "diamond-x-open");
	
	public PlotlyShapeProvider() {
        allShapes.add(CIRCLE);
        allShapes.add(CIRCLE_OPEN);
        allShapes.add(CIRCLE_DOT);
        allShapes.add(CIRCLE_OPEN_DOT);
        allShapes.add(SQUARE);
        allShapes.add(SQUARE_OPEN);
        allShapes.add(SQUARE_DOT);
        allShapes.add(SQUARE_OPEN_DOT);
        allShapes.add(DIAMOND);
        allShapes.add(DIAMOND_OPEN);
        allShapes.add(DIAMOND_DOT);
        allShapes.add(DIAMOND_OPEN_DOT);
        allShapes.add(CROSS);
        allShapes.add(CROSS_OPEN);
        allShapes.add(CROSS_DOT);
        allShapes.add(CROSS_OPEN_DOT);
        allShapes.add(X_OPEN);
        allShapes.add(X_DOT);
        allShapes.add(X_OPEN_DOT);
        allShapes.add(TRIANGLE_UP);
        allShapes.add(TRIANGLE_OPEN);
        allShapes.add(TRIANGLE_UP_DOT);
        allShapes.add(TRIANGLE_UP_OPEN_DOT);
        allShapes.add(TRIANGLE_DOWN);
        allShapes.add(TRIANGLE_DOWN_OPEN);
        allShapes.add(TRIANGLE_DOWN_DOT);
        allShapes.add(TRIANGLE_DOWN_OPEN_DOT);
        allShapes.add(TRIANGLE_LEFT);
        allShapes.add(TRIANGLE_LEFT_OPEN);
        allShapes.add(TRIANGLE_LEFT_DOT);
        allShapes.add(TRIANGLE_LEFT_OPEN_DOT);
        allShapes.add(TRIANGLE_RIGHT);
        allShapes.add(TRIANGLE_RIGHT_OPEN);
        allShapes.add(TRIANGLE_RIGHT_DOT);
        allShapes.add(TRIANGLE_RIGHT_OPEN_DOT);
        allShapes.add(TRIANGLE_NE);
        allShapes.add(TRIANGLE_NE_OPEN);
        allShapes.add(TRIANGLE_NE_DOT);
        allShapes.add(TRIANGLE_NE_OPEN_DOT);
        allShapes.add(TRIANGLE_SE);
        allShapes.add(TRIANGLE_SE_OPEN);
        allShapes.add(TRIANGLE_SE_DOT);
        allShapes.add(TRIANGLE_SE_OPEN_DOT);
        allShapes.add(TRIANGLE_SW);
        allShapes.add(TRIANGLE_SW_OPEN);
        allShapes.add(TRIANGLE_SW_DOT);
        allShapes.add(TRIANGLE_SW_OPEN_DOT);
        allShapes.add(TRIANGLE_NW);
        allShapes.add(TRIANGLE_NW_OPEN);
        allShapes.add(TRIANGLE_NW_DOT);
        allShapes.add(TRIANGLE_NW_OPEN_DOT);
        allShapes.add(PENTAGON);
        allShapes.add(PENTAGON_OPEN);
        allShapes.add(PENTAGON_DOT);
        allShapes.add(PENTAGON_OPEN_DOT);
        allShapes.add(HEXAGON);
        allShapes.add(HEXAGON_OPEN);
        allShapes.add(HEXAGON_DOT);
        allShapes.add(HEXAGON_OPEN_DOT);
        allShapes.add(OCTAGON);
        allShapes.add(OCTAGON_OPEN);
        allShapes.add(OCTAGON_DOT);
        allShapes.add(OCTAGON_OPEN_DOT);
        allShapes.add(STAR);
        allShapes.add(STAR_OPEN);
        allShapes.add(STAR_DOT);
        allShapes.add(STAR_OPEN_DOT);
        allShapes.add(HEXAGRAM);
        allShapes.add(HEXAGRAM_OPEN);
        allShapes.add(HEXAGRAM_DOT);
        allShapes.add(HEXAGRAM_OPEN_DOT);
        allShapes.add(STAR_TRIANGLE_UP);
        allShapes.add(STAR_TRIANGLE_UP_OPEN);
        allShapes.add(STAR_TRIANGLE_UP_DOT);
        allShapes.add(STAR_TRIANGLE_UP_OPEN_DOT);
        allShapes.add(STAR_TRIANGLE_DOWN);
        allShapes.add(STAR_TRIANGLE_DOWN_OPEN);
        allShapes.add(STAR_TRIANGLE_DOWN_DOT);
        allShapes.add(STAR_TRIANGLE_DOWN_OPEN_DOT);
        allShapes.add(STAR_SQUARE);
        allShapes.add(STAR_SQUARE_OPEN);
        allShapes.add(STAR_SQUARE_DOT);
        allShapes.add(STAR_SQUARE_OPEN_DOT);
        allShapes.add(STAR_DIAMOND);
        allShapes.add(STAR_DIAMOND_OPEN);
        allShapes.add(STAR_DIAMOND_DOT);
        allShapes.add(STAR_DIAMOND_OPEN_DOT);
        allShapes.add(DIAMOND_TALL);
        allShapes.add(DIAMOND_TALL_OPEN);
        allShapes.add(DIAMOND_TALL_DOT);
        allShapes.add(DIAMOND_TALL_OPEN_DOT);
        allShapes.add(DIAMOND_WIDE);
        allShapes.add(DIAMOND_WIDE_OPEN);
        allShapes.add(DIAMOND_WIDE_DOT);
        allShapes.add(DIAMOND_WIDE_OPEN_DOT);
        allShapes.add(HOURGLASS);
        allShapes.add(HOURGLASS_OPEN);
        allShapes.add(BOWTIE);
        allShapes.add(BOWTIE_OPEN);
        allShapes.add(CIRCLE_CROSS);
        allShapes.add(CIRCLE_CROSS_OPEN);
        allShapes.add(CIRCLE_X);
        allShapes.add(CIRCLE_X_OPEN);
        allShapes.add(SQUARE_CROSS);
        allShapes.add(SQUARE_CROSS_OPEN);
        allShapes.add(SQUARE_X);
        allShapes.add(SQUARE_X_OPEN);
        allShapes.add(DIAMOND_CROSS);
        allShapes.add(DIAMOND_CROSS_OPEN);
        allShapes.add(DIAMOND_X);
        allShapes.add(DIAMOND_X_OPEN);
	}
}
