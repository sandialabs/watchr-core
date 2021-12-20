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
import org.apache.commons.lang3.math.NumberUtils;

import gov.sandia.watchr.config.DataLine;
import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.config.PlotConfig.CanvasLayout;
import gov.sandia.watchr.config.derivative.DerivativeLine;
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
import gov.sandia.watchr.parse.extractors.ExtractionResultNameResolver;
import gov.sandia.watchr.parse.generators.line.DataLineGenerator;

/**
 * A tree map generator for Watchr.  We make some implicit assumptions about our
 * configuration's extractors:<br>
 * <ul>
 * <li>The X extractor should be used to designate "moments" at which we should create
 * new treemaps.  For example, creating a new treemap each time a new report file
 * is added.  Therefore, the X extractor cannot be recursive.</li>
 * <li>The Y extractor should be used to extract the entire treemap hierarchy.
 * Therefore, it should be recursive (though this is not strictly enforced).</li>
 * </ul>
 * 
 * @author Elliott Ridgway
 */
public class TreeMapDataLineGenerator extends DataLineGenerator {

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public TreeMapDataLineGenerator(PlotConfig plotConfig, String reportAbsPath, IDatabase db) {
        super(plotConfig, reportAbsPath, db);
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    protected List<PlotWindowModel> applyExtractionResultsToRootPlots(List<ExtractionResult> xResults,
            List<ExtractionResult> yResults, Map<String, ExtractionResult> metadataResults)
            throws WatchrParseException {
        
        List<PlotWindowModel> windowModels = new ArrayList<>();
        if(xResults != null) {
            for(int i = 0; i < xResults.size(); i++) {
                ExtractionResult xResult = xResults.get(i);
                if(xResult != null) {
                    PlotWindowModel windowModel = newPlotWindowModel(line, xResult, yResults, metadataResults, i);
                    windowModels.add(windowModel);
                    db.addPlot(windowModel);
                    
                    boolean isXRecursive =
                        line.getXExtractor().getAmbiguityStrategy().shouldRecurseToChildGraphs();

                    if(isXRecursive) {
                        logger.logWarning("It appears your treemap plot's X extractor is configured to be recursive.  This is a no-op for treemaps.");
                    }
                }
            }
        }
        return windowModels;
    }

    @Override
    protected void applyDerivativeLinesToPlot(PlotWindowModel windowModel, List<DerivativeLine> derivativeLines)
            throws WatchrParseException {
        // Do nothing.  Derivative lines cannot be applied to treemaps.
    }    

    /////////////
    // PRIVATE //
    /////////////

    private PlotWindowModel newPlotWindowModel(
            DataLine line, ExtractionResult xResult, List<ExtractionResult> yResults,
            Map<String, ExtractionResult> metadataResults, int resultIndex) {
    
        String plotName = plotConfig.getName();
        if(StringUtils.isBlank(plotName) && xResult != null) {
            ExtractionResultNameResolver nameResolver = new ExtractionResultNameResolver(nameConfig, logger);
            plotName = nameResolver.determineTargetName(nameConfig, xResult, null, resultIndex);
        }
        if(StringUtils.isBlank(plotName)) {
            throw new IllegalStateException("Plot name cannot be blank!");
        }

        PlotWindowModel newWindow = new PlotWindowModel(plotName);
        newWindow.setCategory(category);
        if(useLegend != null) {
            newWindow.setLegendVisible(useLegend);
        }
        
        PlotCanvasModel newCanvas = newCanvasModel(newWindow, CanvasLayout.SHARED);
        newCanvas.setXAxisLabel(line.getXExtractor().getProperty(Keywords.UNIT));
        newCanvas.setYAxisLabel(line.getYExtractor().getProperty(Keywords.UNIT));

        newPlotTraceModel(newCanvas, yResults, metadataResults);
        return newWindow;
    }

    private void newPlotTraceModel(
            PlotCanvasModel parentCanvas, List<ExtractionResult> yResults, Map<String, ExtractionResult> metadataResults) {        
        List<PlotTracePoint> treemapEntries = new ArrayList<>();
        getTreeMapEntries(yResults, "", treemapEntries, metadataResults);
        createTreeMapTraceModel(parentCanvas, treemapEntries);
    }

    private void getTreeMapEntries(
            List<ExtractionResult> yResults, String currentParent, List<PlotTracePoint> treemapEntries,
            Map<String, ExtractionResult> metadataResults) {

        if(yResults != null) {
            for(ExtractionResult yResult : yResults) {
                String[] pathElements = yResult.getPath().split("/");
                String lastPathElement = pathElements[pathElements.length - 1];
                String label = (StringUtils.isBlank(currentParent)) ? yResult.getPath() : lastPathElement;
                label = adjustTermIfAmbiguous(treemapEntries, label);

                String parentValue = yResult.getValue();
                String finalValue = yResult.getChildren().isEmpty() ? parentValue : Double.toString(sumChildren(yResult.getChildren()));
                if(StringUtils.isNotBlank(parentValue) && !parentValue.equals(finalValue)) {
                    String warning = "While creating treemap, we found a parent value (" + parentValue + ") that " +
                                     "does not equal the sum of its children values (" + finalValue + ").  We " +
                                     "choose to favor the sum of the children over the parent value, but be aware that this " +
                                     "may indicate a mistake in your data.";
                    logger.logWarning(warning);
                }

                PlotTracePoint point = new PlotTracePoint(label, currentParent, finalValue);
                applyMetadataToDatabasePlot(point, metadataResults);
                treemapEntries.add(point);

                // We use the X dimension to store labels, the Y dimension to store parents of those labels,
                // and the Z dimension to store the size values.
                getTreeMapEntries(yResult.getChildren(), label, treemapEntries, metadataResults);
            }
        }
    }

    private double sumChildren(List<ExtractionResult> yResults) {
        double sum = 0.0;
        for(ExtractionResult yResult : yResults) {
            if(yResult.getChildren().isEmpty()) {
                String value = yResult.getValue();
                if(NumberUtils.isCreatable(value)) {
                    sum += Double.parseDouble(value);
                }
            } else {
                sum += sumChildren(yResult.getChildren());
            }
        }
        return sum;
    }

    private String adjustTermIfAmbiguous(List<PlotTracePoint> treemapEntries, String newTerm) {
        boolean ambiguous = true;
        String replacementTerm = newTerm;
        int replacementTry = 0;

        while(ambiguous) {
            boolean found = false;
            for(PlotTracePoint point : treemapEntries) {
                if(point.x.equals(replacementTerm)) {
                    replacementTry++;
                    replacementTerm = newTerm + "_" + replacementTry;
                    found = true;
                    break;
                }
            }

            if(!found) {
                ambiguous = false;
            }
        }
        return replacementTerm;
    }

    private void createTreeMapTraceModel(PlotCanvasModel parentCanvas, List<PlotTracePoint> treemapEntries) {
        PlotTraceModel traceModel = new PlotTraceModel(parentCanvas.getUUID());
        traceModel.setName(line.getName());
        traceModel.set(PlotToken.TRACE_POINT_TYPE, PlotType.TREE_MAP);
        traceModel.setPoints(treemapEntries);
    }
}
