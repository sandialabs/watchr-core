package gov.sandia.watchr.config.element.yaml;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import gov.sandia.watchr.config.element.ConfigConverter;
import gov.sandia.watchr.config.element.ConfigElement;

public class YamlConfigElement implements ConfigElement {

    private final Object yamlElement;

    public YamlConfigElement(Object yamlElement) {
        this.yamlElement = yamlElement;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Object> getValueAsList() {
        return (List<Object>) yamlElement;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getValueAsMap() {
        Map<String, Object> newMap = new LinkedHashMap<>();

        Map<String, ?> map = (Map<String, ?>) yamlElement;
        for(Entry<String, ?> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            newMap.put(key, value);
        }
        return newMap;
    }

    @Override
    public ConfigConverter getConverter() {
        return new YamlConfigConverter();
    }
    
}
