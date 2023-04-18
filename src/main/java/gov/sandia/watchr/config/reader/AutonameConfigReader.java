package gov.sandia.watchr.config.reader;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.NameConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.element.ConfigConverter;
import gov.sandia.watchr.config.element.ConfigElement;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;

public class AutonameConfigReader extends AbstractExtractorConfigReader<NameConfig> {

    ////////////
    // FIELDS //
    ////////////

    private final FileConfig fileConfig;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public AutonameConfigReader(FileConfig fileConfig, ILogger logger) {
        super(logger);
        this.fileConfig = fileConfig;
        if(fileConfig == null) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "AutonameConfigReader: No file config defined."));
        }
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public Set<String> getRequiredKeywords() {
        return new HashSet<>();
    }

    @Override
    public NameConfig handle(ConfigElement element, IConfig parent) {
        NameConfig nameConfig = new NameConfig(fileConfig, parent.getConfigPath());
        ConfigConverter converter = element.getConverter();

        Map<String, Object> map = element.getValueAsMap();
        for(Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if(key.equals(Keywords.USE_PROPERTY)) {
                seenKeywords.add(Keywords.USE_PROPERTY);
                nameConfig.setNameUseProperty(converter.asString(value));
            } else if(key.equals(Keywords.EXTRACTOR)) {
                seenKeywords.add(Keywords.EXTRACTOR);
                handleDataForExtractor(converter.asChild(value), nameConfig.getNameUseExtractor(), nameConfig);
            } else if(key.equals(Keywords.FORMAT_BY_REMOVING_PREFIX)) {
                seenKeywords.add(Keywords.FORMAT_BY_REMOVING_PREFIX);
                nameConfig.setNameFormatRemovePrefix(converter.asString(value));
            } else {
                logger.log(new WatchrConfigError(ErrorLevel.WARNING, "handleAsNameConfig: Unrecognized element `" + key + "`."));
            }
        }
        validateMissingKeywords();

        return nameConfig;
    }
    
}
