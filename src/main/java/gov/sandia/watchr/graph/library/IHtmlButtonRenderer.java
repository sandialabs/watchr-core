package gov.sandia.watchr.graph.library;

import java.util.List;

import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.graph.options.AbstractButtonBar;
import gov.sandia.watchr.graph.options.ButtonType;

public interface IHtmlButtonRenderer {

    public IHtmlGraphRenderer getParentGraphRenderer();

    public List<ButtonType> getButtons();

    public AbstractButtonBar getButtonBar();

    public void setButtonBar(AbstractButtonBar buttonBar);

    public int getNumberOfPlotChildren(PlotWindowModel plot);
}
