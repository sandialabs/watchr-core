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

import gov.sandia.watchr.config.FilterConfig;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;

public class FilterConfigReader extends AbstractConfigReader<FilterConfig> {

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public FilterConfig handle(JsonElement element, IConfig parent) {
        FilterConfig filterConfig = new FilterConfig(parent.getConfigPath());

        JsonObject jsonObject = element.getAsJsonObject();
        Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        List<PlotTracePoint> filterPoints = new ArrayList<>();

        for(Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if(key.equals(Keywords.X)) {
                seenKeywords.add(Keywords.X);
                for(String filterValue : handleAsArray(value)) {
                    filterPoints.add(new PlotTracePoint(filterValue, "", ""));
                }
            } else if(key.equals(Keywords.Y)) {
                seenKeywords.add(Keywords.Y);
                for(String filterValue : handleAsArray(value)) {
                    filterPoints.add(new PlotTracePoint("", filterValue, ""));
                }
            }
        }

        filterConfig.getFilterPoints().addAll(filterPoints);

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

    private List<String> handleAsArray(JsonElement element) {
        List<String> filterPoints = new ArrayList<>();
        JsonArray jsonArray = element.getAsJsonArray();
        for(int i = 0; i < jsonArray.size(); i++) {
            String nextFilterPoint = jsonArray.get(i).getAsString();
            filterPoints.add(nextFilterPoint);
        }
        return filterPoints;
    }
}
