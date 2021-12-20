/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config.derivative;

import java.util.ArrayList;
import java.util.List;

import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.util.RGB;
import gov.sandia.watchr.util.RGBA;

public abstract class DerivativeLine implements IConfig {

    ////////////
    // FIELDS //
    ////////////

    protected String configPath;
    protected String name;
    protected RGB color;

    protected final ILogger logger;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    protected DerivativeLine(String configPathPrefix, ILogger logger) {
        this.name = "";
        this.configPath = configPathPrefix;
        this.logger = logger;
    }

    protected DerivativeLine(DerivativeLine copy) {
        this.name = copy.getName();
        this.configPath = copy.getConfigPath();
        this.color = new RGB(copy.getColor());
        this.logger = copy.logger;
    }

    /////////////
    // GETTERS //
    /////////////

    public String getName() {
        return name;
    }

    public RGB getColor() {
        return color;
    }

    @Override
    public String getConfigPath() {
        return configPath;
    } 

    /////////////
    // SETTERS //
    /////////////

    public void setColor(int r, int g, int b) {
        this.color = new RGB(r, g, b);
    }

    public void setColor(int r, int g, int b, double a) {
        this.color = new RGBA(r,g,b,a);
    }

    public void setColor(RGB color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void validate() {
        // Nothing to validate
    }

    @Override
    public List<WatchrDiff<?>> diff(IConfig other) {
        DerivativeLine otherDerivativeLine = (DerivativeLine) other;
        List<WatchrDiff<?>> diffList = new ArrayList<>();

        if((color == null ^ otherDerivativeLine.color == null) ||
           (color != null && !color.equals(otherDerivativeLine.color))) {
            WatchrDiff<RGB> diff = new WatchrDiff<>(configPath, DiffCategory.DERIVATIVE_LINE_COLOR);
            diff.setBeforeValue(color);
            diff.setNowValue(otherDerivativeLine.color);
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
            equals = color.equals(otherDerivativeLine.color);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + name.hashCode());
        hash = 31 * (hash + color.hashCode());
        return hash;
    }

    //////////////
    // ABSTRACT //
    //////////////

    public abstract DerivativeLine applyOverTemplate(DerivativeLine template);
}
