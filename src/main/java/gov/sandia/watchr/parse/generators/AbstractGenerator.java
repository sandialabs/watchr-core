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
import gov.sandia.watchr.parse.WatchrParseException;

public abstract class AbstractGenerator<E> {

    protected boolean diffed(IConfig config, List<WatchrDiff<?>> diffs, DiffCategory diffType) {
        for(WatchrDiff<?> diff : diffs) {
            if(diff.getPath().startsWith(config.getConfigPath()) && diff.getProperty() == diffType) {
                return true;
            }
        }
        return false;
    }

    protected WatchrDiff<?> getDiff(IConfig config, List<WatchrDiff<?>> diffs, DiffCategory diffType) {
        for(WatchrDiff<?> diff : diffs) {
            if(diff.getPath().startsWith(config.getConfigPath()) && diff.getProperty() == diffType) {
                return diff;
            }
        }
        return null;
    }

    public abstract void generate(E config, List<WatchrDiff<?>> diffs) throws WatchrParseException;
}
