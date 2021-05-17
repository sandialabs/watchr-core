/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.library.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.HtmlUtil;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.generator.plotly.PlotlyCanvasGenerator;
import gov.sandia.watchr.graph.chartreuse.generator.plotly.PlotlyHtmlFragmentGenerator;
import gov.sandia.watchr.graph.chartreuse.generator.plotly.PlotlyTraceGenerator;
import gov.sandia.watchr.graph.chartreuse.generator.plotly.PlotlyWindowGenerator;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.graph.library.GraphOperationMetadata;
import gov.sandia.watchr.graph.library.GraphOperationResult;
import gov.sandia.watchr.graph.library.IHtmlGraphRenderer;
import gov.sandia.watchr.graph.options.ButtonType;
import gov.sandia.watchr.graph.options.DefaultButtonBar;
import gov.sandia.watchr.graph.options.AbstractButtonBar;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.util.CommonConstants;
import gov.sandia.watchr.util.FileUtil;
import gov.sandia.watchr.util.ListUtil;
import gov.sandia.watchr.util.OsUtil;

public class PlotlyGraphRenderer implements IHtmlGraphRenderer {

    ////////////
    // FIELDS //
    ////////////

    private PlotlyWindowGenerator windowGenerator;
    private PlotlyCanvasGenerator canvasGenerator;
    private PlotlyTraceGenerator traceGenerator;

    private AbstractButtonBar buttonBarConfiguration;
    
    private final List<ButtonType> buttons;
    private final IDatabase db;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PlotlyGraphRenderer(IDatabase db) {
        traceGenerator  = new PlotlyTraceGenerator();
        canvasGenerator = new PlotlyCanvasGenerator(traceGenerator);
        windowGenerator = new PlotlyWindowGenerator(canvasGenerator, true);

        canvasGenerator.setParent(windowGenerator);
        traceGenerator.setParent(canvasGenerator);

        this.db = db;
        this.buttons = new ArrayList<>();
        this.buttonBarConfiguration = new DefaultButtonBar(this);
    }

    //////////////
    // OVERRIDE //
    //////////////

    
    public GraphOperationResult getGraphHtml(GraphDisplayConfig plotConfiguration, boolean standalone) {

        GraphOperationResult result;
        GraphDisplayConfig copyConfiguration = new GraphDisplayConfig(plotConfiguration);

        List<PlotWindowModel> plots = new ArrayList<>(
            db.getChildren(
                copyConfiguration.getNextPlotDbLocation(),
                copyConfiguration.getDisplayCategory()
            ));
        
        boolean atTheTop = copyConfiguration.getNextPlotDbLocation().equals(CommonConstants.ROOT_PATH_ALIAS);
        boolean travelUpIfEmpty = copyConfiguration.shouldTravelUpIfEmpty();

        if(plots.isEmpty() && travelUpIfEmpty && !atTheTop) {
            PlotWindowModel parentPlot =
                db.getParent(copyConfiguration.getLastPlotDbLocation(), copyConfiguration.getDisplayCategory());
            if(parentPlot != null) {
                copyConfiguration.setNextPlotDbLocation(parentPlot.getName());
                plots.addAll(db.getChildren(
                    copyConfiguration.getNextPlotDbLocation(), copyConfiguration.getDisplayCategory()
                ));
            } else {
                copyConfiguration.setNextPlotDbLocation(CommonConstants.ROOT_PATH_ALIAS);
                plots.addAll(db.getChildren(
                    CommonConstants.ROOT_PATH_ALIAS, copyConfiguration.getDisplayCategory()
                ));
            }
        }
        result = standalone ?
                 renderStandalonePlots(copyConfiguration, plots) :
                 renderEmbeddedPlots(copyConfiguration, plots);

        result.getMetadata().put(GraphOperationMetadata.PLOT_DB_LOCATION.get(), copyConfiguration.getNextPlotDbLocation());
        result.getMetadata().put(GraphOperationMetadata.PLOT_NAME_MANIFEST.get(), writePlotNamesAsString(plots));
        result.getMetadata().put(GraphOperationMetadata.NUMBER_OF_GRAPHS.get(), Integer.toString(plots.size()));
        copyConfiguration.setLastPlotDbLocation(copyConfiguration.getNextPlotDbLocation());

        return result;
    }

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
    public void exportAllGraphHtml(
            GraphDisplayConfig plotConfiguration, List<String> categories, File destDir, boolean standalone) throws IOException {
        List<String> categoriesWithBlank = new ArrayList<>(categories);
        if(categories.isEmpty()) {
            categoriesWithBlank.add("");
        }

        for(String category : categoriesWithBlank) {
            GraphDisplayConfig plotConfigurationForCategory = new GraphDisplayConfig(plotConfiguration);
            plotConfigurationForCategory.setDisplayCategory(category);

            GraphOperationResult result = getGraphHtml(plotConfigurationForCategory, standalone);
            String finalHtml = result.getHtml();
            String numberOfGraphsStr = result.getMetadata().get(GraphOperationMetadata.NUMBER_OF_GRAPHS.get());
            int numberOfGraphs = 0;
            if(NumberUtils.isCreatable(numberOfGraphsStr)) {
                numberOfGraphs = Integer.parseInt(numberOfGraphsStr);
            }

            if(StringUtils.isNotBlank(finalHtml) && numberOfGraphs > 0) {
                String escapedPlotFilename = FileUtil.removeIllegalCharactersFromFilename(plotConfigurationForCategory.getNextPlotDbLocation());
                StringBuilder sb = new StringBuilder();
                sb.append(escapedPlotFilename);
                if(StringUtils.isNotBlank(category)) {
                    sb.append("_").append(category);
                }
                sb.append(".html");

                File destHtml = new File(destDir, sb.toString());
                FileUtils.write(destHtml, finalHtml, StandardCharsets.UTF_8);

                String plotNames = result.getMetadata().get(GraphOperationMetadata.PLOT_NAME_MANIFEST.get());
                String[] plotNamesArr = plotNames.split(CommonConstants.UNUSUAL_COMMA);

                for(String nextPlotParentName : plotNamesArr) {
                    if(StringUtils.isNotBlank(nextPlotParentName) && !nextPlotParentName.equals(CommonConstants.ROOT_PATH_ALIAS)) {
                        GraphDisplayConfig childPlotConfiguration = new GraphDisplayConfig(plotConfigurationForCategory);
                        childPlotConfiguration.setNextPlotDbLocation(nextPlotParentName);

                        List<String> childCategories = new ArrayList<>();
                        childCategories.add(category);
                        exportAllGraphHtml(childPlotConfiguration, childCategories, destDir, standalone);
                    }
                }
            }
        }
    }

