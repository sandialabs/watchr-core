/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.options;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.HtmlUtil;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.graph.library.IHtmlButtonRenderer;

public class DefaultButtonBar extends AbstractButtonBar {

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public DefaultButtonBar(IHtmlButtonRenderer parentRenderer) {
        super(parentRenderer);
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public String getHtmlForButton(PlotWindowModel plot, ButtonType type) throws UnsupportedEncodingException {
        if(type == ButtonType.GO_TO_CHILD_GRAPH) {
            return getChildGraphButton(plot);
        }
        return "";
    }

    /////////////
    // PRIVATE //
    /////////////

    private String getChildGraphButton(PlotWindowModel plot) throws UnsupportedEncodingException {
        IDatabase db = parentRenderer.getDatabase();
        Map<String, String> paramsMap = getParameterMapForChildButton(db, plot);

        String params = HtmlUtil.createParameterList(paramsMap);
        if(!params.isEmpty()) {
            return HtmlUtil.createLink(params, HtmlUtil.createButton("button", "Down One Level"));
        } else {
            return "";
        }
    }
}
