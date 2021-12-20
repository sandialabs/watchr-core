/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators.line;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import gov.sandia.watchr.config.DataLine;
import gov.sandia.watchr.config.MetadataConfig;
import gov.sandia.watchr.config.NameConfig;
import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.config.RuleConfig;
import gov.sandia.watchr.config.PlotConfig.CanvasLayout;
import gov.sandia.watchr.config.derivative.DerivativeLine;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.extractors.ExtractionResult;
import gov.sandia.watchr.parse.extractors.strategy.AmbiguityStrategy;
import gov.sandia.watchr.parse.generators.AbstractGenerator;
import gov.sandia.watchr.util.DateUtil;
import gov.sandia.watchr.util.RGB;

public abstract class DataLineGenerator extends AbstractGenerator<DataLine>{
    
    ////////////
    // FIELDS //
    ////////////

    protected final PlotConfig plotConfig;
    protected final String reportAbsPath;
    protected final IDatabase db;
    protected final List<PlotWindowModel> rootPlots;

    protected DataLine line;
    protected final List<WatchrDiff<?>> diffs;

    protected String name = "";
    protected NameConfig nameConfig = null;

    protected List<RuleConfig> rules;
    protected String category = "";
    protected Boolean useLegend = false; 

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    protected DataLineGenerator(PlotConfig plotConfig, String reportAbsPath, IDatabase db) {
        super(plotConfig.getLogger());

        this.plotConfig = plotConfig;
        this.reportAbsPath = reportAbsPath;
        this.db = db;

        this.rootPlots = new ArrayList<>();
        this.diffs = new ArrayList<>();

        this.name = plotConfig.getName();
        this.category = plotConfig.getCategory();
        this.nameConfig = plotConfig.getNameConfig();
        this.useLegend = plotConfig.shouldUseLegend();

        this.rules = new ArrayList<>();
        this.rules.addAll(plotConfig.getPlotRules());        
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
        logger.logDebug("DataLineGenerator.generate()");
        this.line = line;
        this.diffs.clear();
        this.diffs.addAll(diffs);

        boolean dependsOnTemplate = StringUtils.isNotBlank(line.getInheritTemplate());
        if(dependsOnTemplate) {
            TemplateDataLineGenerator templateDataLineGenerator =
                new TemplateDataLineGenerator(plotConfig.getDataLines(), logger);
            this.line = templateDataLineGenerator.handleDataLineGenerationForTemplate(line);
        }

        List<ExtractionResult> xResults = this.line.getXExtractor().extract(reportAbsPath);
        List<ExtractionResult> yResults = this.line.getYExtractor().extract(reportAbsPath);

        Map<String, ExtractionResult> metadataResults = new HashMap<>();
        for(MetadataConfig metadata : this.line.getMetadata()) {
            List<ExtractionResult> results = metadata.getMetadataExtractor().extract(reportAbsPath);
            if(results != null && !results.isEmpty()) {
                // Note: There can be only one valid value for a piece of metadata, so
                // we only keep the first entry in the list.
                ExtractionResult metadataResult = results.get(0);
                metadataResults.put(metadata.getName(), metadataResult);
            }
        }

        rootPlots.clear();
        rootPlots.addAll(applyExtractionResultsToRootPlots(xResults, yResults, metadataResults));

        logger.logDebug("Update diffable properties for " + rootPlots.size() + " plots that were updated...");
        updateDiffableProperties();
        logger.logDebug("DONE: DataLineGenerator.generate()");
    }

    /////////////
    // UTILITY //
    /////////////

    protected PlotTraceModel findPlotTraceModel(PlotWindowModel parentWindow, String traceName) {
        for(PlotCanvasModel canvasModel : parentWindow.getCanvasModels()) {
            for(PlotTraceModel traceModel : canvasModel.getTraceModels()) {
                if(traceModel.getName().equals(traceName)) {
                    return traceModel;
                }
            }
        }
        return null;
    }    

    protected CombinationStrategy getCombinationStrategy(List<ExtractionResult> xResults, List<ExtractionResult> yResults) {
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
        return comboStrategy;
    }

    protected void applyMetadataToDatabasePlot(PlotTracePoint targetPoint, Map<String, ExtractionResult> metadataResults) {
        for(Entry<String, ExtractionResult> metadataResult : metadataResults.entrySet()) {
            String key = metadataResult.getKey();
            String value = metadataResult.getValue().getValue();

            targetPoint.metadata.put(key, value);
        }
    }

    protected void updatePlotTraceModel(
            PlotTraceModel traceModel,
            ExtractionResult xResult, ExtractionResult yResult,
            Map<String, ExtractionResult> metadataResults) {

        if(xResult != null && yResult != null) {
            String xValue = formatValue(xResult.getValue(), line.getXExtractor().getProperty(Keywords.FORMAT_AS));
            String yValue = formatValue(yResult.getValue(), line.getYExtractor().getProperty(Keywords.FORMAT_AS));
            if(!traceModel.containsPoint(xValue, yValue)) {
                PlotTracePoint newPoint = new PlotTracePoint(xValue, yValue);
                traceModel.add(newPoint);
                applyMetadataToDatabasePlot(newPoint, metadataResults);
            }
        }
    }

