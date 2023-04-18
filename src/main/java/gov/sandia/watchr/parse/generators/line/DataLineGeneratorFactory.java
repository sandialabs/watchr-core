/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators.line;

import gov.sandia.watchr.config.PlotConfig;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.parse.generators.line.impl.AreaPlotDataLineGenerator;
import gov.sandia.watchr.parse.generators.line.impl.ScatterPlotDataLineGenerator;
import gov.sandia.watchr.parse.generators.line.impl.TreeMapDataLineGenerator;

public class DataLineGeneratorFactory {
    
    private static DataLineGeneratorFactory INSTANCE;

    private DataLineGeneratorFactory() {}

    public static DataLineGeneratorFactory getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new DataLineGeneratorFactory();
        }
        return INSTANCE;
    }

    public DataLineGenerator create(PlotConfig plotConfig, String reportAbsPath, IDatabase db) {
        PlotType type = plotConfig.getType();
        if(type == PlotType.SCATTER_PLOT) {
            return new ScatterPlotDataLineGenerator(plotConfig, reportAbsPath, db);
        } else if(type == PlotType.TREE_MAP) {
            return new TreeMapDataLineGenerator(plotConfig, reportAbsPath, db);
        } else if(type == PlotType.AREA_PLOT) {
            return new AreaPlotDataLineGenerator(plotConfig, reportAbsPath, db);
        }
        return null;
    }
}
