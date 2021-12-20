package gov.sandia.watchr.db.impl.bc;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import gov.sandia.watchr.config.file.DefaultFileReader;

public class FileReaderMarshaller implements JsonSerializer<Object>, JsonDeserializer<Object> {

    private static final String CLASS_META_KEY = "classMetaKey";

    @Override
    public Object deserialize(JsonElement jsonElement, Type type,
            JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException {

        JsonObject jsonObj = jsonElement.getAsJsonObject();
        JsonElement classMetaKey = jsonObj.get(CLASS_META_KEY);
        String className = "";
        if(classMetaKey != null) {
            className = classMetaKey.getAsString();
        } else {
            className = DefaultFileReader.class.getCanonicalName(); // This is a reasonable default for backwards-compatibility.
        }

        try {
            Class<?> clz = Class.forName(className);
            return jsonDeserializationContext.deserialize(jsonElement, clz);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(Object object, Type type,
            JsonSerializationContext jsonSerializationContext) {

        JsonElement jsonEle = jsonSerializationContext.serialize(object, object.getClass());
        jsonEle.getAsJsonObject().addProperty(CLASS_META_KEY, object.getClass().getCanonicalName());
        return jsonEle;
    }
}