    /////////////
    // PRIVATE //
    /////////////

    private GraphOperationResult renderStandalonePlots(
            GraphDisplayConfig plotConfiguration, List<PlotWindowModel> plots) {
        GraphOperationResult result = new GraphOperationResult();
        String templateHtml = getTemplateHtml();
        
        if(templateHtml != null) {
            Collections.sort(plots, new PlotWindowModelComparator());
            List<String> renderedHtmlStrings = getRenderedHtmlPlots(plotConfiguration, plots);

            StringBuilder renderedHtmlSb = new StringBuilder();
            for(String htmlString : renderedHtmlStrings) {
                renderedHtmlSb.append(htmlString);
                renderedHtmlSb.append(OsUtil.getOSLineBreak());
            }
            
            templateHtml = templateHtml.replace("$$$PLOTLY_SCRIPTS", renderedHtmlSb.toString());
            templateHtml = templateHtml.replace("$$$DIVS", createDivLayout(plotConfiguration, plots, true));
            templateHtml = templateHtml.replace("$$$WATCHR_CSS", PlotlyHtmlFragmentGenerator.getWatchrGraphCss());

            result.setHtml(templateHtml);
        }
        return result;
    }

    private GraphOperationResult renderEmbeddedPlots(
            GraphDisplayConfig plotConfiguration, List<PlotWindowModel> plots) {
        GraphOperationResult result = new GraphOperationResult();
        Collections.sort(plots, new PlotWindowModelComparator());
        List<String> renderedHtmlStrings = getRenderedHtmlPlots(plotConfiguration, plots);

        StringBuilder renderedHtmlSb = new StringBuilder();
        renderedHtmlSb.append(HtmlUtil.createScriptSrc("https://cdn.plot.ly/plotly-latest.min.js"));
        renderedHtmlSb.append(PlotlyHtmlFragmentGenerator.getWatchrGraphCss());
        renderedHtmlSb.append(createDivLayout(plotConfiguration, plots, false));
        renderedHtmlSb.append(OsUtil.getOSLineBreak());

        for(String htmlString : renderedHtmlStrings) {
            renderedHtmlSb.append(htmlString);
            renderedHtmlSb.append(OsUtil.getOSLineBreak());
        }
        result.setHtml(renderedHtmlSb.toString());
        return result;
    }

    private List<String> getRenderedHtmlPlots(
            GraphDisplayConfig plotConfiguration, List<PlotWindowModel> plotWindowModels) {

        List<String> renderedPlotHtmlStrings = new ArrayList<>();
        int currentPage = plotConfiguration.getPage();
        int graphsPerPage = plotConfiguration.getGraphsPerPage();
        if(graphsPerPage < 0 || currentPage < 0) { // Get all plots instead.
            currentPage = 1;
            graphsPerPage = plotWindowModels.size();
        }
        int startPlotIndex = (currentPage-1) * graphsPerPage;
        int displayRange = plotConfiguration.getDisplayRange();

        windowGenerator.resetCanvasIndexOffset();

        for(int i = 0; i < graphsPerPage; i++) {
            int nextPlotIndex = startPlotIndex + i;
            if(nextPlotIndex < plotWindowModels.size()) {
                PlotWindowModel plot = plotWindowModels.get(nextPlotIndex);
                plot.setDivName("plotDiv_" + UUID.randomUUID().toString());
                plot.setViewHeight(plotConfiguration.getGraphHeight());
                plot.setViewWidth(plotConfiguration.getGraphWidth());
                try {
                    renderedPlotHtmlStrings.add(
                        windowGenerator.generatePlotWindow(plot, PlotType.DEFAULT, displayRange)
                    );
                } catch(IOException e) {
                    ILogger logger = WatchrCoreApp.getInstance().getLogger();
                    logger.logError("An error occurred serializing a Plotly plot.", e);
                }
            }
        }
        return renderedPlotHtmlStrings;
    }
    
