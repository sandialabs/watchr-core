/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.extractors.strategy;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.extractors.ExtractionResult;
import gov.sandia.watchr.parse.extractors.ExtractionResultIndexParser;
import gov.sandia.watchr.util.StringUtil;

public class JsonExtractionStrategy extends ExtractionStrategy<JsonElement> {

    ////////////
    // FIELDS //
    ////////////

    private String fileAbsPath;
    private ExtractionResultIndexParser indexParser;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public JsonExtractionStrategy(
            Map<String, String> properties, AmbiguityStrategy strategy, ILogger logger, IFileReader fileReader) {
        super(properties, strategy, logger, fileReader);
        indexParser = new ExtractionResultIndexParser();
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public List<ExtractionResult> extract(String fileAbsPath) throws WatchrParseException {
        this.fileAbsPath = fileAbsPath;
      
        String jsonFileContents = fileReader.readFromFile(fileAbsPath);
        JsonElement root = JsonParser.parseString(jsonFileContents);
        Deque<String> stops = getPathStops();
        return getNextPathStop("", stops, root);
    }     

    @Override
    protected List<ExtractionResult> getNextPathStop(String pathSoFar, Deque<String> remainingStops, JsonElement element) {
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
    
    /////////////
    // PRIVATE //
    /////////////    

    private List<ExtractionResult> handleAsArray(Deque<String> remainingStops, JsonArray array, String pathSoFar) {
        List<ExtractionResult> results = new ArrayList<>();

        String nextStop = "";
        String poppedNextStop = "";
        if(!remainingStops.isEmpty()) {
            poppedNextStop = remainingStops.pop();
            nextStop = poppedNextStop;
        }

        if(!strategy.shouldGetFirstMatchOnly()) {
            if(indexParser.isIndexRangeSyntax(nextStop)) {
                results.addAll(handleAsArrayAndGetRangeOfElements(remainingStops, nextStop, array, pathSoFar));
            } else {
                results.addAll(handleAsArrayAndGetAllElements(remainingStops, array, pathSoFar));
            }  
        } else {
            results.addAll(handleAsArrayAndGetSingleElement(remainingStops, nextStop, array, pathSoFar));
        }

        remainingStops.push(poppedNextStop);
        return results;
    }

    private List<ExtractionResult> handleAsArrayAndGetRangeOfElements(
            Deque<String> remainingStops, String nextStop, JsonArray array, String pathSoFar) {

        List<ExtractionResult> results = new ArrayList<>();
        Pair<Integer, Integer> range = indexParser.getRangeFromIndexRangeSyntax(nextStop, array.size());
        if(range != null) {
            int start = range.getLeft();
            int end = range.getRight() + 1;
            for(int i = start; i < end; i++) {
                JsonElement arrayElement = array.get(i);
                List<ExtractionResult> arrayResults = getNextPathStop(pathSoFar, remainingStops, arrayElement);
                if(arrayResults.isEmpty() && StringUtils.isNotBlank(strategy.getIterateWithOtherExtractor())) {
                    results.add(null);
                } else {
                    results.addAll(arrayResults);
                }
            }
        }
        return results;
    }

    private List<ExtractionResult> handleAsArrayAndGetAllElements(Deque<String> remainingStops, JsonArray array, String pathSoFar) {
        List<ExtractionResult> results = new ArrayList<>();
        for(int i = 0; i < array.size(); i++) {
            JsonElement arrayElement = array.get(i);
            if(remainingStops.isEmpty()) {
                ExtractionResult result = handleAsTargetValue(arrayElement, pathSoFar);
                if(result != null || StringUtils.isNotBlank(strategy.getIterateWithOtherExtractor())) {
                    results.add(result);
                }
            } else {
                results.addAll(getNextPathStop(pathSoFar, remainingStops, arrayElement));
            }
        }
        return results;
    }

    private List<ExtractionResult> handleAsArrayAndGetSingleElement(
            Deque<String> remainingStops, String nextStop, JsonArray array, String pathSoFar) {
        List<ExtractionResult> results = new ArrayList<>();

        if(indexParser.isIndexSyntax(nextStop)) {
            int indexValue = indexParser.getIndexFromIndexSyntax(nextStop);
            JsonElement arrayElement = array.get(indexValue);
            results.addAll(getNextPathStop(pathSoFar, remainingStops, arrayElement));
        } else if(array.size() > 0) {
            JsonElement arrayElement = array.get(0);
            List<ExtractionResult> arrayResults = getNextPathStop(pathSoFar, remainingStops, arrayElement);
            if(arrayResults.isEmpty() && StringUtils.isNotBlank(strategy.getIterateWithOtherExtractor())) {
                results.add(null);
            } else {
                results.addAll(arrayResults);
            }
        } else {
            String message = "Given path " + path + ", could not determine where to go next in JSON hierarchy.  " +
                             "Either provide index syntax (i.e. {1}) to speficy which JSON array element you want, " +
                             "or configure your Watchr settings to consume all array elements by setting the getFirstMatchOnly " +
                             "property to false.";
            logger.logWarning(message);
        }

        return results;
    }

    private List<ExtractionResult> handleAsObject(Deque<String> remainingStops, JsonObject object, String pathSoFar) {
        List<ExtractionResult> results = new ArrayList<>();

        String nextStop = "*";
        String poppedNextStop = "";
        if(!remainingStops.isEmpty()) {
            poppedNextStop = remainingStops.pop();
            nextStop = poppedNextStop;
        }
        nextStop = StringUtil.convertToRegex(nextStop);

        boolean indexSyntax =
            indexParser.isIndexSyntax(nextStop) ||
            indexParser.isIndexRangeSyntax(nextStop);
        
        List<Entry<String, JsonElement>> entries = new ArrayList<>(object.entrySet());
        int start = 0;
        int end = entries.size();

        if(indexSyntax) {
            if(indexParser.isIndexSyntax(nextStop)) {
                start = indexParser.getIndexFromIndexSyntax(nextStop);
                end = start + 1;
            } else { // indexParser.isIndexRangeSyntax(nextStop)
                Pair<Integer, Integer> range = indexParser.getRangeFromIndexRangeSyntax(nextStop, entries.size());
                if(range != null) {
                    start = range.getLeft();
                    end = range.getRight() + 1;
                }
            }
        }

        for(int i = start; i < end; i++) {
            if(i < entries.size()) {
                Entry<String, JsonElement> entry = entries.get(i);
                String key = entry.getKey();
                String newPathSoFar = pathSoFar + "/" + key;
                JsonElement value = entry.getValue();
                if(indexSyntax || key.matches(nextStop)) {
                    if(remainingStops.isEmpty()) {
                        ExtractionResult result = handleAsTargetValue(value, newPathSoFar);
                        if(result != null || StringUtils.isNotBlank(strategy.getIterateWithOtherExtractor())) {
                            results.add(result);
                        }
                    } else {
                        List<ExtractionResult> childResults = getNextPathStop(newPathSoFar, remainingStops, value);
                        if(childResults.isEmpty() && StringUtils.isNotBlank(strategy.getIterateWithOtherExtractor())) {
                            results.add(null);
                        } else {
                            results.addAll(childResults);
                        }
                    }
                }
            }
        }
        remainingStops.push(poppedNextStop);
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
                    return new ExtractionResult(fileAbsPath, pathSoFar, key, value);
                }
            }
        }
        return null;
    }
}
