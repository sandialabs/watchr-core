/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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
import gov.sandia.watchr.graph.chartreuse.Dimension;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.extractors.ExtractionResult;
import gov.sandia.watchr.parse.extractors.ExtractionResultNameResolver;
import gov.sandia.watchr.parse.generators.DerivativeLineGenerator;
import gov.sandia.watchr.parse.generators.line.CombinationStrategy;
import gov.sandia.watchr.parse.generators.line.DataLineGenerator;
import gov.sandia.watchr.util.RGB;

public class ScatterPlotDataLineGenerator extends DataLineGenerator {

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
    protected List<PlotWindowModel> applyExtractionResultsToRootPlots(
            List<ExtractionResult> xResults, List<ExtractionResult> yResults,
            Map<String, ExtractionResult> metadataResults) throws WatchrParseException {
        logger.logDebug("ScatterPlotDataLineGenerator.applyExtractionResultsToRootPlots()");

        List<PlotWindowModel> windowModels = new ArrayList<>();
        if(xResults != null && yResults != null) {
            CombinationStrategy comboStrategy = getCombinationStrategy(xResults, yResults);

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

        logger.logDebug("DONE: ScatterPlotDataLineGenerator.applyExtractionResultsToRootPlots()");
        return windowModels;
    }

    @Override
    protected void applyDerivativeLinesToPlot(
            PlotWindowModel windowModel, List<DerivativeLine> derivativeLines) throws WatchrParseException {
        logger.logDebug("ScatterPlotDataLineGenerator.applyDerivativeLinesToPlot()");
        logger.logDebug("Generate derivative lines for plot " + windowModel.getName() + "...");
        for(PlotCanvasModel canvasModel : windowModel.getCanvasModels()) {
            for(PlotTraceModel traceModel : canvasModel.getNonDerivativeTraceModels()) {
                logger.logDebug("Generate derivative lines for trace " + traceModel.getName() + "...");
                DerivativeLineGenerator derivativeLineGenerator = new DerivativeLineGenerator(traceModel, logger);
                derivativeLineGenerator.generate(derivativeLines, diffs);
                logger.logDebug("Done generating derivative line.");
            }
        }
        logger.logDebug("DONE: ScatterPlotDataLineGenerator.applyDerivativeLinesToPlot()");
    }  

    /////////////
    // PRIVATE //
    /////////////

    private List<PlotWindowModel> applyExtractionResultsToRootPlotsViaFullCombinatorial(
        List<ExtractionResult> xResults, List<ExtractionResult> yResults, Map<String, ExtractionResult> metadataResults)
        throws WatchrParseException {

        logger.logDebug("ScatterPlotDataLineGenerator.applyExtractionResultsToRootPlotsViaFullCombinatorial()");
        logger.logDebug("Number of X results: " + xResults.size());
        logger.logDebug("Number of Y results: " + yResults.size());
        List<PlotWindowModel> windowModels = new ArrayList<>();
        for(ExtractionResult xResult : xResults) {
            if(xResult != null) {
                for(ExtractionResult yResult : yResults) {
                    if(yResult != null) {

                        String plotName = name;
                        logger.logDebug("Name: " + plotName);
                        if(StringUtils.isBlank(plotName)) {
                            ExtractionResultNameResolver nameResolver = new ExtractionResultNameResolver(nameConfig, logger);
                            plotName = nameResolver.getName(xResult, yResult, "plot_");
                            logger.logDebug("Name after name resolver: " + plotName);
                        }

                        PlotWindowModel windowModel =
                            applyExtractionResultsToChildPlots(plotName, xResult, yResult, metadataResults);
                        windowModels.add(windowModel);
                    }
                }
            }
        }

        logger.logDebug("DONE: ScatterPlotDataLineGenerator.applyExtractionResultsToRootPlotsViaFullCombinatorial()");
        return windowModels;
    }

    private List<PlotWindowModel> applyExtractionResultsToRootPlotsViaIteration(
        List<ExtractionResult> xResults, List<ExtractionResult> yResults,
        Map<String, ExtractionResult> metadataResults)
        throws WatchrParseException {
        logger.logDebug("ScatterPlotDataLineGenerator.applyExtractionResultsToRootPlotsViaIteration()");
        List<PlotWindowModel> windowModels = new ArrayList<>();

        logger.logDebug("Number of X results: " + xResults.size());
        logger.logDebug("Number of Y results: " + yResults.size());
        for(int i = 0; i < xResults.size() && i < yResults.size(); i++) {
            ExtractionResult xResult = xResults.get(i);
            ExtractionResult yResult = yResults.get(i);
            if(xResult != null && yResult != null) {

                String plotName = name;
                logger.logDebug("Name: " + plotName);
                if(StringUtils.isBlank(plotName)) {
                    ExtractionResultNameResolver nameResolver = new ExtractionResultNameResolver(nameConfig, logger);
                    plotName = nameResolver.getName(xResult, yResult, "plot_", i);
                    logger.logDebug("Name after name resolver: " + plotName);
                }

                PlotWindowModel windowModel =
                    applyExtractionResultsToChildPlots(plotName, xResult, yResult, metadataResults, i);
                windowModels.add(windowModel);
            }
        }
        logger.logDebug("DONE: ScatterPlotDataLineGenerator.applyExtractionResultsToRootPlotsViaIteration()");
        return windowModels;
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

        logger.logDebug("ScatterPlotDataLineGenerator.applyExtractionResultsToChildPlots()");

        logger.logDebug("Looking for plot " + plotName + " in category " + category + "...");
        PlotWindowModel windowModel = db.searchPlot(plotName, category);
        if(windowModel == null) {
            logger.logInfo("Could not find plot with name \"" + plotName + "\" in category \"" + category + "\".  Creating a new one...");
            windowModel = newPlotWindowModel(xResult, yResult, metadataResults, resultIndex);
            if(windowModel != null) {
                db.addPlot(windowModel);
            }
        } else {
            logger.logDebug("Updating existing plot. First, resolve data line name...");
            String dataLineName = resolveDataLineName(xResult, yResult);
            logger.logDebug("Data line name is " + dataLineName);
            PlotTraceModel traceModel = findPlotTraceModel(windowModel, dataLineName);
            if(traceModel != null) {
                logger.logDebug("Update the existing data line trace model...");
                updatePlotTraceModel(traceModel, xResult, yResult, metadataResults);
            } else {
                logger.logDebug("Create a new data line trace model...");
                PlotCanvasModel canvasModel = newCanvasModel(windowModel);
                String xValue = formatValue(xResult.getValue(), line.getXExtractor().getProperty(Keywords.FORMAT_AS));
                String yValue = formatValue(yResult.getValue(), line.getYExtractor().getProperty(Keywords.FORMAT_AS));
                newPlotTraceModel(canvasModel, dataLineName, xValue, yValue, metadataResults);
            }
        }

        boolean isXRecursive = line.getXExtractor().getAmbiguityStrategy().shouldRecurseToChildGraphs();
        boolean isYRecursive = line.getYExtractor().getAmbiguityStrategy().shouldRecurseToChildGraphs();

        if(isXRecursive && isYRecursive) {
            throw new UnsupportedOperationException("Cannot simultaneously recurse to child graphs in both X and Y directions!");
        }

        List<PlotWindowModel> childPlots = new ArrayList<>();
        if(isYRecursive && yResult != null && !yResult.getChildren().isEmpty()) {
            for(int i = 0; i < yResult.getChildren().size(); i++) {
                ExtractionResult childResult = yResult.getChildren().get(i);
                ExtractionResultNameResolver nameResolver = new ExtractionResultNameResolver(nameConfig, logger);
                String childPlotName = nameResolver.getChildName(name, childResult, i);
                logger.logDebug("name: " + name);
                logger.logDebug("childPlotName: " + childPlotName);
                PlotWindowModel updatedChildPlot =
                    applyExtractionResultsToChildPlots(childPlotName, xResult, childResult, metadataResults);
                childPlots.add(updatedChildPlot);
            }
        } else if(isXRecursive && xResult != null && !xResult.getChildren().isEmpty()) {
            for(int i = 0; i < xResult.getChildren().size(); i++) {
                ExtractionResult childResult = xResult.getChildren().get(i);
                ExtractionResultNameResolver nameResolver = new ExtractionResultNameResolver(nameConfig, logger);
                String childPlotName = nameResolver.getChildName(name, childResult, i);
                logger.logDebug("name: " + name);
                logger.logDebug("childPlotName: " + childPlotName);
                PlotWindowModel updatedChildPlot =
                    applyExtractionResultsToChildPlots(childPlotName, childResult, yResult, metadataResults);
                childPlots.add(updatedChildPlot);
            }
        }

        if(windowModel != null) {
            applyCanvasLayoutDisplaySettingsToPlot(windowModel);
        }

        // Update database's parent-child linkage between plot models.
        if(!childPlots.isEmpty() && windowModel != null) {
            logger.logDebug("Update database's parent-child linkage between plot models.");
            db.setPlotsAsChildren(windowModel, childPlots);
        }

        // Update derivative lines on this plot.
        logger.logDebug("Apply derivative lines to plot.");
        applyDerivativeLinesToPlot(windowModel, line.getDerivativeLines());

        logger.logDebug("DONE: ScatterPlotDataLineGenerator.applyExtractionResultsToChildPlots()");
        return windowModel;
    }

    private String resolveDataLineName(ExtractionResult xResult, ExtractionResult yResult) {
        String dataLineName = line.getName();
        if(StringUtils.isBlank(dataLineName) && line.getNameConfig() != null) {
            ExtractionResultNameResolver nameResolver = new ExtractionResultNameResolver(line.getNameConfig(), logger);
            dataLineName = nameResolver.getName(xResult, yResult, "line_");
        }
        return dataLineName;
    }

    private PlotWindowModel newPlotWindowModel(
        ExtractionResult xResult, ExtractionResult yResult,
        Map<String, ExtractionResult> metadataResults, int resultIndex) {

        if(xResult != null && yResult != null) {

            String plotName = name;
            if(StringUtils.isBlank(plotName)) {
                ExtractionResultNameResolver nameResolver = new ExtractionResultNameResolver(nameConfig, logger);
                plotName = nameResolver.getName(xResult, yResult, "plot_", resultIndex);
            }
            PlotWindowModel newWindow = new PlotWindowModel(plotName);
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
            newPlotTraceModel(newCanvas, dataLineName, xValue, yValue, metadataResults);
            
            return newWindow;
        }
        return null;
    }

    protected void newPlotTraceModel(
            PlotCanvasModel parentCanvas, String traceName,
            String xValue, String yValue, Map<String, ExtractionResult> metadataResults) {

        PlotTraceModel newTrace = new PlotTraceModel(parentCanvas.getUUID());
        newTrace.setName(traceName);
        RGB color = line.getColor();
        newTrace.setPrimaryRGB(color == null ? new RGB(0,0,0) : color);
        newTrace.set(PlotToken.TRACE_POINT_TYPE, PlotType.SCATTER_PLOT);
        newTrace.set(PlotToken.TRACE_POINT_MODE, "Circle");
        newTrace.set(PlotToken.TRACE_DRAW_LINES, true);

        PlotTracePoint newPoint = new PlotTracePoint(xValue, yValue);
        newTrace.add(newPoint);

        applyMetadataToDatabasePlot(newPoint, metadataResults);
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