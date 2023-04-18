/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators.line.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.config.derivative.DerivativeLine;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.db.NewPlotDatabaseSearchCriteria;
import gov.sandia.watchr.graph.chartreuse.ChartreuseException;
import gov.sandia.watchr.graph.chartreuse.Dimension;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.generators.DerivativeLineGenerator;
import gov.sandia.watchr.parse.generators.line.CombinationStrategy;
import gov.sandia.watchr.parse.generators.line.DataLineGenerator;
import gov.sandia.watchr.parse.generators.line.extractors.ExtractionResult;
import gov.sandia.watchr.parse.generators.line.extractors.ExtractionResultNameResolver;
import gov.sandia.watchr.util.OsUtil;
import gov.sandia.watchr.util.RGB;

public class ScatterPlotDataLineGenerator extends DataLineGenerator {

    private static final String CLASSNAME = ScatterPlotDataLineGenerator.class.getSimpleName();

    enum ScatterPlotDataLineGeneratorState {
        IDLE,
        APPLY_EXTRACTION_RESULTS,
        APPLY_DERIVATIVE_LINES_TO_PLOT,
        APPLY_EXTRACTION_RESULTS_TO_CHILD_PLOTS,
        APPLY_EXTRACTION_RESULTS_VIA_FULL_COMBINATORIAL,
        APPLY_EXTRACTION_RESULTS_VIA_ITERATION,
        GET_TARGET_PLOT_NAME,
        SEARCH_AND_MAKE_NEW_IF_MISSING,
        UPDATING
    }
    private ScatterPlotDataLineGeneratorState state = ScatterPlotDataLineGeneratorState.IDLE;
    private String currentPlotName;
    private String currentPlotCategory;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ScatterPlotDataLineGenerator(PlotConfig plotConfig, String reportAbsPath, IDatabase db) {
        super(plotConfig, reportAbsPath, db);
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    protected List<PlotWindowModel> applyExtractionResults(
            List<ExtractionResult> xResults,
            List<ExtractionResult> yResults,
            Map<String, ExtractionResult> metadataResults)
            throws WatchrParseException {
        logger.logDebug("ScatterPlotDataLineGenerator.applyExtractionResultsToRootPlots()", CLASSNAME);
        state = ScatterPlotDataLineGeneratorState.APPLY_EXTRACTION_RESULTS;

        List<PlotWindowModel> windowModels = new ArrayList<>();
        if(xResults != null && yResults != null) {
            CombinationStrategy comboStrategy = getCombinationStrategy(xResults, yResults);

            if(comboStrategy == CombinationStrategy.MULTIPLE_ITERATE) {
                windowModels.addAll(applyExtractionResultsViaIteration(xResults, yResults, metadataResults));
            } else {
                windowModels.addAll(applyExtractionResultsViaFullCombinatorial(xResults, yResults, metadataResults));
            }
        }

        logger.logDebug("DONE: ScatterPlotDataLineGenerator.applyExtractionResultsToRootPlots()", CLASSNAME);
        return windowModels;
    }

    @Override
    protected void applyDerivativeLinesToPlot(
            PlotWindowModel windowModel, List<DerivativeLine> derivativeLines) throws WatchrParseException {
        logger.logDebug("ScatterPlotDataLineGenerator.applyDerivativeLinesToPlot()", CLASSNAME);
        logger.logDebug("Generate derivative lines for plot " + windowModel.getName() + "...", CLASSNAME);
        state = ScatterPlotDataLineGeneratorState.APPLY_DERIVATIVE_LINES_TO_PLOT;

        for(PlotCanvasModel canvasModel : windowModel.getCanvasModels()) {
            for(PlotTraceModel traceModel : canvasModel.getNonDerivativeTraceModels()) {
                logger.logDebug("Generate derivative lines for trace " + traceModel.getName() + "...", CLASSNAME);
                DerivativeLineGenerator derivativeLineGenerator = new DerivativeLineGenerator(traceModel, logger);
                derivativeLineGenerator.generate(derivativeLines, diffs);
                logger.logDebug("Done generating derivative line.", CLASSNAME);
            }
        }
        logger.logDebug("DONE: ScatterPlotDataLineGenerator.applyDerivativeLinesToPlot()", CLASSNAME);
    }  
    
    @Override
    public String getProblemStatus() {
        if(state == ScatterPlotDataLineGeneratorState.SEARCH_AND_MAKE_NEW_IF_MISSING) {
            return "ScatterPlotDataLineGenerator: Searching for plot: " + currentPlotName +
                   " in category " + currentPlotCategory + OsUtil.getOSLineBreak();
        } else if(state == ScatterPlotDataLineGeneratorState.UPDATING) {
            return "ScatterPlotDataLineGenerator: Plot locked for editing: " + currentPlotName +
                   " in category " + currentPlotCategory + OsUtil.getOSLineBreak();
        } else {
            return "ScatterPlotDataLineGenerator: State is " + state.toString() + OsUtil.getOSLineBreak();
        }
    }

    /////////////
    // PRIVATE //
    /////////////

    private List<PlotWindowModel> applyExtractionResultsViaFullCombinatorial(
            List<ExtractionResult> xResults,
            List<ExtractionResult> yResults,
            Map<String, ExtractionResult> metadataResults) throws WatchrParseException {
        logger.logDebug("ScatterPlotDataLineGenerator.applyExtractionResultsToRootPlotsViaFullCombinatorial()", CLASSNAME);
        logger.logDebug("Number of X results: " + xResults.size(), CLASSNAME);
        logger.logDebug("Number of Y results: " + yResults.size(), CLASSNAME);
        state = ScatterPlotDataLineGeneratorState.APPLY_EXTRACTION_RESULTS_VIA_FULL_COMBINATORIAL;

        List<PlotWindowModel> windowModels = new ArrayList<>();
        for(ExtractionResult xResult : xResults) {
            if(xResult != null) {
                for(ExtractionResult yResult : yResults) {
                    if(yResult != null) {
                        String plotName = getTargetPlotName(xResult, yResult, -1);
                        if(plotName != null) {
                            PlotWindowModel windowModel =
                                applyExtractionResultsToChildPlots(plotName, xResult, yResult, metadataResults);
                            if(windowModel != null) {
                                windowModels.add(windowModel);
                            }
                        }
                    }
                }
            }
        }

        logger.logDebug("DONE: ScatterPlotDataLineGenerator.applyExtractionResultsToRootPlotsViaFullCombinatorial()", CLASSNAME);
        return windowModels;
    }

    private List<PlotWindowModel> applyExtractionResultsViaIteration(
            List<ExtractionResult> xResults,
            List<ExtractionResult> yResults,
            Map<String, ExtractionResult> metadataResults) throws WatchrParseException {
        logger.logDebug("ScatterPlotDataLineGenerator.applyExtractionResultsToRootPlotsViaIteration()", CLASSNAME);
        state = ScatterPlotDataLineGeneratorState.APPLY_EXTRACTION_RESULTS_VIA_ITERATION;
        List<PlotWindowModel> windowModels = new ArrayList<>();

        logger.logDebug("Number of X results: " + xResults.size(), CLASSNAME);
        logger.logDebug("Number of Y results: " + yResults.size(), CLASSNAME);
        for(int i = 0; i < xResults.size() && i < yResults.size(); i++) {
            ExtractionResult xResult = xResults.get(i);
            ExtractionResult yResult = yResults.get(i);
            if(xResult != null && yResult != null) {
                String plotName = getTargetPlotName(xResult, yResult, i);
                if(plotName != null) {
                    PlotWindowModel windowModel = applyExtractionResultsToChildPlots(plotName, xResult, yResult, metadataResults, i);
                    windowModels.add(windowModel);
                }
            }
        }
        logger.logDebug("DONE: ScatterPlotDataLineGenerator.applyExtractionResultsToRootPlotsViaIteration()", CLASSNAME);
        return windowModels;
    }

    private String getTargetPlotName(ExtractionResult xResult, ExtractionResult yResult, int iterator) {
        state = ScatterPlotDataLineGeneratorState.GET_TARGET_PLOT_NAME;
        String plotName = name;
        logger.logDebug("Name: " + plotName, CLASSNAME);
        if(StringUtils.isBlank(plotName)) {
            ExtractionResultNameResolver nameResolver = new ExtractionResultNameResolver(nameConfig, logger);
            plotName = nameResolver.getName(xResult, yResult, iterator);
            if(plotName != null) {
                logger.logDebug("Name after name resolver: " + plotName, CLASSNAME);
            }
        }
        return plotName;
    }

    private PlotWindowModel applyExtractionResultsToChildPlots(
        String plotName, ExtractionResult xResult, ExtractionResult yResult,
        Map<String, ExtractionResult> metadataResults) throws WatchrParseException {

        return applyExtractionResultsToChildPlots(plotName, xResult, yResult, metadataResults, -1);
    }

    private PlotWindowModel applyExtractionResultsToChildPlots(
            String plotName, ExtractionResult xResult, ExtractionResult yResult,
            Map<String, ExtractionResult> metadataResults, int resultIndex)
            throws WatchrParseException {

        logger.logDebug("ScatterPlotDataLineGenerator.applyExtractionResultsToChildPlots()", CLASSNAME);
        logger.logDebug("Looking for plot " + plotName + " in category " + category + "...", CLASSNAME);
        state = ScatterPlotDataLineGeneratorState.APPLY_EXTRACTION_RESULTS_TO_CHILD_PLOTS;

        NewPlotDatabaseSearchCriteria searchCriteria = new NewPlotDatabaseSearchCriteria(plotName, category);
        searchCriteria.setNameConfig(nameConfig);
        searchCriteria.setXResult(xResult);
        searchCriteria.setYResult(yResult);
        searchCriteria.setResultIndex(resultIndex);

        currentPlotName = searchCriteria.getName();
        currentPlotCategory = searchCriteria.getCategory();

        state = ScatterPlotDataLineGeneratorState.SEARCH_AND_MAKE_NEW_IF_MISSING;
        PlotWindowModel windowModel = db.searchAndMakeNewIfMissing(searchCriteria);

        try {
            while(windowModel == null) {
                Thread.sleep(10);
                windowModel = db.searchAndMakeNewIfMissing(searchCriteria);
            }
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.logDebug(
                "Threading interruption occurred - could not get access to plot with name " + searchCriteria.getName() +
                " and category " + searchCriteria.getCategory(), CLASSNAME);
        }

        if(windowModel != null) {
            state = ScatterPlotDataLineGeneratorState.UPDATING;
            synchronized(windowModel) {
                currentPlotName = windowModel.getName();
                currentPlotCategory = windowModel.getCategory();

                if(windowModel.isEmpty2D()) {
                    initPlotWindowModel(windowModel, xResult, yResult, metadataResults);
                }

                logger.logDebug("Updating existing plot. First, resolve data line name...", CLASSNAME);
                String dataLineName = resolveDataLineName(xResult, yResult);
                if(dataLineName != null) {
                    logger.logDebug("Data line name is " + dataLineName, CLASSNAME);
                    PlotTraceModel traceModel = findPlotTraceModel(windowModel, dataLineName);
                    if(traceModel != null) {
                        logger.logDebug("Update the existing data line trace model...", CLASSNAME);
                        updatePlotTraceModel(traceModel, xResult, yResult, metadataResults);
                    } else if(xResult != null && yResult != null) {
                        logger.logDebug("Create a new data line trace model...", CLASSNAME);
                        PlotCanvasModel canvasModel = newCanvasModel(windowModel);
                        String xValue = formatValue(xResult.getValue(), line.getXExtractor().getProperty(Keywords.FORMAT_AS));
                        String yValue = formatValue(yResult.getValue(), line.getYExtractor().getProperty(Keywords.FORMAT_AS));
                        newPlotTraceModel(canvasModel, dataLineName, xValue, yValue, metadataResults);
                    }

                    boolean isXRecursive = line.getXExtractor().getAmbiguityStrategy().shouldRecurseToChildGraphs();
                    boolean isYRecursive = line.getYExtractor().getAmbiguityStrategy().shouldRecurseToChildGraphs();

                    if(isXRecursive && isYRecursive) {
                        throw new UnsupportedOperationException("Cannot simultaneously recurse to child graphs in both X and Y directions!");
                    }

                    List<PlotWindowModel> childPlots =
                        updateChildPlots(xResult, yResult, metadataResults, isXRecursive, isYRecursive);

                    logger.logDebug("Update canvas layout.", CLASSNAME);
                    applyCanvasLayoutDisplaySettingsToPlot(windowModel);

                    if(!childPlots.isEmpty()) {
                        logger.logDebug("Update database's parent-child linkage between plot models.", CLASSNAME);
                        db.setPlotsAsChildren(windowModel, childPlots);
                    }

                    logger.logDebug("Apply derivative lines to plot.", CLASSNAME);
                    applyDerivativeLinesToPlot(windowModel, line.getDerivativeLines());
                }
                db.updatePlot(windowModel, false);
            }

            logger.logDebug("DONE: ScatterPlotDataLineGenerator.applyExtractionResultsToChildPlots()", CLASSNAME);
            return windowModel;
        }
        return null;
    }

    private String resolveDataLineName(ExtractionResult xResult, ExtractionResult yResult) {
        String dataLineName = line.getName();
        if(StringUtils.isBlank(dataLineName) && line.getNameConfig() != null) {
            ExtractionResultNameResolver nameResolver = new ExtractionResultNameResolver(line.getNameConfig(), logger);
            dataLineName = nameResolver.getName(xResult, yResult, -1);
        }
        return dataLineName;
    }

    private PlotWindowModel initPlotWindowModel(
        PlotWindowModel newWindow, ExtractionResult xResult, ExtractionResult yResult,
        Map<String, ExtractionResult> metadataResults) throws WatchrParseException {

        if(xResult != null && yResult != null) {
            newWindow.setCategory(category);
            if(useLegend != null) {
                newWindow.setLegendVisible(useLegend);
            }
            
            PlotCanvasModel newCanvas = newCanvasModel(newWindow);
            newCanvas.setXAxisLabel(line.getXExtractor().getProperty(Keywords.UNIT));
            newCanvas.setYAxisLabel(line.getYExtractor().getProperty(Keywords.UNIT));

            String formatXPoints = line.getXExtractor().getProperty(Keywords.FORMAT_AS);
            String formatYPoints = line.getYExtractor().getProperty(Keywords.FORMAT_AS);
            String xValue = StringUtils.isBlank(formatXPoints) ? xResult.getValue() : format(xResult.getValue(), formatXPoints);
            String yValue = StringUtils.isBlank(formatYPoints) ? yResult.getValue() : format(yResult.getValue(), formatYPoints);
            
            String dataLineName = resolveDataLineName(xResult, yResult);
            if(dataLineName != null) {
                newPlotTraceModel(newCanvas, dataLineName, xValue, yValue, metadataResults);
            }
            return newWindow;
        }
        return null;
    }

    protected void newPlotTraceModel(
            PlotCanvasModel parentCanvas, String traceName,
            String xValue, String yValue,
            Map<String, ExtractionResult> metadataResults) throws WatchrParseException {

        try {
            PlotTraceModel newTrace = new PlotTraceModel(parentCanvas.getUUID());
            newTrace.setName(traceName);
            RGB color = line.getColor();
            newTrace.setPrimaryRGB(color == null ? new RGB(0,0,0) : color);
            newTrace.set(PlotToken.TRACE_POINT_TYPE, PlotType.SCATTER_PLOT);
            newTrace.set(PlotToken.TRACE_POINT_MODE, "Circle");
            newTrace.set(PlotToken.TRACE_DRAW_LINES, true);

            PlotTracePoint newPoint = new PlotTracePoint(xValue, yValue);
            newTrace.add(newPoint);

            applyMetadataToPlot(newPoint, metadataResults);
            applyFiltersToDataLine(newTrace, line.getPointFilterConfig(), false);
        } catch(ChartreuseException e) {
            throw new WatchrParseException(e);
        }
    }

    private List<PlotWindowModel> updateChildPlots(
            ExtractionResult xResult, ExtractionResult yResult,
            Map<String, ExtractionResult> metadataResults,
            boolean isXRecursive, boolean isYRecursive)
            throws WatchrParseException {

        List<PlotWindowModel> childPlots = new ArrayList<>();
        if(isYRecursive && yResult != null && !yResult.getChildren().isEmpty()) {
            for(int i = 0; i < yResult.getChildren().size(); i++) {
                ExtractionResult childResult = yResult.getChildren().get(i);
                ExtractionResultNameResolver nameResolver = new ExtractionResultNameResolver(nameConfig, logger);
                String childPlotName = nameResolver.getChildName(name, childResult, i);
                logger.logDebug("name: " + name, CLASSNAME);
                logger.logDebug("childPlotName: " + childPlotName, CLASSNAME);
                PlotWindowModel updatedChildPlot =
                    applyExtractionResultsToChildPlots(childPlotName, xResult, childResult, metadataResults);
                if(updatedChildPlot != null) {
                    childPlots.add(updatedChildPlot);
                }
            }
        } else if(isXRecursive && xResult != null && !xResult.getChildren().isEmpty()) {
            for(int i = 0; i < xResult.getChildren().size(); i++) {
                ExtractionResult childResult = xResult.getChildren().get(i);
                ExtractionResultNameResolver nameResolver = new ExtractionResultNameResolver(nameConfig, logger);
                String childPlotName = nameResolver.getChildName(name, childResult, i);
                logger.logDebug("name: " + name, CLASSNAME);
                logger.logDebug("childPlotName: " + childPlotName, CLASSNAME);
                PlotWindowModel updatedChildPlot =
                    applyExtractionResultsToChildPlots(childPlotName, childResult, yResult, metadataResults);
                if(updatedChildPlot != null) {
                    childPlots.add(updatedChildPlot);
                }
            }
        }
        return childPlots;
    }

    private void applyCanvasLayoutDisplaySettingsToPlot(PlotWindowModel windowModel) {
        int rowCount = windowModel.getRowCount();
        for(PlotCanvasModel canvasModel : windowModel.getCanvasModels()) {
            int rowPosition = canvasModel.getInvertedRowPosition();
            int colPosition = canvasModel.getColPosition();

            boolean aboveBottomEdge = rowPosition > 0;
            if(aboveBottomEdge) {
                PlotCanvasModel bottomEdgePlotCanvas = windowModel.getCanvasModel(rowCount-1, colPosition, false);
                if(rangesMatch(bottomEdgePlotCanvas, canvasModel, Dimension.X)) {
                    hideAxis(canvasModel, Dimension.X);
                }
            }

            boolean awayFromLeftEdge = colPosition > 0;
            if(awayFromLeftEdge) {
                PlotCanvasModel leftEdgePlotCanvas = windowModel.getCanvasModel(rowPosition, 0, false);
                if(rangesMatch(leftEdgePlotCanvas, canvasModel, Dimension.Y)) {
                    hideAxis(canvasModel, Dimension.Y);
                }
            }
        }
    }

    private boolean rangesMatch(PlotCanvasModel canvasModel1, PlotCanvasModel canvasModel2, Dimension dim) {
        String smallestPoint1 = canvasModel1.getSmallestValueAcrossTraces(dim);
        String largestPoint1 = canvasModel1.getLargestValueAcrossTraces(dim);
        String smallestPoint2 = canvasModel2.getSmallestValueAcrossTraces(dim);
        String largestPoint2 = canvasModel2.getLargestValueAcrossTraces(dim);

        return smallestPoint1.equals(smallestPoint2) && largestPoint1.equals(largestPoint2);
    }

    private void hideAxis(PlotCanvasModel canvasModel, Dimension dim) {
        if(dim == Dimension.X) {
            canvasModel.setDrawXAxisLabels(false);
        } else if(dim == Dimension.Y) {
            canvasModel.setDrawYAxisLabels(false);
        }
    }
}