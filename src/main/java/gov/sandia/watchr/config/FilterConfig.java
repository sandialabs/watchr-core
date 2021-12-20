/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config;

import java.util.ArrayList;
import java.util.List;

import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.log.ILogger;

public class FilterConfig implements IConfig {

    ////////////
    // FIELDS //
    ////////////

    private final ILogger logger;
    private final List<PlotTracePoint> filterPoints;
    private final String configPath;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FilterConfig(String configPathPrefix, ILogger logger) {
        this.logger = logger;
        this.filterPoints = new ArrayList<>();
        this.configPath = configPathPrefix + "/filterConfig";
    }

    public FilterConfig(FilterConfig copy) {
        this.logger = copy.getLogger();
        this.filterPoints = new ArrayList<>();
        for(PlotTracePoint point : copy.getFilterPoints()) {
            this.filterPoints.add(new PlotTracePoint(point));
        }
        this.configPath = copy.getConfigPath();
    }

    /////////////
    // GETTERS //
    /////////////

    public List<PlotTracePoint> getFilterPoints() {
        return filterPoints;
    }

    public boolean isBlank() {
        return filterPoints.isEmpty();
    }

    @Override
    public String getConfigPath() {
        return configPath;
    } 

    @Override
    public ILogger getLogger() {
        return logger;
    } 

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void validate() {
        // Do nothing
    }

    @Override
    public List<WatchrDiff<?>> diff(IConfig other) {
        FilterConfig otherFilterConfig = (FilterConfig) other;
        List<WatchrDiff<?>> diffList = new ArrayList<>();

        if(!(filterPoints.equals(otherFilterConfig.filterPoints))) {
            WatchrDiff<List<PlotTracePoint>> diff = new WatchrDiff<>(configPath, DiffCategory.FILTER_POINTS);
            diff.setBeforeValue(filterPoints);
            diff.setNowValue(otherFilterConfig.filterPoints);
            diffList.add(diff);
        }
        return diffList;
    }
    
    @Override
    public boolean equals(Object other) {
        boolean equals = false;
		if(other == null) {
            return false;
        } else if(other == this) {
            return true;
        } else if(getClass() != other.getClass()) {
            return false;
        } else {
			FilterConfig otherFilterConfig = (FilterConfig) other;
            equals = filterPoints.equals(otherFilterConfig.filterPoints);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + filterPoints.hashCode());
        return hash;
    }
}
