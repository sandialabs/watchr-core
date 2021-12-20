/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators;

import java.util.ArrayList;
import java.util.List;

import gov.sandia.watchr.config.derivative.DerivativeLine;
import gov.sandia.watchr.config.derivative.DerivativeLineFactory;
import gov.sandia.watchr.config.derivative.DerivativeLineType;
import gov.sandia.watchr.config.derivative.RollingDerivativeLine;
import gov.sandia.watchr.config.derivative.SlopeDerivativeLine;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.graph.chartreuse.Dimension;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceOptions;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.WatchrParseException;

public class DerivativeLineGenerator extends AbstractGenerator<List<DerivativeLine>> {

    ////////////
    // FIELDS //
    ////////////

    private PlotTraceModel traceModel;
    private final List<WatchrDiff<?>> diffs;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public DerivativeLineGenerator(PlotTraceModel traceModel, ILogger logger) {
        super(logger);
        this.traceModel = traceModel;
        this.diffs = new ArrayList<>();
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void generate(List<DerivativeLine> derivativeLines, List<WatchrDiff<?>> diffs) throws WatchrParseException {
        this.diffs.clear();
        this.diffs.addAll(diffs);
        updateDerivativeLine(traceModel, derivativeLines);
    }

    /////////////
    // PRIVATE //
    /////////////

    private void updateDerivativeLine(PlotTraceModel traceModel, List<DerivativeLine> derivativeLines) {
        logger.logDebug("DerivativeLineGenerator.updateDerivativeLine()");
        PlotCanvasModel canvasModel = traceModel.getParent();
        if(canvasModel != null) {
            logger.logDebug("Look for associated derivative line.");
            for(DerivativeLine derivativeLine : derivativeLines) {
                DerivativeLineType type = DerivativeLineFactory.getInstance().getTypeFromObject(derivativeLine);
                PlotTraceModel derivateLineTraceModel = canvasModel.findDerivativeLine(type);
                logger.logDebug("Looking for derivative line of type " + type.toString());
                if(derivateLineTraceModel != null) {
                    logger.logDebug("We found the associated derivative line, now recalculate it.");
                    recalculateDerivativeLine(traceModel, derivateLineTraceModel, derivativeLine);
                } else {
                    logger.logDebug("We couldn't find the associated derivative line, so create a new one.");
                    createNewDerivativeLine(traceModel, derivativeLine);
                }

                if(derivateLineTraceModel != null) {
                    logger.logDebug("Check to see if derivative line color has diffed...");
                    if(diffed(derivativeLine, diffs, DiffCategory.DERIVATIVE_LINE_COLOR)) {
                        derivateLineTraceModel.setPrimaryRGB(derivativeLine.getColor());
                    }
                }
            }
        }
    }

    private void createNewDerivativeLine(PlotTraceModel mainReferenceTrace,
            DerivativeLine derivativeLineConfiguration) {
        logger.logDebug("DerivativeLineGenerator.createNewDerivativeLine()");

        PlotTraceModel newDerivativeTrace = new PlotTraceModel(mainReferenceTrace.getParentUUID());
        DerivativeLineType type = DerivativeLineFactory.getInstance().getTypeFromObject(derivativeLineConfiguration);

        newDerivativeTrace
            .setName(mainReferenceTrace.getName())
            .setDerivativeLineType(type);

        newDerivativeTrace.set(PlotToken.TRACE_POINT_TYPE, PlotType.SCATTER_PLOT);
        newDerivativeTrace.set(PlotToken.TRACE_POINT_MODE, "Circle");
        newDerivativeTrace.set(PlotToken.TRACE_DRAW_LINES, true);

        newDerivativeTrace.setPrimaryRGB(derivativeLineConfiguration.getColor());

        if(!mainReferenceTrace.getPoints().isEmpty()) {
            logger.logDebug("Calculate new derivative line for graph with " + mainReferenceTrace.getPoints().size() + " points.");
            recalculateDerivativeLine(mainReferenceTrace, newDerivativeTrace, derivativeLineConfiguration);
        }
    }

    private void recalculateDerivativeLine(
            PlotTraceModel mainDataLine, PlotTraceModel derivativeLine,
            DerivativeLine derivativeLineConfiguration) {
        logger.logDebug("DerivativeLineGenerator.recalculateDerivativeLine()");

        if(!mainDataLine.isEmpty2D()) {
            logger.logDebug("Line is not empty, so proceed.");
            PlotTraceOptions options = new PlotTraceOptions();
            options.sortAlongDimension = Dimension.X;

            if(derivativeLineConfiguration instanceof RollingDerivativeLine) {
                logger.logDebug("Configuration is RollingDerivativeLine");
                RollingDerivativeLine rollingLineConfig = (RollingDerivativeLine) derivativeLineConfiguration;

                logger.logDebug("Retrieve points from the original ine, with options...");
                logger.logDebug("Line size: " + mainDataLine.getPoints().size());
                List<PlotTracePoint> points = new ArrayList<>(mainDataLine.getPoints(options));
                List<PlotTracePoint> newDerivativeLinePoints = rollingLineConfig.calculateRollingLine(points);

                derivativeLine.clear();
                derivativeLine.addAll(newDerivativeLinePoints);
                
            } else if(derivativeLineConfiguration instanceof SlopeDerivativeLine) {
                logger.logDebug("Configuration is SlopeDerivativeLine");
                SlopeDerivativeLine slopeLineConfig = (SlopeDerivativeLine) derivativeLineConfiguration;
                List<PlotTracePoint> points = new ArrayList<>(mainDataLine.getPoints(options));
                List<PlotTracePoint> newDerivativeLinePoints = slopeLineConfig.calculateSlopeLine(points);

                derivativeLine.clear();
                derivativeLine.addAll(newDerivativeLinePoints);
            }  
        } else {
            logger.logDebug("Main data line had no points.");
        }
    }    
}
