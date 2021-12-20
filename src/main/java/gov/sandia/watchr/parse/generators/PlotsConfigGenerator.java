/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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

import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.FileFilterConfig;
import gov.sandia.watchr.config.FilterConfig;
import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.config.PlotsConfig;
import gov.sandia.watchr.config.RuleConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.util.CommonConstants;

public class PlotsConfigGenerator extends FileAwareGenerator<PlotsConfig> {

    ////////////
    // FIELDS //
    ////////////

    private final IDatabase db;
    private final List<WatchrConfigError> errors;
    private final List<WatchrDiff<?>> diffs;

    private final Map<PlotConfig, Collection<PlotWindowModel>> plotConfigToPlotsMap;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PlotsConfigGenerator(IDatabase db, FileConfig fileConfig) {
        super(fileConfig);
        this.db = db;
        this.diffs = new ArrayList<>();
        this.errors = new ArrayList<>();

        this.plotConfigToPlotsMap = new HashMap<>();
    }

    ////////////
    // PUBLIC //
    ////////////

    @Override
    public void generate(PlotsConfig config, List<WatchrDiff<?>> diffs) throws WatchrParseException {
        logger.logDebug("PlotsConfigGenerator.generate()");
        this.diffs.clear();
        this.diffs.addAll(diffs);
        this.errors.clear();
        this.plotConfigToPlotsMap.clear();

        logger.logDebug("Read files and generate plots.");
        List<String> reportAbsPaths = readReports();
        List<PlotConfig> plotConfigs = config.getPlotConfigs();
        List<PlotWindowModel> newPlots = generatePlotsFromPlotConfigs(reportAbsPaths, plotConfigs);
        addToRootPlot(newPlots);

        logger.logDebug("Get latest plots from database.");
        List<PlotWindowModel> plots = db.getAllPlots();

        logger.logDebug("Update (or generate new) global point filters.");
        if(!config.getPointFilterConfig().isBlank()) {
            for(int i = 0; i < plots.size(); i++) {
                PlotWindowModel plot = plots.get(i);
                applyFiltersToPlotWindow(plot, config.getPointFilterConfig(), true);
                db.updatePlot(plot);
            }
        }

        logger.logDebug("Update plots based on any per-plot configuration that has changed.");
        for(PlotConfig plotConfig : plotConfigs) {
            updateExistingPlots(plotConfig);
        }

        logger.logDebug("Update (or generate new) categories.");
        CategoryListGenerator categoryGenerator = new CategoryListGenerator(db);
        categoryGenerator.generate(config.getCategoryConfig(), diffs);

        logger.logDebug("DONE: PlotsConfigGenerator.generate()");
    }

    /////////////
    // PRIVATE //
    /////////////

    private List<String> readReports() {
        List<String> reportsToReturn = new ArrayList<>();
        List<String> reportsToRead = new ArrayList<>();

        if(fileConfig != null) {
            ILogger logger = fileConfig.getLogger();
            logger.logDebug("FileAwareGenerator.readReports()");
            
            String startFile = fileConfig.getStartFile();
            IFileReader fileReader = fileConfig.getFileReader();
            if(fileReader.isDirectory(startFile)) {
                logger.logDebug("Loading subdirectory " + startFile);
                reportsToRead = getReports(startFile);
            } else if(fileReader.isFile(startFile)) {
                logger.logDebug("Loading file " + startFile);
                reportsToRead.add(startFile);
            } else {
                throw new IllegalStateException("File \"" + startFile + "\" could not be identified as a folder or a file!");
            }

            logger.logDebug("Number of reports to read: " + reportsToRead.size());
            for(String report : reportsToRead) {
                logger.logDebug("Reading report " + report);
                if(!db.hasSeenFile(report) || !fileConfig.shouldIgnoreOldFiles()) {
                    logger.logInfo("Reading new report " + report);
                    reportsToReturn.add(report);
                } else {
                    logger.logDebug("Report " + report + " has already been parsed.");
                }
            }
        }       
        return reportsToReturn;
    }

    private List<PlotWindowModel> generatePlotsFromPlotConfigs(
            List<String> reportAbsPaths, List<PlotConfig> plotConfigs) throws WatchrParseException {
        logger.logDebug("PlotsConfigGenerator.generatePlotsFromPlotConfigs()");

        ILogger logger = fileConfig.getLogger();
        IFileReader fileReader = fileConfig.getFileReader();

        List<PlotWindowModel> allPlots = new ArrayList<>();
        for(String reportAbsPath : reportAbsPaths) {
            String fileName = fileReader.getName(reportAbsPath);
            logger.logInfo("Generating plot from report " + fileName);

            for(PlotConfig plotConfig : plotConfigs) {
                PlotConfigGenerator plotConfigGenerator =
                    new PlotConfigGenerator(reportAbsPath, db);
                List<PlotWindowModel> newPlots = 
                    generatePlotsFromPlotConfig(plotConfigs, plotConfig, fileName, plotConfigGenerator);
                allPlots.addAll(newPlots);
                db.addFileToCache(reportAbsPath);
            }
        }

        logger.logDebug("DONE: PlotsConfigGenerator.generatePlotsFromPlotConfigs()");
        return allPlots;
    }

    private List<PlotWindowModel> generatePlotsFromPlotConfig(
            List<PlotConfig> plotConfigs, PlotConfig plotConfig, String fileName, PlotConfigGenerator plotConfigGenerator)
            throws WatchrParseException {
        logger.logDebug("PlotsConfigGenerator.generate()");
        List<PlotWindowModel> newPlots = new ArrayList<>();

        PlotConfig fullPlotConfig = applyTemplateToPlotConfig(plotConfig, plotConfigs);

        if(doesFilePassNameFilter(fileName, fullPlotConfig.getFileFilterConfig())) {            
            plotConfigGenerator.generate(fullPlotConfig, diffs);
            newPlots.addAll(plotConfigGenerator.getPlots());
            applySettingsToNewPlots(newPlots, fullPlotConfig);
            plotConfigToPlotsMap.put(plotConfig, newPlots);
        }

        logger.logDebug("DONE: PlotsConfigGenerator.generate()");
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
        PlotWindowModel rootPlot = db.getRootPlot();
        if (rootPlot == null) {
            rootPlot = new PlotWindowModel(CommonConstants.ROOT_PATH_ALIAS);
            db.addPlot(rootPlot);
        }

        // Note: Because IDatabase uses Sets to store plots, plots whose
        // UUIDs already exist in the database will not be re-inserted.
        db.setPlotsAsChildren(rootPlot, childPlots);
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
    
    private void applyFiltersToPlotWindow(
            PlotWindowModel windowModel, FilterConfig config, 
            boolean clearFilterValuesBeforeApplying) throws WatchrParseException{

        for(PlotCanvasModel canvasModel : windowModel.getCanvasModels()) {
            for(PlotTraceModel traceModel : canvasModel.getTraceModels()) {
                FilterConfigGenerator filterGenerator =
                    new FilterConfigGenerator(traceModel, clearFilterValuesBeforeApplying, fileConfig.getLogger());
                filterGenerator.generate(config, diffs);
            }
        }
    }
}