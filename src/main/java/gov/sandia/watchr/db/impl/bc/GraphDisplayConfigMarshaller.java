package gov.sandia.watchr.db.impl.bc;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.GraphDisplayConfig.ExportMode;
import gov.sandia.watchr.config.GraphDisplayConfig.GraphDisplaySort;
import gov.sandia.watchr.config.GraphDisplayConfig.LeafNodeStrategy;
import gov.sandia.watchr.log.ILogger;

public class GraphDisplayConfigMarshaller implements JsonSerializer<Object>, JsonDeserializer<Object> {

    private static final String VERSION_META_KEY = "versionMetaKey";

    private static final String LEAF_STRATEGY = "leafStrategy";
    private static final String TRAVEL_UP_IF_EMPTY = "travelUpIfEmpty";

    private final ILogger logger;

    public GraphDisplayConfigMarshaller(ILogger logger) {
        this.logger = logger;
    }

    @Override
    public JsonElement serialize(Object object, Type type,
            JsonSerializationContext jsonSerializationContext) {

        JsonElement jsonEle = jsonSerializationContext.serialize(object, object.getClass());
        jsonEle.getAsJsonObject().addProperty(VERSION_META_KEY, WatchrCoreApp.getVersion());
        return jsonEle;
    }

    @Override
    public Object deserialize(JsonElement jsonElement, Type type,
            JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        JsonObject jsonObj = jsonElement.getAsJsonObject();
        JsonElement versionElement = jsonObj.get(VERSION_META_KEY);
        String version = WatchrCoreApp.getVersion();
        if(versionElement == null) {
            version = "1.4.0";
        }

        if(version.equals("1.4.0")) {
            JsonElement jsonTravelUpIfEmpty = jsonObj.get(TRAVEL_UP_IF_EMPTY);
            if(jsonTravelUpIfEmpty != null) {
                boolean travelUpIfEmpty = jsonTravelUpIfEmpty.getAsBoolean();
                if(travelUpIfEmpty) {
                    jsonObj.addProperty(LEAF_STRATEGY, LeafNodeStrategy.TRAVEL_UP_TO_PARENT.toString());
                } else {
                    jsonObj.addProperty(LEAF_STRATEGY, LeafNodeStrategy.SHOW_NOTHING.toString());
                }
                jsonObj.remove(TRAVEL_UP_IF_EMPTY);
            }
        }
        return doDeserialize(jsonElement);
    }
 
    private GraphDisplayConfig doDeserialize(JsonElement jsonElement) throws JsonParseException {
        JsonObject jsonObj = jsonElement.getAsJsonObject();

        String configPath = jsonObj.get("configPath").getAsString();
        String lastPlotDbLocation = jsonObj.get("lastPlotDbLocation").getAsString();
        String nextPlotDbLocation = jsonObj.get("nextPlotDbLocation").getAsString();
        int page = jsonObj.has("page") ? jsonObj.get("page").getAsInt() : -1;
        String displayCategory = jsonObj.has("displayCategory") ? jsonObj.get("displayCategory").getAsString() : "";
        int displayRange = jsonObj.has("displayRange") ? jsonObj.get("displayRange").getAsInt() : -1;
        int graphWidth = jsonObj.has("graphWidth") ? jsonObj.get("graphWidth").getAsInt() : -1;
        int graphHeight = jsonObj.has("graphHeight") ? jsonObj.get("graphHeight").getAsInt() : -1;
        int graphsPerRow = jsonObj.has("graphsPerRow") ? jsonObj.get("graphsPerRow").getAsInt() : -1;
        int graphsPerPage = jsonObj.has("graphsPerPage") ? jsonObj.get("graphsPerPage").getAsInt() : -1;
        int displayedDecimalPlaces = jsonObj.has("displayedDecimalPlaces") ? jsonObj.get("displayedDecimalPlaces").getAsInt() : -1;
        String leafStrategyStr = jsonObj.has(LEAF_STRATEGY) ? jsonObj.get(LEAF_STRATEGY).getAsString() : LeafNodeStrategy.TRAVEL_UP_TO_PARENT.toString();
        String sortStr = jsonObj.has("sort") ? jsonObj.get("sort").getAsString() : GraphDisplaySort.ASCENDING.toString();
        String exportModeStr = jsonObj.has("exportMode") ? jsonObj.get("exportMode").getAsString() : ExportMode.PER_CATEGORY.toString();

        GraphDisplayConfig newGraphDisplayConfig = new GraphDisplayConfig(configPath, logger);
        newGraphDisplayConfig.setLastPlotDbLocation(lastPlotDbLocation);
        newGraphDisplayConfig.setNextPlotDbLocation(nextPlotDbLocation);
        newGraphDisplayConfig.setPage(page);
        newGraphDisplayConfig.setDisplayCategory(displayCategory);
        newGraphDisplayConfig.setDisplayRange(displayRange);
        newGraphDisplayConfig.setGraphWidth(graphWidth);
        newGraphDisplayConfig.setGraphHeight(graphHeight);
        newGraphDisplayConfig.setGraphsPerRow(graphsPerRow);
        newGraphDisplayConfig.setGraphsPerPage(graphsPerPage);
        newGraphDisplayConfig.setDisplayedDecimalPlaces(displayedDecimalPlaces);

        newGraphDisplayConfig.setLeafNodeStrategy(leafStrategyStr);
        newGraphDisplayConfig.setSort(sortStr);
        newGraphDisplayConfig.setExportMode(exportModeStr);

        return newGraphDisplayConfig;
    }
}
