package gov.sandia.watchr.config.rule;

import gov.sandia.watchr.log.ILogger;

public class AlwaysRuleConfig extends RuleConfig {

    private static final String ALWAYS = "always";

    public AlwaysRuleConfig(String configPathPrefix, ILogger logger) {
        super(configPathPrefix, logger);
    }

    public AlwaysRuleConfig(RuleConfig copy) {
        super(copy);
    }

    @Override
    public String getCondition() {
        return ALWAYS;
    }
}
