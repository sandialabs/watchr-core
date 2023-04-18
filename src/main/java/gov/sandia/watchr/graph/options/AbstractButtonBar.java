/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.options;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.graph.library.IHtmlButtonRenderer;

public abstract class AbstractButtonBar {

    ////////////
    // FIELDS //
    ////////////

    protected final IHtmlButtonRenderer parentRenderer;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    protected AbstractButtonBar(IHtmlButtonRenderer parentRenderer) {
        this.parentRenderer = parentRenderer;
    }

    ////////////
    // PUBLIC //
    ////////////
    
    public String getHtml(PlotWindowModel currentPlot, List<ButtonType> buttons) throws UnsupportedEncodingException {
        StringBuilder htmlSb = new StringBuilder();
        for(ButtonType button : buttons) {
            htmlSb.append(getHtmlForButton(currentPlot, button));
        }
        return htmlSb.toString();
    }

    public IHtmlButtonRenderer getButtonRenderer() {
        return parentRenderer;
    }

    ///////////////
    // PROTECTED //
    ///////////////

    protected String escapePlotName(PlotWindowModel plot) throws UnsupportedEncodingException {
        return URLEncoder.encode(plot.getName(), StandardCharsets.UTF_8.name());
}

    //////////////
    // ABSTRACT //
    //////////////

    public abstract String getHtmlForButton(PlotWindowModel currentPlot, ButtonType type) throws UnsupportedEncodingException;
}
