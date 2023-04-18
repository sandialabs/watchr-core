/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators;

import java.util.List;

import gov.sandia.watchr.config.DataFilterConfig;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.filter.IFilterable;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.WatchrParseException;

public class FilterConfigGenerator extends AbstractGenerator<DataFilterConfig> {

    private final IFilterable filterableObject;
    private final boolean clearFilterValuesBeforeApplying;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public FilterConfigGenerator(IFilterable filterableObject, boolean clearFilterValuesBeforeApplying, ILogger logger) {
        super(logger);
        this.filterableObject = filterableObject;
        this.clearFilterValuesBeforeApplying = clearFilterValuesBeforeApplying;
    }

    @Override
    public void generate(DataFilterConfig config, List<WatchrDiff<?>> diffs) throws WatchrParseException {
        if(clearFilterValuesBeforeApplying) {
            filterableObject.setFilterValues(config.getFilters());
        } else {
            filterableObject.addFilterValues(config.getFilters());
        }
    }    
}
