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

import gov.sandia.watchr.config.DataLine;
import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.generators.line.DataLineGenerator;
import gov.sandia.watchr.parse.generators.line.DataLineGeneratorFactory;

public class PlotConfigGenerator extends AbstractGenerator<PlotConfig> {

    ////////////
    // FIELDS //
    ////////////

    private final String reportAbsPath;
    private final IDatabase db;
    private final List<PlotWindowModel> plots;
    private final List<WatchrDiff<?>> diffs;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PlotConfigGenerator(String reportAbsPath, IDatabase db) {
        super(db.getLogger());
        this.reportAbsPath = reportAbsPath;
        this.db = db;
        this.plots = new ArrayList<>();
        this.diffs = new ArrayList<>();
    }

    /////////////
    // GETTERS //
    /////////////

    public List<PlotWindowModel> getPlots() {
        return plots;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void generate(PlotConfig config, List<WatchrDiff<?>> diffs) throws WatchrParseException {
        logger.logDebug("PlotConfigGenerator.generate()");
        this.diffs.clear();
        this.diffs.addAll(diffs);
        
        plots.clear();
        for(DataLine line : config.getDataLines()) {
            DataLineGeneratorFactory factory = DataLineGeneratorFactory.getInstance();
            DataLineGenerator lineGenerator = factory.create(config, reportAbsPath, db);
            lineGenerator.generate(line, diffs);
            plots.addAll(lineGenerator.getRootPlots());
        }
        logger.logDebug("DONE: PlotConfigGenerator.generate()");
    }
}
