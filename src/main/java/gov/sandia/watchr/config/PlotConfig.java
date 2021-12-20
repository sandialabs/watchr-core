/*******************************************************************************
* Watchr
* ------
* Copyright 2021 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.graph.chartreuse.PlotType;
import gov.sandia.watchr.log.ILogger;

public class PlotConfig implements IConfig {

    ////////////
    // FIELDS //
    ////////////

    public enum CanvasLayout {
        GRID,
        STACKX,
        STACKY,
        SHARED,
        INDEPENDENT
    }

    private String category = "";
    private Boolean useLegend;

    private String name = "";
    private NameConfig autonameConfig;

    private PlotType type = PlotType.SCATTER_PLOT;

    private CanvasLayout canvasLayout = CanvasLayout.SHARED;
    private int canvasPerRow = 1;

    private String templateName = "";
    private String inheritTemplate = "";

    private List<DataLine> dataLines;
    private List<RuleConfig> plotRules;

    private FileFilterConfig fileFilterConfig;
    private FilterConfig pointFilterConfig;

    private final String configPath;
    private final ILogger logger;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PlotConfig(String configPathPrefix, ILogger logger) {
        this.configPath = configPathPrefix + "/plotConfig";

        this.dataLines = new ArrayList<>();
        this.plotRules = new ArrayList<>();
        this.logger = logger;
    }

    public PlotConfig(PlotConfig copy) {
        this.category = copy.getCategory();
        this.useLegend = copy.shouldUseLegend();
        this.name = copy.getName();
        if(copy.getNameConfig() != null) {
            this.autonameConfig = new NameConfig(copy.getNameConfig());
        }

        this.type = copy.getType();

        this.templateName = copy.getTemplateName();
        this.inheritTemplate = copy.getInheritTemplate();

        this.canvasLayout = copy.getCanvasLayout();
        this.canvasPerRow = copy.getCanvasPerRow();

        this.dataLines = new ArrayList<>();
        for(DataLine dataLine : copy.getDataLines()) {
            this.dataLines.add(new DataLine(dataLine));
        }

        this.plotRules = new ArrayList<>();
        for(RuleConfig plotRule : copy.getPlotRules()) {
            this.plotRules.add(new RuleConfig(plotRule));
        }

        if(copy.getPointFilterConfig() != null) {
            pointFilterConfig = new FilterConfig(copy.getPointFilterConfig());
        }
        if(copy.getFileFilterConfig() != null) {
            fileFilterConfig = new FileFilterConfig(copy.getFileFilterConfig());
        }

        this.configPath = copy.getConfigPath();
        this.logger = copy.getLogger();
    }

    /////////////
    // GETTERS //
    /////////////

    public String getName() {
        return name;
    }

    public NameConfig getNameConfig() {
        return autonameConfig;
    }

    public String getCategory() {
        return category;
    }

    public PlotType getType() {
        return type;
    }

    public List<DataLine> getDataLines() {
        return dataLines;
    }

    public List<RuleConfig> getPlotRules() {
        return plotRules;
    }

    public FileFilterConfig getFileFilterConfig() {
        return fileFilterConfig;
    }

    public FilterConfig getPointFilterConfig() {
        return pointFilterConfig;
    }

    public Boolean shouldUseLegend() {
        return useLegend;
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getInheritTemplate() {
        return inheritTemplate;
    }

    @Override
    public String getConfigPath() {
        return configPath;
    }

    public CanvasLayout getCanvasLayout() {
        // This field has the potential to be uninitialized, since it was
        // added after version 1.0.0.
        if(canvasLayout == null) {
            canvasLayout = CanvasLayout.SHARED;
        }
        return canvasLayout;
    }

    public int getCanvasPerRow() {
        // This field has the potential to be uninitialized, since it was
        // added after version 1.0.0.
        if(canvasPerRow <= 0) {
            canvasPerRow = 1;
        }
        return canvasPerRow;
    }

    @Override
    public ILogger getLogger() {
        return logger;
    }

    /////////////
    // SETTERS //
    /////////////

    public void setName(String name) {
        this.name = name;
    }

    public void setNameConfig(NameConfig nameConfig) {
        this.autonameConfig = nameConfig;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setType(PlotType type) {
        this.type = type;
    }

    public void setType(String typeAsString) {
        if(typeAsString.equalsIgnoreCase("areaPlot")) {
            setType(PlotType.AREA_PLOT);
        } else if(typeAsString.equalsIgnoreCase("scatterPlot")) {
            setType(PlotType.SCATTER_PLOT);
        } else if(typeAsString.equalsIgnoreCase("treeMap")) {
            setType(PlotType.TREE_MAP);
        }
    }

    public void setFileFilterConfig(FileFilterConfig fileFilterConfig) {
        this.fileFilterConfig = fileFilterConfig;
    }

    public void setPointFilterConfig(FilterConfig filterConfig) {
        this.pointFilterConfig = filterConfig;
    }

    public void setUseLegend(boolean useLegend) {
        this.useLegend = useLegend;
    }

    public void setInheritTemplate(String inheritTemplate) {
        this.inheritTemplate = inheritTemplate;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setCanvasLayout(CanvasLayout canvasLayout) {
        this.canvasLayout = canvasLayout;
    }

    public void setCanvasLayout(String canvasLayoutString) {
        if(canvasLayoutString.equalsIgnoreCase(CanvasLayout.GRID.toString())) {
            this.canvasLayout = CanvasLayout.GRID;
        } else if(canvasLayoutString.equalsIgnoreCase(CanvasLayout.INDEPENDENT.toString())) {
            this.canvasLayout = CanvasLayout.INDEPENDENT;
        } else if(canvasLayoutString.equalsIgnoreCase(CanvasLayout.STACKX.toString())) {
            this.canvasLayout = CanvasLayout.STACKX;
        } else if(canvasLayoutString.equalsIgnoreCase(CanvasLayout.STACKY.toString())) {
            this.canvasLayout = CanvasLayout.STACKY;
        } else {
            this.canvasLayout = CanvasLayout.SHARED;
        }
    }

    public void setCanvasPerRow(int canvasPerRow) {
        this.canvasPerRow = canvasPerRow;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void validate() {
        if(StringUtils.isBlank(inheritTemplate) &&
           StringUtils.isBlank(name) &&
           (autonameConfig == null || autonameConfig.isBlank())) {   
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "All plots must have a \"name\" or \"autoname\" configuration specified."));
        }
        if(type == null) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "Plot type cannot be null."));
        }
        if(autonameConfig != null) {
            autonameConfig.validate();
        }
        for(DataLine dataLine : dataLines) {
            dataLine.validate();
        }
        for(RuleConfig plotRule : plotRules) {
            plotRule.validate();
        }
        if(pointFilterConfig != null) {
            pointFilterConfig.validate();
        }
        if(fileFilterConfig != null) {
            fileFilterConfig.validate();
        }
    }

    @Override
    public List<WatchrDiff<?>> diff(IConfig other) {
        PlotConfig otherPlotConfig = (PlotConfig) other;
        List<WatchrDiff<?>> diffList = new ArrayList<>();

        if(!(category.equals(otherPlotConfig.category))) {
            WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.CATEGORY);
            diff.setBeforeValue(category);
            diff.setNowValue(otherPlotConfig.category);
            diffList.add(diff);
        }
        if((useLegend == null ^ otherPlotConfig.useLegend == null) ||
           (useLegend != null && otherPlotConfig.useLegend != null && !useLegend.equals(otherPlotConfig.useLegend))) {
            WatchrDiff<Boolean> diff = new WatchrDiff<>(configPath, DiffCategory.USE_LEGEND);
            diff.setBeforeValue(useLegend);
            diff.setNowValue(otherPlotConfig.useLegend);
            diffList.add(diff);
        }
        if(!name.equals(otherPlotConfig.getName())) {
            WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.NAME);
            diff.setBeforeValue(name);
            diff.setNowValue(otherPlotConfig.name);
            diffList.add(diff);
        }
        if(type != otherPlotConfig.type) {
            WatchrDiff<PlotType> diff = new WatchrDiff<>(configPath, DiffCategory.TYPE);
            diff.setBeforeValue(type);
            diff.setNowValue(otherPlotConfig.type);
            diffList.add(diff);
        }
        if(!templateName.equals(otherPlotConfig.templateName)) {
            WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.TEMPLATE_NAME);
            diff.setBeforeValue(templateName);
            diff.setNowValue(otherPlotConfig.templateName);
            diffList.add(diff);
        }
        if(!inheritTemplate.equals(otherPlotConfig.inheritTemplate)) {
            WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.INHERIT_TEMPLATE);
            diff.setBeforeValue(inheritTemplate);
            diff.setNowValue(otherPlotConfig.inheritTemplate);
            diffList.add(diff);
        }

        if(autonameConfig == null ^ otherPlotConfig.autonameConfig == null) {
            WatchrDiff<NameConfig> diff = new WatchrDiff<>(configPath, DiffCategory.NAME_CONFIG);
            diff.setBeforeValue(autonameConfig);
            diff.setNowValue(otherPlotConfig.autonameConfig);
            diffList.add(diff);
        } else if(autonameConfig != null && otherPlotConfig.autonameConfig != null && !autonameConfig.equals(otherPlotConfig.autonameConfig)) {
            diffList.addAll(autonameConfig.diff(otherPlotConfig.autonameConfig));
        }
        
        if(pointFilterConfig == null ^ otherPlotConfig.pointFilterConfig == null) {
            WatchrDiff<FilterConfig> diff = new WatchrDiff<>(configPath, DiffCategory.POINT_FILTER_CONFIG);
            diff.setBeforeValue(pointFilterConfig);
            diff.setNowValue(otherPlotConfig.pointFilterConfig);
            diffList.add(diff);
        } else if(pointFilterConfig != null && otherPlotConfig.pointFilterConfig != null && !pointFilterConfig.equals(otherPlotConfig.pointFilterConfig)) {
            diffList.addAll(pointFilterConfig.diff(otherPlotConfig.pointFilterConfig));
        }

        if(fileFilterConfig == null ^ otherPlotConfig.fileFilterConfig == null) {
            WatchrDiff<FileFilterConfig> diff = new WatchrDiff<>(configPath, DiffCategory.FILE_FILTER_CONFIG);
            diff.setBeforeValue(fileFilterConfig);
            diff.setNowValue(otherPlotConfig.fileFilterConfig);
            diffList.add(diff);
        } else if(fileFilterConfig != null && otherPlotConfig.fileFilterConfig != null && !fileFilterConfig.equals(otherPlotConfig.fileFilterConfig)) {
            diffList.addAll(fileFilterConfig.diff(otherPlotConfig.fileFilterConfig));
        }

        if(!(dataLines.equals(otherPlotConfig.dataLines))) {
            for(int i = 0; i < dataLines.size() && i < otherPlotConfig.dataLines.size(); i++) {
                DataLine dataLine = dataLines.get(i);
                DataLine otherDataLine = otherPlotConfig.dataLines.get(i);
                diffList.addAll(dataLine.diff(otherDataLine));
            }
            // Check for new elements added to list
            int newStart = dataLines.size();
            for(int i = newStart; i < otherPlotConfig.dataLines.size(); i++) {
                FileConfig dummyFileConfig = new FileConfig(otherPlotConfig.getConfigPath(), logger, null);
                DataLine dummyDataLine = new DataLine(dummyFileConfig, otherPlotConfig.getConfigPath());
                DataLine otherDataLine = otherPlotConfig.dataLines.get(i);
                diffList.addAll(dummyDataLine.diff(otherDataLine));
            }
        }

        if(!(plotRules.equals(otherPlotConfig.plotRules))) {
            for(int i = 0; i < plotRules.size() && i < otherPlotConfig.plotRules.size(); i++) {
                RuleConfig rule = plotRules.get(i);
                RuleConfig otherRule = otherPlotConfig.plotRules.get(i);
                diffList.addAll(rule.diff(otherRule));
            }
            // Check for new elements added to list
            int newStart = plotRules.size();
            for(int i = newStart; i < otherPlotConfig.plotRules.size(); i++) {
                RuleConfig dummyRule = new RuleConfig(otherPlotConfig.getConfigPath(), logger);
                RuleConfig otherRule = otherPlotConfig.plotRules.get(i);
                diffList.addAll(dummyRule.diff(otherRule));
            }
        }

        if(getCanvasLayout() != otherPlotConfig.getCanvasLayout()) {
            WatchrDiff<CanvasLayout> diff = new WatchrDiff<>(configPath, DiffCategory.CANVAS_LAYOUT);
            diff.setBeforeValue(getCanvasLayout());
            diff.setNowValue(otherPlotConfig.getCanvasLayout());
            diffList.add(diff);
        }

        if(getCanvasPerRow() != otherPlotConfig.getCanvasPerRow()) {
            WatchrDiff<Integer> diff = new WatchrDiff<>(configPath, DiffCategory.CANVAS_PER_ROW);
            diff.setBeforeValue(getCanvasPerRow());
            diff.setNowValue(otherPlotConfig.getCanvasPerRow());
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
			PlotConfig otherPlotConfig = (PlotConfig) other;

            equals = category.equals(otherPlotConfig.category);
            equals = equals && type == otherPlotConfig.type;

            equals = equals &&
                ((useLegend == null && otherPlotConfig.useLegend == null) ||
                 (useLegend != null && otherPlotConfig.useLegend != null &&
                  useLegend.equals(otherPlotConfig.useLegend)));

            equals = equals && name.equals(otherPlotConfig.name);

            equals = equals &&
                ((autonameConfig == null && otherPlotConfig.autonameConfig == null) ||
                 (autonameConfig != null && otherPlotConfig.autonameConfig != null &&
                  autonameConfig.equals(otherPlotConfig.autonameConfig)));

            equals = equals && dataLines.equals(otherPlotConfig.dataLines);
            equals = equals && plotRules.equals(otherPlotConfig.plotRules);

            equals = equals &&
                ((pointFilterConfig == null && otherPlotConfig.pointFilterConfig == null) ||
                 (pointFilterConfig != null && otherPlotConfig.pointFilterConfig != null &&
                  pointFilterConfig.equals(otherPlotConfig.pointFilterConfig)));

            equals = equals &&
                ((fileFilterConfig == null && otherPlotConfig.fileFilterConfig == null) ||
                 (fileFilterConfig != null && otherPlotConfig.fileFilterConfig != null &&
                  fileFilterConfig.equals(otherPlotConfig.fileFilterConfig)));

            equals = equals && templateName.equals(otherPlotConfig.templateName);
            equals = equals && inheritTemplate.equals(otherPlotConfig.inheritTemplate);
            equals = equals && getCanvasLayout() == otherPlotConfig.getCanvasLayout();
            equals = equals && getCanvasPerRow() == otherPlotConfig.getCanvasPerRow();
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + category.hashCode());
        if(type != null) hash = 31 * (hash + type.hashCode());
        if(useLegend != null) hash = 31 * (hash + useLegend.hashCode());
        hash = 31 * (hash + name.hashCode());
        if(autonameConfig != null) hash = 31 * (hash + autonameConfig.hashCode());
        hash = 31 * (hash + dataLines.hashCode());
        hash = 31 * (hash + plotRules.hashCode());
        if(fileFilterConfig != null) hash = 31 * (hash + fileFilterConfig.hashCode());
        if(pointFilterConfig != null) hash = 31 * (hash + pointFilterConfig.hashCode());
        hash = 31 * (hash + templateName.hashCode());
        hash = 31 * (hash + inheritTemplate.hashCode());
        hash = 31 * (hash + getCanvasLayout().hashCode());
        hash = 31 * (hash + Integer.hashCode(getCanvasPerRow()));
        return hash;
    }
}
