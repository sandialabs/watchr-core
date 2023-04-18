package gov.sandia.watchr.config.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.element.ConfigConverter;
import gov.sandia.watchr.config.element.ConfigElement;
import gov.sandia.watchr.config.rule.AlwaysRuleConfig;
import gov.sandia.watchr.config.rule.RuleConfig;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;

public class RuleConfigReader extends AbstractExtractorConfigReader<List<RuleConfig>> {

    protected RuleConfigReader(ILogger logger) {
        super(logger);
    }

    @Override
    public Set<String> getRequiredKeywords() {
        Set<String> requiredKeywords = new HashSet<>();
        requiredKeywords.add(Keywords.CONDITION);
        requiredKeywords.add(Keywords.ACTION);
        return requiredKeywords;
    }

    @Override
    public List<RuleConfig> handle(ConfigElement element, IConfig parent) {
        List<RuleConfig> plotRules = new ArrayList<>();
        List<Object> array = element.getValueAsList();
        ConfigConverter converter = element.getConverter();
        
        for(int i = 0; i < array.size(); i++) {
            Object arrayElement = array.get(i);
            ConfigElement configElement = converter.asChild(arrayElement);
            RuleConfig ruleConfig = new RuleConfig(parent.getConfigPath() + "/" + Integer.toString(i), logger);

            Map<String, Object> map = configElement.getValueAsMap();
            for(Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if(key.equals(Keywords.CONDITION)) {
                    seenKeywords.add(Keywords.CONDITION);
                    String conditionValue = converter.asString(value);
                    if(conditionValue.equals(Keywords.ALWAYS)) {
                        ruleConfig = new AlwaysRuleConfig(parent.getConfigPath() + "/" + Integer.toString(i), logger);
                    } else {
                        ruleConfig.setCondition(conditionValue);
                    }
                } else if(key.equals(Keywords.ACTION)) {
                    seenKeywords.add(Keywords.ACTION);
                    ruleConfig.setAction(converter.asString(value));
                } else if(key.equals(Keywords.WHEN)) {
                    seenKeywords.add(Keywords.WHEN);
                    ruleConfig.setWhen(converter.asString(value));
                } else if(key.equals(Keywords.ACTION_PROPERTIES)) {
                    seenKeywords.add(Keywords.ACTION_PROPERTIES);
                    ruleConfig.getActionProperties().putAll(handleAsActionProperties(converter.asChild(value)));
                } else {
                    logger.log(new WatchrConfigError(ErrorLevel.WARNING, "handleAsRules: Unrecognized element `" + key + "`."));
                }
            }

            plotRules.add(ruleConfig);
        }

        validateMissingKeywords();
        return plotRules;
    }
    
    private Map<String, String> handleAsActionProperties(ConfigElement element) {
        Map<String, String> actionProperties = new HashMap<>();
        ConfigConverter converter = element.getConverter();

        Map<String, Object> map = element.getValueAsMap();
        for(Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            actionProperties.put(key, converter.asString(value));
        }
        return actionProperties;
    }
}
