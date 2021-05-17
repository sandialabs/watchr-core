package gov.sandia.watchr.graph.chartreuse.generator.plotly;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.TestFileUtils;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.generator.PlotCanvasGenerator;
import gov.sandia.watchr.graph.chartreuse.generator.PlotWindowGenerator;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;

public class PlotlyWindowGeneratorTest {
    
    ////////////
    // FIELDS //
    ////////////
    
    private PlotCanvasGenerator canvasGenerator;
	private PlotWindowGenerator windowGenerator;
	private PlotWindowModel windowModel;
	
	////////////
	// BEFORE //
	////////////
	
	@Before
	public void setup() {
		try {
		    windowModel = new PlotWindowModel("");
			windowGenerator = new PlotlyWindowGenerator(canvasGenerator, false);
		} catch(Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	//////////
	// TEST //
	//////////
	
	@Test
	public void testGeneratePlotWindow() {
		String resultPlot = "";
		
		try {
			resultPlot = windowGenerator.generatePlotWindow(windowModel, PlotType.DEFAULT);
			TestFileUtils.compareStringToTestFile(PlotlyWindowGeneratorTest.class, resultPlot, "plots/generated_from_templates/plot_window_result.html");
		} catch(IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}
