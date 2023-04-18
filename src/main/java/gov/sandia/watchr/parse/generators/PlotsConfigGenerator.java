/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.config.FileFilterConfig;
import gov.sandia.watchr.config.DataFilterConfig;
import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.config.PlotsConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.config.filter.DataFilter;
import gov.sandia.watchr.config.rule.RuleConfig;
import gov.sandia.watchr.db.DatabaseMetadata;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.generators.rule.RuleGenerator;

public class PlotsConfigGenerator extends AbstractGenerator<PlotsConfig> {

    ////////////
    // FIELDS //
    ////////////

    private static final String CLASSNAME = PlotsConfigGenerator.class.getSimpleName();

    private final IDatabase db;
    private final IFileReader fileReader;
    private final List<WatchrConfigError> errors;
    private final List<WatchrDiff<?>> diffs;
    private final DataFilterConfig dataFilter;

    private final List<String> reportAbsPaths;
    private final Map<PlotConfig, Collection<PlotWindowModel>> plotConfigToPlotsMap;

    private PlotConfigGenerator plotConfigGenerator;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PlotsConfigGenerator(
            IDatabase db, ILogger logger, IFileReader fileReader,
            DataFilterConfig dataFilter, List<String> reportAbsPaths) {
        super(logger);
        this.db = db;
        this.fileReader = fileReader;
        this.dataFilter = dataFilter;
        this.diffs = new ArrayList<>();
        this.errors = new ArrayList<>();

        this.reportAbsPaths = new ArrayList<>();
        this.reportAbsPaths.addAll(reportAbsPaths);
        this.plotConfigToPlotsMap = new HashMap<>();
    }

    ////////////
    // PUBLIC //
    ////////////

    @Override
    public void generate(PlotsConfig config, List<WatchrDiff<?>> diffs) throws WatchrParseException {
        logger.logDebug("PlotsConfigGenerator.generate()", CLASSNAME);
        this.diffs.clear();
        this.diffs.addAll(diffs);
        this.errors.clear();
        this.plotConfigToPlotsMap.clear();

        List<PlotConfig> plotConfigs = config.getPlotConfigs();
        applyFiltersToPlotConfigs(plotConfigs, dataFilter);
        List<PlotWindowModel> newPlots = generatePlotsFromPlotConfigs(reportAbsPaths, plotConfigs);
        addToRootPlot(newPlots);

        logger.logDebug("Get latest plots from database.", CLASSNAME);
        List<PlotWindowModel> plots = db.getAllPlots();

        logger.logDebug("Update (or generate new) global point filters.", CLASSNAME);
        if(!config.getPointFilterConfig().isBlank()) {
            for(int i = 0; i < plots.size(); i++) {
                PlotWindowModel plot = plots.get(i);
                applyFiltersToPlotWindow(plot, config.getPointFilterConfig(), false);
                db.updatePlot(plot, false);
            }
        }

        logger.logDebug("Update plots based on any per-plot configuration that has changed.", CLASSNAME);
        for(PlotConfig plotConfig : plotConfigs) {
            updateExistingPlots(plotConfig);
        }

        logger.logDebug("Update (or generate new) categories.", CLASSNAME);
        CategoryListGenerator categoryGenerator = new CategoryListGenerator(db);
        categoryGenerator.generate(config.getCategoryConfig(), diffs);

        logger.logDebug("DONE: PlotsConfigGenerator.generate()", CLASSNAME);
    }

    @Override
    public String getProblemStatus() {
        if(plotConfigGenerator != null) {
            return plotConfigGenerator.getProblemStatus();
        }
        return "";
    }

    /////////////
    // PRIVATE //
    /////////////

    private List<PlotWindowModel> generatePlotsFromPlotConfigs(
            List<String> reportAbsPaths, List<PlotConfig> plotConfigs) throws WatchrParseException {
        logger.logDebug("PlotsConfigGenerator.generatePlotsFromPlotConfigs()", CLASSNAME);

        List<PlotWindowModel> allPlots = new ArrayList<>();
        for(String reportAbsPath : reportAbsPaths) {
            String fileName = fileReader.getName(reportAbsPath);
            logger.logInfo("Generating plot from report " + fileName);

            for(PlotConfig plotConfig : plotConfigs) {
                plotConfigGenerator = new PlotConfigGenerator(reportAbsPath, db);
                List<PlotWindowModel> newPlots = 
                    generatePlotsFromPlotConfig(plotConfigs, plotConfig, fileName);
                allPlots.addAll(newPlots);
                db.addFileToCache(reportAbsPath);
            }
        }

        logger.logDebug("DONE: PlotsConfigGenerator.generatePlotsFromPlotConfigs()", CLASSNAME);
        return allPlots;
    }

    private List<PlotWindowModel> generatePlotsFromPlotConfig(
            List<PlotConfig> plotConfigs, PlotConfig plotConfig, String fileName) throws WatchrParseException {
        logger.logDebug("PlotsConfigGenerator.generate()", CLASSNAME);
        List<PlotWindowModel> newPlots = new ArrayList<>();

        PlotConfig fullPlotConfig = applyTemplateToPlotConfig(plotConfig, plotConfigs);

        if(doesFilePassNameFilter(fileName, fullPlotConfig.getFileFilterConfig())) {            
            plotConfigGenerator.generate(fullPlotConfig, diffs);
            newPlots.addAll(plotConfigGenerator.getPlots());
            applySettingsToNewPlots(newPlots, fullPlotConfig);
            plotConfigToPlotsMap.put(plotConfig, newPlots);
        }

        logger.logDebug("DONE: PlotsConfigGenerator.generate()", CLASSNAME);
        return newPlots;
    }

