/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config.reader;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.PlotypusConfig;
import gov.sandia.watchr.config.element.ConfigConverter;
import gov.sandia.watchr.config.element.ConfigElement;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;

public class PlotypusConfigReader extends AbstractConfigReader<PlotypusConfig> {

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PlotypusConfigReader(ILogger logger) {
        super(logger);
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public PlotypusConfig handle(ConfigElement element, IConfig parent) {
        PlotypusConfig plotypusConfig = new PlotypusConfig(parent.getConfigPath(), logger);

        ConfigConverter converter = element.getConverter();
        Map<String, Object> map = element.getValueAsMap();
        for(Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if(key.equals(Keywords.TENTACLES)) {
                seenKeywords.add(Keywords.TENTACLES);
                plotypusConfig.setNumberOfTentacles(converter.asInt(value));
            } else if(key.equals(Keywords.TIMEOUT)) {
                seenKeywords.add(Keywords.TIMEOUT);
                plotypusConfig.setPayloadTimeout(converter.asInt(value) * 1000);
            } else if(key.equals(Keywords.TIME_WARNING)) {
                seenKeywords.add(Keywords.TIME_WARNING);
                plotypusConfig.setPayloadTimeWarning(converter.asInt(value) * 1000);
            }
        }

        validateMissingKeywords();
        return plotypusConfig;
    }

    @Override
    public Set<String> getRequiredKeywords() {
        Set<String> requiredKeywords = new HashSet<>();
        requiredKeywords.add(Keywords.TENTACLES);
        return requiredKeywords;
    }
}
