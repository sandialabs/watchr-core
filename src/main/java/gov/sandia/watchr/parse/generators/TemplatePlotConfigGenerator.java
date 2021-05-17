/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.DataLine;
import gov.sandia.watchr.config.DerivativeLine;
import gov.sandia.watchr.config.FileFilterConfig;
import gov.sandia.watchr.config.FilterConfig;
import gov.sandia.watchr.config.HierarchicalExtractor;
import gov.sandia.watchr.config.MetadataConfig;
import gov.sandia.watchr.config.NameConfig;
import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.config.RuleConfig;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.extractors.strategy.AmbiguityStrategy;

public class TemplatePlotConfigGenerator {

    ////////////
    // FIELDS //
    ////////////

    private final List<PlotConfig> allPlotConfigs;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public TemplatePlotConfigGenerator(List<PlotConfig> allPlotConfigs) {
        this.allPlotConfigs = new ArrayList<>();
        this.allPlotConfigs.addAll(allPlotConfigs);
    }

    ////////////
    // PUBLIC //
    ////////////
    
    public PlotConfig handleDataLineGenerationForTemplate(PlotConfig childConfig) {
        PlotConfig templateConfig = getTemplatePlotConfig(childConfig.getInheritTemplate());
        if(templateConfig != null) {
            return applyChildOverTemplate(templateConfig, childConfig);
        } else {
            ILogger logger = WatchrCoreApp.getInstance().getLogger();
            logger.logError("Plot depends on template " + childConfig.getInheritTemplate() + ", but this template does not exist in the configuration.");
        }
        return null;
    }

    /////////////
    // PRIVATE //
    /////////////

    private PlotConfig getTemplatePlotConfig(String templateName) {
        for(PlotConfig plotConfig : allPlotConfigs) {
            if(plotConfig.getTemplateName().equals(templateName)) {
                return plotConfig;
            }
        }
        return null;
    }

    private PlotConfig applyChildOverTemplate(PlotConfig templateConfig, PlotConfig childConfig) {
        PlotConfig newConfig = new PlotConfig(templateConfig);

        if(StringUtils.isNotBlank(childConfig.getCategory())) {
            newConfig.setCategory(childConfig.getCategory());
        }
        if(childConfig.shouldUseLegend() != null) {
            newConfig.setUseLegend(childConfig.shouldUseLegend());
        }

        if(StringUtils.isNotBlank(childConfig.getName())) {
            newConfig.setName(childConfig.getName());
        }
        if(childConfig.getNameConfig() != null) {
            newConfig.setNameConfig(new NameConfig(childConfig.getNameConfig()));
        }
        if(childConfig.getFileFilterConfig() != null) {
            newConfig.setFileFilterConfig(new FileFilterConfig(childConfig.getFileFilterConfig()));
        }
        if(childConfig.getPointFilterConfig() != null) {
            newConfig.setPointFilterConfig(new FilterConfig(childConfig.getPointFilterConfig()));
        }

        newConfig.setTemplateName(childConfig.getTemplateName());
        newConfig.setInheritTemplate("");

        if(!newConfig.getDataLines().isEmpty()) {
            applyChildDataLinesOverTemplate(newConfig, templateConfig.getDataLines(), childConfig.getDataLines());
        }

        if(!newConfig.getPlotRules().isEmpty()) {
            applyRulesOverTemplate(newConfig.getPlotRules(), childConfig.getPlotRules());
        }
        return newConfig;
    }

