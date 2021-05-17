/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.config.DataLine;
import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.parse.WatchrParseException;

public class PlotConfigGenerator extends AbstractGenerator<PlotConfig> {

    ////////////
    // FIELDS //
    ////////////

    private final File report;
    private final IDatabase db;
    private final List<PlotWindowModel> plots;
    private final List<WatchrDiff<?>> diffs;

    private final List<PlotConfig> allPlotConfigs; // Needed to look up template relationships

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PlotConfigGenerator(File report, IDatabase db, List<PlotConfig> allPlotConfigs) {
        this.report = report;
        this.db = db;
        this.plots = new ArrayList<>();
        this.diffs = new ArrayList<>();

        this.allPlotConfigs = new ArrayList<>();
        this.allPlotConfigs.addAll(allPlotConfigs);
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
        this.diffs.clear();
        this.diffs.addAll(diffs);

        boolean dependsOnTemplate = StringUtils.isNotBlank(config.getInheritTemplate());
        if(dependsOnTemplate) {
            TemplatePlotConfigGenerator templatePlotConfigGenerator = new TemplatePlotConfigGenerator(allPlotConfigs);
            PlotConfig newChildConfig = templatePlotConfigGenerator.handleDataLineGenerationForTemplate(config);
            handleNormalDataLineGeneration(newChildConfig);
        } else {
            handleNormalDataLineGeneration(config);
        }
    }

    /////////////
    // PRIVATE //
    /////////////

    private void handleNormalDataLineGeneration(PlotConfig config) throws WatchrParseException {
        plots.clear();
        DataLineGenerator lineGenerator = new DataLineGenerator(config, report, db);
        for(DataLine line : config.getDataLines()) {
            lineGenerator.generate(line, diffs);
            plots.addAll(lineGenerator.getRootPlots());
        }
    }
}
