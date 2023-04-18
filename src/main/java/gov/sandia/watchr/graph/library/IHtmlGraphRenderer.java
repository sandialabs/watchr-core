/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.library;

import java.util.List;

import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;

public interface IHtmlGraphRenderer {

    public String getDatabaseName();

    public GraphOperationResult getGraphHtml(GraphDisplayConfig plotConfiguration, boolean standalone);

    public void exportGraphHtml(GraphDisplayConfig plotConfiguration, List<String> categories, String destDirAbsPath);

    public IHtmlButtonRenderer getButtonRenderer();

    public int getNumberOfPlotChildren(PlotWindowModel plot);
}
