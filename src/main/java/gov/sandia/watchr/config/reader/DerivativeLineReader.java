package gov.sandia.watchr.config.reader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.derivative.DerivativeLine;
import gov.sandia.watchr.config.derivative.DerivativeLineFactory;
import gov.sandia.watchr.config.derivative.DerivativeLineType;
import gov.sandia.watchr.config.derivative.RollingDerivativeLine;
import gov.sandia.watchr.config.derivative.SlopeDerivativeLine;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.util.RGB;
import gov.sandia.watchr.util.RgbUtil;

public class DerivativeLineReader extends AbstractExtractorConfigReader<List<DerivativeLine>> {

    public DerivativeLineReader(ILogger logger) {
        super(logger);
    }

    //////////////
    // OVERRIDE //
    //////////////

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

            String derivativeLinePath = parent.getConfigPath() + "/" + Integer.toString(i);
            DerivativeLine newDerivativeLine = buildDerivativeLine(derivativeLinePath, entrySet);
            derivativeLines.add(newDerivativeLine);
        }

        validateMissingKeywords();
        return derivativeLines;
    }

    /////////////
    // PRIVATE //
    /////////////
    
    private DerivativeLine buildDerivativeLine(String path, Set<Entry<String, JsonElement>> entrySet) {
        DerivativeLineType type = null;
        int rollingRange = 0;
        RGB color = new RGB(0,0,0);
        boolean ignoreFilteredData = false;
        String name = "";
        String numberFormat = "#.####";
        String xExpression = "";
        String yExpression = "";

        for(Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if(key.equals(Keywords.TYPE)) {
                seenKeywords.add(Keywords.TYPE);
                if(value.getAsString().equalsIgnoreCase("average")) {
                    type = DerivativeLineType.AVERAGE;
                } else if(value.getAsString().equalsIgnoreCase("standardDeviation")) {
                    type = DerivativeLineType.STANDARD_DEVIATION;
                } else if(value.getAsString().equalsIgnoreCase("standardDeviationOffset")) {
                    type = DerivativeLineType.STANDARD_DEVIATION_OFFSET;
                } else if(value.getAsString().equalsIgnoreCase("standardDeviationNegativeOffset")) {
                    type = DerivativeLineType.STANDARD_DEVIATION_NEG_OFFSET;
                } else if(value.getAsString().equalsIgnoreCase("slope")) {
                    type = DerivativeLineType.SLOPE;
                }
            } else if(key.equals(Keywords.NAME)) {
                seenKeywords.add(Keywords.NAME);
                name = value.getAsString();
            } else if(key.equals(Keywords.RANGE)) {
                seenKeywords.add(Keywords.RANGE);
                rollingRange = (int) value.getAsLong();
            } else if(key.equals(Keywords.COLOR)) {
                seenKeywords.add(Keywords.COLOR);
                color = RgbUtil.parseColor(value.getAsString());
            } else if(key.equals(Keywords.IGNORE_FILTERED_DATA)) {
                seenKeywords.add(Keywords.IGNORE_FILTERED_DATA);
                ignoreFilteredData = value.getAsBoolean();
            } else if(key.equals(Keywords.NUMBER_FORMAT)) {
                seenKeywords.add(Keywords.NUMBER_FORMAT);
                numberFormat = value.getAsString();
            } else if(key.equals(Keywords.X)) {
                seenKeywords.add(Keywords.X);
                xExpression = value.getAsString();
            } else if(key.equals(Keywords.Y)) {
                seenKeywords.add(Keywords.Y);
                yExpression = value.getAsString();
            } else {
                logger.log(new WatchrConfigError(ErrorLevel.WARNING, "handleDataForDerivativeLines: Unrecognized element `" + key + "`."));
            }
        }

        DerivativeLine derivativeLine = DerivativeLineFactory.getInstance().create(type, path, logger);

        if(type == DerivativeLineType.AVERAGE ||
           type == DerivativeLineType.STANDARD_DEVIATION ||
           type == DerivativeLineType.STANDARD_DEVIATION_OFFSET ||
           type == DerivativeLineType.STANDARD_DEVIATION_NEG_OFFSET) {
            ((RollingDerivativeLine)derivativeLine).setRollingRange(rollingRange);
            ((RollingDerivativeLine)derivativeLine).setIgnoreFilteredData(ignoreFilteredData);
            ((RollingDerivativeLine)derivativeLine).setNumberFormat(numberFormat);
        } else if(type == DerivativeLineType.SLOPE) {
            derivativeLine = new SlopeDerivativeLine(path, logger);
            ((SlopeDerivativeLine)derivativeLine).setXExpression(xExpression);
            ((SlopeDerivativeLine)derivativeLine).setYExpression(yExpression);
        }

        if(derivativeLine != null) {
            derivativeLine.setName(name);
            derivativeLine.setColor(color);
        }
        return derivativeLine;
    }
}
