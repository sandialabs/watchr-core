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

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.util.RGB;

public class DerivativeLine implements IConfig {

    ////////////
    // FIELDS //
    ////////////

    public enum DerivativeLineType {
        AVERAGE("Average"),
        STANDARD_DEVIATION("Std. Dev."),
        STANDARD_DEVIATION_OFFSET("Average + Std. Dev.");

        private final String label;

        private DerivativeLineType(String label) {
            this.label = label;
        }

        public String get() {
            return label;
        }
    }

    private DerivativeLineType type;
    private int rollingRange;
    private RGB color;
    private boolean ignoreFilteredData;

    private final String configPath;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public DerivativeLine(String configPathPrefix) {
        this.configPath = configPathPrefix + "/derivativeLine";
    }

    public DerivativeLine(DerivativeLine copy) {
        this.type = copy.getType();
        this.rollingRange = copy.getRollingRange();
        this.color = new RGB(copy.getColor());
        this.ignoreFilteredData = copy.shouldIgnoreFilteredData();
        this.configPath = copy.getConfigPath();
    }

    /////////////
    // GETTERS //
    /////////////

    public DerivativeLineType getType() {
        return type;
    }

    public int getRollingRange() {
        return rollingRange;
    }

    public RGB getColor() {
        return color;
    }

    public boolean shouldIgnoreFilteredData() {
        return ignoreFilteredData;
    }

    @Override
    public String getConfigPath() {
        return configPath + "/" + type.toString();
    } 

    /////////////
    // SETTERS //
    /////////////

    public void setType(DerivativeLineType type) {
        this.type = type;
    }

    public void setRollingRange(int rollingRange) {
        this.rollingRange = rollingRange;
    }

    public void setColor(int r, int g, int b) {
        this.color = new RGB(r, g, b);
    }

    public void setColor(RGB color) {
        this.color = color;
    }

    public void setIgnoreFilteredData(boolean ignoreFilteredData) {
        this.ignoreFilteredData = ignoreFilteredData;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void validate() {
        ILogger logger = WatchrCoreApp.getInstance().getLogger();
        
        if(type == null) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "Derivative line was defined, but no type was specified!"));
        }
        if(rollingRange < 1) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "The rolling range for a derivative line must use 1 or more points!"));
        }
    }

    @Override
    public List<WatchrDiff<?>> diff(IConfig other) {
        DerivativeLine otherDerivativeLine = (DerivativeLine) other;
        List<WatchrDiff<?>> diffList = new ArrayList<>();

        if(type == null ^ otherDerivativeLine.type == null) {
            WatchrDiff<DerivativeLineType> diff = new WatchrDiff<>(configPath, DiffCategory.TYPE);
            diff.setBeforeValue(type);
            diff.setNowValue(otherDerivativeLine.type);
            diffList.add(diff);
        }
        if(type != null && !(type.equals(otherDerivativeLine.type))) {
            WatchrDiff<DerivativeLineType> diff = new WatchrDiff<>(configPath, DiffCategory.TYPE);
            diff.setBeforeValue(getType());
            diff.setNowValue(otherDerivativeLine.getType());
            diffList.add(diff);
        }
        if(rollingRange != otherDerivativeLine.rollingRange) {
            WatchrDiff<Integer> diff = new WatchrDiff<>(configPath, DiffCategory.ROLLING_RANGE);
            diff.setBeforeValue(rollingRange);
            diff.setNowValue(otherDerivativeLine.rollingRange);
            diffList.add(diff);
        }
        
        if((color == null ^ otherDerivativeLine.color == null) ||
           (color != null && !color.equals(otherDerivativeLine.color))) {
            WatchrDiff<RGB> diff = new WatchrDiff<>(configPath, DiffCategory.DERIVATIVE_LINE_COLOR);
            diff.setBeforeValue(color);
            diff.setNowValue(otherDerivativeLine.color);
            diffList.add(diff);
        }

        if(ignoreFilteredData != otherDerivativeLine.ignoreFilteredData) {
            WatchrDiff<Boolean> diff = new WatchrDiff<>(configPath, DiffCategory.IGNORE_FILTERED_DATA);
            diff.setBeforeValue(ignoreFilteredData);
            diff.setNowValue(otherDerivativeLine.ignoreFilteredData);
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
			DerivativeLine otherDerivativeLine = (DerivativeLine) other;
            equals = type.equals(otherDerivativeLine.type);
            equals = equals && rollingRange == otherDerivativeLine.rollingRange;
            equals = equals && color.equals(otherDerivativeLine.color);
            equals = equals && ignoreFilteredData == otherDerivativeLine.ignoreFilteredData;
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + type.hashCode());
        hash = 31 * (hash + Integer.hashCode(rollingRange));
        hash = 31 * (hash + color.hashCode());
        hash = 31 * (hash + Boolean.hashCode(ignoreFilteredData));
        return hash;
    }   
}
