/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config.reader;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import gov.sandia.watchr.config.CategoryConfiguration;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.log.ILogger;

public class CategoryConfigReader extends AbstractConfigReader<CategoryConfiguration> {

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    protected CategoryConfigReader(ILogger logger) {
        super(logger);
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public CategoryConfiguration handle(JsonElement element, IConfig parent) {
        JsonArray jsonArray = element.getAsJsonArray();
        CategoryConfiguration categoryConfiguration = new CategoryConfiguration(parent.getConfigPath(), logger);

        for(int i = 0; i < jsonArray.size(); i++) {
            JsonElement value = jsonArray.get(i);
            categoryConfiguration.getCategories().add(value.getAsString());
        }
        validateMissingKeywords();
        return categoryConfiguration;
    }

    @Override
    public Set<String> getRequiredKeywords() {
        return new HashSet<>();
    }
}
