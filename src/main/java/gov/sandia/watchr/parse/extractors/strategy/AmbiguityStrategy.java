/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.extractors.strategy;

import java.util.ArrayList;
import java.util.List;

import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;

public class AmbiguityStrategy {
    
    ////////////
    // FIELDS //
    ////////////

    private boolean getFirstMatchOnly = true;
    private boolean recurseToChildGraphs = false;
    private String iterateWithOtherExtractor = "";

    private final String configPath;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public AmbiguityStrategy(String configPathPrefix) {
        this.configPath = configPathPrefix + "/strategy";
    }

    public AmbiguityStrategy(AmbiguityStrategy copy) {
        this.getFirstMatchOnly = copy.shouldGetFirstMatchOnly();
        this.recurseToChildGraphs = copy.shouldRecurseToChildGraphs();
        this.iterateWithOtherExtractor = copy.getIterateWithOtherExtractor();
        this.configPath = copy.getConfigPath();
    }

    /////////////
    // GETTERS //
    /////////////

    public boolean shouldGetFirstMatchOnly() {
        return getFirstMatchOnly;
    }

    public boolean shouldRecurseToChildGraphs() {
        return recurseToChildGraphs;
    }

    public String getIterateWithOtherExtractor() {
        return iterateWithOtherExtractor;
    }

    public String getConfigPath() {
        return configPath;
    }

    /////////////
    // SETTERS //
    /////////////

    public void setShouldGetFirstMatchOnly(boolean getFirstMatchOnly) {
        this.getFirstMatchOnly = getFirstMatchOnly;
    }

    public void setShouldRecurseToChildGraphs(boolean recurseToChildGraphs) {
        this.recurseToChildGraphs = recurseToChildGraphs;
    }

    public void setIterateWithOtherExtractor(String iterateWithOtherExtractor) {
        this.iterateWithOtherExtractor = iterateWithOtherExtractor;
    }

    //////////////
    // OVERRIDE //
    //////////////

    public List<WatchrDiff<?>> getDiffs(AmbiguityStrategy other) {
        List<WatchrDiff<?>> diffList = new ArrayList<>();

        if(getFirstMatchOnly != other.getFirstMatchOnly) {
            WatchrDiff<Boolean> diff = new WatchrDiff<>(configPath, DiffCategory.GET_FIRST_MATCH_ONLY);
            diff.setBeforeValue(getFirstMatchOnly);
            diff.setNowValue(other.getFirstMatchOnly);
        }
        if(recurseToChildGraphs != other.recurseToChildGraphs) {
            WatchrDiff<Boolean> diff = new WatchrDiff<>(configPath, DiffCategory.RECURSE_TO_CHILD_GRAPHS);
            diff.setBeforeValue(recurseToChildGraphs);
            diff.setNowValue(other.recurseToChildGraphs);
        }
        if(!iterateWithOtherExtractor.equals(other.iterateWithOtherExtractor)) {
            WatchrDiff<Boolean> diff = new WatchrDiff<>(configPath, DiffCategory.ITERATE_WITH);
            diff.setBeforeValue(recurseToChildGraphs);
            diff.setNowValue(other.recurseToChildGraphs);
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
			AmbiguityStrategy otherStrategy = (AmbiguityStrategy) other;
            equals = getFirstMatchOnly == otherStrategy.getFirstMatchOnly;
            equals = equals && recurseToChildGraphs == otherStrategy.recurseToChildGraphs;
            equals = equals && iterateWithOtherExtractor.equals(otherStrategy.iterateWithOtherExtractor);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + Boolean.hashCode(getFirstMatchOnly));
        hash = 31 * (hash + Boolean.hashCode(recurseToChildGraphs));
        hash = 31 * (hash + iterateWithOtherExtractor.hashCode());
        return hash;
    }      
}
