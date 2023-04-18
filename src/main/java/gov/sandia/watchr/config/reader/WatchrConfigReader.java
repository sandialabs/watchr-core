/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.parser.ParserException;

import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.element.ConfigConverter;
import gov.sandia.watchr.config.element.ConfigElement;
import gov.sandia.watchr.config.element.json.JsonConfigElement;
import gov.sandia.watchr.config.element.yaml.YamlConfigElement;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;

public class WatchrConfigReader extends AbstractConfigReader<WatchrConfig> {

    ////////////
    // FIELDS //
    ////////////

    private final String startFileAbsPath;
    private final IFileReader fileReader;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WatchrConfigReader(String startFileAbsPath, ILogger logger, IFileReader fileReader) {
        super(logger);
        this.startFileAbsPath = startFileAbsPath;
        this.fileReader = fileReader;
    }

    ////////////
    // PUBLIC //
    ////////////

    /**
     * Convert a configuration {@link File} to a {@link WatchrConfig} object.
     * 
     * @param jsonFile The configuration file.
     * @return The WatchrConfig containing all the data from the configuration file.
     * @throws IOException Thrown if there was an error reading the file.
     */
    public WatchrConfig deserialize(File configFile) throws IOException {
        String fileContents = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8);
        String extension = FilenameUtils.getExtension(configFile.getName());
        return deserialize(fileContents, extension);
    }

    /**
     * Convert an {@link InputStream} from a configuration file to a {@link WatchrConfig}
     * object.
     * 
     * @param is The input stream of the configuration file.
     * @return The WatchrConfig containing all the data from the configuration file.
     * @throws IOException Thrown if there was an error reading the file.
     */
    public WatchrConfig deserialize(InputStream is, String extension) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, StandardCharsets.UTF_8); // UTF-8 is a safe assumption
        return deserialize(writer.toString(), extension);
    }

    /**
     * 
     * @param fileContents
     * @param extension
     * @return
     */
    public WatchrConfig deserialize(String fileContents, String extension) {
        if(StringUtils.isBlank(extension)) {
            extension = detectFileType(fileContents);
        }
        if(extension.equalsIgnoreCase("json")) {
            return deserializeJsonConfigFile(fileContents);
        } else if(extension.equalsIgnoreCase("yaml")) {
            return deserializeYamlConfigFile(fileContents);
        } else {
            throw new IllegalStateException("Could not parse contents of " + extension);
        }
    }

    /**
     * 
     * @param fileContents
     * @return
     */
    public WatchrConfig deserialize(String fileContents) {
        String extension = detectFileType(fileContents);
        if(StringUtils.isNotBlank(extension)) {
            return deserialize(fileContents, extension);
        }
        return null;
    }

    /**
     * 
     * @param testString
     * @return
     */
    public String detectFileType(String testString) {
        // Try JSON first.
        try {
            JsonParser.parseString(testString);
            return "json";
        } catch(JsonSyntaxException e) {
            logger.logInfo("Config file was tested as JSON format, but it could not be parsed.");
            logger.logInfo(e.getMessage());
        }

        // Try YAML next.
        try {
            Yaml yaml = new Yaml();
            Object obj = yaml.load(testString);
            if(obj instanceof Map<?,?>) {
                return "yaml";
            } else {
                logger.logInfo("Config file was tested as YAML format, but it could not be parsed.");
                logger.logInfo("We excepted a Map, but received a " + obj.getClass().getSimpleName());
            }
        } catch(ParserException e) {
            logger.logInfo("Config file was tested as YAML format, but it could not be parsed.");
            logger.logInfo(e.getMessage());
        }

        return "";
    }

    /**
     * Convert a {@link String} of JSON file contents to a {@link WatchrConfig}
     * object.
     * 
     * @param jsonFileContents The JSON file contents.
     * @return The WatchrConfig containing all the data from the JSON.
     */
    public WatchrConfig deserializeJsonConfigFile(String jsonFileContents) {
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonFileContents).getAsJsonObject();
            JsonConfigElement rootConfigElement = new JsonConfigElement(jsonObject);
            return handle(rootConfigElement, null);
        } catch(JsonSyntaxException e) {
            logger.logError("Config file was tested as JSON format, but it could not be parsed.", e);
        }
        return null;
    }

    /**
     * Convert a {@link String} of YAML file contents to a {@link WatchrConfig}
     * object.
     * 
     * @param jsonFileContents The YAML file contents.
     * @return The WatchrConfig containing all the data from the YAML.
     */
    public WatchrConfig deserializeYamlConfigFile(String yamlFileContents) {
        try {
            Yaml yaml = new Yaml();
            YamlConfigElement rootConfigElement = new YamlConfigElement(yaml.load(yamlFileContents));
            return handle(rootConfigElement, null);
        } catch(ParserException e) {
            logger.logError("Config file was tested as YAML format, but it could not be parsed.", e);
        }
        return null;
    }
     
    @Override
    public WatchrConfig handle(ConfigElement element, IConfig parent) {
        WatchrConfig watchrConfig = new WatchrConfig(logger, fileReader);
        ConfigConverter converter = element.getConverter();
        Map<String, Object> map = element.getValueAsMap();
        for(Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if(key.equals(Keywords.PLOTS)) {
                seenKeywords.add(Keywords.PLOTS);
                PlotsConfigReader plotsConfigReader = new PlotsConfigReader(startFileAbsPath, logger, fileReader);
                watchrConfig.setPlotsConfig(plotsConfigReader.handle(converter.asChild(value), watchrConfig));
            } else if (key.equals(Keywords.GRAPH_DISPLAY)) {
                seenKeywords.add(Keywords.GRAPH_DISPLAY);
                GraphDisplayConfigReader graphDisplayConfigReader = new GraphDisplayConfigReader(logger);
                watchrConfig.setGraphDisplayConfig(graphDisplayConfigReader.handle(converter.asChild(value), watchrConfig));
            } else if (key.equals(Keywords.LOGGING)) {
                seenKeywords.add(Keywords.LOGGING);
                LogConfigReader logConfigReader = new LogConfigReader(logger);
                watchrConfig.setLogConfig(logConfigReader.handle(converter.asChild(value), watchrConfig));
            } else if (key.equals(Keywords.RULES)) {
                seenKeywords.add(Keywords.RULES);
                RuleConfigReader ruleConfigReader = new RuleConfigReader(logger);
                watchrConfig.setRuleConfigs(ruleConfigReader.handle(converter.asChild(value), watchrConfig));
            } else if (key.equals(Keywords.FILTERS)) {
                seenKeywords.add(Keywords.FILTERS);
                DataFilterConfigReader filterConfigReader = new DataFilterConfigReader(logger);
                watchrConfig.setFilterConfig(filterConfigReader.handle(converter.asChild(value), watchrConfig));
            } else {
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
