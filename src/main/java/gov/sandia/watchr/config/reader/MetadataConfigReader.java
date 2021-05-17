/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config.reader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.MetadataConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
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

    public MetadataConfigReader(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
        if(fileConfig == null) {
            ILogger logger = WatchrCoreApp.getInstance().getLogger();
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "MetadataConfigReader: No file config defined."));
        }
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public List<MetadataConfig> handle(JsonElement jsonElement, IConfig parent) {
        List<MetadataConfig> metadataList = new ArrayList<>();
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        for(int i = 0; i < jsonArray.size(); i++) {
            JsonElement arrayElement = jsonArray.get(i);
            JsonObject jsonObject = arrayElement.getAsJsonObject();
            Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
            MetadataConfig metadata = new MetadataConfig(fileConfig, parent.getConfigPath());

            for(Entry<String, JsonElement> entry : entrySet) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();

                if(key.equals(Keywords.NAME)) {
                    seenKeywords.add(Keywords.NAME);
                    metadata.setName(value.getAsString());
                } else if(key.equals(Keywords.EXTRACTOR)) {
                    seenKeywords.add(Keywords.EXTRACTOR);
                    handleDataForExtractor(value, metadata.getMetadataExtractor(), metadata);
                } else {
                    ILogger logger = WatchrCoreApp.getInstance().getLogger();
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
