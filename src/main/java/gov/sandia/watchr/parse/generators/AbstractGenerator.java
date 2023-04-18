/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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

    ////////////
    // FIELDS //
    ////////////

    protected final ILogger logger;
    private static final String CLASSNAME = AbstractGenerator.class.getSimpleName();

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    protected AbstractGenerator(ILogger logger) {
        this.logger = logger;
    }

    /////////////
    // UTILITY //
    /////////////

    protected boolean diffed(IConfig config, List<WatchrDiff<?>> diffs, DiffCategory diffType) {
        if(!diffs.isEmpty()) {
            logger.logDebug("AbstractGenerator.diffed()", CLASSNAME);
            logger.logDebug("Number of diffs: " + diffs.size(), CLASSNAME);
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

    public String getProblemStatus() {
        return "";
    }

    //////////////
    // ABSTRACT //
    //////////////

    public abstract void generate(E config, List<WatchrDiff<?>> diffs) throws WatchrParseException;
}
