/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.extractors.strategy;

import java.util.Map;

import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.log.ILogger;

public class ExtractionStrategyFactory {
    
    private static ExtractionStrategyFactory INSTANCE;

    public static ExtractionStrategyFactory getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ExtractionStrategyFactory();
        }
        return INSTANCE;
    }

    public ExtractionStrategy<?> create(
            ExtractionStrategyType type, Map<String, String> properties, AmbiguityStrategy strategy,
            ILogger logger, IFileReader fileReader) {
        if(type == ExtractionStrategyType.XML) {
            return new XmlExtractionStrategy(properties, strategy, logger, fileReader);
        } else if(type == ExtractionStrategyType.JSON) {
            return new JsonExtractionStrategy(properties, strategy, logger, fileReader);
        }
        return null;
    }

    public String getExtension(ExtractionStrategyType type) {
        if(type == ExtractionStrategyType.XML) {
            return "xml";
        } else if(type == ExtractionStrategyType.JSON) {
            return "json";
        }
        return "";
    }

    public ExtractionStrategyType getTypeFromExtension(String extension) {
        if(extension.equalsIgnoreCase("xml")) {
            return ExtractionStrategyType.XML;
        } else if(extension.equalsIgnoreCase("json")) {
            return ExtractionStrategyType.JSON;
        }
        return null;
    }
}
