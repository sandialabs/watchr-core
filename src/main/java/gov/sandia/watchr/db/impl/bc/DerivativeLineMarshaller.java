package gov.sandia.watchr.db.impl.bc;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import gov.sandia.watchr.config.derivative.AverageDerivativeLine;
import gov.sandia.watchr.config.derivative.StdDevPositiveOffsetDerivativeLine;

public class DerivativeLineMarshaller implements JsonSerializer<Object>, JsonDeserializer<Object> {

    private static final String CLASS_META_KEY = "classMetaKey";
    private static final String CONFIG_PATH = "configPath";

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
            // Backwards-compatibility for derivative lines before version 1.3.0.
            JsonElement configPath = jsonObj.get(CONFIG_PATH);
            if(configPath != null) {
                String configPathFullStr = configPath.getAsString();
                String[] pathTokens = configPathFullStr.split("/");
                String classClue = pathTokens[pathTokens.length-1];
                if(classClue.equalsIgnoreCase("average")) {
                    className = AverageDerivativeLine.class.getCanonicalName();
                } else if(classClue.equalsIgnoreCase("STANDARD_DEVIATION_OFFSET") ||
                          classClue.equalsIgnoreCase("stdDevPositiveOffset")) {
                    className = StdDevPositiveOffsetDerivativeLine.class.getCanonicalName();
                } else {
                    throw new UnsupportedOperationException("Could not identify derivative line " + classClue);
                }
            } 
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
