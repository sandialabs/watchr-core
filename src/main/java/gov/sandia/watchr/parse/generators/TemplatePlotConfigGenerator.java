/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.config.DataLine;
import gov.sandia.watchr.config.FileFilterConfig;
import gov.sandia.watchr.config.NameConfig;
import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.generators.line.TemplateDataLineGenerator;

public class TemplatePlotConfigGenerator extends AbstractTemplateGenerator {

    ////////////
    // FIELDS //
    ////////////

    private final List<PlotConfig> allPlotConfigs;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public TemplatePlotConfigGenerator(List<PlotConfig> allPlotConfigs, ILogger logger) {
        super(logger);
        this.allPlotConfigs = new ArrayList<>();
        this.allPlotConfigs.addAll(allPlotConfigs);
    }

    ////////////
    // PUBLIC //
    ////////////
    
    public PlotConfig handlePlotGenerationForTemplate(PlotConfig childConfig) {
        PlotConfig templateConfig = getTemplatePlotConfig(childConfig.getInheritTemplate());
        if(templateConfig != null) {
            if(!templateConfig.isTemplateApplied()) {
                templateConfig = handlePlotGenerationForTemplate(templateConfig);
            }
            return applyChildOverTemplate(templateConfig, childConfig);
        } else {
            logger.logError("Plot depends on template " + childConfig.getInheritTemplate() + ", but this template does not exist in the configuration.");
        }
        return null;
    }

    /////////////
    // PRIVATE //
    /////////////

    private PlotConfig getTemplatePlotConfig(String templateName) {
        if(StringUtils.isNotBlank(templateName)) {
            for(PlotConfig plotConfig : allPlotConfigs) {
                if(plotConfig.getTemplateName().equals(templateName)) {
                    return plotConfig;
                }
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

        newConfig.setTemplateName(childConfig.getTemplateName());
        newConfig.setInheritTemplate("");

        applyTemplateDataLinesInTemplatePlot(newConfig);
        applyTemplateDataLinesInInheritPlot(newConfig, childConfig);

        applyFiltersOverTemplate(newConfig.getPointFilterConfig(), childConfig.getPointFilterConfig());

        if(!childConfig.getPlotRules().isEmpty()) {
            applyRulesOverTemplate(newConfig.getPlotRules(), childConfig.getPlotRules());
        }
        
        newConfig.setTemplateApplied(true);
        return newConfig;
    }

    private void applyTemplateDataLinesInTemplatePlot(PlotConfig templateConfig) {
        List<DataLine> newDataLines = new ArrayList<>();
        for(DataLine line : templateConfig.getDataLines()) {
            boolean dependsOnTemplate = StringUtils.isNotBlank(line.getInheritTemplate());
            if(dependsOnTemplate) {
                TemplateDataLineGenerator templateDataLineGenerator =
                    new TemplateDataLineGenerator(templateConfig.getDataLines(), logger);
                DataLine newLine = templateDataLineGenerator.handleDataLineGenerationForTemplate(line);
                newDataLines.add(newLine);
            } else {
                newDataLines.add(line);
            }
        }
        templateConfig.getDataLines().clear();
        templateConfig.getDataLines().addAll(newDataLines);
    }

    private void applyTemplateDataLinesInInheritPlot(PlotConfig templateConfig, PlotConfig inheritPlotConfig) {
        for(DataLine newDataLine : inheritPlotConfig.getDataLines()) {
            String inheritName = newDataLine.getInheritTemplate();
            boolean dependsOnTemplate = StringUtils.isNotBlank(inheritName);
            if(dependsOnTemplate) {
                TemplateDataLineGenerator templateDataLineGenerator =
                    new TemplateDataLineGenerator(templateConfig.getDataLines(), logger);
                DataLine replacedDataLine = templateDataLineGenerator.handleDataLineGenerationForTemplate(newDataLine);

                // Replace line from template.
                for(DataLine parentLine : templateConfig.getDataLines()) {
                    if(parentLine.getTemplateName().equals(inheritName)) {
                        templateConfig.getDataLines().remove(parentLine);
                        templateConfig.getDataLines().add(replacedDataLine);
                        break;
                    }
                }
            } else {
                templateConfig.getDataLines().add(newDataLine);
            }
        }
    }
}
