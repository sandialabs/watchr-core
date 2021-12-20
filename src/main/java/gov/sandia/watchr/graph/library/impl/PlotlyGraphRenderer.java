/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.graph.library.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.config.GraphDisplayConfig.ExportMode;
import gov.sandia.watchr.config.GraphDisplayConfig.LeafNodeStrategy;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.graph.HtmlUtil;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.graph.chartreuse.generator.plotly.PlotlyCanvasGenerator;
import gov.sandia.watchr.graph.chartreuse.generator.plotly.PlotlyHtmlFragmentGenerator;
import gov.sandia.watchr.graph.chartreuse.generator.plotly.PlotlyTraceGenerator;
import gov.sandia.watchr.graph.chartreuse.generator.plotly.PlotlyWindowGenerator;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModelComparator;
import gov.sandia.watchr.graph.library.GraphOperationMetadata;
import gov.sandia.watchr.graph.library.GraphOperationResult;
import gov.sandia.watchr.graph.library.IHtmlButtonRenderer;
import gov.sandia.watchr.graph.library.IHtmlGraphRenderer;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.log.StringOutputLogger;
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
    
    private final IDatabase db;
    private final PlotlyButtonRenderer buttonRenderer;

    private final ILogger mainLogger;
    private StringOutputLogger graphRenderingLog;

    private final IFileReader fileReader;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PlotlyGraphRenderer(IDatabase db, ILogger mainLogger, IFileReader fileReader) {
        traceGenerator  = new PlotlyTraceGenerator();
        canvasGenerator = new PlotlyCanvasGenerator(traceGenerator);
        windowGenerator = new PlotlyWindowGenerator(canvasGenerator, true);

        canvasGenerator.setParent(windowGenerator);
        traceGenerator.setParent(canvasGenerator);

        this.db = db;
        this.buttonRenderer = new PlotlyButtonRenderer(db);
        this.mainLogger = mainLogger;
        this.fileReader = fileReader;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public GraphOperationResult getGraphHtml(GraphDisplayConfig plotConfiguration, boolean standalone) {

        GraphDisplayConfig copyConfiguration = new GraphDisplayConfig(plotConfiguration);
        String parentPlotLocation = copyConfiguration.getNextPlotDbLocation();
        String category = copyConfiguration.getDisplayCategory();

        List<PlotWindowModel> plots = new ArrayList<>(db.getChildren(parentPlotLocation, category));
        if(plots.isEmpty()) {
            // If we didn't find any plots, it means we are either at a leaf node with no children, or
            // the tree is empty.  We need to check the graph configuration to see what to do next.
            boolean atTheTop = copyConfiguration.getNextPlotDbLocation().equals(CommonConstants.ROOT_PATH_ALIAS);
            LeafNodeStrategy leafStrategy = copyConfiguration.getLeafNodeStrategy();
            if(!atTheTop) {
                if(leafStrategy == LeafNodeStrategy.TRAVEL_UP_TO_PARENT) {
                    plots.addAll(getParentPlotsIfNoChildren(copyConfiguration));
                } else if(leafStrategy == LeafNodeStrategy.SHOW_CHILD_ONLY) {
                    PlotWindowModel thisPlot = db.searchPlot(parentPlotLocation, category);
                    if(thisPlot != null) {
                        plots.add(thisPlot);
                    }
                }
            }
        }
        try {
            return renderPlots(plots, standalone, copyConfiguration);
        } catch(UnsupportedEncodingException e) {
            mainLogger.logError("An error occurred retrieving graphs.", e);
        }

        return null;
    }

    @Override
    public void exportGraphHtml(
            GraphDisplayConfig plotConfiguration, List<String> categories, String destDirAbsPath) {

        try {
            List<String> categoriesWithBlank = new ArrayList<>(categories);
            if(categories.isEmpty()) {
                categoriesWithBlank.add("");
            }

            if(plotConfiguration.getExportMode() == ExportMode.PER_PLOT) {
                exportGraphHtmlByPlot(plotConfiguration, categoriesWithBlank, destDirAbsPath);
            } else { // plotConfiguration.getExportMode() == ExportMode.PER_CATEGORY
                for(String category : categoriesWithBlank) {
                    exportGraphHtmlByCategory(plotConfiguration, category, destDirAbsPath);
                }
            }
        } catch(UnsupportedEncodingException e) {
            mainLogger.logError("An error occurred exporting graphs.", e);
        }
    }

    @Override
    public IHtmlButtonRenderer getButtonRenderer() {
        return buttonRenderer;
    }

    /////////////
    // PRIVATE //
    /////////////

    private void exportGraphHtmlByPlot(
        GraphDisplayConfig configuration, List<String> categories, String destDirAbsPath) throws UnsupportedEncodingException {

        for(String category : categories) {
            GraphDisplayConfig plotConfigurationForCategory = new GraphDisplayConfig(configuration);
            plotConfigurationForCategory.setDisplayCategory(category);
            String parentPlotLocation = plotConfigurationForCategory.getNextPlotDbLocation();

            List<PlotWindowModel> plots = new ArrayList<>(db.getChildren(parentPlotLocation, category));
            int pageCount = getPageCount(plotConfigurationForCategory, parentPlotLocation, category);

            for(int i = 1; i <= pageCount; i++) {
                GraphDisplayConfig plotConfigurationForPage = new GraphDisplayConfig(plotConfigurationForCategory);
                plotConfigurationForPage.setPage(i);
                for(PlotWindowModel plot : plots) {
                    List<PlotWindowModel> singlePlotList = new ArrayList<>();
                    singlePlotList.add(plot);
                    GraphOperationResult result = renderPlots(singlePlotList, true, plotConfigurationForPage);
                    if(StringUtils.isNotBlank(result.getHtml())) {
                        if(pageCount == i) {
                            writeHtmlToFile(result.getHtml(), plot.getName(), category, destDirAbsPath);
                        } else {
                            writeHtmlToFile(result.getHtml(), plot.getName(), i, category, destDirAbsPath);
                        }
                        exportGraphChildrenHtml(result, plotConfigurationForPage, category, destDirAbsPath);
                    }
                }
            }
        }
    }

    private void exportGraphHtmlByCategory(
            GraphDisplayConfig plotConfiguration, String category, String destDirAbsPath) {

        GraphDisplayConfig plotConfigurationForCategory = new GraphDisplayConfig(plotConfiguration);
        plotConfigurationForCategory.setDisplayCategory(category);
        String currentPlotName = plotConfigurationForCategory.getNextPlotDbLocation();

        int pageCount = getPageCount(plotConfigurationForCategory, plotConfigurationForCategory.getNextPlotDbLocation(), category);
        for(int i = 1; i <= pageCount; i++) {
            GraphDisplayConfig plotConfigurationForPage = new GraphDisplayConfig(plotConfigurationForCategory);
            plotConfigurationForPage.setPage(i);

            GraphOperationResult result = getGraphHtml(plotConfigurationForPage, true);
            String numberOfGraphsStr = result.getMetadata().get(GraphOperationMetadata.NUMBER_OF_GRAPHS.get());
            if(NumberUtils.isCreatable(numberOfGraphsStr)) {
                String finalHtml = result.getHtml();
                int numberOfGraphs = Integer.parseInt(numberOfGraphsStr);
                if(StringUtils.isNotBlank(finalHtml) && numberOfGraphs > 0) {
                    if(pageCount == 1) {
                        writeHtmlToFile(finalHtml, currentPlotName, category, destDirAbsPath);
                    } else {
                        writeHtmlToFile(finalHtml, currentPlotName, i, category, destDirAbsPath);
                    }
                    exportGraphChildrenHtml(
                        result, plotConfigurationForPage, category, destDirAbsPath);
                }
            }
        }
    }

    private int getPageCount(GraphDisplayConfig graphConfig, String parentPlotLocation, String category) {
        List<PlotWindowModel> plots = new ArrayList<>(db.getChildren(parentPlotLocation, category));
        double plotCount = plots.size();
        double graphsPerPage = graphConfig.getGraphsPerPage();
        if(graphsPerPage <= 0) {
            return 1;
        } else {
            return (int) Math.ceil(plotCount / graphsPerPage);
        }
    }

    private void exportGraphChildrenHtml(
            GraphOperationResult result, GraphDisplayConfig configuration,
            String category, String destDirAbsPath) {
        
        List<String> plotNames = getPlotNameManifestFromResult(result);
        for(String nextPlotName : plotNames) {
            if(shouldExportNextPlot(nextPlotName)) {
                GraphDisplayConfig childConfiguration = new GraphDisplayConfig(configuration);
                childConfiguration.setNextPlotDbLocation(nextPlotName);
                List<String> childCategories = new ArrayList<>();
                childCategories.add(category);

                exportGraphHtml(childConfiguration, childCategories, destDirAbsPath);
            }
        }
    }

    private boolean shouldExportNextPlot(String nextPlotName) {
        boolean proceed = StringUtils.isNotBlank(nextPlotName);
        proceed = proceed && !nextPlotName.equals(CommonConstants.ROOT_PATH_ALIAS);
        return proceed;
    }

    ////////////////////
    // RENDER AS HTML //
    ////////////////////

    private GraphOperationResult renderPlots(
            List<PlotWindowModel> plots, boolean standalone, GraphDisplayConfig configuration)
            throws UnsupportedEncodingException {

        GraphOperationResult result = standalone ?
                                      renderStandalonePlots(configuration, plots) :
                                      renderEmbeddedPlots(configuration, plots);

        result.getMetadata().put(GraphOperationMetadata.PLOT_DB_LOCATION.get(), configuration.getNextPlotDbLocation());
        result.getMetadata().put(GraphOperationMetadata.PLOT_NAME_MANIFEST.get(), writePlotNamesAsString(plots));
        result.getMetadata().put(GraphOperationMetadata.NUMBER_OF_GRAPHS.get(), Integer.toString(plots.size()));
        return result;
    }        

    private GraphOperationResult renderStandalonePlots(
            GraphDisplayConfig plotConfiguration, List<PlotWindowModel> plots) throws UnsupportedEncodingException {

        GraphOperationResult result = new GraphOperationResult();

        graphRenderingLog = new StringOutputLogger();
        graphRenderingLog.logDebug("Number of plots is " + plots.size());

        PlotlyGraphDivBuilder divBuilder = new PlotlyGraphDivBuilder(plotConfiguration, true, buttonRenderer);        
        String templateHtml = getTemplateHtml();
        if(templateHtml != null) {
            Collections.sort(plots, new PlotWindowModelComparator(plotConfiguration));
            List<String> renderedHtmlStrings = getRenderedHtmlPlots(plotConfiguration, plots);

            StringBuilder renderedHtmlSb = new StringBuilder();
            for(String htmlString : renderedHtmlStrings) {
                renderedHtmlSb.append(htmlString);
                renderedHtmlSb.append(OsUtil.getOSLineBreak());
            }
            
            templateHtml = templateHtml.replace("$$$PLOTLY_SCRIPTS", renderedHtmlSb.toString());
            templateHtml = templateHtml.replace("$$$DIVS", divBuilder.createDivLayout(plots));
            templateHtml = templateHtml.replace("$$$WATCHR_CSS", PlotlyHtmlFragmentGenerator.getWatchrGraphCss());

            result.setHtml(templateHtml);
        } else {
            result.setLog("templateHtml was blank.");
        }
        
        result.setLog(graphRenderingLog.getLogAsString());
        return result;
    }

    private GraphOperationResult renderEmbeddedPlots(
            GraphDisplayConfig plotConfiguration, List<PlotWindowModel> plots) throws UnsupportedEncodingException {

        PlotlyGraphDivBuilder divBuilder = new PlotlyGraphDivBuilder(plotConfiguration, false, buttonRenderer);
        graphRenderingLog = new StringOutputLogger();

        Collections.sort(plots, new PlotWindowModelComparator(plotConfiguration));
        List<String> renderedHtmlStrings = getRenderedHtmlPlots(plotConfiguration, plots);

        StringBuilder renderedHtmlSb = new StringBuilder();
        renderedHtmlSb.append(HtmlUtil.createScriptSrc("https://cdn.plot.ly/plotly-latest.min.js"));
        renderedHtmlSb.append(PlotlyHtmlFragmentGenerator.getWatchrGraphCss());
        renderedHtmlSb.append(divBuilder.createDivLayout(plots));
        renderedHtmlSb.append(OsUtil.getOSLineBreak());

        for(String htmlString : renderedHtmlStrings) {
            renderedHtmlSb.append(htmlString);
            renderedHtmlSb.append(OsUtil.getOSLineBreak());
        }

        GraphOperationResult result = new GraphOperationResult();
        result.setHtml(renderedHtmlSb.toString());
        result.setLog(graphRenderingLog.getLogAsString());
        return result;
    }

    private List<String> getRenderedHtmlPlots(
            GraphDisplayConfig plotConfiguration, List<PlotWindowModel> plotWindowModels) {

        List<String> renderedPlotHtmlStrings = new ArrayList<>();
        int currentPage = plotConfiguration.getPage();
        int graphsPerPage = plotConfiguration.getGraphsPerPage();
        if(graphsPerPage <= 0 || currentPage <= 0) { // Get all plots instead.
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
                    mainLogger.logError("An error occurred serializing a Plotly plot.", e);
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

    /////////////
    // UTILITY //
    /////////////

    private Set<PlotWindowModel> getParentPlotsIfNoChildren(GraphDisplayConfig graphConfig) {
        PlotWindowModel parentPlot = db.getParent(graphConfig.getLastPlotDbLocation(), graphConfig.getDisplayCategory());
        if(parentPlot != null) {
            graphConfig.setNextPlotDbLocation(parentPlot.getName());
        } else {
            graphConfig.setNextPlotDbLocation(CommonConstants.ROOT_PATH_ALIAS);
        }
        return db.getChildren(graphConfig.getNextPlotDbLocation(), graphConfig.getDisplayCategory());
    }

    private List<String> getPlotNameManifestFromResult(GraphOperationResult result) {
        String plotNames = result.getMetadata().get(GraphOperationMetadata.PLOT_NAME_MANIFEST.get());
        String[] plotNamesArr = plotNames.split(CommonConstants.UNUSUAL_COMMA);
        return Arrays.asList(plotNamesArr);
    }

    private void writeHtmlToFile(String html, String plotName, int pageSuffix, String categorySuffix, String destDirAbsPath) {
        String destFileName = buildExportFilename(plotName, pageSuffix, categorySuffix);
        String destFileAbsPath = destDirAbsPath + "/" + destFileName;
        fileReader.writeToFile(destFileAbsPath, html);
    }    

    private void writeHtmlToFile(String html, String plotName, String categorySuffix, String destDirAbsPath) {
        String destFileName = buildExportFilename(plotName, categorySuffix);
        String destFileAbsPath = destDirAbsPath + "/" + destFileName;
        fileReader.writeToFile(destFileAbsPath, html);
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

    private String buildExportFilename(String dbLocation, String category) {
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtil.removeIllegalCharactersFromFilename(dbLocation));
        if(StringUtils.isNotBlank(category)) {
            sb.append("_").append(category);
        }
        sb.append(".html");
        return sb.toString();
    }    

    private String buildExportFilename(String dbLocation, int pageSuffix, String category) {
        StringBuilder sb = new StringBuilder();
        sb.append(FileUtil.removeIllegalCharactersFromFilename(dbLocation));
        if(StringUtils.isNotBlank(category)) {
            sb.append("_").append(pageSuffix);
            sb.append("_").append(category);
        }
        sb.append(".html");
        return sb.toString();
    }    
}
