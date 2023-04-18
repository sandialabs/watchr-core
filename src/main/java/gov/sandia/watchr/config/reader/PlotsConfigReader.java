/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config.reader;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import gov.sandia.watchr.config.CategoryConfiguration;
import gov.sandia.watchr.config.DataFilterConfig;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.config.PlotsConfig;
import gov.sandia.watchr.config.PlotypusConfig;
import gov.sandia.watchr.config.element.ConfigConverter;
import gov.sandia.watchr.config.element.ConfigElement;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;

public class PlotsConfigReader extends AbstractConfigReader<PlotsConfig> {

    ////////////
    // FIELDS //
    ////////////

    private final String startDirectoryAbsolutePath;
    private final IFileReader fileReader;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PlotsConfigReader(String startDirectoryAbsolutePath, ILogger logger, IFileReader fileReader) {
        super(logger);
        this.startDirectoryAbsolutePath = startDirectoryAbsolutePath;
        this.fileReader = fileReader;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public PlotsConfig handle(ConfigElement element, IConfig parent) {
        PlotsConfig plotsConfig = new PlotsConfig(parent.getConfigPath(), logger, fileReader);

        ConfigConverter converter = element.getConverter();
        Map<String, Object> map = element.getValueAsMap();
        for(Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if(key.equals(Keywords.FILES)) {
                seenKeywords.add(Keywords.FILES);
                FileConfigReader fileConfigReader = new FileConfigReader(startDirectoryAbsolutePath, logger, fileReader);
                plotsConfig.setFileConfig(fileConfigReader.handle(converter.asChild(value), plotsConfig));
            } else if(key.equals(Keywords.PLOT)) {
                seenKeywords.add(Keywords.PLOT);
                PlotConfigReader plotsConfigReader = new PlotConfigReader(plotsConfig.getFileConfig(), logger);
                List<PlotConfig> plotConfigList = plotsConfigReader.handle(converter.asChild(value), plotsConfig);
                plotsConfig.getPlotConfigs().addAll(plotConfigList);
            } else if(key.equals(Keywords.CATEGORIES)) {
                seenKeywords.add(Keywords.CATEGORIES);
                CategoryConfigReader categoryReader = new CategoryConfigReader(logger);
                CategoryConfiguration categoryConfig = categoryReader.handle(converter.asChild(value), plotsConfig);
                plotsConfig.setCategoryConfig(categoryConfig);

            } else if(key.equals(Keywords.FILTERS)) {
                seenKeywords.add(Keywords.FILTERS);
                DataFilterConfigReader filterReader = new DataFilterConfigReader(logger);
                DataFilterConfig filterConfig = filterReader.handle(converter.asChild(value), plotsConfig);
                plotsConfig.setPointFilterConfig(filterConfig);
            } else if(key.equals(Keywords.PLOTYPUS)) {
                seenKeywords.add(Keywords.PLOTYPUS);
                PlotypusConfigReader plotypusReader = new PlotypusConfigReader(logger);
                PlotypusConfig plotypusConfig = plotypusReader.handle(converter.asChild(value), plotsConfig);
                plotsConfig.setPlotypusConfig(plotypusConfig);
            }
        }

        validateMissingKeywords();
        return plotsConfig;
    }

    @Override
    public Set<String> getRequiredKeywords() {
        Set<String> requiredKeywords = new HashSet<>();
        requiredKeywords.add(Keywords.FILES);
        requiredKeywords.add(Keywords.PLOT);
        return requiredKeywords;
    }
}
