/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config.reader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.element.ConfigConverter;
import gov.sandia.watchr.config.element.ConfigElement;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;

public class PlotConfigReader extends AbstractExtractorConfigReader<List<PlotConfig>> {
    
    ////////////
    // FIELDS //
    ////////////

    private final FileConfig fileConfig;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PlotConfigReader(FileConfig fileConfig, ILogger logger) {
        super(logger);
        this.fileConfig = fileConfig;
        if(fileConfig == null) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "PlotConfigReader: No file config defined."));
        }
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public List<PlotConfig> handle(ConfigElement element, IConfig parent) {
        List<PlotConfig> plotConfigList = new ArrayList<>();
        ConfigConverter converter = element.getConverter();

        for(int i = 0; i < element.getValueAsList().size(); i++) {
            Object listObj = element.getValueAsList().get(i);
            PlotConfig plotConfig = handleAsPlot(converter.asChild(listObj), fileConfig, parent, i);
            plotConfigList.add(plotConfig);
        }

        validateMissingKeywords();
        return plotConfigList;
    }

    @Override
    public Set<String> getRequiredKeywords() {
        Set<String> requiredKeywords = new HashSet<>();
        requiredKeywords.add(Keywords.DATA_LINES);
        return requiredKeywords;
    }

    /////////////
    // PRIVATE //
    /////////////

    private PlotConfig handleAsPlot(ConfigElement element, FileConfig fileConfig, IConfig parent, int plotIndex) {
        PlotConfig plotConfig = new PlotConfig(parent.getConfigPath() + "/" + Integer.toString(plotIndex), parent.getLogger());

        ConfigConverter converter = element.getConverter();
        
        Map<String, Object> map = element.getValueAsMap();
        for(Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if(key.equals(Keywords.NAME)) {
                seenKeywords.add(Keywords.NAME);
                plotConfig.setName(converter.asString(value));
            } else if(key.equals(Keywords.AUTONAME)) {
                seenKeywords.add(Keywords.AUTONAME);
                AutonameConfigReader nameConfigReader = new AutonameConfigReader(fileConfig, logger);
                plotConfig.setNameConfig(nameConfigReader.handle(converter.asChild(value), plotConfig));
            } else if(key.equals(Keywords.TYPE)) {
                seenKeywords.add(Keywords.TYPE);
                plotConfig.setType(converter.asString(value));
            } else if(key.equals(Keywords.CATEGORY)) {
                seenKeywords.add(Keywords.CATEGORY);
                plotConfig.setCategory(converter.asString(value));
            } else if(key.equals(Keywords.FILE_FILTER)) {
                seenKeywords.add(Keywords.FILE_FILTER);
                FileFilterConfigReader fileFilterConfigReader = new FileFilterConfigReader(logger);
                plotConfig.setFileFilterConfig(fileFilterConfigReader.handle(converter.asChild(value), plotConfig));
            } else if(key.equals(Keywords.TEMPLATE)) {
                seenKeywords.add(Keywords.TEMPLATE);
                plotConfig.setTemplateName(converter.asString(value));
            } else if(key.equals(Keywords.LEGEND)) {
                seenKeywords.add(Keywords.LEGEND);
                plotConfig.setUseLegend(converter.asBoolean(value));
            } else if(key.equals(Keywords.DATA_LINES)) {
                seenKeywords.add(Keywords.DATA_LINES);
                DataLineReader dataLineReader = new DataLineReader(fileConfig, logger);
                plotConfig.getDataLines().addAll(dataLineReader.handle(converter.asChild(value), plotConfig));
            } else if(key.equals(Keywords.FILTERS)) {
                seenKeywords.add(Keywords.FILTERS);
                DataFilterConfigReader filterConfigReader = new DataFilterConfigReader(logger);
                plotConfig.setPointFilterConfig(filterConfigReader.handle(converter.asChild(value), plotConfig));
            } else if(key.equals(Keywords.RULES)) {
                seenKeywords.add(Keywords.RULES);
                RuleConfigReader ruleReader = new RuleConfigReader(logger);
                plotConfig.getPlotRules().addAll(ruleReader.handle(converter.asChild(value), plotConfig));
            } else if(key.equals(Keywords.INHERIT)) {
                seenKeywords.add(Keywords.INHERIT);
                plotConfig.setInheritTemplate(converter.asString(value));
            } else if(key.equals(Keywords.CANVAS_LAYOUT)) {
                seenKeywords.add(Keywords.CANVAS_LAYOUT);
                plotConfig.setCanvasLayout(converter.asString(value));
            } else if(key.equals(Keywords.CANVAS_PER_ROW)) {
                seenKeywords.add(Keywords.CANVAS_PER_ROW);
                plotConfig.setCanvasPerRow(converter.asInt(value));
            } else {
                logger.log(new WatchrConfigError(ErrorLevel.WARNING, "handleAsPlot: Unrecognized element `" + key + "`."));
            }
        }
        
        return plotConfig;
    }
}