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

import gov.sandia.watchr.config.DataFilterConfig;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.element.ConfigConverter;
import gov.sandia.watchr.config.element.ConfigElement;
import gov.sandia.watchr.config.filter.DataFilter;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;

public class DataFilterConfigReader extends AbstractConfigReader<DataFilterConfig> {

    protected DataFilterConfigReader(ILogger logger) {
        super(logger);
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public DataFilterConfig handle(ConfigElement element, IConfig parent) {
        DataFilterConfig filterConfig = new DataFilterConfig(parent.getConfigPath(), logger);

        List<Object> list = element.getValueAsList();
        List<DataFilter> filters = new ArrayList<>();
        ConfigConverter converter = element.getConverter();

        for(Object value : list) {
            DataFilter dataFilter = buildDataFilter(converter.asChild(value));
            filters.add(dataFilter);
        }

        filterConfig.getFilters().addAll(filters);

        validateMissingKeywords();
        return filterConfig;
    }
    
    @Override
    public Set<String> getRequiredKeywords() {
        return new HashSet<>();
    }

    /////////////
    // PRIVATE //
    /////////////

    private DataFilter buildDataFilter(ConfigElement element) {
        DataFilter newFilter = new DataFilter();
        ConfigConverter converter = element.getConverter();
        Map<String, Object> properties = element.getValueAsMap();

        for(Entry<String, Object> property : properties.entrySet()) {
            String key = property.getKey();
            Object value = property.getValue();

            if(key.equals(Keywords.TYPE)) {
                newFilter.setType(converter.asString(value));
            } else if(key.equals(Keywords.EXPRESSION)) {
                newFilter.setExpression(converter.asString(value));
            } else if(key.equals(Keywords.POLICY)) {
                newFilter.setPolicy(converter.asString(value));
            }
        }

        return newFilter;
    }
}
