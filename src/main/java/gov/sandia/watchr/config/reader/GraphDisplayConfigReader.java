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
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
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
    public GraphDisplayConfig handle(JsonElement element, IConfig parent) {
        GraphDisplayConfig graphDisplayConfig = new GraphDisplayConfig(parent.getConfigPath(), logger);

        JsonObject jsonObject = element.getAsJsonObject();
        Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        for(Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if(key.equals(Keywords.DB_LOCATION)) {
                graphDisplayConfig.setNextPlotDbLocation(value.getAsString());
            } else if(key.equals(Keywords.DISPLAY_CATEGORY)) {
                graphDisplayConfig.setDisplayCategory(value.getAsString());
            } else if(key.equals(Keywords.DISPLAY_RANGE)) {
                graphDisplayConfig.setDisplayRange(value.getAsInt());
            } else if(key.equals(Keywords.GRAPH_WIDTH)) {
                graphDisplayConfig.setGraphWidth(value.getAsInt());
            } else if(key.equals(Keywords.GRAPH_HEIGHT)) {
                graphDisplayConfig.setGraphHeight(value.getAsInt());
            } else if(key.equals(Keywords.GRAPHS_PER_ROW)) {
                graphDisplayConfig.setGraphsPerRow(value.getAsInt());
            } else if(key.equals(Keywords.GRAPHS_PER_PAGE)) {
                graphDisplayConfig.setGraphsPerPage(value.getAsInt());
            } else if(key.equals(Keywords.DISPLAYED_DECIMAL_PLACES)) {
                graphDisplayConfig.setDisplayedDecimalPlaces(value.getAsInt());
            } else if(key.equals(Keywords.SORT)) {
                graphDisplayConfig.setSort(value.getAsString());
            } else if(key.equals(Keywords.EXPORT_MODE)) {
                graphDisplayConfig.setExportMode(value.getAsString());
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
