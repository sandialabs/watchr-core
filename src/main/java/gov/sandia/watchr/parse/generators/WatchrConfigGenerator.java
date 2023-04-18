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

import gov.sandia.watchr.config.DataFilterConfig;
import gov.sandia.watchr.config.PlotsConfig;
import gov.sandia.watchr.config.WatchrConfig;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.db.impl.AbstractDatabase;
import gov.sandia.watchr.parse.WatchrParseException;

public class WatchrConfigGenerator extends AbstractGenerator<WatchrConfig> {

    ////////////
    // FIELDS //
    ////////////

    private final AbstractDatabase db;
    private final List<String> reportAbsPaths;
    private PlotsConfigGenerator plotsGenerator;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WatchrConfigGenerator(AbstractDatabase db, List<String> reportAbsPaths) {
        super(db.getLogger());
        this.db = db;
        this.reportAbsPaths = new ArrayList<>();
        this.reportAbsPaths.addAll(reportAbsPaths);
    }

    /////////////
    // GETTERS //
    /////////////
    
    public List<String> getReportAbsPaths() {
        return reportAbsPaths;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void generate(WatchrConfig config, List<WatchrDiff<?>> diffs) throws WatchrParseException {
        PlotsConfig plotsConfig = config.getPlotsConfig();
        if(plotsConfig != null) {
            IFileReader fileReader = plotsConfig.getFileConfig().getFileReader();
            DataFilterConfig globalDataFilter = config.getFilterConfig();
            plotsGenerator = new PlotsConfigGenerator(db, logger, fileReader, globalDataFilter, reportAbsPaths);
            plotsGenerator.generate(plotsConfig, diffs);
        }
        db.setWatchrConfig(new WatchrConfig(config));
        db.updateMetadata();
    }

    @Override
    public String getProblemStatus() {
        if(plotsGenerator != null) {
            return plotsGenerator.getProblemStatus();
        }
        return "";
    }
}
