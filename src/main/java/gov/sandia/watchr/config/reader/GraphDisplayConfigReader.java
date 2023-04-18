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

import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.element.ConfigConverter;
import gov.sandia.watchr.config.element.ConfigElement;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;

public class GraphDisplayConfigReader extends AbstractConfigReader<GraphDisplayConfig> {
    
    protected GraphDisplayConfigReader(ILogger logger) {
        super(logger);
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public GraphDisplayConfig handle(ConfigElement element, IConfig parent) {
        GraphDisplayConfig graphDisplayConfig = new GraphDisplayConfig(parent.getConfigPath(), logger);
        ConfigConverter converter = element.getConverter();

        Map<String, Object> map = element.getValueAsMap();
        for(Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if(key.equals(Keywords.DB_LOCATION)) {
                graphDisplayConfig.setNextPlotDbLocation(converter.asString(value));
            } else if(key.equals(Keywords.DISPLAY_CATEGORY)) {
                graphDisplayConfig.setDisplayCategory(converter.asString(value));
            } else if(key.equals(Keywords.DISPLAY_RANGE)) {
                graphDisplayConfig.setDisplayRange(converter.asInt(value));
            } else if(key.equals(Keywords.GRAPH_WIDTH)) {
                graphDisplayConfig.setGraphWidth(converter.asInt(value));
            } else if(key.equals(Keywords.GRAPH_HEIGHT)) {
                graphDisplayConfig.setGraphHeight(converter.asInt(value));
            } else if(key.equals(Keywords.GRAPHS_PER_ROW)) {
                graphDisplayConfig.setGraphsPerRow(converter.asInt(value));
            } else if(key.equals(Keywords.GRAPHS_PER_PAGE)) {
                graphDisplayConfig.setGraphsPerPage(converter.asInt(value));
            } else if(key.equals(Keywords.DISPLAYED_DECIMAL_PLACES)) {
                graphDisplayConfig.setDisplayedDecimalPlaces(converter.asInt(value));
            } else if(key.equals(Keywords.SORT)) {
                graphDisplayConfig.setSort(converter.asString(value));
            } else if(key.equals(Keywords.EXPORT_MODE)) {
                graphDisplayConfig.setExportMode(converter.asString(value));
            } else if(key.equals(Keywords.SEARCH_QUERY)) {
                graphDisplayConfig.setSearchQuery(converter.asString(value));
            } else {
                logger.log(new WatchrConfigError(ErrorLevel.WARNING, "handleAsGraphDisplayConfig: Unrecognized element `" + key + "`."));
            }
        }

        validateMissingKeywords();
        return graphDisplayConfig;
    }

    @Override
    public Set<String> getRequiredKeywords() {
        return new HashSet<>();
    }
}
