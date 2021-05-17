/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.options;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.graph.library.IHtmlGraphRenderer;
import gov.sandia.watchr.log.ILogger;

public abstract class AbstractButtonBar {

    ////////////
    // FIELDS //
    ////////////

    protected final IHtmlGraphRenderer parentGraphRenderer;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    protected AbstractButtonBar(IHtmlGraphRenderer parentGraphLibrary) {
        this.parentGraphRenderer = parentGraphLibrary;
    }

    ////////////
    // PUBLIC //
    ////////////
    
    public String getHtml(PlotWindowModel currentPlot, List<ButtonType> buttons) {
        StringBuilder htmlSb = new StringBuilder();
        for(ButtonType button : buttons) {
            htmlSb.append(getHtmlForButton(currentPlot, button));
        }
        return htmlSb.toString();
    }

    public IHtmlGraphRenderer getParentGraphRenderer() {
        return parentGraphRenderer;
    }

    ///////////////
    // PROTECTED //
    ///////////////

    protected Map<String, String> getParameterMapForChildButton(IDatabase db, PlotWindowModel plot) {
        Map<String, String> paramsMap = new HashMap<>();
        if(!db.getChildren(plot, "").isEmpty()) {
            paramsMap.put("path", escapePlotName(plot));
        }
        return paramsMap;
    }

    protected String escapePlotName(PlotWindowModel plot) {
        String escapedPlotFilename = "";
        try {
            escapedPlotFilename = URLEncoder.encode(plot.getName(), StandardCharsets.UTF_8.name());
        } catch(UnsupportedEncodingException e) {
            ILogger logger = WatchrCoreApp.getInstance().getLogger();
            logger.logError("An error occurred encoding " + plot.getName(), e);
        }
        return escapedPlotFilename;
}

    //////////////
    // ABSTRACT //
    //////////////

    public abstract String getHtmlForButton(PlotWindowModel currentPlot, ButtonType type);
}
