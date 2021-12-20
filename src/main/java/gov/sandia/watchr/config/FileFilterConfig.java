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
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.util.StringUtil;

public class FileFilterConfig implements IConfig {

    ////////////
    // FIELDS //
    ////////////

    private String namePattern = "";
    private final String configPath;
    private final ILogger logger;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FileFilterConfig(String configPathPrefix, ILogger logger) {
        this.configPath = configPathPrefix + "/fileFilterConfig";
        this.logger = logger;
    }

    public FileFilterConfig(FileFilterConfig copy) {
        this.configPath = copy.getConfigPath();
        this.namePattern = copy.getNamePattern();
        this.logger = copy.getLogger();
    }

    /////////////
    // GETTERS //
    /////////////

    public String getNamePattern() {
        return namePattern;
    }

    public String getNamePatternAsRegex() {
        return StringUtil.convertToRegex(namePattern);
    }

    @Override
    public String getConfigPath() {
        return configPath;
    }

    @Override
    public ILogger getLogger() {
        return logger;
    }  

    /////////////
    // SETTERS //
    /////////////

    public void setNamePattern(String namePattern) {
        this.namePattern = namePattern;
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
        if(other instanceof FileFilterConfig) {
            FileFilterConfig otherFileConfig = (FileFilterConfig) other;

            if(!(namePattern.equals(otherFileConfig.namePattern))) {
                WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.FILENAME_PATTERN);
                diff.setBeforeValue(namePattern);
                diff.setNowValue(otherFileConfig.namePattern);
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
			FileFilterConfig otherFileConfig = (FileFilterConfig) other;
            equals = namePattern.equals(otherFileConfig.namePattern);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + namePattern.hashCode());
        return hash;
    }
}
