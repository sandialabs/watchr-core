/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.extractors.ExtractionResult;
import gov.sandia.watchr.parse.extractors.strategy.AmbiguityStrategy;
import gov.sandia.watchr.parse.extractors.strategy.ExtractionStrategy;
import gov.sandia.watchr.parse.extractors.strategy.ExtractionStrategyFactory;
import gov.sandia.watchr.parse.extractors.strategy.ExtractionStrategyType;

public class HierarchicalExtractor implements IConfig {

    ////////////
    // FIELDS //
    ////////////

    public static final String PATH_SEPARATOR = "/";

    private final FileConfig fileConfig;

    private final ExtractionStrategyType extractionStrategyType;
    private AmbiguityStrategy ambiguityStrategy;
    
    private final Map<String, String> properties = new HashMap<>();

    private final String configPath;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public HierarchicalExtractor(FileConfig fileConfig, String configPathPrefix, String extractorName) {
        this.fileConfig = fileConfig;
        this.ambiguityStrategy = new AmbiguityStrategy(configPathPrefix);

        this.configPath = configPathPrefix + "/" + extractorName;
        if(fileConfig != null) {
            this.extractionStrategyType =
               ExtractionStrategyFactory.getInstance().getTypeFromExtension(fileConfig.getFileExtension());
        } else {
            this.extractionStrategyType = null;
        }
    }

    public HierarchicalExtractor(FileConfig fileConfig, String configPathPrefix) {
        this.fileConfig = fileConfig;
        this.ambiguityStrategy = new AmbiguityStrategy(configPathPrefix);
        this.configPath = configPathPrefix;
        this.extractionStrategyType = ExtractionStrategyFactory.getInstance().getTypeFromExtension(fileConfig.getFileExtension());
    }

    public HierarchicalExtractor(HierarchicalExtractor copy) {
        this.fileConfig = new FileConfig(copy.getFileConfig());
        this.ambiguityStrategy = new AmbiguityStrategy(copy.getAmbiguityStrategy());
        this.configPath = copy.getConfigPath();
        this.extractionStrategyType = ExtractionStrategyFactory.getInstance().getTypeFromExtension(fileConfig.getFileExtension());
        this.properties.putAll(copy.properties);
    }

    /////////////
    // GETTERS //
    /////////////

    public FileConfig getFileConfig() {
        return fileConfig;
    }

    public AmbiguityStrategy getAmbiguityStrategy() {
        return ambiguityStrategy;
    }

    public String getFileExtension() {
        return ExtractionStrategyFactory.getInstance().getExtension(extractionStrategyType);
    }

    public String getProperty(String propertyKey) {
        return properties.get(propertyKey);
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public String getConfigPath() {
        return configPath;
    }

    /////////////
    // SETTERS //
    /////////////

    public void setPath(String path) {
        properties.put(Keywords.GET_PATH, path);
    }

    public void setKey(String key) {
        properties.put(Keywords.GET_KEY, key);
    }

    public void setAmbiguityStrategy(AmbiguityStrategy ambiguityStrategy) {
        this.ambiguityStrategy = ambiguityStrategy;
    }

    public void setUnit(String unit) {
        properties.put(Keywords.UNIT, unit);
    }

    public void setProperty(String propertyKey, String propertyValue) {
        properties.put(propertyKey, propertyValue);
    }

    /////////////
    // UTILITY //
    /////////////

    public List<ExtractionResult> extract(File targetFile) throws WatchrParseException {
        ExtractionStrategy extractionStrategy =
            ExtractionStrategyFactory.getInstance().create(extractionStrategyType, properties, ambiguityStrategy);
        return extractionStrategy.extract(targetFile);
    }

    @Override
    public List<WatchrDiff<?>> diff(IConfig other) {
        HierarchicalExtractor otherExtractor = (HierarchicalExtractor) other;
        List<WatchrDiff<?>> diffList = new ArrayList<>();
        
        if(otherExtractor != null) {
            diffList.addAll(fileConfig.diff(otherExtractor.getFileConfig()));
            diffList.addAll(ambiguityStrategy.getDiffs(otherExtractor.getAmbiguityStrategy()));

            if(!(properties.equals(otherExtractor.properties))) {
                WatchrDiff<Map<String,String>> diff = new WatchrDiff<>(getConfigPath(), DiffCategory.PROPERTIES);
                diff.setBeforeValue(properties);
                diff.setNowValue(otherExtractor.properties);
                diffList.add(diff);
            }
        }
        
        return diffList;
    }

    @Override
    public boolean equals(Object other) {
        boolean equals = false;
		if(other == null) {
            return false;
        } else if(other == this) {
            return true;
        } else if(getClass() != other.getClass()) {
            return false;
        } else {
			HierarchicalExtractor otherExtractor = (HierarchicalExtractor) other;
            equals = fileConfig.equals(otherExtractor.fileConfig);
            equals = equals && ambiguityStrategy.equals(otherExtractor.ambiguityStrategy);
            equals = equals && properties.equals(otherExtractor.properties);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + fileConfig.hashCode());
        hash = 31 * (hash + ambiguityStrategy.hashCode());
        hash = 31 * (hash + properties.hashCode());
        return hash;
    }

    @Override
    public void validate() {
        ILogger logger = WatchrCoreApp.getInstance().getLogger();
        if(fileConfig == null) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "File configuration is not set for extractor."));
        }
        if(extractionStrategyType == null) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "No extraction strategy set."));
        }
    }
}
