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

import org.apache.commons.lang3.math.NumberUtils;

import gov.sandia.watchr.config.DerivativeLine;
import gov.sandia.watchr.config.DerivativeLine.DerivativeLineType;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.graph.chartreuse.Dimension;
import gov.sandia.watchr.graph.chartreuse.PlotToken;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.model.PlotCanvasModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceOptions;
import gov.sandia.watchr.graph.chartreuse.model.PlotTracePoint;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.util.ArrayUtil;
import gov.sandia.watchr.util.StatUtil;

public class DerivativeLineGenerator extends AbstractGenerator<List<DerivativeLine>> {

    ////////////
    // FIELDS //
    ////////////

    private PlotTraceModel traceModel;
    private final List<WatchrDiff<?>> diffs;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public DerivativeLineGenerator(PlotTraceModel traceModel) {
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
        PlotCanvasModel canvasModel = traceModel.getParent();
        if (canvasModel != null) {
            // Look for associated derivative line.
            for (DerivativeLine derivativeLine : derivativeLines) {
                PlotTraceModel derivateLineTraceModel = canvasModel.findDerivativeLine(derivativeLine.getType());
                if(derivateLineTraceModel != null) {
                    // We found the associated derivative line, now update it.
                    recalculateDerivativeLine(traceModel, derivateLineTraceModel, derivativeLine);
                } else {
                    // If we couldn't find it, add a new one.
                    createNewDerivativeLine(traceModel, derivativeLine);
                }

                if(derivateLineTraceModel != null && diffed(derivativeLine, diffs, DiffCategory.DERIVATIVE_LINE_COLOR)) {
                    derivateLineTraceModel.setPrimaryRGB(derivativeLine.getColor());
                }
            }
        }
    }

    private void createNewDerivativeLine(PlotTraceModel mainReferenceTrace,
            DerivativeLine derivativeLineConfiguration) {

        PlotTraceModel newDerivativeTrace = new PlotTraceModel(mainReferenceTrace.getParentUUID());

        newDerivativeTrace
            .setName(mainReferenceTrace.getName())
            .setDerivativeLineType(derivativeLineConfiguration.getType());

        newDerivativeTrace.set(PlotToken.TRACE_POINT_TYPE, PlotType.SCATTER_PLOT);
        newDerivativeTrace.set(PlotToken.TRACE_POINT_MODE, "Circle");
        newDerivativeTrace.set(PlotToken.TRACE_DRAW_LINES, true);

        newDerivativeTrace.setPrimaryRGB(derivativeLineConfiguration.getColor());

        if(!mainReferenceTrace.getPoints().isEmpty()) {
            recalculateDerivativeLine(mainReferenceTrace, newDerivativeTrace, derivativeLineConfiguration);
        }
    }

    private void recalculateDerivativeLine(
            PlotTraceModel mainDataLine, PlotTraceModel derivativeLine,
            DerivativeLine derivativeLineConfiguration) {

        if(!mainDataLine.isEmpty2D()) {
            PlotTraceOptions options = new PlotTraceOptions();
            options.sortAlongDimension = Dimension.X;
            List<PlotTracePoint> points = new ArrayList<>(mainDataLine.getPoints(options));
            
            derivativeLine.clear();
            derivativeLine.add(points.get(0));

            for(int i = 1; i < points.size(); i++) {
                String lastXValue = points.get(i).x;

                int rangeSize = derivativeLineConfiguration.getRollingRange();
                if(i < derivativeLineConfiguration.getRollingRange()) {
                    rangeSize = i;
                }

                List<Double> listRangeToInspect = new ArrayList<>();
                for(int j = i; j >= i-rangeSize; j--) {
                    if(NumberUtils.isCreatable(points.get(j).y)) {
                        listRangeToInspect.add(Double.parseDouble(points.get(j).y));
                    }
                }

                double newValue;
                if (derivativeLineConfiguration.getType() == DerivativeLineType.AVERAGE) {
                    newValue = StatUtil.avg(ArrayUtil.asDoubleArrFromDoubleList(listRangeToInspect));
                } else if (derivativeLineConfiguration.getType() == DerivativeLineType.STANDARD_DEVIATION) {
                    newValue = StatUtil.stdDev(ArrayUtil.asDoubleArrFromDoubleList(listRangeToInspect));
                } else { // DerivativeLineType.STANDARD_DEVIATION_OFFSET
                    newValue = StatUtil.avg(ArrayUtil.asDoubleArrFromDoubleList(listRangeToInspect)) +
                               StatUtil.stdDev(ArrayUtil.asDoubleArrFromDoubleList(listRangeToInspect));
                }

                derivativeLine.add(new PlotTracePoint(lastXValue, Double.toString(newValue)));
            }
        }                
    }    
}
