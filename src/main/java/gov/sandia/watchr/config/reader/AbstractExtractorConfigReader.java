/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config.reader;

import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gov.sandia.watchr.config.HierarchicalExtractor;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.extractors.strategy.AmbiguityStrategy;

public abstract class AbstractExtractorConfigReader<E> extends AbstractConfigReader<E> {
    
    ////////////
    // FIELDS //
    ////////////

    protected AbstractExtractorConfigReader(ILogger logger) {
        super(logger);
    }

    /////////////
    // UTILITY //
    /////////////

    protected void handleDataForExtractor(JsonElement jsonElement, HierarchicalExtractor extractor, IConfig parent) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        for(Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if(key.equals(Keywords.STRATEGY)) {
                extractor.setAmbiguityStrategy(handleAsStrategy(value, parent));
            } else {
                extractor.setProperty(key, value.getAsString());
                String message = "handleDataForExtractor: Handling generic element `" + key + "`...";
                logger.log(new WatchrConfigError(ErrorLevel.INFO, message));
            }
        }
    }

    protected AmbiguityStrategy handleAsStrategy(JsonElement jsonElement, IConfig parent) {
        AmbiguityStrategy strategy = new AmbiguityStrategy(parent.getConfigPath());

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        for(Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if(key.equals(Keywords.RECURSE_CHILD_GRAPHS)) {
                strategy.setShouldRecurseToChildGraphs(value.getAsBoolean());
            } else if(key.equals(Keywords.GET_FIRST_MATCH_ONLY)) {
                strategy.setShouldGetFirstMatchOnly(value.getAsBoolean());
            } else if(key.equals(Keywords.ITERATE_WITH)) {
                strategy.setIterateWithOtherExtractor(value.getAsString());
            } else {
                logger.log(new WatchrConfigError(ErrorLevel.WARNING, "handleAsStrategy: Unrecognized element `" + key + "`."));
            }
        }
        return strategy;
    }
}
