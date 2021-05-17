package gov.sandia.watchr.config.reader;

import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.FileFilterConfig;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;

public class FileFilterConfigReader extends AbstractExtractorConfigReader<FileFilterConfig> {

    @Override
    public Set<String> getRequiredKeywords() {
        Set<String> requiredKeywords = new HashSet<>();
        requiredKeywords.add(Keywords.NAME_PATTERN);
        return requiredKeywords;
    }

    @Override
    public FileFilterConfig handle(JsonElement element, IConfig parent) {
        FileFilterConfig fileFilterConfig = new FileFilterConfig(parent.getConfigPath());

        JsonObject jsonObject = element.getAsJsonObject();
        Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        for(Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if(key.equals(Keywords.NAME_PATTERN)) {
                seenKeywords.add(Keywords.NAME_PATTERN);
                fileFilterConfig.setNamePattern(value.getAsString());
            } else {
                ILogger logger = WatchrCoreApp.getInstance().getLogger();
                logger.log(new WatchrConfigError(ErrorLevel.WARNING, "handleAsFileFilterConfig: Unrecognized element `" + key + "`."));
            }
        }

        validateMissingKeywords();
        return fileFilterConfig;
    }
    
}
