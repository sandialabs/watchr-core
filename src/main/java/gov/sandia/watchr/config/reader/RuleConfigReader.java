package gov.sandia.watchr.config.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.RuleConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;

public class RuleConfigReader extends AbstractExtractorConfigReader<List<RuleConfig>> {

    @Override
    public Set<String> getRequiredKeywords() {
        Set<String> requiredKeywords = new HashSet<>();
        requiredKeywords.add(Keywords.CONDITION);
        requiredKeywords.add(Keywords.ACTION);
        return requiredKeywords;
    }

    @Override
    public List<RuleConfig> handle(JsonElement element, IConfig parent) {
        List<RuleConfig> plotRules = new ArrayList<>();
        JsonArray jsonArray = element.getAsJsonArray();
        for(int i = 0; i < jsonArray.size(); i++) {
            JsonElement arrayElement = jsonArray.get(i);
            JsonObject jsonObject = arrayElement.getAsJsonObject();
            Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
            RuleConfig plotRule = new RuleConfig(parent.getConfigPath() + "/" + Integer.toString(i));

            for(Entry<String, JsonElement> entry : entrySet) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();

                if(key.equals(Keywords.CONDITION)) {
                    seenKeywords.add(Keywords.CONDITION);
                    plotRule.setCondition(value.getAsString());
                } else if(key.equals(Keywords.ACTION)) {
                    seenKeywords.add(Keywords.ACTION);
                    plotRule.setAction(value.getAsString());
                } else if(key.equals(Keywords.ACTION_PROPERTIES)) {
                    seenKeywords.add(Keywords.ACTION_PROPERTIES);
                    plotRule.getActionProperties().putAll(handleAsActionProperties(value));
                } else {
                    ILogger logger = WatchrCoreApp.getInstance().getLogger();
                    logger.log(new WatchrConfigError(ErrorLevel.WARNING, "handleAsRules: Unrecognized element `" + key + "`."));
                }
            }

            plotRules.add(plotRule);
        }

        validateMissingKeywords();
        return plotRules;
    }
    
    private Map<String, String> handleAsActionProperties(JsonElement jsonElement) {
        Map<String, String> actionProperties = new HashMap<>();
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        for(Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            actionProperties.put(key, value.getAsString());
        }
        return actionProperties;
    }
}
