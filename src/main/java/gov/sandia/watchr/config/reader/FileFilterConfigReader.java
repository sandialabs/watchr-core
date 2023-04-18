package gov.sandia.watchr.config.reader;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import gov.sandia.watchr.config.FileFilterConfig;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.element.ConfigConverter;
import gov.sandia.watchr.config.element.ConfigElement;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;

public class FileFilterConfigReader extends AbstractExtractorConfigReader<FileFilterConfig> {

    protected FileFilterConfigReader(ILogger logger) {
        super(logger);
    }

    @Override
    public Set<String> getRequiredKeywords() {
        Set<String> requiredKeywords = new HashSet<>();
        requiredKeywords.add(Keywords.NAME_PATTERN);
        return requiredKeywords;
    }

    @Override
    public FileFilterConfig handle(ConfigElement element, IConfig parent) {
        FileFilterConfig fileFilterConfig = new FileFilterConfig(parent.getConfigPath(), logger);

        ConfigConverter converter = element.getConverter();
        Map<String, Object> map = element.getValueAsMap();
        for(Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if(key.equals(Keywords.NAME_PATTERN)) {
                seenKeywords.add(Keywords.NAME_PATTERN);
                fileFilterConfig.setNamePattern(converter.asString(value));
            } else {
                logger.log(new WatchrConfigError(ErrorLevel.WARNING, "handleAsFileFilterConfig: Unrecognized element `" + key + "`."));
            }
        }

        validateMissingKeywords();
        return fileFilterConfig;
    }
    
}
