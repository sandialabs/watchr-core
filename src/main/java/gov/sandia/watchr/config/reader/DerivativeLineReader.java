package gov.sandia.watchr.config.reader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.DerivativeLine;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.DerivativeLine.DerivativeLineType;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.util.RgbUtil;

public class DerivativeLineReader extends AbstractExtractorConfigReader<List<DerivativeLine>> {

    @Override
    public Set<String> getRequiredKeywords() {
        Set<String> requiredKeywords = new HashSet<>();
        requiredKeywords.add(Keywords.TYPE);
        return requiredKeywords;
    }

    @Override
    public List<DerivativeLine> handle(JsonElement element, IConfig parent) {
        JsonArray jsonArray = element.getAsJsonArray();
        List<DerivativeLine> derivativeLines = new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++) {
            JsonElement arrayElement = jsonArray.get(i);
            JsonObject jsonObject = arrayElement.getAsJsonObject();
            Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();

            DerivativeLine derivativeLine = new DerivativeLine(parent.getConfigPath() + "/" + Integer.toString(i));
            for(Entry<String, JsonElement> entry : entrySet) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();

                if(key.equals(Keywords.TYPE)) {
                    seenKeywords.add(Keywords.TYPE);
                    if(value.getAsString().equalsIgnoreCase("average")) {
                        derivativeLine.setType(DerivativeLineType.AVERAGE);
                    } else if(value.getAsString().equalsIgnoreCase("standardDeviation")) {
                        derivativeLine.setType(DerivativeLineType.STANDARD_DEVIATION);
                    } else if(value.getAsString().equalsIgnoreCase("standardDeviationOffset")) {
                        derivativeLine.setType(DerivativeLineType.STANDARD_DEVIATION_OFFSET);
                    }
                } else if(key.equals(Keywords.RANGE)) {
                    seenKeywords.add(Keywords.RANGE);
                    derivativeLine.setRollingRange((int) value.getAsLong());
                } else if(key.equals(Keywords.COLOR)) {
                    seenKeywords.add(Keywords.COLOR);
                    derivativeLine.setColor(RgbUtil.parseColor(value.getAsString()));
                } else if(key.equals(Keywords.IGNORE_FILTERED_DATA)) {
                    seenKeywords.add(Keywords.IGNORE_FILTERED_DATA);
                    derivativeLine.setIgnoreFilteredData(value.getAsBoolean());
                } else {
                    ILogger logger = WatchrCoreApp.getInstance().getLogger();
                    logger.log(new WatchrConfigError(ErrorLevel.WARNING, "handleDataForDerivativeLines: Unrecognized element `" + key + "`."));
                }
            }
            derivativeLines.add(derivativeLine);
        }

        validateMissingKeywords();
        return derivativeLines;
    }
    
}
