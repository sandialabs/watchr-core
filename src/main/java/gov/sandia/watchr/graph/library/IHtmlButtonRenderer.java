package gov.sandia.watchr.graph.library;

import java.util.List;

import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.options.AbstractButtonBar;
import gov.sandia.watchr.graph.options.ButtonType;

public interface IHtmlButtonRenderer {

    public IDatabase getDatabase();

    public List<ButtonType> getButtons();

    public AbstractButtonBar getButtonBar();

    public void setButtonBar(AbstractButtonBar buttonBar);
}
