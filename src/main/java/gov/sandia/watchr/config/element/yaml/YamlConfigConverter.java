package gov.sandia.watchr.config.element.yaml;

import gov.sandia.watchr.config.element.ConfigConverter;
import gov.sandia.watchr.config.element.ConfigElement;

public class YamlConfigConverter implements ConfigConverter {

    @Override
    public String asString(Object value) {
        if(value instanceof String) {
            return (String) value;
        } else {
            throw new IllegalStateException("Unrecognized data type " + value.getClass().getSimpleName());
        }
    }

    @Override
    public Integer asInt(Object value) {
        if(value instanceof Integer) {
            return (Integer) value;
        } else {
            throw new IllegalStateException("Unrecognized data type " + value.getClass().getSimpleName());
        }
    }

    @Override
    public Double asDouble(Object value) {
        if(value instanceof Double) {
            return (Double) value;
        } else {
            throw new IllegalStateException("Unrecognized data type " + value.getClass().getSimpleName());
        }
    }

    @Override
    public Boolean asBoolean(Object value) {
        if(value instanceof Boolean) {
            return (Boolean) value;
        } else {
            throw new IllegalStateException("Unrecognized data type " + value.getClass().getSimpleName());
        }
    }

    @Override
    public ConfigElement asChild(Object value) {
        return new YamlConfigElement(value);
    }
    
}
