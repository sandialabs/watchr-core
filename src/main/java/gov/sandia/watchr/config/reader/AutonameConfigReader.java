package gov.sandia.watchr.config.reader;

import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.NameConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
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
    public NameConfig handle(JsonElement element, IConfig parent) {
        NameConfig nameConfig = new NameConfig(fileConfig, parent.getConfigPath());

        JsonObject jsonObject = element.getAsJsonObject();
        Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        for(Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if(key.equals(Keywords.USE_PROPERTY)) {
                seenKeywords.add(Keywords.USE_PROPERTY);
                nameConfig.setNameUseProperty(value.getAsString());
            } else if(key.equals(Keywords.EXTRACTOR)) {
                seenKeywords.add(Keywords.EXTRACTOR);
                handleDataForExtractor(value, nameConfig.getNameUseExtractor(), nameConfig);
            } else if(key.equals(Keywords.FORMAT_BY_REMOVING_PREFIX)) {
                seenKeywords.add(Keywords.FORMAT_BY_REMOVING_PREFIX);
                nameConfig.setNameFormatRemovePrefix(value.getAsString());
            } else {
                logger.log(new WatchrConfigError(ErrorLevel.WARNING, "handleAsNameConfig: Unrecognized element `" + key + "`."));
            }
        }
        validateMissingKeywords();

        return nameConfig;
    }
    
}
