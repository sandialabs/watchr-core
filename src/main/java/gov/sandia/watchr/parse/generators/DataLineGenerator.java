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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.config.DataLine;
import gov.sandia.watchr.config.DerivativeLine;
import gov.sandia.watchr.config.MetadataConfig;
import gov.sandia.watchr.config.NameConfig;
import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.config.RuleConfig;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.reader.Shorthand;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.extractors.ExtractionResult;
import gov.sandia.watchr.parse.extractors.strategy.AmbiguityStrategy;
import gov.sandia.watchr.util.RGB;
import gov.sandia.watchr.util.RgbUtil;

public class DataLineGenerator extends AbstractGenerator<DataLine> {

    ////////////
    // FIELDS //
    ////////////

    private enum CombinationStrategy {
        ONE_X_ONE_Y,
        ONE_X_MULTIPLE_Y,
        MULTIPLE_X_ONE_Y,
        MULTIPLE_COMBINATORIAL,
        MULTIPLE_ITERATE
    }

    private DataLine line;

    private final File report;
    private final IDatabase db;
    private final List<PlotWindowModel> rootPlots;

    private final String name;
    private final NameConfig nameConfig;

    private List<RuleConfig> rules;
    private final String category;

    private final Boolean useLegend;

    private final List<WatchrDiff<?>> diffs;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public DataLineGenerator(PlotConfig parentPlotConfig, File report, IDatabase db) {
        this.report = report;
        this.db = db;

        this.rootPlots = new ArrayList<>();

        this.name = parentPlotConfig.getName();

        this.rules = new ArrayList<>();
        this.rules.addAll(parentPlotConfig.getPlotRules());
        this.category = parentPlotConfig.getCategory();
        this.nameConfig = parentPlotConfig.getNameConfig();
        this.useLegend = parentPlotConfig.shouldUseLegend();

        this.diffs = new ArrayList<>();
    }

    /////////////
    // GETTERS //
    /////////////

