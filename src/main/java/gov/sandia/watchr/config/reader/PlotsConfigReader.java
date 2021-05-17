/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config.reader;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gov.sandia.watchr.config.CategoryConfiguration;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.config.PlotsConfig;
import gov.sandia.watchr.config.schema.Keywords;

public class PlotsConfigReader extends AbstractConfigReader<PlotsConfig> {

    ////////////
    // FIELDS //
    ////////////

    private final File startDir;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PlotsConfigReader(File startDir) {
        this.startDir = startDir;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public PlotsConfig handle(JsonElement jsonElement, IConfig parent) {
        PlotsConfig plotsConfig = new PlotsConfig(parent.getConfigPath());

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        for (Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if(key.equals(Keywords.FILES)) {
                seenKeywords.add(Keywords.FILES);
                FileConfigReader fileConfigReader = new FileConfigReader(startDir);
                plotsConfig.setFileConfig(fileConfigReader.handle(value, plotsConfig));
            } else if (key.equals(Keywords.PLOT)) {
                seenKeywords.add(Keywords.PLOT);
                PlotConfigReader plotsConfigReader = new PlotConfigReader(plotsConfig.getFileConfig());
                List<PlotConfig> plotConfigList = plotsConfigReader.handle(value, plotsConfig);
                plotsConfig.getPlotConfigs().addAll(plotConfigList);
            } else if (key.equals(Keywords.CATEGORIES)) {
                seenKeywords.add(Keywords.CATEGORIES);
                CategoryConfigReader categoryReader = new CategoryConfigReader();
                CategoryConfiguration categoryConfig = categoryReader.handle(value, plotsConfig);
                plotsConfig.setCategoryConfig(categoryConfig);
            }
        }

        validateMissingKeywords();
        return plotsConfig;
    }

    @Override
    public Set<String> getRequiredKeywords() {
        Set<String> requiredKeywords = new HashSet<>();
        requiredKeywords.add(Keywords.FILES);
        requiredKeywords.add(Keywords.PLOT);
        return requiredKeywords;
    }
}