    protected String formatValue(String original, String formatProperty) {
        String formattedValue = StringUtils.isBlank(formatProperty) ? original : format(original, formatProperty);
        if(DateUtil.isTimestamp(formattedValue) && !DateUtil.isTimestampValid(formattedValue)) {
            String warning = formattedValue + " appears to be a timestamp, but is not properly formatted.";
            logger.logWarning(warning);
        }
        return formattedValue;
    }

    protected String format(String original, String formatType) {
        if(formatType.equalsIgnoreCase("timestamp")) {
            return convertFromEpochTimeSecondsToTimestamp(original);
        } else if(formatType.equalsIgnoreCase("timestamp_ms")) {
            return convertFromEpochTimeMillisToTimestamp(original);
        }
        return original;
    }

    protected String convertFromEpochTimeSecondsToTimestamp(String original) {
        if(NumberUtils.isCreatable(original)) {
            if(original.contains(".")) {
                original = original.substring(0, original.indexOf("."));
            }
            Long originalAsLong = Long.parseLong(original);
            return DateUtil.epochTimeSecondsToTimestamp(originalAsLong);
        }
        return original;
    }

    protected String convertFromEpochTimeMillisToTimestamp(String original) {
        if(NumberUtils.isCreatable(original)) {
            if(original.contains(".")) {
                original = original.substring(0, original.indexOf("."));
            }
            Long originalAsLong = Long.parseLong(original);
            return DateUtil.epochTimeToTimestamp(originalAsLong);
        }
        return original;
    }

    protected PlotCanvasModel newCanvasModel(PlotWindowModel windowModel) {
        return newCanvasModel(windowModel, plotConfig.getCanvasLayout(), plotConfig.getCanvasPerRow());
    }

    protected PlotCanvasModel newCanvasModel(PlotWindowModel windowModel, CanvasLayout canvasLayout) {
        return newCanvasModel(windowModel, canvasLayout, plotConfig.getCanvasPerRow());
    }

    protected PlotCanvasModel newCanvasModel(PlotWindowModel windowModel, CanvasLayout canvasLayout, int canvasGridRowSize) {
        if(canvasLayout == CanvasLayout.GRID) {
            if(windowModel.getCanvasModels().isEmpty()) {
                return new PlotCanvasModel(windowModel.getUUID());
            } else {
                int nextRow = windowModel.getNextCanvasRow(canvasGridRowSize);
                int nextColumn = windowModel.getNextCanvasColumn(canvasGridRowSize);
                
                PlotCanvasModel newCanvas = new PlotCanvasModel(windowModel.getUUID());
                newCanvas.setRowPosition(nextRow)
                         .setColPosition(nextColumn);
                return newCanvas;
            }
        } else if(canvasLayout == CanvasLayout.INDEPENDENT) {
            PlotCanvasModel baseCanvas = null;
            if(windowModel.getCanvasModels().isEmpty()) {
                baseCanvas = new PlotCanvasModel(windowModel.getUUID());
                return baseCanvas;
            } else {
                baseCanvas = windowModel.getCanvasModels().get(0);
                PlotCanvasModel overlaidCanvasModel = new PlotCanvasModel(windowModel.getUUID());
                baseCanvas.addOverlaidCanvasModel(overlaidCanvasModel);
                return overlaidCanvasModel;
            }
        } else if(canvasLayout == CanvasLayout.STACKX) {
            int nextColumn = windowModel.getCanvasModels().size();
            PlotCanvasModel newCanvas = new PlotCanvasModel(windowModel.getUUID());
            newCanvas.setColPosition(nextColumn);
            return newCanvas;
        } else if(canvasLayout == CanvasLayout.STACKY) {
            int nextRow = windowModel.getCanvasModels().size();
            PlotCanvasModel newCanvas = new PlotCanvasModel(windowModel.getUUID());
            newCanvas.setRowPosition(nextRow);
            return newCanvas;
        } else {
            PlotCanvasModel newCanvas = null;
            if(windowModel.getCanvasModels().isEmpty()) {
                newCanvas = new PlotCanvasModel(windowModel.getUUID());
            } else {
                newCanvas = windowModel.getCanvasModels().get(0);
            }
            return newCanvas;
        }
    }    

    /////////////
    // PRIVATE //
    /////////////
    
    private void updateDiffableProperties() throws WatchrParseException {
        boolean anythingDiffed =
            diffed(line, diffs, DiffCategory.DERIVATIVE_LINES) ||
            diffed(line, diffs, DiffCategory.LINE_COLOR);

        if(anythingDiffed) {
            List<PlotWindowModel> allPlots = db.getAllPlots();
            for(int i = 0; i < allPlots.size(); i++) {
                PlotWindowModel windowModel = allPlots.get(i);

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
                    }
                }
                db.updatePlot(windowModel);
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

    //////////////
    // ABSTRACT //
    //////////////

    protected abstract List<PlotWindowModel> applyExtractionResultsToRootPlots(
        List<ExtractionResult> xResults, List<ExtractionResult> yResults,
        Map<String, ExtractionResult> metadataResults) throws WatchrParseException;

    protected abstract void applyDerivativeLinesToPlot(
        PlotWindowModel windowModel, List<DerivativeLine> derivativeLines) throws WatchrParseException;
}
