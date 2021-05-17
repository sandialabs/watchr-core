/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;

public class CategoryConfiguration implements IConfig {
    
    ////////////
    // FIELDS //
    ////////////

    private Set<String> categories;
    private final String configPath;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public CategoryConfiguration(String configPathPrefix) {
        this.categories = new LinkedHashSet<>();
        this.configPath = configPathPrefix + "/categories";
    }

    public CategoryConfiguration(CategoryConfiguration copy) {
        this.categories = new LinkedHashSet<>();
        this.categories.addAll(copy.getCategories());
        this.configPath = copy.getConfigPath();
    }

    /////////////
    // GETTERS //
    /////////////

    public Set<String> getCategories() {
        return categories;
    }

    @Override
    public String getConfigPath() {
        return configPath;
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
        List<WatchrDiff<?>> diffList = new ArrayList<>();
        if(other instanceof CategoryConfiguration) {
            CategoryConfiguration categoryConfiguration = (CategoryConfiguration) other;

            if(!(categories.equals(categoryConfiguration.categories))) {            
                WatchrDiff<Set<String>> diff = new WatchrDiff<>(configPath, DiffCategory.CATEGORIES);
                diff.setBeforeValue(getCategories());
                diff.setNowValue(categoryConfiguration.getCategories());
                diffList.add(diff);
            }
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
			CategoryConfiguration categoryConfiguration = (CategoryConfiguration) other;
            equals = getCategories().equals(categoryConfiguration.getCategories());
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + categories.hashCode());
        return hash;
    }
}
