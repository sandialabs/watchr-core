package gov.sandia.watchr.graph.chartreuse.model;

import java.util.Comparator;

import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.GraphDisplayConfig.GraphDisplaySort;

public class PlotWindowModelComparator implements Comparator<PlotWindowModel> {

    private final GraphDisplayConfig config;

    public PlotWindowModelComparator(GraphDisplayConfig config) {
        this.config = config;
    }

    @Override
    public int compare(PlotWindowModel p1, PlotWindowModel p2) {
        if(config.getSort() != null && config.getSort() == GraphDisplaySort.DESCENDING) {
            return p2.getName().compareTo(p1.getName());
        }
        return p1.getName().compareTo(p2.getName());
    }
}
