/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.FileConfig;
import gov.sandia.watchr.config.FileFilterConfig;
import gov.sandia.watchr.config.FilterConfig;
import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.config.PlotsConfig;
import gov.sandia.watchr.config.RuleConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
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

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PlotsConfigGenerator(IDatabase db, FileConfig fileConfig) {
        super(fileConfig);
        this.db = db;
        this.diffs = new ArrayList<>();
        this.errors = new ArrayList<>();
    }

    ////////////
    // PUBLIC //
    ////////////

    @Override
    public void generate(PlotsConfig config, List<WatchrDiff<?>> diffs) throws WatchrParseException {
        this.diffs.clear();
        this.diffs.addAll(diffs);
        this.errors.clear();

        // Read files & generate plots
        List<File> reports = readReports();
        generatePlots(reports, config.getPlotConfigs());

        // Categories
        CategoryListGenerator categoryGenerator = new CategoryListGenerator(db);
        categoryGenerator.generate(config.getCategoryConfig(), diffs);
    }

    /////////////
    // PRIVATE //
    /////////////

    private List<File> readReports() {
        List<File> reportsToReturn = new ArrayList<>();

        File reportFolder = getReportLocation();
        if (reportFolder != null && reportFolder.exists()) {
            List<File> reports = getReports(reportFolder);
            for (File report : reports) {
                if (!db.hasSeenFile(report) || !fileConfig.shouldIgnoreOldFiles()) {
                    reportsToReturn.add(report);
                }
            }
        }
        return reportsToReturn;
    }

    private void generatePlots(List<File> reports, List<PlotConfig> plotConfigs) throws WatchrParseException {
        ILogger logger = WatchrCoreApp.getInstance().getLogger();
        List<PlotWindowModel> rootChildren = new ArrayList<>();
        for(File report : reports) {
            logger.logInfo("Reading report " + report.getName());
            for(PlotConfig plotConfig : plotConfigs) {
                if(doesFilePassNameFilter(report.getName(), plotConfig.getFileFilterConfig())) {
                    PlotConfigGenerator plotConfigGenerator = new PlotConfigGenerator(report, db, plotConfigs);
                    plotConfigGenerator.generate(plotConfig, diffs);
                    
                    rootChildren.addAll(plotConfigGenerator.getPlots());
                    db.addFileToCache(report);
                }
            }
        }
        addToRootPlot(rootChildren);

        for(PlotConfig plotConfig : plotConfigs) {
            updateExistingPlots(plotConfig);
        }
    }

    /*package*/ boolean doesFilePassNameFilter(String fileName, FileFilterConfig fileFilterConfig) {
        boolean proceed = true;
        if(fileFilterConfig != null) {
            String namePattern = fileFilterConfig.getNamePattern();
            if(StringUtils.isNotBlank(namePattern)) {
                String regexPattern = namePattern.replace(".", "\\.");
                regexPattern = regexPattern.replace("*", ".*");
                proceed = fileName.matches(regexPattern);
            }
        }
        return proceed;
    }

    private void addToRootPlot(List<PlotWindowModel> childPlots) {
        PlotWindowModel rootPlot = db.getRootPlot();
        if (rootPlot == null) {
            rootPlot = new PlotWindowModel(CommonConstants.ROOT_PATH_ALIAS);
            db.addPlot(rootPlot);
        }
        db.addChildPlots(rootPlot, childPlots);
    }

    private void updateExistingPlots(PlotConfig config) throws WatchrParseException {
        for(PlotWindowModel plot : db.getAllPlots()) {
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
                applyFilterDataToTrace(plot, config.getPointFilterConfig());
            }
        }
    }

    private void applyRulesToPlotWindow(
        PlotWindowModel windowModel, List<RuleConfig> ruleConfigs) throws WatchrParseException {

        for(PlotCanvasModel canvasModel : windowModel.getCanvasModels()) {
            for(PlotTraceModel traceModel : canvasModel.getTraceModels()) {
                if(traceModel.getDerivativeLineType() == null) {
                    RuleGenerator ruleGenerator = new RuleGenerator(traceModel);
                    ruleGenerator.generate(ruleConfigs, diffs);
                }
            }
        }
    }

    private void applyFilterDataToTrace (PlotWindowModel windowModel, FilterConfig filterConfig) {
        for(PlotCanvasModel canvasModel : windowModel.getCanvasModels()) {
            for(PlotTraceModel traceModel : canvasModel.getTraceModels()) {
                traceModel.setFilterValues(filterConfig.getFilterPoints());
            }
        }
    }
}