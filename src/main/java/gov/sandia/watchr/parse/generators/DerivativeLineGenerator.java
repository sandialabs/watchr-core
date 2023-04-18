/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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
import gov.sandia.watchr.graph.chartreuse.ChartreuseException;
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
    private static final String CLASSNAME = AbstractGenerator.class.getSimpleName();
    private boolean shouldSetUUID = true;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public DerivativeLineGenerator(PlotTraceModel traceModel, ILogger logger) {
        super(logger);
        this.traceModel = traceModel;
        this.diffs = new ArrayList<>();
    }

    /////////////
    // SETTERS //
    /////////////

    public void setShouldSetUUID(boolean shouldSetUUID) {
        this.shouldSetUUID = shouldSetUUID;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void generate(List<DerivativeLine> derivativeLines, List<WatchrDiff<?>> diffs) throws WatchrParseException {
        this.diffs.clear();
        this.diffs.addAll(diffs);
        try {
            updateDerivativeLine(traceModel, derivativeLines, shouldSetUUID);
        } catch(ChartreuseException e) {
            throw new WatchrParseException(e);
        }
    }

    /////////////
    // PRIVATE //
    /////////////

    private void updateDerivativeLine(
            PlotTraceModel traceModel, List<DerivativeLine> derivativeLines, boolean shouldSetUUID) throws ChartreuseException {
        logger.logDebug("DerivativeLineGenerator.updateDerivativeLine()", CLASSNAME);
        PlotCanvasModel canvasModel = traceModel.getParent();
        if(canvasModel != null) {
            logger.logDebug("Look for associated derivative line.", CLASSNAME);
            for(DerivativeLine derivativeLine : derivativeLines) {
                DerivativeLineType type = DerivativeLineFactory.getInstance().getTypeFromObject(derivativeLine);
                PlotTraceModel derivateLineTraceModel = canvasModel.findDerivativeLine(type);
                if(type != null) {
                    logger.logDebug("Looking for derivative line of type " + type.toString(), CLASSNAME);
                    if(derivateLineTraceModel != null) {
                        logger.logDebug("We found the associated derivative line, now recalculate it.", CLASSNAME);
                        recalculateDerivativeLine(traceModel, derivateLineTraceModel, derivativeLine);
                    } else {
                        logger.logDebug("We couldn't find the associated derivative line, so create a new one.", CLASSNAME);
                        createNewDerivativeLine(traceModel, derivativeLine, shouldSetUUID);
                    }
    
                    if(derivateLineTraceModel != null) {
                        logger.logDebug("Check to see if derivative line color has diffed...", CLASSNAME);
                        if(diffed(derivativeLine, diffs, DiffCategory.DERIVATIVE_LINE_COLOR)) {
                            derivateLineTraceModel.setPrimaryRGB(derivativeLine.getColor());
                        }
                    }
                } else {
                    logger.logWarning("Derivative line type is not defined!");
                }
            }
        }
    }

    private void createNewDerivativeLine(
            PlotTraceModel mainReferenceTrace,
            DerivativeLine derivativeLineConfiguration,
            boolean shouldSetUUID) throws ChartreuseException {
        logger.logDebug("DerivativeLineGenerator.createNewDerivativeLine()", CLASSNAME);

        PlotTraceModel newDerivativeTrace = new PlotTraceModel(mainReferenceTrace.getParentUUID(), shouldSetUUID);
        DerivativeLineType type = DerivativeLineFactory.getInstance().getTypeFromObject(derivativeLineConfiguration);
        if(type != null) {
            newDerivativeTrace
                .setName(mainReferenceTrace.getName())
                .setDerivativeLineType(type);

            newDerivativeTrace.set(PlotToken.TRACE_POINT_TYPE, PlotType.SCATTER_PLOT);
            newDerivativeTrace.set(PlotToken.TRACE_POINT_MODE, "Circle");
            newDerivativeTrace.set(PlotToken.TRACE_DRAW_LINES, true);

            newDerivativeTrace.setPrimaryRGB(derivativeLineConfiguration.getColor());

            if(!mainReferenceTrace.getPoints().isEmpty()) {
                logger.logDebug(
                    "Calculate new derivative line for graph with " + mainReferenceTrace.getPoints().size() + " points.", CLASSNAME);
                recalculateDerivativeLine(mainReferenceTrace, newDerivativeTrace, derivativeLineConfiguration);
            }
        } else {
            logger.logWarning("Derivative line type is not defined!");
        }
    }

    private void recalculateDerivativeLine(
            PlotTraceModel mainDataLine, PlotTraceModel derivativeLine,
            DerivativeLine derivativeLineConfiguration) {
        logger.logDebug("DerivativeLineGenerator.recalculateDerivativeLine()", CLASSNAME);

        if(!mainDataLine.isEmpty2D()) {
            logger.logDebug("Line is not empty, so proceed.", CLASSNAME);
            PlotTraceOptions options = new PlotTraceOptions();
            options.sortAlongDimension = Dimension.X;

            if(derivativeLineConfiguration instanceof RollingDerivativeLine) {
                logger.logDebug("Configuration is RollingDerivativeLine", CLASSNAME);
                RollingDerivativeLine rollingLineConfig = (RollingDerivativeLine) derivativeLineConfiguration;

                logger.logDebug("Retrieve points from the original ine, with options...", CLASSNAME);
                logger.logDebug("Line size: " + mainDataLine.getPoints().size(), CLASSNAME);
                List<PlotTracePoint> points = new ArrayList<>(mainDataLine.getPoints(options));
                List<PlotTracePoint> newDerivativeLinePoints = rollingLineConfig.calculateRollingLine(points);

                derivativeLine.clear();
                derivativeLine.setPoints(newDerivativeLinePoints);
                
            } else if(derivativeLineConfiguration instanceof SlopeDerivativeLine) {
                logger.logDebug("Configuration is SlopeDerivativeLine", CLASSNAME);
                SlopeDerivativeLine slopeLineConfig = (SlopeDerivativeLine) derivativeLineConfiguration;
                List<PlotTracePoint> points = new ArrayList<>(mainDataLine.getPoints(options));
                List<PlotTracePoint> newDerivativeLinePoints = slopeLineConfig.calculateSlopeLine(points);

                derivativeLine.clear();
                derivativeLine.setPoints(newDerivativeLinePoints);
            }  
        } else {
            logger.logDebug("Main data line had no points.", CLASSNAME);
        }
    }    
}
