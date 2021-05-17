/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.library;

import java.io.File;
import java.io.IOException;
import java.util.List;

import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.options.AbstractButtonBar;
import gov.sandia.watchr.graph.options.ButtonType;

public interface IHtmlGraphRenderer {

    public GraphOperationResult getGraphHtml(GraphDisplayConfig plotConfiguration, boolean standalone);

    public void exportAllGraphHtml(GraphDisplayConfig plotConfiguration, List<String> categories, File destDir, boolean standalone) throws IOException;

    public IDatabase getDatabase();

    public List<ButtonType> getButtons();

    public void setButtonBar(AbstractButtonBar buttonBar);
}
