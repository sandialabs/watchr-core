/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config.reader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.PlotsConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;

public class WatchrConfigJsonReader extends AbstractConfigReader<WatchrConfig> {

    ////////////
    // FIELDS //
    ////////////

    private final File startDir;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WatchrConfigJsonReader(File startDir) {
        this.startDir = startDir;
    }

    ////////////
    // PUBLIC //
    ////////////

    /**
     * Convert a JSON {@link File} to a {@link PlotsConfig} object.
     * 
     * @param jsonFile The JSON file.
     * @return The PlotConfig containing all the data from the JSON.
     * @throws IOException Thrown if there was an error reading the file.
     */
    public WatchrConfig deserialize(File jsonFile) throws IOException {
        return deserialize(FileUtils.readFileToString(jsonFile, StandardCharsets.UTF_8));
    }

    /**
     * Convert an {@link InputStream} from a JSON file to a {@link PlotsConfig}
     * object.
     * 
     * @param is The input stream of the JSON file.
     * @return The PlotConfig containing all the data from the JSON.
     * @throws IOException Thrown if there was an error reading the file.
     */
    public WatchrConfig deserialize(InputStream is) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, StandardCharsets.UTF_8); // UTF-8 is a safe assumption
        return deserialize(writer.toString());
    }

    /**
     * Convert a {@link String} of JSON file contents to a {@link PlotsConfig}
     * object.
     * 
     * @param jsonFileContents The JSON file contents.
     * @return The PlotConfig containing all the data from the JSON.
     */
    public WatchrConfig deserialize(String jsonFileContents) {
        
        JsonObject jsonObject = new JsonParser().parse(jsonFileContents).getAsJsonObject();
        return handle(jsonObject, null);
    }
     
    @Override
    public WatchrConfig handle(JsonElement element, IConfig parent) {
        JsonObject jsonObject = element.getAsJsonObject();
        WatchrConfig watchrConfig = new WatchrConfig();

        Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
        for (Entry<String, JsonElement> entry : entrySet) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();
            if(key.equals(Keywords.PLOTS)) {
                seenKeywords.add(Keywords.PLOTS);
                PlotsConfigReader plotsConfigReader = new PlotsConfigReader(startDir);
                watchrConfig.setPlotsConfig(plotsConfigReader.handle(value, watchrConfig));
            } else if (key.equals(Keywords.GRAPH_DISPLAY)) {
                seenKeywords.add(Keywords.GRAPH_DISPLAY);
                GraphDisplayConfigReader graphDisplayConfigReader = new GraphDisplayConfigReader();
                watchrConfig.setGraphDisplayConfig(graphDisplayConfigReader.handle(value, watchrConfig));
            } else {
                ILogger logger = WatchrCoreApp.getInstance().getLogger();
                logger.log(new WatchrConfigError(ErrorLevel.WARNING, "WatchrConfigJsonReader: Unrecognized element `" + key + "`."));
            }
        }
        validateMissingKeywords();

        return watchrConfig;
    }

    @Override
    public Set<String> getRequiredKeywords() {
        Set<String> requiredKeywords = new HashSet<>();
        requiredKeywords.add(Keywords.PLOTS);
        return requiredKeywords;
    }
}
