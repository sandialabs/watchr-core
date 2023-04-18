package gov.sandia.watchr.config.element.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import gov.sandia.watchr.config.element.ConfigConverter;
import gov.sandia.watchr.config.element.ConfigElement;

public class JsonConfigElement implements ConfigElement {

    private final JsonElement jsonElement;

    public JsonConfigElement(JsonElement jsonElement) {
        this.jsonElement = jsonElement;
    }

    @Override
    public List<Object> getValueAsList() {
        List<Object> list = new ArrayList<>();
        JsonArray array = jsonElement.getAsJsonArray();
        for(int i = 0; i < array.size(); i++) {
            list.add(array.get(i));
        }
        return list;
    }

    @Override
    public Map<String, Object> getValueAsMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        for(Entry<String, JsonElement> element : jsonElement.getAsJsonObject().entrySet()) {
            map.put(element.getKey(), element.getValue());
        }
        return map;
    }

    @Override
    public ConfigConverter getConverter() {
        return new JsonConfigConverter();
    }
}
