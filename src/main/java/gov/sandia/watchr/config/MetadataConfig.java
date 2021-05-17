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

import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;

public class MetadataConfig implements IConfig {

    ////////////
    // FIELDS //
    ////////////

    // Non-configurable properties
    private String link = "";

    // Configurable properties
    private String name = "";
    private HierarchicalExtractor metadataExtractor;
    private final FileConfig fileConfig;

    private final String configPath;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public MetadataConfig(FileConfig fileConfig,  String pathPrefix) {
        this.metadataExtractor = new HierarchicalExtractor(fileConfig, pathPrefix, "metadata");
        this.fileConfig = fileConfig;
        this.configPath = pathPrefix + "/metadataConfig";
    }

    public MetadataConfig(MetadataConfig copy) {
        this.name = copy.getName();
        this.link = copy.getLink();
        this.fileConfig = new FileConfig(copy.getFileConfig());
        this.metadataExtractor = new HierarchicalExtractor(copy.getMetadataExtractor());
        this.configPath = copy.getConfigPath();
    }

    /////////////
    // GETTERS //
    /////////////

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public HierarchicalExtractor getMetadataExtractor() {
        return metadataExtractor;
    }

    public FileConfig getFileConfig() {
        return fileConfig;
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

    public void setLink(String link) {
        this.link = link;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void validate() {
        // Do nothing
    }

    @Override
    public List<WatchrDiff<?>> diff(IConfig other) {
        MetadataConfig otherMetadataConfig = (MetadataConfig) other;
        List<WatchrDiff<?>> diffList = new ArrayList<>();

        if(!(name.equals(otherMetadataConfig.name))) {
            WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.NAME);
            diff.setBeforeValue(name);
            diff.setNowValue(otherMetadataConfig.name);
            diffList.add(diff);
        }
        diffList.addAll(fileConfig.diff(otherMetadataConfig.getFileConfig()));
        diffList.addAll(metadataExtractor.diff(otherMetadataConfig.getMetadataExtractor()));
        
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
			MetadataConfig otherMetadataConfig = (MetadataConfig) other;

            equals = name.equals(otherMetadataConfig.name);
            equals = equals && link.equals(otherMetadataConfig.link);
            equals = equals && metadataExtractor.equals(otherMetadataConfig.metadataExtractor);
            equals = equals && fileConfig.equals(otherMetadataConfig.fileConfig);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + name.hashCode());
        hash = 31 * (hash + link.hashCode());
        hash = 31 * (hash + metadataExtractor.hashCode());
        hash = 31 * (hash + fileConfig.hashCode());
        return hash;
    }   
}
