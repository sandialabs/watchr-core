/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.extractors.strategy;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.io.FileUtils;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.extractors.ExtractionResult;

public class JsonExtractionStrategy extends ExtractionStrategy {

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public JsonExtractionStrategy(Map<String, String> properties, AmbiguityStrategy strategy) {
        super(properties, strategy);
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public List<ExtractionResult> extract(File targetFile) throws WatchrParseException {
        try {
            String jsonFileContents = FileUtils.readFileToString(targetFile, StandardCharsets.UTF_8);
            JsonElement root = new JsonParser().parse(jsonFileContents);
            Deque<String> stops = getPathStops();
            return getNextPathStop(stops, root, "");
        } catch(Exception e) {
            throw new WatchrParseException(e);
        }
    }
    
    /////////////
    // PRIVATE //
    /////////////

    private List<ExtractionResult> getNextPathStop(Deque<String> remainingStops, JsonElement element, String pathSoFar) {
        List<ExtractionResult> results = new ArrayList<>();

        if((!remainingStops.isEmpty())) {
            if(element instanceof JsonArray) {
                results.addAll(handleAsArray(remainingStops, element.getAsJsonArray(), pathSoFar));
            } else if(element instanceof JsonObject) {
                results.addAll(handleAsObject(remainingStops, element.getAsJsonObject(), pathSoFar));
            }
        } else if(remainingStops.isEmpty()) {
            ExtractionResult result = handleAsTargetValue(element, pathSoFar);
            if(result != null) {
                results.add(result);
            }
        }
        return results;
    }    

    private List<ExtractionResult> handleAsArray(Deque<String> remainingStops, JsonArray array, String pathSoFar) {
        List<ExtractionResult> results = new ArrayList<>();

        String nextStop = "";
        if(!remainingStops.isEmpty()) {
            nextStop = remainingStops.pop();
        }

        if(!strategy.shouldGetFirstMatchOnly() && !nextStopIsIndexSyntax(nextStop)) {
            for(int i = 0; i < array.size(); i++) {
                JsonElement arrayElement = array.get(i);
                if(remainingStops.isEmpty()) {
                    ExtractionResult result = handleAsTargetValue(arrayElement, pathSoFar);
                    if(result != null) {
                        results.add(result);
                    }
                } else {
                    results.addAll(getNextPathStop(remainingStops, arrayElement, pathSoFar));
                }
            }
        } else {
            if(nextStopIsIndexSyntax(nextStop)) {
                int indexValue = getIndexFromIndexSyntax(nextStop);
                JsonElement arrayElement = array.get(indexValue);
                results.addAll(getNextPathStop(remainingStops, arrayElement, pathSoFar));
            } else {
                ILogger logger = WatchrCoreApp.getInstance().getLogger();
                String message = "Given path " + path + ", could not determine where to go next in JSON hierarchy.  " +
                                 "Either provide index syntax (i.e. {1}) to speficy which JSON array element you want, " +
                                 "or configure your Watchr settings to consume all array elements by setting the getFirstMatchOnly " +
                                 "property to false.";
                logger.logWarning(message);
            }
        }
        return results;
    }

    private List<ExtractionResult> handleAsObject(Deque<String> remainingStops, JsonObject object, String pathSoFar) {
        List<ExtractionResult> results = new ArrayList<>();

        String nextStop = "*";
        if(!remainingStops.isEmpty()) {
            nextStop = remainingStops.pop();
        }
        nextStop = nextStop.replace("*", ".*");

        Set<Entry<String, JsonElement>> entries = object.entrySet();
        for(Entry<String, JsonElement> entry : entries) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if(key.matches(nextStop)) {
                if(remainingStops.isEmpty()) {
                    ExtractionResult result = handleAsTargetValue(value, pathSoFar + "/" + key);
                    results.add(result);
                } else {
                    results.addAll(getNextPathStop(remainingStops, value, pathSoFar + "/" + key));
                }
            }
        }

        return results;
    }

    private ExtractionResult handleAsTargetValue(JsonElement element, String pathSoFar) {
        if(element instanceof JsonObject) {
            JsonObject object = (JsonObject) element;
            Set<Entry<String, JsonElement>> entries = object.entrySet();
            for(Entry<String, JsonElement> entry : entries) {
                String thisKey = entry.getKey();
                if(thisKey.equals(key)) {
                    String value = entry.getValue().getAsString();
                    return new ExtractionResult(pathSoFar, key, value);
                }
            }
        }
        return null;
    }

    private boolean nextStopIsIndexSyntax(String str) {
        if(str.length() > 1) {
            boolean isIndexSyntax = str.charAt(0) == '{' && str.charAt(str.length()-1) == '}';
            String insideStr = str.substring(1, str.length()-1);
            isIndexSyntax = isIndexSyntax && !insideStr.contains("{") && !insideStr.contains("}");
            return isIndexSyntax;
        }
        return false;
    }

    private int getIndexFromIndexSyntax(String str) {
        String insideStr = str.substring(1, str.length()-1);
        return Integer.parseInt(insideStr);
    }
}
