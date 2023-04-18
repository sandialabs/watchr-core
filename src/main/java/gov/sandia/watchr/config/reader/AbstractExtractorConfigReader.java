/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config.reader;

import java.util.Map;
import java.util.Map.Entry;

import gov.sandia.watchr.config.HierarchicalExtractor;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.element.ConfigConverter;
import gov.sandia.watchr.config.element.ConfigElement;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.generators.line.extractors.strategy.AmbiguityStrategy;

public abstract class AbstractExtractorConfigReader<E> extends AbstractConfigReader<E> {
    
    ////////////
    // FIELDS //
    ////////////

    private static final String CLASSNAME = AbstractExtractorConfigReader.class.getSimpleName();

    protected AbstractExtractorConfigReader(ILogger logger) {
        super(logger);
    }

    /////////////
    // UTILITY //
    /////////////

    protected void handleDataForExtractor(ConfigElement element, HierarchicalExtractor extractor, IConfig parent) {
        ConfigConverter converter = element.getConverter();
        Map<String, Object> map = element.getValueAsMap();
        for(Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if(key.equals(Keywords.STRATEGY)) {
                extractor.setAmbiguityStrategy(handleAsStrategy(converter.asChild(value), parent));
            } else {
                extractor.setProperty(key, converter.asString(value));
                String message = "handleDataForExtractor: Handling generic element `" + key + "`...";
                logger.logDebug(message, CLASSNAME);
            }
        }
    }

    protected AmbiguityStrategy handleAsStrategy(ConfigElement element, IConfig parent) {
        AmbiguityStrategy strategy = new AmbiguityStrategy(parent.getConfigPath());
        ConfigConverter converter = element.getConverter();
        Map<String, Object> map = element.getValueAsMap();
        for(Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if(key.equals(Keywords.RECURSE_CHILD_GRAPHS)) {
                strategy.setShouldRecurseToChildGraphs(converter.asBoolean(value));
            } else if(key.equals(Keywords.GET_FIRST_MATCH_ONLY)) {
                strategy.setShouldGetFirstMatchOnly(converter.asBoolean(value));
            } else if(key.equals(Keywords.ITERATE_WITH)) {
                strategy.setIterateWithOtherExtractor(converter.asString(value));
            } else {
                logger.log(new WatchrConfigError(ErrorLevel.WARNING, "handleAsStrategy: Unrecognized element `" + key + "`."));
            }
        }
        return strategy;
    }
}
