/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config.reader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonElement;

import gov.sandia.watchr.config.IConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.log.ILogger;

public abstract class AbstractConfigReader<E> {

    ////////////
    // FIELDS //
    ////////////

    protected final Set<String> seenKeywords;
    protected final ILogger logger;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    protected AbstractConfigReader(ILogger logger) {
        seenKeywords = new HashSet<>();
        this.logger = logger;
    }

    /////////////
    // UTILITY //
    /////////////

    public void validateMissingKeywords() {
        for(String requiredKeyword : getRequiredKeywords()) {
            if(!seenKeywords.contains(requiredKeyword)) {
                logger.log(new WatchrConfigError(ErrorLevel.ERROR, "Missing keyword `" + requiredKeyword + "` in config file!"));
            }
        }
    }

    public List<WatchrDiff<?>> getDiffs(WatchrConfig then, WatchrConfig now) {
        return then.diff(now);
    }

    //////////////
    // ABSTRACT //
    //////////////

    public abstract Set<String> getRequiredKeywords();

    public abstract E handle(JsonElement element, IConfig parent);
}
