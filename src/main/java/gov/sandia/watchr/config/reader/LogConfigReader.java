package gov.sandia.watchr.config.reader;

import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.LogConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;

public class LogConfigReader extends AbstractConfigReader<LogConfig> {
    
    protected LogConfigReader(ILogger logger) {
        super(logger);
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public Set<String> getRequiredKeywords() {
        return new HashSet<>();
    }

    @Override
    public LogConfig handle(JsonElement element, IConfig parent) {
        LogConfig logConfig = new LogConfig(parent.getConfigPath(), logger);

        JsonObject jsonObject = element.getAsJsonObject();
        Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        for(Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if(key.equals(Keywords.LEVEL)) {
                logConfig.setLoggingLevel(value.getAsString());
            } else {
                logger.log(new WatchrConfigError(ErrorLevel.WARNING, "LogConfigReader.handle: Unrecognized element `" + key + "`."));
            }
        }

        validateMissingKeywords();
        return logConfig;
    }
}
