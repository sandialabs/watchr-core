package gov.sandia.watchr.config.reader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.LogConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.element.ConfigConverter;
import gov.sandia.watchr.config.element.ConfigElement;
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
    public LogConfig handle(ConfigElement element, IConfig parent) {
        LogConfig logConfig = new LogConfig(parent.getConfigPath(), logger);

        ConfigConverter converter = element.getConverter();
        Map<String, Object> map = element.getValueAsMap();
        for(Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if(key.equals(Keywords.LEVEL)) {
                logConfig.setLoggingLevel(converter.asString(value));
            } else if(key.equals(Keywords.LOGGABLE_CLASSES)) {
                ConfigElement childConfigElement = converter.asChild(value);
                List<String> loggableDebugClasses = new ArrayList<>();
                for(Object childValue : childConfigElement.getValueAsList()) {
                    loggableDebugClasses.add(converter.asString(childValue));
                }
                logConfig.setLoggableDebugClasses(loggableDebugClasses);
            } else {
                logger.log(new WatchrConfigError(ErrorLevel.WARNING, "LogConfigReader.handle: Unrecognized element `" + key + "`."));
            }
        }

        validateMissingKeywords();
        return logConfig;
    }
}
