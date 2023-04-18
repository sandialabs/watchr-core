package gov.sandia.watchr.config.element.json;

import com.google.gson.JsonElement;

import gov.sandia.watchr.config.element.ConfigConverter;
import gov.sandia.watchr.config.element.ConfigElement;

public class JsonConfigConverter implements ConfigConverter {

    @Override
    public String asString(Object value) {
        if(value instanceof JsonElement) {
            return ((JsonElement)value).getAsString();
        } else {
            throw new IllegalStateException("Unrecognized data type " + value.getClass().getSimpleName());
        }
    }

    @Override
    public Integer asInt(Object value) {
        if(value instanceof JsonElement) {
            return ((JsonElement)value).getAsInt();
        } else {
            throw new IllegalStateException("Unrecognized data type " + value.getClass().getSimpleName());
        }
    }

    @Override
    public Double asDouble(Object value) {
        if(value instanceof JsonElement) {
            return ((JsonElement)value).getAsDouble();
        } else {
            throw new IllegalStateException("Unrecognized data type " + value.getClass().getSimpleName());
        }
    }

    @Override
    public Boolean asBoolean(Object value) {
        if(value instanceof JsonElement) {
            return ((JsonElement)value).getAsBoolean();
        } else {
            throw new IllegalStateException("Unrecognized data type " + value.getClass().getSimpleName());
        }
    }

    @Override
    public ConfigElement asChild(Object value) {
        if(value instanceof JsonElement) {
            return new JsonConfigElement((JsonElement)value);
        } else {
            throw new IllegalStateException("Unrecognized data type " + value.getClass().getSimpleName());
        }
    }
    
}
