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
import gov.sandia.watchr.parse.extractors.strategy.ExtractionStrategyFactory;
import gov.sandia.watchr.parse.extractors.strategy.ExtractionStrategyType;
import gov.sandia.watchr.util.RGB;

public class DataLine implements IConfig {
    
    ////////////
    // FIELDS //
    ////////////

    private final ExtractionStrategyType extractorType;
    private HierarchicalExtractor xExtractor;
    private HierarchicalExtractor yExtractor;

    private List<MetadataConfig> metadataList;
    private List<DerivativeLine> derivativeLines;
    private RGB color;

    private String name = "";
    private final String configPath;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public DataLine(FileConfig fileConfig, String configPathPrefix) {
        metadataList = new ArrayList<>();

        if(fileConfig != null) {
            this.extractorType = ExtractionStrategyFactory.getInstance().getTypeFromExtension(fileConfig.getFileExtension());
            this.xExtractor = new HierarchicalExtractor(fileConfig, configPathPrefix, "x");
            this.yExtractor = new HierarchicalExtractor(fileConfig, configPathPrefix, "y");
        } else {
            this.extractorType = null;
        }

        this.derivativeLines = new ArrayList<>();
        this.configPath = configPathPrefix + "/dataLine";
    }

    public DataLine(DataLine copy) {
        metadataList = new ArrayList<>();
        for(MetadataConfig metadata : copy.getMetadata()) {
            metadataList.add(new MetadataConfig(metadata));
        }

        this.extractorType = copy.getExtractionStrategyType();
        this.xExtractor = new HierarchicalExtractor(copy.getXExtractor());
        this.yExtractor = new HierarchicalExtractor(copy.getYExtractor());

        derivativeLines = new ArrayList<>();
        for(DerivativeLine derivativeLine : copy.getDerivativeLines()) {
            derivativeLines.add(new DerivativeLine(derivativeLine));
        }

        if(copy.getColor() != null) {
            color = new RGB(copy.getColor());
        }

        this.name = copy.getName();
        this.configPath = copy.getConfigPath();
    }

    /////////////
    // GETTERS //
    /////////////

    public ExtractionStrategyType getExtractionStrategyType() {
        return extractorType;
    }

    public HierarchicalExtractor getXExtractor() {
        return xExtractor;
    }    

    public HierarchicalExtractor getYExtractor() {
        return yExtractor;
    }    

    public List<MetadataConfig> getMetadata() {
        return metadataList;
    }

    public List<DerivativeLine> getDerivativeLines() {
        return derivativeLines;
    }

    public RGB getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getConfigPath() {
        return configPath;
    }

    /////////////
    // SETTERS //
    /////////////

    public void setXExtractor(HierarchicalExtractor xExtractor) {
        this.xExtractor = xExtractor;
    }

    public void setYExtractor(HierarchicalExtractor yExtractor) {
        this.yExtractor = yExtractor;
    }

    public void setDerivativeLines(List<DerivativeLine> derivativeLines) {
        this.derivativeLines = derivativeLines;
    }

    public void setColor(int r, int g, int b) {
        this.color = new RGB(r,g,b);
    }

    public void setColor(RGB color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void validate() {
        for(DerivativeLine derivativeLine : derivativeLines) {
            derivativeLine.validate();
        }
        for(MetadataConfig metadata : metadataList) {
            metadata.validate();
        }
    }

    @Override
    public List<WatchrDiff<?>> diff(IConfig other) {
        DataLine otherDataLine = (DataLine) other;
        List<WatchrDiff<?>> diffList = new ArrayList<>();

        if(getXExtractor() != null) {
            diffList.addAll(getXExtractor().diff(otherDataLine.getXExtractor()));
        }
        if(getYExtractor() != null) {
            diffList.addAll(getYExtractor().diff(otherDataLine.getYExtractor()));
        }

        if(name != null && !name.equals(otherDataLine.name)) {
            WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.NAME);
            diff.setBeforeValue(name);
            diff.setNowValue(otherDataLine.name);
            diffList.add(diff);
        }

        if((color != null ^ otherDataLine.color != null) || (color != null && otherDataLine.color != null)) {
            WatchrDiff<RGB> diff = new WatchrDiff<>(configPath, DiffCategory.LINE_COLOR);
            diff.setBeforeValue(color);
            diff.setNowValue(otherDataLine.color);
            diffList.add(diff);
        }

        if(metadataList != null && otherDataLine.metadataList != null && !(metadataList.equals(otherDataLine.metadataList))) {
            for(int i = 0; i < metadataList.size() && i < otherDataLine.metadataList.size(); i++) {
                MetadataConfig metadataConfig = metadataList.get(i);
                MetadataConfig otherMetadataConfig = otherDataLine.metadataList.get(i);
                diffList.addAll(metadataConfig.diff(otherMetadataConfig));
            }
            // Check for new elements added to list
            int newStart = metadataList.size();
            for(int i = newStart; i < otherDataLine.metadataList.size(); i++) {
                MetadataConfig dummyMetadata = new MetadataConfig(new FileConfig(""), otherDataLine.getConfigPath());
                MetadataConfig otherMetadata = otherDataLine.metadataList.get(i);
                diffList.addAll(dummyMetadata.diff(otherMetadata));
            }
        }
        if(derivativeLines != null && otherDataLine.derivativeLines != null && !(derivativeLines.equals(otherDataLine.derivativeLines))) {
            for(int i = 0; i < derivativeLines.size() && i < otherDataLine.derivativeLines.size(); i++) {
                DerivativeLine derivativeLine = derivativeLines.get(i);
                DerivativeLine otherDerivativeLine = otherDataLine.derivativeLines.get(i);
                diffList.addAll(derivativeLine.diff(otherDerivativeLine));
            }
            // Check for new elements added to list
            int newStart = derivativeLines.size();
            for(int i = newStart; i < otherDataLine.derivativeLines.size(); i++) {
                DerivativeLine derivativeLine = new DerivativeLine(otherDataLine.getConfigPath());
                DerivativeLine otherDerivativeLine = otherDataLine.derivativeLines.get(i);
                diffList.addAll(derivativeLine.diff(otherDerivativeLine));
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
			DataLine otherDataLine = (DataLine) other;
            
            equals = xExtractor.equals(otherDataLine.xExtractor);
            equals = equals && yExtractor.equals(otherDataLine.yExtractor);
            equals = equals && name.equals(otherDataLine.name);
            equals = equals && metadataList.equals(otherDataLine.metadataList);
            equals = equals && derivativeLines.equals(otherDataLine.derivativeLines);

            if(color != null ^ otherDataLine.color != null) {
                equals = equals && color == null && otherDataLine.color == null;
            } else if(color != null && otherDataLine.color != null) {
                equals = equals && color.equals(otherDataLine.color);
            }
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + name.hashCode());
        hash = 31 * (hash + xExtractor.hashCode());
        hash = 31 * (hash + yExtractor.hashCode());
        hash = 31 * (hash + metadataList.hashCode());
        hash = 31 * (hash + derivativeLines.hashCode());
        if(color != null) {
            hash = 31 * (hash + color.hashCode());
        }
        return hash;
    }
}
