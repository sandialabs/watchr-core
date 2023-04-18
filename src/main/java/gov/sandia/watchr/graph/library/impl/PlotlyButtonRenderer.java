package gov.sandia.watchr.graph.library.impl;

import java.util.ArrayList;
import java.util.List;

import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.graph.library.IHtmlButtonRenderer;
import gov.sandia.watchr.graph.library.IHtmlGraphRenderer;
import gov.sandia.watchr.graph.options.AbstractButtonBar;
import gov.sandia.watchr.graph.options.ButtonType;
import gov.sandia.watchr.graph.options.DefaultButtonBar;

public class PlotlyButtonRenderer implements IHtmlButtonRenderer {
    
    private final IHtmlGraphRenderer graphRenderer;
    private final List<ButtonType> buttons;
    private AbstractButtonBar buttonBarConfiguration;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PlotlyButtonRenderer(IHtmlGraphRenderer graphRenderer) {
        this.graphRenderer = graphRenderer;
        this.buttons = new ArrayList<>();
        this.buttonBarConfiguration = new DefaultButtonBar(this);
    }

    //////////////
    // OVERRIDE //
    //////////////
    
    @Override
    public IHtmlGraphRenderer getParentGraphRenderer() {
        return graphRenderer;
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

    @Override
    public int getNumberOfPlotChildren(PlotWindowModel plot) {
        return graphRenderer.getNumberOfPlotChildren(plot);
    }
}