    public List<PlotWindowModel> getRootPlots() {
        return rootPlots;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void generate(DataLine line, List<WatchrDiff<?>> diffs) throws WatchrParseException {
        this.line = line;
        this.diffs.clear();
        this.diffs.addAll(diffs);

        List<ExtractionResult> xResults = line.getXExtractor().extract(report);
        List<ExtractionResult> yResults = line.getYExtractor().extract(report);

        Map<String, ExtractionResult> metadataResults = new HashMap<>();
        for(MetadataConfig metadata : line.getMetadata()) {
            List<ExtractionResult> results = metadata.getMetadataExtractor().extract(report);
            if(results != null && !results.isEmpty()) {
                // Note: There can be only one valid value for a piece of metadata, so
                // we only keep the first entry in the list.
                ExtractionResult metadataResult = results.get(0);
                metadataResults.put(metadata.getName(), metadataResult);
            }
        }

        rootPlots.clear();
        rootPlots.addAll(applyExtractionResultsToRootPlots(xResults, yResults, metadataResults));

        updateDiffableProperties();
    }

    //////////////////////
    // NAME DETERMINING //
    //////////////////////

    private String getPlotName(ExtractionResult xResult, ExtractionResult yResult) {
        String targetPlotName = "";
        if(StringUtils.isNotBlank(name)) {
            targetPlotName = name;
        }
        if((nameConfig != null && !nameConfig.isBlank()) && xResult != null && yResult != null) {
            targetPlotName = determineTargetPlotName(nameConfig, xResult, yResult);
        }
        if(StringUtils.isBlank(targetPlotName)) {
            if(xResult == null && yResult == null) {
                throw new IllegalStateException("xResult and yResult are both null!");
            } else if(xResult != null && yResult == null) {
                targetPlotName = "plot_" + xResult.hashCode() + "_null";
            } else if(xResult == null) {
                targetPlotName = "plot_null_" + yResult.hashCode();
            } else {
                targetPlotName = "plot_" + xResult.hashCode() + "_" + yResult.hashCode();
            }
            
        }
        return targetPlotName;
    }

    private String getChildPlotName(String prefix, ExtractionResult childResult) {
        String childPlotName = "";
        if(StringUtils.isNotBlank(name)) {
            childPlotName = prefix + "_plot_" + childResult.hashCode();
        }
        if(!nameConfig.isBlank()) {
            childPlotName = determineTargetPlotName(nameConfig, childResult, childResult);
        }
        return childPlotName;
    }    

    private String determineTargetPlotName(NameConfig nameConfig, ExtractionResult xResult, ExtractionResult yResult) {
        String targetPlotName = "";

        String nameUse = nameConfig.getNameUseProperty();
        if (StringUtils.isNotBlank(nameUse)) {
            Shorthand shorthand = new Shorthand(nameUse);
            if (shorthand.getAxis().equals("x")) {
                if (shorthand.getGroupingField().equals("key")) {
                    targetPlotName = xResult.getKey();
                } else if (shorthand.getGroupingField().equals("path")) {
                    targetPlotName = xResult.getPath();
                }
            } else if (shorthand.getAxis().equals("y")) {
                if (shorthand.getGroupingField().equals("key")) {
                    targetPlotName = yResult.getKey();
                } else if (shorthand.getGroupingField().equals("path")) {
                    targetPlotName = yResult.getPath();
                }
            } else {
                // Error state?
            }
        } else {
            // Error state?
        }

        if (StringUtils.isNotBlank(nameConfig.getNameFormatRemovePrefix())) {
            Pattern pattern = Pattern.compile(nameConfig.getNameFormatRemovePrefix());
            Matcher matcher = pattern.matcher(targetPlotName);
            if (matcher.find()) {
                int endIndex = matcher.end();
                targetPlotName = targetPlotName.substring(endIndex);
            }
        }

        return targetPlotName;
    }

    private PlotTraceModel findPlotTraceModel(PlotWindowModel parentWindow, String traceName) {
        for(PlotCanvasModel canvasModel : parentWindow.getCanvasModels()) {
            for(PlotTraceModel traceModel : canvasModel.getTraceModels()) {
                if(traceModel.getName().equals(traceName)) {
                    return traceModel;
                }
            }
        }
        return null;
    }

    //////////////////////////////
    // APPLY EXTRACTION RESULTS //
    //////////////////////////////

    private List<PlotWindowModel> applyExtractionResultsToRootPlots(
            List<ExtractionResult> xResults, List<ExtractionResult> yResults,
            Map<String, ExtractionResult> metadataResults) throws WatchrParseException {

        List<PlotWindowModel> windowModels = new ArrayList<>();
        if(xResults != null && yResults != null) {
            CombinationStrategy comboStrategy = null;
            if(xResults.size() == 1) {
                if(yResults.size() == 1) {
                    comboStrategy = CombinationStrategy.ONE_X_ONE_Y;
                } else {
                    comboStrategy = CombinationStrategy.ONE_X_MULTIPLE_Y;
                }
            } else {
                if(yResults.size() == 1) {
                    comboStrategy = CombinationStrategy.MULTIPLE_X_ONE_Y;
                } else {
                    AmbiguityStrategy xAmbiguityStrategy = line.getXExtractor().getAmbiguityStrategy();
                    AmbiguityStrategy yAmbiguityStrategy = line.getYExtractor().getAmbiguityStrategy();

                    if(xAmbiguityStrategy.getIterateWithOtherExtractor().equalsIgnoreCase("y") ||
                       yAmbiguityStrategy.getIterateWithOtherExtractor().equalsIgnoreCase("x")) {
                        comboStrategy = CombinationStrategy.MULTIPLE_ITERATE;
                    } else {
                        comboStrategy = CombinationStrategy.MULTIPLE_COMBINATORIAL;
                    }
                }
            }

            if(comboStrategy == CombinationStrategy.MULTIPLE_ITERATE) {
                windowModels.addAll(
                    applyExtractionResultsToRootPlotsViaIteration(xResults, yResults, metadataResults)
                );
            } else {
                windowModels.addAll(
                    applyExtractionResultsToRootPlotsViaFullCombinatorial(xResults, yResults, metadataResults)
                );
            }
        }
        return windowModels;
    }

    private List<PlotWindowModel> applyExtractionResultsToRootPlotsViaFullCombinatorial(
        List<ExtractionResult> xResults, List<ExtractionResult> yResults, Map<String, ExtractionResult> metadataResults)
        throws WatchrParseException {

        List<PlotWindowModel> windowModels = new ArrayList<>();
        for(ExtractionResult xResult : xResults) {
            if(xResult != null) {
                for(ExtractionResult yResult : yResults) {
                    if(yResult != null) {
                        String plotName = getPlotName(xResult, yResult);
                        PlotWindowModel windowModel =
                            applyExtractionResultsToChildPlots(plotName, xResult, yResult, metadataResults);
                        windowModels.add(windowModel);
                    }
                }
            }
        }

        return windowModels;
    }

    private List<PlotWindowModel> applyExtractionResultsToRootPlotsViaIteration(
        List<ExtractionResult> xResults, List<ExtractionResult> yResults, Map<String, ExtractionResult> metadataResults)
        throws WatchrParseException {

        List<PlotWindowModel> windowModels = new ArrayList<>();

        for(int i = 0; i < xResults.size() && i < yResults.size(); i++) {
            ExtractionResult xResult = xResults.get(i);
            ExtractionResult yResult = yResults.get(i);
            if(xResult != null && yResult != null) {
                String plotName = getPlotName(xResult, yResult);
                PlotWindowModel windowModel =
                    applyExtractionResultsToChildPlots(plotName, xResult, yResult, metadataResults);
                windowModels.add(windowModel);
            }
        }
        return windowModels;
    }

    private PlotWindowModel applyExtractionResultsToChildPlots(
            String plotName, ExtractionResult xResult, ExtractionResult yResult,
            Map<String, ExtractionResult> metadataResults) throws WatchrParseException {

        PlotWindowModel windowModel = db.getPlot(plotName, category);
        if(windowModel == null) {
            windowModel = newPlotWindowModel(line, xResult, yResult, metadataResults);
            if(windowModel != null) {
                applyDerivativeLinesToPlot(windowModel, line.getDerivativeLines());
                db.addPlot(windowModel);
            }
        } else {
            PlotTraceModel traceModel = findPlotTraceModel(windowModel, line.getName());
            if(traceModel != null) {
                updatePlotTraceModel(traceModel, xResult, yResult, metadataResults);
            } else {
                // For now, everything has one canvas model.
                PlotCanvasModel canvasModel = windowModel.getCanvasModels().get(0);
                newPlotTraceModel(canvasModel, xResult, yResult, metadataResults);
            }
            applyDerivativeLinesToPlot(windowModel, line.getDerivativeLines());
        }

        boolean isXRecursive = line.getXExtractor().getAmbiguityStrategy().shouldRecurseToChildGraphs();
        boolean isYRecursive = line.getYExtractor().getAmbiguityStrategy().shouldRecurseToChildGraphs();

        if(isXRecursive && isYRecursive) {
            throw new UnsupportedOperationException("Cannot simultaneously recurse to child graphs in both X and Y directions!");
        }

        List<PlotWindowModel> childPlots = new ArrayList<>();
        if(isYRecursive && yResult != null && !yResult.getChildren().isEmpty()) {
            for(ExtractionResult childResult : yResult.getChildren()) {
                String childPlotName = getChildPlotName(name, childResult);
                PlotWindowModel updatedChildPlot =
                    applyExtractionResultsToChildPlots(childPlotName, xResult, childResult, metadataResults);
                childPlots.add(updatedChildPlot);
            }
        } else if(isXRecursive && xResult != null && !xResult.getChildren().isEmpty()) {
            for(ExtractionResult childResult : xResult.getChildren()) {
                String childPlotName = getChildPlotName(name, childResult);
                PlotWindowModel updatedChildPlot =
                    applyExtractionResultsToChildPlots(childPlotName, childResult, yResult, metadataResults);
                childPlots.add(updatedChildPlot);
            }
        }

        // Update database's parent-child linkage between plot models.
        if(!childPlots.isEmpty() && windowModel != null) {
            db.addChildPlots(windowModel, childPlots);
        }

        return windowModel;
    }

    //////////////////////////////
    // PLOT CREATION / UPDATING //
    //////////////////////////////

    private PlotWindowModel newPlotWindowModel(
            DataLine line,
            ExtractionResult xResult, ExtractionResult yResult,
            Map<String, ExtractionResult> metadataResults) {

        if(xResult != null && yResult != null) {
            String plotName = getPlotName(xResult, yResult);
            PlotWindowModel newWindow = new PlotWindowModel(plotName);
            newWindow.setCategory(category);
            if(useLegend != null) {
                newWindow.setLegendVisible(useLegend);
            }
            
            PlotCanvasModel newCanvas = new PlotCanvasModel(newWindow.getUUID());
            newCanvas.setXAxisRGB(RgbUtil.blackRGB());
            newCanvas.setYAxisRGB(RgbUtil.blackRGB());
            newCanvas.setZAxisRGB(RgbUtil.blackRGB());
            newCanvas.setXAxisLabel(line.getXExtractor().getProperty(Keywords.UNIT));
            newCanvas.setYAxisLabel(line.getYExtractor().getProperty(Keywords.UNIT));

            newPlotTraceModel(newCanvas, xResult, yResult, metadataResults);
            return newWindow;
        }
        return null;
    }

    private void newPlotTraceModel(
            PlotCanvasModel parentCanvas,
            ExtractionResult xResult, ExtractionResult yResult,
            Map<String, ExtractionResult> metadataResults) {

        PlotTraceModel newTrace = new PlotTraceModel(parentCanvas.getUUID());
        newTrace.setName(line.getName());
        RGB color = line.getColor();
        newTrace.setPrimaryRGB(color == null ? new RGB(0,0,0) : color);
        newTrace.set(PlotToken.TRACE_POINT_TYPE, PlotType.SCATTER_PLOT);
        newTrace.set(PlotToken.TRACE_POINT_MODE, "Circle");
        newTrace.set(PlotToken.TRACE_DRAW_LINES, true);

        PlotTracePoint newPoint = new PlotTracePoint(xResult.getValue(), yResult.getValue());
        newTrace.add(newPoint);

        applyMetadataToDatabasePlot(newPoint, metadataResults);
    }

    private void updatePlotTraceModel(
            PlotTraceModel traceModel,
            ExtractionResult xResult, ExtractionResult yResult,
            Map<String, ExtractionResult> metadataResults) {

        if(xResult != null && yResult != null) {
            String xValue = xResult.getValue();
            String yValue = yResult.getValue();

            if(!traceModel.containsPoint(xValue, yValue)) {
                PlotTracePoint newPoint = new PlotTracePoint(xValue, yValue);
                traceModel.add(newPoint);
                applyMetadataToDatabasePlot(newPoint, metadataResults);
            }
        }
    }

    private void updateDiffableProperties() throws WatchrParseException {
        for(PlotWindowModel windowModel : db.getAllPlots()) {
            if(diffed(line, diffs, DiffCategory.DERIVATIVE_LINES)) {
                removeDerivativeLines(windowModel);
                applyDerivativeLinesToPlot(windowModel, line.getDerivativeLines());
            }

            for(PlotCanvasModel canvasModel : windowModel.getCanvasModels()) {
                for(PlotTraceModel traceModel : canvasModel.getNonDerivativeTraceModels()) {
                    if(diffed(line, diffs, DiffCategory.LINE_COLOR)) {
                        RGB color = line.getColor();
                        traceModel.setPrimaryRGB(color == null ? new RGB(0,0,0) : color);
                    }

                    if(diffed(line, diffs, DiffCategory.UNIT)) {
                        traceModel.getParent().setXAxisLabel(line.getXExtractor().getProperty(Keywords.UNIT));
                        traceModel.getParent().setYAxisLabel(line.getYExtractor().getProperty(Keywords.UNIT));
                    }
                }
            }
        }
    }

    private void applyMetadataToDatabasePlot(PlotTracePoint targetPoint,
            Map<String, ExtractionResult> metadataResults) {

        for (Entry<String, ExtractionResult> metadataResult : metadataResults.entrySet()) {
            String key = metadataResult.getKey();
            String value = metadataResult.getValue().getValue();

            targetPoint.metadata.put(key, value);
        }
    }

    private void applyDerivativeLinesToPlot(
            PlotWindowModel windowModel, List<DerivativeLine> derivativeLines) throws WatchrParseException {
        for(PlotCanvasModel canvasModel : windowModel.getCanvasModels()) {
            for(PlotTraceModel traceModel : canvasModel.getNonDerivativeTraceModels()) {
                DerivativeLineGenerator derivativeLineGenerator = new DerivativeLineGenerator(traceModel);
                derivativeLineGenerator.generate(derivativeLines, diffs);
            }
        }
    }
    
    private void removeDerivativeLines(PlotWindowModel windowModel) {
        for(PlotCanvasModel canvasModel : windowModel.getCanvasModels()) {
            Iterator<PlotTraceModel> iter = canvasModel.getTraceModels().iterator();
            while(iter.hasNext()) {
                PlotTraceModel traceModel = iter.next();
                if(traceModel.getDerivativeLineType() != null) {
                    iter.remove();
                }
            }
        }
    }
}
