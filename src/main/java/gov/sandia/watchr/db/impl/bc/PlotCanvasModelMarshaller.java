package gov.sandia.watchr.db.impl.bc;

import java.lang.reflect.Type;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.util.RGB;

public class PlotCanvasModelMarshaller implements JsonSerializer<Object>, JsonDeserializer<Object> {

    private static final String VERSION_META_KEY = "versionMetaKey";
    private static final String DRAW_AXIS_LINES = "drawAxisLines";
    private static final String DRAW_X_AXIS_LINES = "drawXAxisLines";
    private static final String DRAW_Y_AXIS_LINES = "drawYAxisLines";
    private static final String DRAW_Z_AXIS_LINES = "drawZAxisLines";

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
            version = "1.3.0";
        }

        if(version.equals("1.3.0")) {
            JsonElement drawAxisLines = jsonObj.get(DRAW_AXIS_LINES);
            if(drawAxisLines != null) {
                boolean shouldDrawAxisLines = drawAxisLines.getAsBoolean();
                jsonObj.addProperty(DRAW_X_AXIS_LINES, Boolean.toString(shouldDrawAxisLines));
                jsonObj.addProperty(DRAW_Y_AXIS_LINES, Boolean.toString(shouldDrawAxisLines));
                jsonObj.addProperty(DRAW_Z_AXIS_LINES, Boolean.toString(shouldDrawAxisLines));
                jsonObj.remove(DRAW_AXIS_LINES);
            }
        }
        return doDeserialize(jsonElement, jsonDeserializationContext);
    }

    private PlotCanvasModel doDeserialize(
            JsonElement jsonElement, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        JsonObject jsonObj = jsonElement.getAsJsonObject();
        String uuid = jsonObj.get("uuid").getAsString();
        String parentWindowModelUUID = jsonObj.get("parentWindowModelUUID").getAsString();
        String name = jsonObj.has("name") ? jsonObj.get("name").getAsString() : "";
        int rowPosition = jsonObj.has("rowPosition") ? jsonObj.get("rowPosition").getAsInt() : 0;
        int colPosition = jsonObj.has("colPosition") ? jsonObj.get("colPosition").getAsInt() : 0;
        int axisPrecision = jsonObj.has("axisPrecision") ? jsonObj.get("axisPrecision").getAsInt() : PlotCanvasModel.DEFAULT_CANVAS_DECIMAL_PRECISION;
        String xAxisLabel = jsonObj.has("xAxisLabel") ? jsonObj.get("xAxisLabel").getAsString() : "";
        String yAxisLabel = jsonObj.has("yAxisLabel") ? jsonObj.get("yAxisLabel").getAsString() : "";
        String zAxisLabel = jsonObj.has("zAxisLabel") ? jsonObj.get("zAxisLabel").getAsString() : "";
        JsonElement xAxisRGB = jsonObj.get("xAxisRGB");
        JsonElement yAxisRGB = jsonObj.get("yAxisRGB");
        JsonElement zAxisRGB = jsonObj.get("zAxisRGB");
        double xAxisRangeStart = jsonObj.has("xAxisRangeStart") ? jsonObj.get("xAxisRangeStart").getAsDouble() : 0.0;
        double yAxisRangeStart = jsonObj.has("yAxisRangeStart") ? jsonObj.get("yAxisRangeStart").getAsDouble() : 0.0;
        double zAxisRangeStart = jsonObj.has("zAxisRangeStart") ? jsonObj.get("zAxisRangeStart").getAsDouble() : 0.0;
        double xAxisRangeEnd = jsonObj.has("xAxisRangeEnd") ? jsonObj.get("xAxisRangeEnd").getAsDouble() : 0.0;
        double yAxisRangeEnd = jsonObj.has("yAxisRangeEnd") ? jsonObj.get("yAxisRangeEnd").getAsDouble() : 0.0;
        double zAxisRangeEnd = jsonObj.has("zAxisRangeEnd") ? jsonObj.get("zAxisRangeEnd").getAsDouble() : 0.0;
        boolean autoscale = jsonObj.has("autoscale") ? jsonObj.get("autoscale").getAsBoolean() : true;
        boolean xLogScale = jsonObj.has("xLogScale") ? jsonObj.get("xLogScale").getAsBoolean() : false;
        boolean yLogScale = jsonObj.has("yLogScale") ? jsonObj.get("yLogScale").getAsBoolean() : false;
        boolean zLogScale = jsonObj.has("zLogScale") ? jsonObj.get("zLogScale").getAsBoolean() : false;
        boolean drawGridLines = jsonObj.has("drawGridLines") ? jsonObj.get("drawGridLines").getAsBoolean() : true;
        boolean drawXAxisLines = jsonObj.has("drawXAxisLines") ? jsonObj.get("drawXAxisLines").getAsBoolean() : true;
        boolean drawYAxisLines = jsonObj.has("drawYAxisLines") ? jsonObj.get("drawYAxisLines").getAsBoolean() : true;
        boolean drawZAxisLines = jsonObj.has("drawZAxisLines") ? jsonObj.get("drawZAxisLines").getAsBoolean() : true;
        boolean drawXAxisLabels = jsonObj.has("drawXAxisLabels") ? jsonObj.get("drawXAxisLabels").getAsBoolean() : true;
        boolean drawYAxisLabels = jsonObj.has("drawYAxisLabels") ? jsonObj.get("drawYAxisLabels").getAsBoolean() : true;
        boolean drawZAxisLabels = jsonObj.has("drawZAxisLabels") ? jsonObj.get("drawZAxisLabels").getAsBoolean() : true;

        PlotCanvasModel newPlotCanvasModel = new PlotCanvasModel(UUID.fromString(uuid), UUID.fromString(parentWindowModelUUID), false);
        newPlotCanvasModel.setName(name);
        newPlotCanvasModel.setRowPosition(rowPosition);
        newPlotCanvasModel.setColPosition(colPosition);
        newPlotCanvasModel.setAxisPrecision(axisPrecision);
        newPlotCanvasModel.setXAxisLabel(xAxisLabel);
        newPlotCanvasModel.setYAxisLabel(yAxisLabel);
        newPlotCanvasModel.setZAxisLabel(zAxisLabel);
        newPlotCanvasModel.setXAxisRGB(jsonDeserializationContext.deserialize(xAxisRGB, RGB.class));
        newPlotCanvasModel.setYAxisRGB(jsonDeserializationContext.deserialize(yAxisRGB, RGB.class));
        newPlotCanvasModel.setZAxisRGB(jsonDeserializationContext.deserialize(zAxisRGB, RGB.class));
        newPlotCanvasModel.setXAxisRangeStart(xAxisRangeStart);
        newPlotCanvasModel.setYAxisRangeStart(yAxisRangeStart);
        newPlotCanvasModel.setZAxisRangeStart(zAxisRangeStart);
        newPlotCanvasModel.setXAxisRangeEnd(xAxisRangeEnd);
        newPlotCanvasModel.setYAxisRangeEnd(yAxisRangeEnd);
        newPlotCanvasModel.setZAxisRangeEnd(zAxisRangeEnd);
        newPlotCanvasModel.setAutoscale(autoscale);
        newPlotCanvasModel.setXLogScale(xLogScale);
        newPlotCanvasModel.setYLogScale(yLogScale);
        newPlotCanvasModel.setZLogScale(zLogScale);
        newPlotCanvasModel.setDrawGridLines(drawGridLines);
        newPlotCanvasModel.setDrawXAxisLines(drawXAxisLines);
        newPlotCanvasModel.setDrawYAxisLines(drawYAxisLines);
        newPlotCanvasModel.setDrawZAxisLines(drawZAxisLines);
        newPlotCanvasModel.setDrawXAxisLabels(drawXAxisLabels);
        newPlotCanvasModel.setDrawYAxisLabels(drawYAxisLabels);
        newPlotCanvasModel.setDrawZAxisLabels(drawZAxisLabels);

        JsonArray overlaidCanvasModels = jsonObj.get("overlaidCanvasModels").getAsJsonArray();
        JsonArray traceModels = jsonObj.get("traceModels").getAsJsonArray();

        for(JsonElement overlaidCanvas : overlaidCanvasModels) {
            newPlotCanvasModel.addOverlaidCanvasModel(
                jsonDeserializationContext.deserialize(overlaidCanvas, PlotCanvasModel.class)
            );
        }
        for(JsonElement traceModel : traceModels) {
            newPlotCanvasModel.addTraceModel(
                jsonDeserializationContext.deserialize(traceModel, PlotTraceModel.class)
            );
        }

        return newPlotCanvasModel;
    }
}
