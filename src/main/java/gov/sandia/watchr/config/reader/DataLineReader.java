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
import gov.sandia.watchr.config.DataLine;
import gov.sandia.watchr.config.DerivativeLine;
import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.MetadataConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.util.RgbUtil;

public class DataLineReader extends AbstractExtractorConfigReader<List<DataLine>> {

    private final FileConfig fileConfig;

    public DataLineReader(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
    }

    @Override
    public Set<String> getRequiredKeywords() {
        Set<String> requiredKeywords = new HashSet<>();
        requiredKeywords.add(Keywords.X);
        requiredKeywords.add(Keywords.Y);
        return requiredKeywords;
    }

    @Override
    public List<DataLine> handle(JsonElement element, IConfig parent) {
        return handleAsDataLines(element, fileConfig, parent);
    }

    private List<DataLine> handleAsDataLines(JsonElement jsonElement, FileConfig fileConfig, IConfig parent) {
        List<DataLine> lines = new ArrayList<>();

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        for(int i = 0; i < jsonArray.size(); i++) {
            JsonElement arrayElement = jsonArray.get(i);
            JsonObject jsonObject = arrayElement.getAsJsonObject();
            Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();

            DataLine line = new DataLine(fileConfig, parent.getConfigPath() + "/" + Integer.toString(i));            

            for(Entry<String, JsonElement> entry : entrySet) {
                String key = entry.getKey();
                JsonElement value = entry.getValue();

                if(key.equals(Keywords.NAME)) {
                    seenKeywords.add(Keywords.NAME);
                    line.setName(value.getAsString());
                } else if(key.equals(Keywords.X)) {
                    seenKeywords.add(Keywords.X);
                    handleDataForExtractor(value, line.getXExtractor(), line);
                } else if(key.equals(Keywords.Y)) {
                    seenKeywords.add(Keywords.Y);
                    handleDataForExtractor(value, line.getYExtractor(), line);
                } else if(key.equals(Keywords.DERIVATIVE_LINES)) {
                    seenKeywords.add(Keywords.DERIVATIVE_LINES);
                    DerivativeLineReader derivativeLineReader = new DerivativeLineReader();
                    List<DerivativeLine> derivativeLines = derivativeLineReader.handle(value, line);
                    line.getDerivativeLines().addAll(derivativeLines);
                } else if (key.equals(Keywords.METADATA)) {
                    seenKeywords.add(Keywords.METADATA);
                    MetadataConfigReader metadataConfigReader = new MetadataConfigReader(fileConfig);
                    List<MetadataConfig> metadataList = metadataConfigReader.handle(value, line);
                    line.getMetadata().addAll(metadataList);
                } else if(key.equals(Keywords.COLOR)) {
                    seenKeywords.add(Keywords.COLOR);
                    line.setColor(RgbUtil.parseColor(value.getAsString()));
                } else {
                    ILogger logger = WatchrCoreApp.getInstance().getLogger();
                    logger.log(new WatchrConfigError(ErrorLevel.WARNING, "handleAsDataLines: Unrecognized element `" + key + "`."));
                }
            }

            if(line.getXExtractor() != null && line.getYExtractor() != null) {
                boolean isYRecursive = line.getYExtractor().getAmbiguityStrategy().shouldRecurseToChildGraphs();
                boolean isXRecursive = line.getXExtractor().getAmbiguityStrategy().shouldRecurseToChildGraphs();
                
                if(isYRecursive && isXRecursive) {
                    String warningMessage = "Recursion specified for both X and Y data. " +
                                            "This would lead to a combinatorial explosion of plots, which is " +
                                            "probably not what you want.  The Y dimension will take precedence " +
                                            "for recursively considering child data.";
                    ILogger logger = WatchrCoreApp.getInstance().getLogger();
                    logger.log(new WatchrConfigError(ErrorLevel.WARNING, warningMessage));
                }
            }
            lines.add(line);
        }
        
        validateMissingKeywords();
        return lines;
    }
}
