/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.log.ILogger;

public class RuleConfig implements IConfig {

    ////////////
    // FIELDS //
    ////////////

    private String condition = "";
    private String action = "";
    private Map<String, String> actionProperties;

    private final String configPath;
    private final ILogger logger;

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public RuleConfig(String configPathPrefix, ILogger logger) {
        this.configPath = configPathPrefix + "/ruleConfig";
        this.actionProperties = new HashMap<>();
        this.logger = logger;
    }

    public RuleConfig(RuleConfig copy) {
        this.condition = copy.getCondition();
        this.action = copy.getAction();
        this.configPath = copy.getConfigPath();

        this.actionProperties = new HashMap<>();
        this.actionProperties.putAll(copy.actionProperties);
        this.logger = copy.getLogger();
    }

    /////////////
    // GETTERS //
    /////////////

    public String getCondition() {
        return condition;
    }

    public String getAction() {
        return action;
    }

    public String getActionProperty(String key) {
        return actionProperties.get(key);
    }

    public Map<String, String> getActionProperties() {
        return actionProperties;
    }

    /////////////
    // SETTERS //
    /////////////

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setActionProperty(String key, String value) {
        this.actionProperties.put(key, value);
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
        RuleConfig otherRuleConfig = (RuleConfig) other;
        List<WatchrDiff<?>> diffList = new ArrayList<>();

        if(!(condition.equals(otherRuleConfig.condition))) {
            WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.CONDITION);
            diff.setBeforeValue(condition);
            diff.setNowValue(otherRuleConfig.condition);
            diffList.add(diff);
        }
        if(!(action.equals(otherRuleConfig.action))) {
            WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.ACTION);
            diff.setBeforeValue(action);
            diff.setNowValue(otherRuleConfig.action);
            diffList.add(diff);
        }
        if(!(actionProperties.equals(otherRuleConfig.actionProperties))) {
            WatchrDiff<Map<String,String>> diff = new WatchrDiff<>(configPath, DiffCategory.ACTION_PROPERTIES);
            diff.setBeforeValue(actionProperties);
            diff.setNowValue(otherRuleConfig.actionProperties);
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
			RuleConfig otherRuleConfig = (RuleConfig) other;
            equals = condition.equals(otherRuleConfig.condition);
            equals = equals && action.equals(otherRuleConfig.action);

            if(actionProperties != null && otherRuleConfig.actionProperties != null) {
                equals = equals && actionProperties.equals(otherRuleConfig.actionProperties);
            } else {
                equals = equals && actionProperties == null && otherRuleConfig.actionProperties == null;
            }
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + condition.hashCode());
        hash = 31 * (hash + action.hashCode());
        hash = 31 * (hash + actionProperties.hashCode());
        return hash;
    }

    @Override
    public String getConfigPath() {
        return configPath;
    }

    @Override
    public ILogger getLogger() {
        return logger;
    }          
}
