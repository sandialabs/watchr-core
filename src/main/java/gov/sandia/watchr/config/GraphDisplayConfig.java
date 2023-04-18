/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config;

import java.util.ArrayList;
import java.util.List;

import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.util.CommonConstants;
import gov.sandia.watchr.util.OsUtil;

public class GraphDisplayConfig implements IConfig {

    ////////////
    // FIELDS //
    ////////////

    public enum GraphDisplaySort {
        NONE,
        ASCENDING,
        DESCENDING;
    }
    public enum ExportMode {
        NONE,
        PER_CATEGORY,
        PER_PLOT;
    }
    public enum LeafNodeStrategy {
        TRAVEL_UP_TO_PARENT,
        SHOW_CHILD_ONLY,
        SHOW_NOTHING
    }
    private final String configPath;

    // Non-configurable properties
    private String lastPlotDbLocation = "";
    private String nextPlotDbLocation = "";
    private int page = -1;

    // Configurable properties
    private String displayCategory = "";
    private String searchQuery = "";
    private int displayRange = -1;
    private int graphWidth = -1;
    private int graphHeight = -1;
    private int graphsPerRow = -1;
    private int graphsPerPage = -1;
    private int displayedDecimalPlaces = -1;
    private LeafNodeStrategy leafStrategy;
    private GraphDisplaySort sort;
    private ExportMode exportMode;

    private final ILogger logger;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public GraphDisplayConfig(String configPathPrefix, ILogger logger) {
        this.configPath = configPathPrefix + "/graphDisplayConfig";
        this.logger = logger;

        this.lastPlotDbLocation = CommonConstants.ROOT_PATH_ALIAS;
        this.nextPlotDbLocation = CommonConstants.ROOT_PATH_ALIAS;
        this.page = 1;

        this.searchQuery = "/";
        this.displayRange = 30;
        this.graphWidth = 500;
        this.graphHeight = 500;
        this.graphsPerRow = 3;
        this.graphsPerPage = 15;
        this.displayedDecimalPlaces = 3;
        this.leafStrategy = LeafNodeStrategy.SHOW_NOTHING;
        this.sort = GraphDisplaySort.ASCENDING;
        this.exportMode = ExportMode.NONE;
    }

    public GraphDisplayConfig(GraphDisplayConfig copy) {
        this.nextPlotDbLocation = copy.getNextPlotDbLocation();
        this.lastPlotDbLocation = copy.getLastPlotDbLocation();
        this.page = copy.getPage();
        this.displayCategory = copy.getDisplayCategory();
        this.searchQuery = copy.getSearchQuery();
        this.displayRange = copy.getDisplayRange();
        this.graphWidth = copy.getGraphWidth();
        this.graphHeight = copy.getGraphHeight();
        this.graphsPerRow = copy.getGraphsPerRow();
        this.graphsPerPage = copy.getGraphsPerPage();
        this.displayedDecimalPlaces = copy.getDisplayedDecimalPlaces();
        this.leafStrategy = copy.getLeafNodeStrategy();
        this.sort = copy.getSort();
        this.exportMode = copy.getExportMode();

        this.configPath = copy.getConfigPath();
        this.logger = copy.getLogger();
    }

    /////////////
    // GETTERS //
    /////////////

    public String getNextPlotDbLocation() {
        return nextPlotDbLocation;
    }

    public String getLastPlotDbLocation() {
        return lastPlotDbLocation;
    }

    public int getPage() {
        return page;
    }

    public String getDisplayCategory() {
        return displayCategory;
    }

    public int getDisplayRange() {
        return displayRange;
    }

    public int getGraphWidth() {
        return graphWidth;
    }

    public int getGraphHeight() {
        return graphHeight;
    }

    public int getGraphsPerRow() {
        return graphsPerRow;
    }

    public int getGraphsPerPage() {
        return graphsPerPage;
    }

    public int getDisplayedDecimalPlaces() {
        return displayedDecimalPlaces;
    }

    public String getSearchQuery(){
        return searchQuery;
    }

    public LeafNodeStrategy getLeafNodeStrategy() {
        return leafStrategy;
    }

    public GraphDisplaySort getSort() {
        // This field has the potential to be null, since it was
        // added after version 1.0.0.
        if(sort == null) {
            sort = GraphDisplaySort.ASCENDING;
        }
        return sort;
    }

    public ExportMode getExportMode() {
        // This field has the potential to be null, since it was
        // added after version 1.0.0.
        if(exportMode == null) {
            exportMode = ExportMode.NONE;
        }
        return exportMode;
    }

    @Override
    public String getConfigPath() {
        return configPath;
    }

    @Override
    public ILogger getLogger() {
        return logger;
    }

    /////////////
    // SETTERS //
    /////////////

    public void setNextPlotDbLocation(String nextPlotDbLocation) {
        this.nextPlotDbLocation = nextPlotDbLocation;
    }

