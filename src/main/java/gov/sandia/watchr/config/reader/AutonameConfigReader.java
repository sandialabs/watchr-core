package gov.sandia.watchr.config.reader;

import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.NameConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;

public class AutonameConfigReader extends AbstractExtractorConfigReader<NameConfig> {

    @Override
    public Set<String> getRequiredKeywords() {
        Set<String> requiredKeywords = new HashSet<>();
        requiredKeywords.add(Keywords.USE_PROPERTY);
        return requiredKeywords;
    }

    @Override
    public NameConfig handle(JsonElement element, IConfig parent) {
        NameConfig nameConfig = new NameConfig(parent.getConfigPath());

        JsonObject jsonObject = element.getAsJsonObject();
        Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        for(Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if(key.equals(Keywords.USE_PROPERTY)) {
                seenKeywords.add(Keywords.USE_PROPERTY);
                nameConfig.setNameUseProperty(value.getAsString());
            } else if(key.equals(Keywords.FORMAT_BY_REMOVING_PREFIX)) {
                seenKeywords.add(Keywords.FORMAT_BY_REMOVING_PREFIX);
                nameConfig.setNameFormatRemovePrefix(value.getAsString());
            } else {
                ILogger logger = WatchrCoreApp.getInstance().getLogger();
                logger.log(new WatchrConfigError(ErrorLevel.WARNING, "handleAsNameConfig: Unrecognized element `" + key + "`."));
            }
        }
        validateMissingKeywords();

        return nameConfig;
    }
    
}
