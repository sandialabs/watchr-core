package gov.sandia.watchr.graph.library.impl;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.config.GraphDisplayConfig;
import gov.sandia.watchr.graph.HtmlUtil;
import gov.sandia.watchr.graph.chartreuse.model.PlotWindowModel;
import gov.sandia.watchr.graph.library.IHtmlButtonRenderer;
import gov.sandia.watchr.graph.options.AbstractButtonBar;

public class PlotlyGraphDivBuilder {

    ////////////
    // FIELDS //
    ////////////

    private final GraphDisplayConfig plotConfiguration;
    private final IHtmlButtonRenderer buttonRenderer;
    private final boolean standalone;
    private final int currentPage;
    
    private int graphsPerPage;
    
    private int rowCounter = 0;
    private StringBuilder divRowsSb;
    private StringBuilder divCellsSb;
    
    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PlotlyGraphDivBuilder(
            GraphDisplayConfig plotConfiguration, boolean standalone,
            IHtmlButtonRenderer buttonRenderer) {
        this(
            plotConfiguration, standalone, buttonRenderer,
            plotConfiguration.getGraphsPerPage(), plotConfiguration.getPage());
    }

    public PlotlyGraphDivBuilder(
            GraphDisplayConfig plotConfiguration, boolean standalone,
            IHtmlButtonRenderer buttonRenderer,
            int graphsPerPage, int currentPage) {
        this.plotConfiguration = plotConfiguration;
        this.standalone = standalone;
        this.buttonRenderer = buttonRenderer;
        this.graphsPerPage = graphsPerPage;
        this.currentPage = currentPage;
    }

    ////////////
    // PUBLIC //
    ////////////

    public String createDivLayout(List<PlotWindowModel> plots) throws UnsupportedEncodingException {
        if(graphsPerPage < 1) {
            graphsPerPage = plots.size();
        }

        String divRows = createDivRows(plots);
        String divLayoutStyle = createDivLayoutStyle(plotConfiguration);
        return HtmlUtil.createDiv(divRows, "", "Table", "", divLayoutStyle);
    }

    /////////////
    // PRIVATE //
    /////////////

    private String createDivRows(List<PlotWindowModel> plots) throws UnsupportedEncodingException {
        divRowsSb = new StringBuilder();
        divCellsSb = new StringBuilder();
        rowCounter = 0;

        int graphsPerRow = plotConfiguration.getGraphsPerRow();
        int startPlotIndex = getStartPlotIndex();

        for(int i = 0; i < graphsPerPage; i++) {
            int nextPlotIndex = startPlotIndex + i;
            if(nextPlotIndex < plots.size()) {
                PlotWindowModel plot = plots.get(nextPlotIndex);
                String plotDivString = createDivForPlot(plot);

                if(StringUtils.isNotBlank(plotDivString)) {
                    divCellsSb.append(plotDivString);
                    updateRowCounter();
                }
            }
        }
        if(rowCounter < graphsPerRow) {
            divRowsSb.append(HtmlUtil.createDiv(divCellsSb.toString(), "", "Row", "", ""));
        }
        return divRowsSb.toString();
    }

    private int getStartPlotIndex() {
        return standalone ? 0 : (currentPage-1) * graphsPerPage;
    }

    private String createDivForPlot(PlotWindowModel plot) throws UnsupportedEncodingException {
        StringBuilder divCellSb = new StringBuilder();
        String divName = plot.getDivName();
        if(!divName.equals("plotDiv")) {
            String plotlyLandingDiv =
                HtmlUtil.createDiv("", divName, "", "", createDivLayoutStyle(plotConfiguration));

            AbstractButtonBar buttonBarConfiguration = buttonRenderer.getButtonBar();
            String controlPanelContents = buttonBarConfiguration.getHtml(plot, buttonRenderer.getButtons());
            String controlPanelDiv =
                HtmlUtil.createDiv(
                    controlPanelContents, divName + "_controls", "", "center", "height:50px"
                );

            StringBuilder plotCellSb = new StringBuilder();
            plotCellSb.append(plotlyLandingDiv);
            if(!standalone) {
                plotCellSb.append(controlPanelDiv);
            }

            divCellSb.append(
                HtmlUtil.createDiv(plotCellSb.toString(), "", "Cell", "", createDivLayoutBorderStyle(plotConfiguration))
            );
        }
        return divCellSb.toString();
    }

    private void updateRowCounter() {
        int graphsPerRow = plotConfiguration.getGraphsPerRow();
        rowCounter++;
        if(rowCounter == graphsPerRow) {
            resetRow();
        }
    }

    private void resetRow() {
        rowCounter = 0;
        divRowsSb.append(HtmlUtil.createDiv(divCellsSb.toString(), "", "Row", "", ""));
        divCellsSb = new StringBuilder();
    }

    private String createDivLayoutBorderStyle(GraphDisplayConfig plotConfiguration) {
        int graphWidth = plotConfiguration.getGraphWidth();
        int graphHeight = plotConfiguration.getGraphHeight();

        StringBuilder styleSb = new StringBuilder();
        styleSb.append("width:").append(graphWidth).append("px;");
        styleSb.append("height:").append(graphHeight).append("px;");
        styleSb.append("border:").append("1px solid black");

        return styleSb.toString();
    }

    private String createDivLayoutStyle(GraphDisplayConfig plotConfiguration) {
        int graphWidth = plotConfiguration.getGraphWidth();
        int graphHeight = plotConfiguration.getGraphHeight();

        StringBuilder styleSb = new StringBuilder();
        styleSb.append("width:").append(graphWidth).append("px;");
        styleSb.append("height:").append(graphHeight).append("px;");

        return styleSb.toString();
    }
}
