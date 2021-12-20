/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.library;

import java.util.List;

import gov.sandia.watchr.config.GraphDisplayConfig;

public interface IHtmlGraphRenderer {

    public GraphOperationResult getGraphHtml(GraphDisplayConfig plotConfiguration, boolean standalone);

    public void exportGraphHtml(GraphDisplayConfig plotConfiguration, List<String> categories, String destDirAbsPath);

    public IHtmlButtonRenderer getButtonRenderer();
}
