/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators;

import java.util.List;

import gov.sandia.watchr.config.FilterConfig;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.graph.chartreuse.model.PlotTraceModel;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.WatchrParseException;

public class FilterConfigGenerator extends AbstractGenerator<FilterConfig> {

    private final PlotTraceModel traceModel;
    private final boolean clearFilterValuesBeforeApplying;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FilterConfigGenerator(PlotTraceModel traceModel, boolean clearFilterValuesBeforeApplying, ILogger logger) {
        super(logger);
        this.traceModel = traceModel;
        this.clearFilterValuesBeforeApplying = clearFilterValuesBeforeApplying;
    }

    @Override
    public void generate(FilterConfig config, List<WatchrDiff<?>> diffs) throws WatchrParseException {
        if(clearFilterValuesBeforeApplying) {
            traceModel.setFilterValues(config.getFilterPoints());
        } else {
            traceModel.applyFilterValues(config.getFilterPoints());
        }
    }    
}