    private String getTemplateHtml() {
        StringBuilder sb = new StringBuilder();

        String script = HtmlUtil.createScriptSrc("https://cdn.plot.ly/plotly-latest.min.js");
        sb.append(HtmlUtil.createHead(script));

        StringBuilder bodyContentsSb = new StringBuilder();
        bodyContentsSb.append("$$$WATCHR_CSS").append(OsUtil.getOSLineBreak());
        bodyContentsSb.append("$$$DIVS").append(OsUtil.getOSLineBreak());
        bodyContentsSb.append("$$$PLOTLY_SCRIPTS");

        sb.append(HtmlUtil.createBody(bodyContentsSb.toString()));
        return sb.toString();
    }

    private String createDivLayout(GraphDisplayConfig plotConfiguration, List<PlotWindowModel> plots, boolean standalone) {
        int graphWidth = plotConfiguration.getGraphWidth();
        int graphHeight = plotConfiguration.getGraphHeight();
        int graphsPerRow = plotConfiguration.getGraphsPerRow();

        StringBuilder styleSb = new StringBuilder();
        styleSb.append("width:").append(graphWidth).append("px;");
        styleSb.append("height:").append(graphHeight).append("px;");
        styleSb.append("border:").append("1px solid black");

        StringBuilder styleSb2 = new StringBuilder();
        styleSb2.append("width:").append(graphWidth).append("px;");
        styleSb2.append("height:").append(graphHeight).append("px;");

        StringBuilder divRowsSb = new StringBuilder();
        StringBuilder divCellsSb = new StringBuilder();
        int rowCounter = 0;

        int currentPage = plotConfiguration.getPage();
        int graphsPerPage = plotConfiguration.getGraphsPerPage();
        if(graphsPerPage < 0 || currentPage < 0) { // Get all plots instead.
            currentPage = 1;
            graphsPerPage = plots.size();
        }
        int startPlotIndex = (currentPage-1) * graphsPerPage;

        if(standalone) {
            graphsPerPage = plots.size();
            startPlotIndex = 0;
        }

        for(int i = 0; i < graphsPerPage; i++) {
            int nextPlotIndex = startPlotIndex + i;
            if(nextPlotIndex < plots.size()) {
                PlotWindowModel plot = plots.get(nextPlotIndex);
                String plotlyLandingDiv = HtmlUtil.createDiv("", plot.getDivName(), "", "", styleSb2.toString());

                String controlPanelContents = buttonBarConfiguration.getHtml(plot, buttons);
                String controlPanelDiv =
                    HtmlUtil.createDiv(controlPanelContents, plot.getDivName() + "_controls", "", "center", "height:50px");

                StringBuilder plotCellSb = new StringBuilder();
                plotCellSb.append(plotlyLandingDiv);
                if(!standalone) {
                    plotCellSb.append(controlPanelDiv);
                }

                divCellsSb.append(HtmlUtil.createDiv(plotCellSb.toString(), "", "Cell", "", styleSb.toString()));

                rowCounter++;
                if(rowCounter == graphsPerRow) {
                    rowCounter = 0;
                    divRowsSb.append(HtmlUtil.createDiv(divCellsSb.toString(), "", "Row", "", ""));
                    divCellsSb = new StringBuilder();
                }
            }
        }
        if(rowCounter < graphsPerRow) {
            divRowsSb.append(HtmlUtil.createDiv(divCellsSb.toString(), "", "Row", "", ""));
        }

        return HtmlUtil.createDiv(divRowsSb.toString(), "", "Table", "", styleSb2.toString());
    }

    private String writePlotNamesAsString(List<PlotWindowModel> plots) {
        StringBuilder sb = new StringBuilder();
        for(PlotWindowModel plot : plots) {
            sb.append(plot.getName());
            if(!ListUtil.isLastElement(plots, plot)) {
                sb.append(CommonConstants.UNUSUAL_COMMA);
            }
        }
        return sb.toString();
    }

    ///////////
    // CLASS //
    ///////////

    class PlotWindowModelComparator implements Comparator<PlotWindowModel> {
        @Override
        public int compare(PlotWindowModel p1, PlotWindowModel p2) {
            return p1.getName().compareTo(p2.getName());
        }
    }
}
