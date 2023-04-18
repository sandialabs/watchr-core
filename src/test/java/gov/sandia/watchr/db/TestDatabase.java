package gov.sandia.watchr.db;

import java.util.Set;

import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.db.impl.AbstractDatabase;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.log.ILogger;

public class TestDatabase extends AbstractDatabase {

    public TestDatabase(ILogger logger, IFileReader fileReader) {
        super(logger, fileReader);
    }

    @Override
    public void loadState() {
        // Do nothing
    }

    @Override
    public void saveState() {
        // Do nothing
    }

    public void setListeners(PlotWindowModel windowModel) {
        super.setListeners(windowModel);
    }

    public Set<String> getDirtyPlotUUIDs() {
        return dirtyPlotUUIDs;
    }

    @Override
    public PlotWindowModel loadPlotUsingUUID(String uuid) {
        return null;
    }

    @Override
    public PlotWindowModel loadRootPlot() {
        return null;
    }

    @Override
    public void updateMetadata() {
        // Do nothing
    }

    @Override
    public PlotWindowModel loadPlotUsingInnerFields(String name, String category) {
        for(PlotWindowModel plot : plots) {
            if(plot.getName().equals(name) && plot.getCategory().equals(category)) {
                return plot;
            }
        }
        return null;
    }
}
