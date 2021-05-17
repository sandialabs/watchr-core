package gov.sandia.watchr.graph.chartreuse.model;

import gov.sandia.watchr.graph.chartreuse.PlotToken;

public interface PlotTraceChangeListener {
    
    public void changed();

    public void propertyChanged(PlotToken property);
}
