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

import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.log.ILogger;

public class WatchrConfig implements IConfig {

    ////////////
    // FIELDS //
    ////////////
    
    private static final String START_PATH = "/";

    private PlotsConfig plotsConfig;
    private GraphDisplayConfig graphConfig;
    private LogConfig logConfig;
    private ILogger logger;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public WatchrConfig(ILogger logger, IFileReader fileReader) {
        plotsConfig = new PlotsConfig(START_PATH, logger, fileReader);
        graphConfig = new GraphDisplayConfig(START_PATH, logger);
        logConfig   = new LogConfig(START_PATH, logger);
        this.logger = logger;
    }

    public WatchrConfig(WatchrConfig copy) {
        plotsConfig = new PlotsConfig(copy.getPlotsConfig());
        graphConfig = new GraphDisplayConfig(copy.getGraphDisplayConfig());
        logConfig   = new LogConfig(copy.getLogConfig());
        logger = copy.getLogger();
    }

    /////////////
    // GETTERS //
    /////////////

    public PlotsConfig getPlotsConfig() {
        return plotsConfig;
    }

    public GraphDisplayConfig getGraphDisplayConfig() {
        return graphConfig;
    }

    public LogConfig getLogConfig() {
        return logConfig;
    }

    @Override
    public String getConfigPath() {
        return START_PATH;
    }

    @Override
    public ILogger getLogger() {
        return logger;
    }

    /////////////
    // SETTERS //
    /////////////

    public void setPlotsConfig(PlotsConfig plotConfig) {
        this.plotsConfig = plotConfig;
    }

    public void setGraphDisplayConfig(GraphDisplayConfig graphConfig) {
        this.graphConfig = graphConfig;
    }

    public void setLogConfig(LogConfig logConfig) {
        this.logConfig = logConfig;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void validate() {
        plotsConfig.validate();
        graphConfig.validate();
    }

    @Override
    public List<WatchrDiff<?>> diff(IConfig other) {
        WatchrConfig otherWatchrConfig = (WatchrConfig) other;
        List<WatchrDiff<?>> diffList = new ArrayList<>();

        diffList.addAll(plotsConfig.diff(otherWatchrConfig.plotsConfig));
        diffList.addAll(graphConfig.diff(otherWatchrConfig.graphConfig));
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
			WatchrConfig otherWatchrConfig = (WatchrConfig) other;
            equals = plotsConfig.equals(otherWatchrConfig.plotsConfig);
            equals = equals && graphConfig != otherWatchrConfig.graphConfig;
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + plotsConfig.hashCode());
        hash = 31 * (hash + graphConfig.hashCode());
        return hash;
    }
}