    private PlotConfig applyTemplateToPlotConfig(PlotConfig plotConfig, List<PlotConfig> plotConfigs) {
        PlotConfig fullPlotConfig = plotConfig;
        boolean dependsOnTemplate = StringUtils.isNotBlank(plotConfig.getInheritTemplate());
        if(dependsOnTemplate) {
            TemplatePlotConfigGenerator templatePlotConfigGenerator = new TemplatePlotConfigGenerator(plotConfigs, logger);
            fullPlotConfig = templatePlotConfigGenerator.handlePlotGenerationForTemplate(plotConfig);
        }
        return fullPlotConfig;
    }

    protected boolean doesFilePassNameFilter(String fileName, FileFilterConfig fileFilterConfig) {
        if(fileFilterConfig != null) {
            String namePattern = fileFilterConfig.getNamePatternAsRegex();
            if(StringUtils.isNotBlank(namePattern)) {
                return fileName.matches(namePattern);
            }
        }
        return true;
    }

    private void addToRootPlot(List<PlotWindowModel> childPlots) {
        logger.logDebug("addToRootPlot()", CLASSNAME);
        PlotWindowModel rootPlot = db.createRootPlotIfMissing();
        db.addPlot(rootPlot);

        // Note: Because IDatabase uses Sets to store plots, plots whose
        // UUIDs already exist in the database will not be re-inserted.
        db.setPlotsAsChildren(rootPlot, childPlots);

        DatabaseMetadata dbMetadata = db.getMetadata();
        synchronized(dbMetadata) {
            int newPlotCount = dbMetadata.getNewPlotCount();
            dbMetadata.setNewPlotCount(newPlotCount + childPlots.size());
        }
    }

    protected void applySettingsToNewPlots(List<PlotWindowModel> plots, PlotConfig config) throws WatchrParseException {
        for(PlotWindowModel plot : plots) {
            if(config.shouldUseLegend() != null) {
                plot.setLegendVisible(config.shouldUseLegend());
            }
            if(!config.getPlotRules().isEmpty()) {
                applyRulesToPlotWindow(plot, config.getPlotRules());
            }
            if(config.getPointFilterConfig() != null) {
                applyFiltersToPlotWindow(plot, config.getPointFilterConfig(), false);
            }

            List<PlotWindowModel> children = new ArrayList<>(db.getChildren(plot, ""));
            if(!children.isEmpty()) {
                applySettingsToNewPlots(children, config);
            }
        }
    }

    private void updateExistingPlots(PlotConfig config) throws WatchrParseException {
        Collection<PlotWindowModel> plots = plotConfigToPlotsMap.getOrDefault(config, new ArrayList<>());
        for(PlotWindowModel plot : plots) {
            // Apply legend
            if(diffed(config, diffs, DiffCategory.USE_LEGEND)) {
                plot.setLegendVisible(config.shouldUseLegend());
            }

            // Apply rules
            if(diffed(config, diffs, DiffCategory.CONDITION) || diffed(config, diffs, DiffCategory.ACTION)) {
                applyRulesToPlotWindow(plot, config.getPlotRules());
            }

            // Apply filters
            if(diffed(config, diffs, DiffCategory.POINT_FILTER_CONFIG)) {
                applyFiltersToPlotWindow(plot, config.getPointFilterConfig(), false);
            }
        }
    }

    private void applyRulesToPlotWindow(
        PlotWindowModel windowModel, List<RuleConfig> ruleConfigs) throws WatchrParseException {

        for(PlotCanvasModel canvasModel : windowModel.getCanvasModels()) {
            for(PlotTraceModel traceModel : canvasModel.getNonDerivativeTraceModels()) {
                RuleGenerator ruleGenerator = new RuleGenerator(traceModel, logger);
                ruleGenerator.generate(ruleConfigs, diffs);
            }
        }
    }

    private void applyFiltersToPlotConfigs(List<PlotConfig> plotConfigs, DataFilterConfig upperLevelDataFilterConfig) {
        if(upperLevelDataFilterConfig != null) {
            Collection<DataFilter> upperLevelFilters = upperLevelDataFilterConfig.getFilters();
            for(PlotConfig plotConfig : plotConfigs) {
                DataFilterConfig plotDataFilterConfig = plotConfig.getPointFilterConfig();
                if(plotDataFilterConfig != null) {
                    plotDataFilterConfig.getFilters().addAll(upperLevelFilters);
                }
            }
        }
    }
    
    private void applyFiltersToPlotWindow(
            PlotWindowModel windowModel, DataFilterConfig config, 
            boolean clearFilterValuesBeforeApplying) throws WatchrParseException{

        for(PlotCanvasModel canvasModel : windowModel.getCanvasModels()) {
            for(PlotTraceModel traceModel : canvasModel.getTraceModels()) {
                FilterConfigGenerator filterGenerator =
                    new FilterConfigGenerator(traceModel, clearFilterValuesBeforeApplying, logger);
                filterGenerator.generate(config, diffs);
            }
        }
    }
}