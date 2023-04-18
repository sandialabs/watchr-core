package gov.sandia.watchr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.db.PlotDatabaseSearchCriteria;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.graph.library.GraphOperationResult;
import gov.sandia.watchr.graph.library.IHtmlGraphRenderer;
import gov.sandia.watchr.graph.library.impl.PlotlyGraphRenderer;
import gov.sandia.watchr.log.ILogger;

/**
 * @author Elliott Ridgway
 */
public class WatchrCoreAppGraphSubsystem {

    ////////////
    // FIELDS //
    ////////////

    private WatchrCoreApp parentApp;
    private IFileReader fileReader;
    private ILogger logger;
    protected final List<IHtmlGraphRenderer> graphRendererList;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public WatchrCoreAppGraphSubsystem(WatchrCoreApp parentApp, ILogger logger, IFileReader fileReader) {
        this.parentApp = parentApp;
        this.logger = logger;
        this.fileReader = fileReader;
        graphRendererList = new ArrayList<>();
    }

    /////////////
    // SETTERS //
    /////////////

    public void setLogger(ILogger logger) {
        this.logger = logger;
    }

    public void setFileReader(IFileReader fileReader) {
        this.fileReader = fileReader;
    }   

    /////////////
    // GETTERS //
    /////////////

    public PlotWindowModel getParentPlot(String dbName, PlotDatabaseSearchCriteria search) {
        return parentApp.getDatabaseParentPlot(dbName, search);
    }

    public PlotWindowModel getPlot(String dbName, PlotDatabaseSearchCriteria search) {
        return parentApp.getDatabasePlot(dbName, search);
    }

    public Set<PlotWindowModel> getChildPlots(String dbName, PlotDatabaseSearchCriteria search) {
        return parentApp.getDatabaseChildPlots(dbName, search);
    }

    /**
     * 
     * @return The List of {@link IHtmlGraphRenderer} implementations known to
     *         Watchr.
     */
    public List<Class<? extends IHtmlGraphRenderer>> getKnownGraphRenderers() {
        List<Class<? extends IHtmlGraphRenderer>> graphRenderers = new ArrayList<>();
        graphRenderers.add(PlotlyGraphRenderer.class);
        return graphRenderers;
    }

    /**
     * Given a database, retrieve its graph renderer. If none has been set, this
     * method's first argument will allow you to instantiate a graph renderer for
     * the database.
     * 
     * @param graphLibraryType The type of graph renderer to instantiate if none has
     *                         been.
     * @param databaseName     The name of the database.
     * @return The {@link IHtmlGraphRenderer} implementation for the given database.
     */
    public IHtmlGraphRenderer getGraphRenderer(
            Class<? extends IHtmlGraphRenderer> graphLibraryType, String dbName) {
        IHtmlGraphRenderer thisGraphRenderer = null;
        for(IHtmlGraphRenderer graphRenderer : graphRendererList) {
            if(graphRenderer.getDatabaseName().equals(dbName)) {
                thisGraphRenderer = graphRenderer;
            }
        }

        if(thisGraphRenderer == null && graphLibraryType == PlotlyGraphRenderer.class) {
            thisGraphRenderer = new PlotlyGraphRenderer(this, logger, fileReader, dbName);
            graphRendererList.add(thisGraphRenderer);
        }
        return thisGraphRenderer;
    }

    /**
     * Return specific HTML renders of plots from a given database.
     * 
     * @param databaseName      The name of the database to render plots from.
     * @param plotConfiguration The {@link GraphDisplayConfiguration} that governs
     *                          the layout of the rendered plots.
     * @param standalone        If true, plots will be viewable without any
     *                          surrounding HTML code. Otherwise, the HTML files
     *                          will assume they will be embedded as child divs
     *                          inside existing HTML pages.
     * @return The {@link GraphOperationResult} that contains the rendered HTML
     *         plots, as well as associated metadata about the transaction.
     */
    public GraphOperationResult getGraphHtml(
           String dbName, GraphDisplayConfig plotConfiguration, boolean standalone) {
        IHtmlGraphRenderer graphRenderer = getGraphRenderer(PlotlyGraphRenderer.class, dbName);
        if (graphRenderer != null) {
            return graphRenderer.getGraphHtml(plotConfiguration, standalone);
        } else {
            String logMessage = "No graphRenderer has been configured!";
            logger.logWarning(logMessage);

            GraphOperationResult result = new GraphOperationResult();
            result.setLog(logMessage);
            return result;
        }
    }

    ////////////
    // EXPORT //
    ////////////

    /**
     * Given a database, export all of its plots as HTML graphs.
     * 
     * @param dbName           The name of the database to render plots from.
     * @param graphConfig      The {@link GraphDisplayConfig} to use.
     * @param exportDirAbsPath The absolute path to the directory where rendered
     *                         plots should be written to.
     * @throws IOException Thrown if something goes wrong reading the configuration
     *                     file.
     */
    public void exportAllGraphHtml(
            String dbName, GraphDisplayConfig graphConfig, String exportDirAbsPath) {

        IHtmlGraphRenderer graphRenderer = getGraphRenderer(PlotlyGraphRenderer.class, dbName);
        List<String> categories = new ArrayList<>(parentApp.getDatabaseCategories(dbName));
        graphRenderer.exportGraphHtml(graphConfig, categories, exportDirAbsPath);
    }    
}
