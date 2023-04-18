/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.filter.DataFilter;
import gov.sandia.watchr.log.ILogger;

public class DataFilterConfig implements IConfig {

    ////////////
    // FIELDS //
    ////////////

    private final ILogger logger;
    private final String configPath;

    private final Set<DataFilter> filters;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public DataFilterConfig(String configPathPrefix, ILogger logger) {
        this.logger = logger;
        this.filters = new HashSet<>();
        this.configPath = configPathPrefix + "/filterConfig";
    }

    public DataFilterConfig(DataFilterConfig copy) {
        this.logger = copy.getLogger();
        this.filters = new HashSet<>();
        for(DataFilter filter : copy.getFilters()) {
            this.filters.add(new DataFilter(filter));
        }
        this.configPath = copy.getConfigPath();
    }

    /////////////
    // GETTERS //
    /////////////

    public Collection<DataFilter> getFilters() {
        return filters;
    }

    public boolean isBlank() {
        return filters.isEmpty();
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
        DataFilterConfig otherFilterConfig = (DataFilterConfig) other;
        List<WatchrDiff<?>> diffList = new ArrayList<>();

        if(!(filters.equals(otherFilterConfig.filters))) {
            WatchrDiff<Set<DataFilter>> diff = new WatchrDiff<>(configPath, DiffCategory.FILTER_POINTS);
            diff.setBeforeValue(filters);
            diff.setNowValue(otherFilterConfig.filters);
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
			DataFilterConfig otherFilterConfig = (DataFilterConfig) other;
            equals = filters.equals(otherFilterConfig.filters);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + filters.hashCode());
        return hash;
    }
}