    private void applyChildDataLinesOverTemplate(PlotConfig targetConfig, List<DataLine> templateDataLines, List<DataLine> childDataLines) {
        for(int i = 0; i < templateDataLines.size() && i < childDataLines.size(); i++) {
            DataLine newDataLine = new DataLine(templateDataLines.get(i));
            DataLine childDataLine = childDataLines.get(i);

            if(StringUtils.isNotBlank(childDataLine.getName())) {
                newDataLine.setName(childDataLine.getName());
            }
            if(childDataLine.getColor() != null) {
                newDataLine.setColor(childDataLine.getColor());
            }

            applyChildExtractorOverTemplate(newDataLine.getXExtractor(), childDataLine.getXExtractor());
            applyChildExtractorOverTemplate(newDataLine.getYExtractor(), childDataLine.getYExtractor());
            applyDerivativeLinesOverTemplate(newDataLine.getDerivativeLines(), childDataLine.getDerivativeLines());
            applyMetadataOverTemplate(newDataLine.getMetadata(), childDataLine.getMetadata());

            if(i < targetConfig.getDataLines().size()) {
                targetConfig.getDataLines().set(i, newDataLine);
            } else {
                targetConfig.getDataLines().add(newDataLine);
            }
        }
    }

    private void applyChildExtractorOverTemplate(HierarchicalExtractor newExtractor, HierarchicalExtractor childExtractor) {
        if(newExtractor != null && childExtractor != null) {
            if(childExtractor.getAmbiguityStrategy() != null && newExtractor.getAmbiguityStrategy() == null) {
                AmbiguityStrategy newStrategy = new AmbiguityStrategy(childExtractor.getConfigPath());

                newStrategy.setIterateWithOtherExtractor(
                    childExtractor.getAmbiguityStrategy().getIterateWithOtherExtractor());
                newStrategy.setShouldGetFirstMatchOnly(
                    childExtractor.getAmbiguityStrategy().shouldGetFirstMatchOnly());
                newStrategy.setShouldRecurseToChildGraphs(
                    childExtractor.getAmbiguityStrategy().shouldRecurseToChildGraphs());

                newExtractor.setAmbiguityStrategy(newStrategy);
            }
        
            for(Entry<String,String> entry : childExtractor.getProperties().entrySet()) {
                newExtractor.setProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    private void applyRulesOverTemplate(List<RuleConfig> templateRules, List<RuleConfig> childRules) {
        List<RuleConfig> newRules = new ArrayList<>();

        for(RuleConfig childRule : childRules) {
            boolean found = false;
            for(RuleConfig templateRule : templateRules) {
                if(templateRule.getCondition().equals(childRule.getCondition())) {
                    templateRule.setAction(childRule.getAction());
                    found = true;
                    break;
                }
            }
            if(!found) {
                newRules.add(childRule);
            }
        }

        templateRules.addAll(newRules);
    }

    private void applyMetadataOverTemplate(List<MetadataConfig> templateMetadataConfigs, List<MetadataConfig> childMetadataConfigs) {
        for(int i = 0; i < templateMetadataConfigs.size() && i < childMetadataConfigs.size(); i++) {
            MetadataConfig newMetadataConfig = new MetadataConfig(templateMetadataConfigs.get(i));
            MetadataConfig childMetadataConfig = childMetadataConfigs.get(i);

            if(StringUtils.isNotBlank(childMetadataConfig.getName())) {
                newMetadataConfig.setName(childMetadataConfig.getName());
            }
            applyChildExtractorOverTemplate(newMetadataConfig.getMetadataExtractor(), childMetadataConfig.getMetadataExtractor());
        }
    }

    private void applyDerivativeLinesOverTemplate(List<DerivativeLine> templateDerivativeLines, List<DerivativeLine> childDerivativeLines) {
        List<DerivativeLine> newDerivativeLines = new ArrayList<>();

        for(DerivativeLine childDerivativeLine : childDerivativeLines) {
            boolean found = false;
            for(DerivativeLine templateDerivativeLine : templateDerivativeLines) {
                if(templateDerivativeLine.getType() == childDerivativeLine.getType()) {
                    templateDerivativeLine.setColor(childDerivativeLine.getColor());
                    templateDerivativeLine.setIgnoreFilteredData(childDerivativeLine.shouldIgnoreFilteredData());
                    templateDerivativeLine.setRollingRange(childDerivativeLine.getRollingRange());
                    found = true;
                    break;
                }
            }
            if(!found) {
                newDerivativeLines.add(childDerivativeLine);
            }
        }

        templateDerivativeLines.addAll(newDerivativeLines);
    }
}
