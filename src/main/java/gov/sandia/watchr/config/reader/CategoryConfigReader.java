/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config.reader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gov.sandia.watchr.config.CategoryConfiguration;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.element.ConfigConverter;
import gov.sandia.watchr.config.element.ConfigElement;
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
    public CategoryConfiguration handle(ConfigElement element, IConfig parent) {
        List<Object> list = element.getValueAsList();
        CategoryConfiguration categoryConfiguration = new CategoryConfiguration(parent.getConfigPath(), logger);
        ConfigConverter converter = element.getConverter();
        for(Object value : list) {
            categoryConfiguration.getCategories().add(converter.asString(value));
        }
        validateMissingKeywords();
        return categoryConfiguration;
    }

    @Override
    public Set<String> getRequiredKeywords() {
        return new HashSet<>();
    }
}
