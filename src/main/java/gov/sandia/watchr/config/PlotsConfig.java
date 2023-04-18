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

import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.log.ILogger;

public class PlotsConfig implements IConfig {
    
    ////////////
    // FIELDS //
    ////////////

    private FileConfig fileConfig;
    private List<PlotConfig> plotConfigs;
    private CategoryConfiguration categoryConfig;
    private DataFilterConfig pointFilterConfig;
    private PlotypusConfig plotypusConfig;

    private final String configPath;
    private final ILogger logger;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PlotsConfig(String configPathPrefix, ILogger logger, IFileReader fileReader) {
        this.configPath = configPathPrefix + "/plotsConfig";

        this.fileConfig = new FileConfig(configPath, logger, fileReader);
        this.plotConfigs = new ArrayList<>();
        this.categoryConfig = new CategoryConfiguration(configPath, logger);
        this.pointFilterConfig = new DataFilterConfig(configPath, logger);
        this.plotypusConfig = new PlotypusConfig(configPath, logger);
        this.logger = logger;
    }

    public PlotsConfig(PlotsConfig copy) {
        this.plotConfigs = new ArrayList<>();
        for(PlotConfig plotConfig : copy.getPlotConfigs()) {
            this.plotConfigs.add(new PlotConfig(plotConfig));
        }
        this.fileConfig = new FileConfig(copy.getFileConfig());
        this.categoryConfig = new CategoryConfiguration(copy.getCategoryConfig());
        this.pointFilterConfig = new DataFilterConfig(copy.getPointFilterConfig());
        this.plotypusConfig = new PlotypusConfig(copy.getPlotypusConfig());
        this.configPath = copy.getConfigPath();
        this.logger = copy.getLogger();
    }

    /////////////
    // GETTERS //
    /////////////

    public FileConfig getFileConfig() {
        return fileConfig;
    }

    public List<PlotConfig> getPlotConfigs() {
        return plotConfigs;
    }    

    public CategoryConfiguration getCategoryConfig() {
        return categoryConfig;
    }

    public DataFilterConfig getPointFilterConfig() {
        return pointFilterConfig;
    }

    public PlotypusConfig getPlotypusConfig() {
        return plotypusConfig;
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

    public void setFileConfig(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
    }

    public void setCategoryConfig(CategoryConfiguration categoryConfig) {
        this.categoryConfig = categoryConfig;
    }

    public void setPointFilterConfig(DataFilterConfig pointFilterConfig) {
        this.pointFilterConfig = pointFilterConfig;
    }

    public void setPlotypusConfig(PlotypusConfig plotypusConfig) {
        this.plotypusConfig = plotypusConfig;
    }

    public void setStartFileAbsPath(String startFileAbsPath) {
        this.fileConfig.setStartFileAbsPath(startFileAbsPath);
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void validate() {
        fileConfig.validate();
        for(PlotConfig plotConfig : plotConfigs) {
            plotConfig.validate();
        }
        categoryConfig.validate();
    }

    @Override
    public List<WatchrDiff<?>> diff(IConfig other) {
        PlotsConfig otherPlotsConfig = (PlotsConfig) other;
        List<WatchrDiff<?>> diffList = new ArrayList<>();

        diffList.addAll(fileConfig.diff(otherPlotsConfig.fileConfig));
        diffList.addAll(categoryConfig.diff(otherPlotsConfig.categoryConfig));
        for(int i = 0; i < plotConfigs.size() && i < otherPlotsConfig.plotConfigs.size(); i++) {
            PlotConfig plotConfig = plotConfigs.get(i);
            PlotConfig otherPlotConfig = otherPlotsConfig.plotConfigs.get(i);
            diffList.addAll(plotConfig.diff(otherPlotConfig));
        }
        // Check for new elements added to list
        int newStart = plotConfigs.size();
        for(int i = newStart; i < otherPlotsConfig.plotConfigs.size(); i++) {
            PlotConfig dummyPlotConfig = new PlotConfig(otherPlotsConfig.getConfigPath() + "/" + Integer.toString(i), logger);
            PlotConfig otherPlotConfig = otherPlotsConfig.plotConfigs.get(i);
            diffList.addAll(dummyPlotConfig.diff(otherPlotConfig));
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
			PlotsConfig otherPlotsConfig = (PlotsConfig) other;

            equals = fileConfig.equals(otherPlotsConfig.fileConfig);
            equals = equals && plotConfigs != otherPlotsConfig.plotConfigs;
            equals = equals && categoryConfig.equals(otherPlotsConfig.categoryConfig);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + categoryConfig.hashCode());
        hash = 31 * (hash + fileConfig.hashCode());
        for(PlotConfig plotConfig : plotConfigs) {
            hash = 31 * (hash + plotConfig.hashCode());
        }
        return hash;
    }    
}
