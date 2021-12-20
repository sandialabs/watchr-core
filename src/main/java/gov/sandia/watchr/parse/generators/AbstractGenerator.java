/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators;

import java.util.List;

import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.WatchrParseException;

public abstract class AbstractGenerator<E> {

    protected final ILogger logger;

    protected AbstractGenerator(ILogger logger) {
        this.logger = logger;
    }

    protected boolean diffed(IConfig config, List<WatchrDiff<?>> diffs, DiffCategory diffType) {
        if(!diffs.isEmpty()) {
            logger.logDebug("AbstractGenerator.diffed()");
            logger.logDebug("Number of diffs: " + diffs.size());
        }
        for(WatchrDiff<?> diff : diffs) {
            if(diff.getProperty() == diffType && diff.getPath().startsWith(config.getConfigPath())) {
                return true;
            }
        }
        return false;
    }

    protected WatchrDiff<?> getDiff(IConfig config, List<WatchrDiff<?>> diffs, DiffCategory diffType) {
        for(WatchrDiff<?> diff : diffs) {
            if(diff.getProperty() == diffType && diff.getPath().startsWith(config.getConfigPath())) {
                return diff;
            }
        }
        return null;
    }

    public abstract void generate(E config, List<WatchrDiff<?>> diffs) throws WatchrParseException;
}
