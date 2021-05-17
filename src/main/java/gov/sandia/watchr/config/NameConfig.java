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
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.log.ILogger;

public class NameConfig implements IConfig {

    ////////////
    // FIELDS //
    ////////////

    private String nameUseProperty = "";
    private String nameFormatRemovePrefix = "";
    private final String configPath;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public NameConfig(String configPathPrefix) {
        this.configPath = configPathPrefix + "/nameConfig";
    }

    public NameConfig(NameConfig copy) {
        this.nameUseProperty = copy.getNameUseProperty();
        this.nameFormatRemovePrefix = copy.getNameFormatRemovePrefix();
        this.configPath = copy.getConfigPath();
    }

    /////////////
    // GETTERS //
    /////////////

    public String getNameUseProperty() {
        return nameUseProperty;
    }

    public String getNameFormatRemovePrefix() {
        return nameFormatRemovePrefix;
    }

    @Override
    public String getConfigPath() {
        return configPath;
    }   

    /////////////
    // SETTERS //
    /////////////

    public void setNameUseProperty(String nameUseProperty) {
        this.nameUseProperty = nameUseProperty;
    }

    public void setNameFormatRemovePrefix(String nameFormatRemovePrefix) {
        this.nameFormatRemovePrefix = nameFormatRemovePrefix;
    }

    /////////////
    // UTILITY //
    /////////////

    public boolean isBlank() {
        return StringUtils.isBlank(nameUseProperty);
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void validate() {
        ILogger logger = WatchrCoreApp.getInstance().getLogger();

        if(StringUtils.isNotBlank(nameFormatRemovePrefix)) {
            try {
                Pattern.compile(nameFormatRemovePrefix);
            } catch(PatternSyntaxException e) {
                logger.log(new WatchrConfigError(ErrorLevel.ERROR, e.getMessage()));
            }
        }
    }

    @Override
    public List<WatchrDiff<?>> diff(IConfig other) {
        NameConfig otherNameConfig = (NameConfig) other;
        List<WatchrDiff<?>> diffList = new ArrayList<>();

        if(!(nameUseProperty.equals(otherNameConfig.nameUseProperty))) {
            WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.NAME_USE_PROPERTY);
            diff.setBeforeValue(nameUseProperty);
            diff.setNowValue(otherNameConfig.nameUseProperty);
            diffList.add(diff);
        }
        if(!(nameFormatRemovePrefix.equals(otherNameConfig.nameFormatRemovePrefix))) {
            WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.NAME_FORMAT_REMOVE_PREFIX);
            diff.setBeforeValue(nameFormatRemovePrefix);
            diff.setNowValue(otherNameConfig.nameFormatRemovePrefix);
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
			NameConfig otherNameConfig = (NameConfig) other;
            equals = nameUseProperty.equals(otherNameConfig.nameUseProperty);
            equals = equals && nameFormatRemovePrefix.equals(otherNameConfig.nameFormatRemovePrefix);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + nameUseProperty.hashCode());
        hash = 31 * (hash + nameFormatRemovePrefix.hashCode());
        return hash;
    }   
}
