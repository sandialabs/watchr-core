/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators;

import java.util.List;

import gov.sandia.watchr.config.PlotsConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.parse.WatchrParseException;

public class WatchrConfigGenerator extends AbstractGenerator<WatchrConfig> {

    ////////////
    // FIELDS //
    ////////////

    private final IDatabase db;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WatchrConfigGenerator(IDatabase db) {
        super(db.getLogger());
        this.db = db;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void generate(WatchrConfig config, List<WatchrDiff<?>> diffs) throws WatchrParseException {
        PlotsConfig plotsConfig = config.getPlotsConfig();
        if (plotsConfig != null) {
            PlotsConfigGenerator plotsGenerator = new PlotsConfigGenerator(db, plotsConfig.getFileConfig());
            plotsGenerator.generate(plotsConfig, diffs);
        }

        db.setWatchrConfig(new WatchrConfig(config));
        db.updateMetadata();
    }
}
