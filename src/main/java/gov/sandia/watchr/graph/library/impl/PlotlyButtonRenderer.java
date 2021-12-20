package gov.sandia.watchr.graph.library.impl;

import java.util.ArrayList;
import java.util.List;

import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.library.IHtmlButtonRenderer;
import gov.sandia.watchr.graph.options.AbstractButtonBar;
import gov.sandia.watchr.graph.options.ButtonType;
import gov.sandia.watchr.graph.options.DefaultButtonBar;

public class PlotlyButtonRenderer implements IHtmlButtonRenderer {
    
    private final List<ButtonType> buttons;
    private final IDatabase db;
    private AbstractButtonBar buttonBarConfiguration;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PlotlyButtonRenderer(IDatabase db) {
        this.db = db;
        this.buttons = new ArrayList<>();
        this.buttonBarConfiguration = new DefaultButtonBar(this);
    }

    //////////////
    // OVERRIDE //
    //////////////
    
    @Override
    public IDatabase getDatabase() {
        return db;
    }

    @Override
    public void setButtonBar(AbstractButtonBar buttonBarConfiguration) {
        this.buttonBarConfiguration = buttonBarConfiguration;
    }

    @Override
    public List<ButtonType> getButtons() {
        return buttons;
    }

    @Override
    public AbstractButtonBar getButtonBar() {
        return buttonBarConfiguration;
    }
}
