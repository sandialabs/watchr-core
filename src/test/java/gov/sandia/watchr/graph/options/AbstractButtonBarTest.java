package gov.sandia.watchr.graph.options;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.graph.library.IHtmlButtonRenderer;
import gov.sandia.watchr.graph.library.impl.PlotlyButtonRenderer;

public class AbstractButtonBarTest {
    
    private AbstractButtonBar buttonBar;

    @Before
    public void setup() {
        IDatabase db = null;
        IHtmlButtonRenderer buttonRenderer = new PlotlyButtonRenderer(db);
        buttonBar = new AbstractButtonBar(buttonRenderer) {
            @Override
            public String getHtmlForButton(PlotWindowModel currentPlot, ButtonType type) {
                return null;
            }
        };
    }

    @Test
    public void testEscapePlotName() {
        try {
            PlotWindowModel plot = new PlotWindowModel("/Perf2021-06-16T14:30:26.xml/perf_suite/BLAS");
            String escapedName = buttonBar.escapePlotName(plot);
            assertEquals("%2FPerf2021-06-16T14%3A30%3A26.xml%2Fperf_suite%2FBLAS", escapedName);
        } catch(UnsupportedEncodingException e) {
            fail(e.getMessage());
        }
    }
}
