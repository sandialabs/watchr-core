/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.extractors.strategy;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.sandia.watchr.config.HierarchicalExtractor;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.extractors.ExtractionResult;

public abstract class ExtractionStrategy {
    
    protected final Map<String, String> properties;

    protected final AmbiguityStrategy strategy;
    protected final String path;
    protected final String key;

    protected ExtractionStrategy(Map<String, String> properties, AmbiguityStrategy strategy) {
        this.properties = new HashMap<>();
        this.properties.putAll(properties);

        this.path = properties.getOrDefault(Keywords.GET_PATH, "");
        this.key = properties.getOrDefault(Keywords.GET_KEY, "");

        this.strategy = strategy;
    }

    protected Deque<String> getPathStops() {
        Deque<String> stack = new ArrayDeque<>();
        String[] stops = path.split(HierarchicalExtractor.PATH_SEPARATOR);
        for(int i = stops.length - 1; i >= 0; i--) {
            stack.push(stops[i]);
        }
        return stack;
    }

    public abstract List<ExtractionResult> extract(File targetFile) throws WatchrParseException;
}
