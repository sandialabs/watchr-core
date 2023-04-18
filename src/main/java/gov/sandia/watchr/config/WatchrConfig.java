/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config;

import java.util.ArrayList;
import java.util.List;

import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.config.rule.RuleConfig;
import gov.sandia.watchr.config.rule.RuleConfig.RuleWhen;
import gov.sandia.watchr.log.ILogger;

public class WatchrConfig implements IConfig {

    ////////////
    // FIELDS //
    ////////////
    
    public static final String START_PATH = "/";

    private PlotsConfig plotsConfig;
    private GraphDisplayConfig graphConfig;
    private List<RuleConfig> ruleConfigs;
    private DataFilterConfig filterConfig;
    private LogConfig logConfig;
    private ILogger logger;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public WatchrConfig(ILogger logger, IFileReader fileReader) {
        plotsConfig = new PlotsConfig(START_PATH, logger, fileReader);
        graphConfig = new GraphDisplayConfig(START_PATH, logger);
        logConfig   = new LogConfig(START_PATH, logger);
        ruleConfigs = new ArrayList<>();
        filterConfig = new DataFilterConfig(START_PATH, logger);
        this.logger = logger;
    }

    public WatchrConfig(WatchrConfig copy) {
        plotsConfig = new PlotsConfig(copy.getPlotsConfig());
        graphConfig = new GraphDisplayConfig(copy.getGraphDisplayConfig());
        logConfig   = new LogConfig(copy.getLogConfig());
        ruleConfigs = new ArrayList<>(copy.getRuleConfigs());
        filterConfig = new DataFilterConfig(copy.getFilterConfig());
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

    public List<RuleConfig> getRuleConfigs() {
        return ruleConfigs;
    }

    public List<RuleConfig> getRuleConfigs(RuleWhen when) {
        List<RuleConfig> whenRules = new ArrayList<>();
        for(RuleConfig rule : ruleConfigs) {
            if(rule.getWhen() == when) {
                whenRules.add(rule);
            }
        }
        return whenRules;
    }

    public DataFilterConfig getFilterConfig() {
        return filterConfig;
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

    public void setRuleConfigs(List<RuleConfig> ruleConfigs) {
        this.ruleConfigs = new ArrayList<>();
        this.ruleConfigs.addAll(ruleConfigs);
    }

    public void setFilterConfig(DataFilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    public void setStartFileAbsPath(String startFileAbsPath) {
        plotsConfig.setStartFileAbsPath(startFileAbsPath);
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
        diffList.addAll(filterConfig.diff(otherWatchrConfig.filterConfig));
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
            equals = equals && graphConfig.equals(otherWatchrConfig.graphConfig);
            equals = equals && logConfig.equals(otherWatchrConfig.logConfig);
            equals = equals && ruleConfigs.equals(otherWatchrConfig.ruleConfigs);
            equals = equals && filterConfig.equals(otherWatchrConfig.filterConfig);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + plotsConfig.hashCode());
        hash = 31 * (hash + graphConfig.hashCode());
        hash = 31 * (hash + logConfig.hashCode());
        hash = 31 * (hash + ruleConfigs.hashCode());
        hash = 31 * (hash + filterConfig.hashCode());
        return hash;
    }
}
