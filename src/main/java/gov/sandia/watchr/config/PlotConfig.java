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

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.log.ILogger;

public class PlotConfig implements IConfig {

    ////////////
    // FIELDS //
    ////////////

    private String category = "";
    private Boolean useLegend;

    private String name = "";
    private NameConfig autonameConfig;

    private String templateName = "";
    private String inheritTemplate = "";

    private List<DataLine> dataLines;
    private List<RuleConfig> plotRules;

    private FileFilterConfig fileFilterConfig;
    private FilterConfig pointFilterConfig;

    private final String configPath;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PlotConfig(String configPathPrefix) {
        this.configPath = configPathPrefix + "/plotConfig";

        this.dataLines = new ArrayList<>();
        this.plotRules = new ArrayList<>();
    }

    public PlotConfig(PlotConfig copy) {
        this.category = copy.getCategory();
        this.useLegend = copy.shouldUseLegend();
        this.name = copy.getName();
        if(copy.getNameConfig() != null) {
            this.autonameConfig = new NameConfig(copy.getNameConfig());
        }

        this.templateName = copy.getTemplateName();
        this.inheritTemplate = copy.getInheritTemplate();

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

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void validate() {
        if(autonameConfig != null) {
            autonameConfig.validate();
        }
        for(DataLine dataLine : dataLines) {
            dataLine.validate();
        }
        for(RuleConfig plotRule : plotRules) {
            plotRule.validate();
        }
        if(StringUtils.isBlank(name) && (autonameConfig == null || autonameConfig.isBlank())) {
            ILogger logger = WatchrCoreApp.getInstance().getLogger();
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "All plots must have a \"name\" or \"autoname\" configuration specified."));
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
                DataLine dummyDataLine = new DataLine(null, otherPlotConfig.getConfigPath());
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
                RuleConfig dummyRule = new RuleConfig(otherPlotConfig.getConfigPath());
                RuleConfig otherRule = otherPlotConfig.plotRules.get(i);
                diffList.addAll(dummyRule.diff(otherRule));
            }
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
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + category.hashCode());
        if(useLegend != null) hash = 31 * (hash + useLegend.hashCode());
        hash = 31 * (hash + name.hashCode());
        if(autonameConfig != null) hash = 31 * (hash + autonameConfig.hashCode());
        hash = 31 * (hash + dataLines.hashCode());
        hash = 31 * (hash + plotRules.hashCode());
        if(fileFilterConfig != null) hash = 31 * (hash + fileFilterConfig.hashCode());
        if(pointFilterConfig != null) hash = 31 * (hash + pointFilterConfig.hashCode());
        hash = 31 * (hash + templateName.hashCode());
        hash = 31 * (hash + inheritTemplate.hashCode());
        return hash;
    }
}