    public void setLastPlotDbLocation(String lastPlotDbLocation) {
        this.lastPlotDbLocation = lastPlotDbLocation;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setDisplayCategory(String displayCategory) {
        this.displayCategory = displayCategory;
    }

    public void setSearchQuery(String searchQuery){
        this.searchQuery = searchQuery;
    }

    public void setDisplayRange(int displayRange) {
        this.displayRange = displayRange;
    }

    public void setGraphWidth(int graphWidth) {
        this.graphWidth = graphWidth;
    }

    public void setGraphHeight(int graphHeight) {
        this.graphHeight = graphHeight;
    }

    public void setGraphsPerRow(int graphsPerRow) {
        this.graphsPerRow = graphsPerRow;
    }

    public void setGraphsPerPage(int graphsPerPage) {
        this.graphsPerPage = graphsPerPage;
    }

    public void setDisplayedDecimalPlaces(int displayedDecimalPlaces) {
        this.displayedDecimalPlaces = displayedDecimalPlaces;
    }

    public void setLeafNodeStrategy(LeafNodeStrategy leafStrategy) {
        this.leafStrategy = leafStrategy;
    }

    public void setLeafNodeStrategy(String leafStrategyStr) {
        if(leafStrategyStr.equalsIgnoreCase("SHOW_CHILD_ONLY")) {
            leafStrategy = LeafNodeStrategy.SHOW_CHILD_ONLY;
        } else if(leafStrategyStr.equalsIgnoreCase("SHOW_NOTHING")) {
            leafStrategy = LeafNodeStrategy.SHOW_NOTHING;
        } else {
            leafStrategy = LeafNodeStrategy.TRAVEL_UP_TO_PARENT;
        }
    }

    public void setSort(GraphDisplaySort sort) {
        this.sort = sort;
    }

    public void setSort(String sortStr) {
        if(sortStr.equalsIgnoreCase("NONE")) {
            sort = GraphDisplaySort.NONE;
        } else if(sortStr.equalsIgnoreCase("DESCENDING")) {
            sort = GraphDisplaySort.DESCENDING;
        } else {
            sort = GraphDisplaySort.ASCENDING;
        }
    }

    public void setExportMode(ExportMode exportMode) {
        this.exportMode = exportMode;
    }

    public void setExportMode(String exportModeStr) {
        if(exportModeStr.equalsIgnoreCase("NONE")) {
            exportMode = ExportMode.NONE;
        } else if(exportModeStr.equalsIgnoreCase("PER_CATEGORY") || exportModeStr.equalsIgnoreCase("perCategory")) {
            exportMode = ExportMode.PER_CATEGORY;
        } else {
            exportMode = ExportMode.PER_PLOT;
        }
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("nextPlotDbLocation: " + nextPlotDbLocation + OsUtil.getOSLineBreak());
        sb.append("lastPlotDbLocation: " + lastPlotDbLocation + OsUtil.getOSLineBreak());
        sb.append("page: " + page + OsUtil.getOSLineBreak());
        sb.append("searchQuery: " + searchQuery + OsUtil.getOSLineBreak());
        sb.append("displayCategory: " + displayCategory + OsUtil.getOSLineBreak());
        sb.append("displayRange: " + displayRange + OsUtil.getOSLineBreak());
        sb.append("graphWidth: " + graphWidth + OsUtil.getOSLineBreak());
        sb.append("graphHeight: " + graphHeight + OsUtil.getOSLineBreak());
        sb.append("graphsPerRow: " + graphsPerRow + OsUtil.getOSLineBreak());
        sb.append("graphsPerPage: " + graphsPerPage + OsUtil.getOSLineBreak());
        sb.append("displayedDecimalPlaces: " + displayedDecimalPlaces + OsUtil.getOSLineBreak());
        sb.append("leafNodeStrategy: " + leafStrategy.toString() + OsUtil.getOSLineBreak());

        return sb.toString();
    }

    @Override
    public void validate() {
        if(page < 1) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "Page must start at 1."));
        }
        if(displayRange < 1) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "Display range must be at least 1."));
        }
        if(graphWidth < 1) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "Graph width must be at least 1 pixel."));
        }
        if(graphHeight < 1) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "Graph height must be at least 1 pixel."));
        }
        if(graphsPerRow < 1) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "Must have at least 1 graph per row."));
        }
        if(graphsPerPage < 1) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "Must have at least 1 graph per page."));
        }
        if(displayedDecimalPlaces < 0) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "Number of displayed decimal places cannot be a negative number."));
        }
    }

    @Override
    public List<WatchrDiff<?>> diff(IConfig other) {
        GraphDisplayConfig otherGraphConfig = (GraphDisplayConfig) other;
        List<WatchrDiff<?>> diffList = new ArrayList<>();

        if(!(searchQuery.equals(otherGraphConfig.searchQuery))) {
            WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.SEARCH_QUERY);
            diff.setBeforeValue(searchQuery);
            diff.setNowValue(otherGraphConfig.searchQuery);
            diffList.add(diff);
        }
        if(!(displayCategory.equals(otherGraphConfig.displayCategory))) {
            WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.DISPLAY_CATEGORY);
            diff.setBeforeValue(displayCategory);
            diff.setNowValue(otherGraphConfig.displayCategory);
            diffList.add(diff);
        }
        if(displayRange != otherGraphConfig.displayRange) {
            WatchrDiff<Integer> diff = new WatchrDiff<>(configPath, DiffCategory.DISPLAY_RANGE);
            diff.setBeforeValue(displayRange);
            diff.setNowValue(otherGraphConfig.displayRange);
            diffList.add(diff);
        }
        if(graphWidth != otherGraphConfig.graphWidth) {
            WatchrDiff<Integer> diff = new WatchrDiff<>(configPath, DiffCategory.GRAPH_WIDTH);
            diff.setBeforeValue(graphWidth);
            diff.setNowValue(otherGraphConfig.graphWidth);
            diffList.add(diff);
        }
        if(graphHeight != otherGraphConfig.graphHeight) {
            WatchrDiff<Integer> diff = new WatchrDiff<>(configPath, DiffCategory.GRAPH_HEIGHT);
            diff.setBeforeValue(graphHeight);
            diff.setNowValue(otherGraphConfig.graphHeight);
            diffList.add(diff);
        }
        if(graphsPerRow != otherGraphConfig.graphsPerRow) {
            WatchrDiff<Integer> diff = new WatchrDiff<>(configPath, DiffCategory.GRAPHS_PER_ROW);
            diff.setBeforeValue(graphsPerRow);
            diff.setNowValue(otherGraphConfig.graphsPerRow);
            diffList.add(diff);
        }
        if(graphsPerPage != otherGraphConfig.graphsPerPage) {
            WatchrDiff<Integer> diff = new WatchrDiff<>(configPath, DiffCategory.GRAPHS_PER_PAGE);
            diff.setBeforeValue(graphsPerPage);
            diff.setNowValue(otherGraphConfig.graphsPerPage);
            diffList.add(diff);
        }
        if(displayedDecimalPlaces != otherGraphConfig.displayedDecimalPlaces) {
            WatchrDiff<Integer> diff = new WatchrDiff<>(configPath, DiffCategory.DISPLAYED_DECIMAL_PLACES);
            diff.setBeforeValue(displayedDecimalPlaces);
            diff.setNowValue(otherGraphConfig.displayedDecimalPlaces);
            diffList.add(diff);
        }
        if(sort != otherGraphConfig.sort) {
            WatchrDiff<GraphDisplaySort> diff = new WatchrDiff<>(configPath, DiffCategory.SORT);
            diff.setBeforeValue(sort);
            diff.setNowValue(otherGraphConfig.sort);
            diffList.add(diff);
        }
        if(exportMode != otherGraphConfig.exportMode) {
            WatchrDiff<ExportMode> diff = new WatchrDiff<>(configPath, DiffCategory.EXPORT_MODE);
            diff.setBeforeValue(exportMode);
            diff.setNowValue(otherGraphConfig.exportMode);
            diffList.add(diff);
        }
        return diffList;
    }

    @Override
    public boolean equals(Object other) {
        boolean equals = false;
		if(other == null) {
            return false;
        } else if(other == this) {
            return true;
        } else if(getClass() != other.getClass()) {
            return false;
        } else {
			GraphDisplayConfig otherGraphConfig = (GraphDisplayConfig) other;

            equals = nextPlotDbLocation.equals(otherGraphConfig.nextPlotDbLocation);
            equals = equals && lastPlotDbLocation.equals(otherGraphConfig.lastPlotDbLocation);
            equals = equals && page == otherGraphConfig.page;
            equals = equals && displayCategory.equals(otherGraphConfig.displayCategory);
            equals = equals && displayRange == otherGraphConfig.displayRange;
            equals = equals && graphWidth == otherGraphConfig.graphWidth;
            equals = equals && graphHeight == otherGraphConfig.graphHeight;
            equals = equals && graphsPerRow == otherGraphConfig.graphsPerRow;
            equals = equals && graphsPerPage == otherGraphConfig.graphsPerPage;
            equals = equals && displayedDecimalPlaces == otherGraphConfig.displayedDecimalPlaces;
            equals = equals && leafStrategy == otherGraphConfig.leafStrategy;
            equals = equals && sort == otherGraphConfig.sort;
            equals = equals && exportMode == otherGraphConfig.exportMode;
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + nextPlotDbLocation.hashCode());
        hash = 31 * (hash + lastPlotDbLocation.hashCode());
        hash = 31 * (hash + Integer.hashCode(page));
        hash = 31 * (hash + displayCategory.hashCode());
        hash = 31 * (hash + Integer.hashCode(displayRange));
        hash = 31 * (hash + Integer.hashCode(graphWidth));
        hash = 31 * (hash + Integer.hashCode(graphHeight));
        hash = 31 * (hash + Integer.hashCode(graphsPerRow));
        hash = 31 * (hash + Integer.hashCode(graphsPerPage));
        hash = 31 * (hash + Integer.hashCode(displayedDecimalPlaces));
        hash = 31 * (hash + leafStrategy.ordinal());
        hash = 31 * (hash + Integer.hashCode(sort.ordinal()));
        hash = 31 * (hash + Integer.hashCode(exportMode.ordinal()));
        return hash;
    }
}
