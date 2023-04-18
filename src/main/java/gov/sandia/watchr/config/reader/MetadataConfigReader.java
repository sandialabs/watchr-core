/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config.reader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.MetadataConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.element.ConfigConverter;
import gov.sandia.watchr.config.element.ConfigElement;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;

public class MetadataConfigReader extends AbstractExtractorConfigReader<List<MetadataConfig>> {
    
    ////////////
    // FIELDS //
    ////////////

    private final FileConfig fileConfig;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public MetadataConfigReader(FileConfig fileConfig, ILogger logger) {
        super(logger);
        this.fileConfig = fileConfig;
        if(fileConfig == null) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "MetadataConfigReader: No file config defined."));
        }
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public List<MetadataConfig> handle(ConfigElement element, IConfig parent) {
        List<MetadataConfig> metadataList = new ArrayList<>();
        ConfigConverter converter = element.getConverter();

        for(Object listObj : element.getValueAsList()) {
            ConfigElement listElement = converter.asChild(listObj);
            MetadataConfig metadata = new MetadataConfig(fileConfig, parent.getConfigPath());

            Map<String, Object> map = listElement.getValueAsMap();
            for(Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if(key.equals(Keywords.NAME)) {
                    seenKeywords.add(Keywords.NAME);
                    metadata.setName(converter.asString(value));
                } else if(key.equals(Keywords.EXTRACTOR)) {
                    seenKeywords.add(Keywords.EXTRACTOR);
                    handleDataForExtractor(converter.asChild(value), metadata.getMetadataExtractor(), metadata);
                } else {
                    logger.log(new WatchrConfigError(ErrorLevel.WARNING, "handleAsMetadata: Unrecognized element `" + key + "`."));
                }
            }
            
            metadataList.add(metadata);
            metadata.validate();
        }

        validateMissingKeywords();
        return metadataList;
    }

    @Override
    public Set<String> getRequiredKeywords() {
        Set<String> requiredKeywords = new HashSet<>();
        requiredKeywords.add(Keywords.NAME);
        requiredKeywords.add(Keywords.EXTRACTOR);
        return requiredKeywords;
    }
}
