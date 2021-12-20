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
import java.util.Random;
import java.util.Set;

import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.config.derivative.DerivativeLine;
import gov.sandia.watchr.config.derivative.DerivativeLineType;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.extractors.ExtractionResult;
import gov.sandia.watchr.util.RGB;

/**
 * Data line generator for area plots. Area plots display child lines as solid,
 * colored areas underneath a main "parent" line. This type of data line
 * generator makes extensive use of {@link DerivativeLine}s.<br>
 * <br>
 * Note: There is currently no structural way to force users to show a
 * relationship between a line on the parent window, and lines on all the
 * underlying child windows. Therefore, it is not possible to sum the children
 * and compare against the parent value and warn the user of discrepancies
 * between such a parent line and the child lines. It will be up to users to
 * identify these problems visually by looking at the generated graphs.
 * 
 * @author Elliott Ridgway
 */
public class AreaPlotDataLineGenerator extends ScatterPlotDataLineGenerator {

    ////////////
    // FIELDS //
    ////////////

    private Random rand = new Random();

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public AreaPlotDataLineGenerator(PlotConfig plotConfig, String reportAbsPath, IDatabase db) {
        super(plotConfig, reportAbsPath, db);
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    protected void newPlotTraceModel(
            PlotCanvasModel parentCanvas, String traceName,
            String xValue, String yValue, Map<String, ExtractionResult> metadataResults) {
        super.newPlotTraceModel(parentCanvas, traceName, xValue, yValue, metadataResults);
        
        int newTraceIndex = parentCanvas.getTraceModels().size() - 1;
        PlotTraceModel newTrace = parentCanvas.getTraceModels().get(newTraceIndex);
        RGB color = line.getColor();
        RGB randomColor = new RGB(rand.nextInt(200), rand.nextInt(200), rand.nextInt(200));
        newTrace.setPrimaryRGB(color == null ? randomColor : color);
    }

    @Override
    protected void applyDerivativeLinesToPlot(
            PlotWindowModel windowModel, List<DerivativeLine> derivativeLines) throws WatchrParseException {
        super.applyDerivativeLinesToPlot(windowModel, derivativeLines);

        for(PlotCanvasModel canvasModel : windowModel.getCanvasModels()) {
            boolean firstTime = anyChildPreviewDerivativeLinesExist(canvasModel);
            if(firstTime) {
                createNewChildPreviewDerivativeLine(windowModel, canvasModel);
            } else {
                updateChildPreviewDerivativeLine(windowModel, canvasModel);
            }
        }
    }

    /////////////
    // PRIVATE //
    /////////////

    private List<PlotTraceModel> getChildTraces(PlotWindowModel windowModel) {
        List<PlotTraceModel> childTraces = new ArrayList<>();
        for(PlotCanvasModel canvasModel : windowModel.getCanvasModels()) {
            for(PlotTraceModel traceModel : canvasModel.getNonDerivativeTraceModels()) {
                childTraces.add(traceModel);
            }
        }
        return childTraces;
    }

    private boolean anyChildPreviewDerivativeLinesExist(PlotCanvasModel parentCanvas) {
        for(PlotTraceModel traceModel : parentCanvas.getTraceModels()) {
            if(traceModel.getDerivativeLineType() == DerivativeLineType.CHILD_PREVIEW) {
                return false;
            }
        }
        return true;
    }

    private void createNewChildPreviewDerivativeLine(PlotWindowModel parentWindow, PlotCanvasModel parentCanvas) {
        Set<PlotWindowModel> childWindows = db.getChildren(parentWindow, category);
        for(PlotWindowModel childWindow : childWindows) {
            List<PlotTraceModel> childTraces = getChildTraces(childWindow);
            for(PlotTraceModel childTrace : childTraces) {
                if(childTrace.getDerivativeLineType() == null) {
                    PlotTraceModel newChildDerivativeTraceModel = new PlotTraceModel(parentCanvas.getUUID(), childTrace);
                    newChildDerivativeTraceModel.setName(childWindow.getName());
                    newChildDerivativeTraceModel.setDerivativeLineType(DerivativeLineType.CHILD_PREVIEW);
                    newChildDerivativeTraceModel.set(PlotToken.TRACE_POINT_TYPE, PlotType.AREA_PLOT);
                    break;
                }
            }
        }
    }

    private void updateChildPreviewDerivativeLine(PlotWindowModel parentWindow, PlotCanvasModel parentCanvas) {
        for(PlotTraceModel traceModel : parentCanvas.getTraceModels()) {
            Set<PlotWindowModel> childWindows = db.getChildren(parentWindow, category);
            for(PlotWindowModel childWindow : childWindows) {
                List<PlotTraceModel> childTraces = getChildTraces(childWindow);                
                for(PlotTraceModel childTrace : childTraces) {
                    if(childTrace.getDerivativeLineType() == null &&
                        childWindow.getName().equals(traceModel.getName()) &&
                        traceModel.getDerivativeLineType() == DerivativeLineType.CHILD_PREVIEW) {

                        traceModel.clear();
                        traceModel.addAll(childTrace.getPoints());
                        break;
                    }
                }
            }
        }
    }
}