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

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.config.derivative.DerivativeLine;
import gov.sandia.watchr.config.derivative.DerivativeLineFactory;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.generators.line.extractors.strategy.ExtractionStrategyFactory;
import gov.sandia.watchr.parse.generators.line.extractors.strategy.ExtractionStrategyType;
import gov.sandia.watchr.util.RGB;
import gov.sandia.watchr.util.RGBA;
import gov.sandia.watchr.util.RgbUtil;

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
    private NameConfig autonameConfig;

    private String templateName = "";
    private String inheritTemplate = "";
    private boolean templateApplied = true;

    private final String configPath;
    private final FileConfig fileConfig;
    private DataFilterConfig pointFilterConfig;
    private final ILogger logger;
    private final IFileReader fileReader;


    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public DataLine(FileConfig fileConfig, String configPathPrefix) {
        metadataList = new ArrayList<>();

        this.fileConfig = fileConfig;
        this.logger = fileConfig.getLogger();
        this.fileReader = fileConfig.getFileReader();

        this.extractorType = ExtractionStrategyFactory.getInstance().getTypeFromExtension(fileConfig.getFileExtension());
        this.xExtractor = new HierarchicalExtractor(fileConfig, configPathPrefix, "x");
        this.yExtractor = new HierarchicalExtractor(fileConfig, configPathPrefix, "y");

        this.derivativeLines = new ArrayList<>();
        this.configPath = configPathPrefix + "/dataLine";
    }

    public DataLine(DataLine copy) {
        this.configPath = copy.getConfigPath();
        this.logger = copy.logger;
        this.fileConfig = copy.fileConfig;
        this.fileReader = copy.fileReader;

        metadataList = new ArrayList<>();
        for(MetadataConfig metadata : copy.getMetadata()) {
            metadataList.add(new MetadataConfig(metadata));
        }

        this.extractorType = copy.getExtractionStrategyType();
        this.xExtractor = new HierarchicalExtractor(copy.getXExtractor());
        this.yExtractor = new HierarchicalExtractor(copy.getYExtractor());

        derivativeLines = new ArrayList<>();
        for(DerivativeLine derivativeLine : copy.getDerivativeLines()) {
            DerivativeLine newDerivativeLine = DerivativeLineFactory.getInstance().create(configPath, derivativeLine, true, logger);
            if(newDerivativeLine != null) {
                derivativeLines.add(newDerivativeLine);
            } else {
                logger.logWarning("For some reason, we tried to add a null derivative line!");
            }
        }

        if(copy.getColor() != null) {
            color = RgbUtil.copyColor(copy.getColor());
        }

        this.name = copy.getName();
        if(copy.getNameConfig() != null) {
            this.autonameConfig = new NameConfig(copy.getNameConfig());
        }

        if(copy.getPointFilterConfig() != null) {
            pointFilterConfig = new DataFilterConfig(copy.getPointFilterConfig());
        }

        this.templateName = copy.getTemplateName();
        this.inheritTemplate = copy.getInheritTemplate();
        this.templateApplied = StringUtils.isBlank(inheritTemplate);
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

    public NameConfig getNameConfig() {
        return autonameConfig;
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getInheritTemplate() {
        return inheritTemplate;
    }    

    public boolean isTemplateApplied() {
        return templateApplied;
    }

    @Override
    public String getConfigPath() {
        return configPath;
    }

    @Override
    public ILogger getLogger() {
        return logger;
    }

    public DataFilterConfig getPointFilterConfig() {
        return pointFilterConfig;
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

    public void setColor(int r, int g, int b, double a) {
        this.color = new RGBA(r,g,b,a);
    }

    public void setColor(RGB color) {
        this.color = color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameConfig(NameConfig nameConfig) {
        this.autonameConfig = nameConfig;
    }

    public void setInheritTemplate(String inheritTemplate) {
        this.inheritTemplate = inheritTemplate;
        this.templateApplied = StringUtils.isBlank(inheritTemplate);
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setTemplateApplied(boolean templateApplied) {
        this.templateApplied = templateApplied;
    }

    public void setPointFilterConfig(DataFilterConfig filterConfig) {
        this.pointFilterConfig = filterConfig;
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
        if(autonameConfig != null) {
            autonameConfig.validate();
        }
        if(pointFilterConfig != null) {
            pointFilterConfig.validate();
        }
    }

    @Override
    public List<WatchrDiff<?>> diff(IConfig other) {
        DataLine otherDataLine = (DataLine) other;
        List<WatchrDiff<?>> diffList = new ArrayList<>();

        if(xExtractor != null && !xExtractor.equals(otherDataLine.xExtractor)) {
            diffList.addAll(xExtractor.diff(otherDataLine.xExtractor));
        }
        if(yExtractor != null && !yExtractor.equals(otherDataLine.yExtractor)) {
            diffList.addAll(yExtractor.diff(otherDataLine.yExtractor));
        }

        if(name != null && !name.equals(otherDataLine.name)) {
            WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.NAME);
            diff.setBeforeValue(name);
            diff.setNowValue(otherDataLine.name);
            diffList.add(diff);
        }
        if(!templateName.equals(otherDataLine.templateName)) {
            WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.TEMPLATE_NAME);
            diff.setBeforeValue(templateName);
            diff.setNowValue(otherDataLine.templateName);
            diffList.add(diff);
        }
        if(!inheritTemplate.equals(otherDataLine.inheritTemplate)) {
            WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.INHERIT_TEMPLATE);
            diff.setBeforeValue(inheritTemplate);
            diff.setNowValue(otherDataLine.inheritTemplate);
            diffList.add(diff);
        }

        if(autonameConfig == null ^ otherDataLine.autonameConfig == null) {
            WatchrDiff<NameConfig> diff = new WatchrDiff<>(configPath, DiffCategory.NAME_CONFIG);
            diff.setBeforeValue(autonameConfig);
            diff.setNowValue(otherDataLine.autonameConfig);
            diffList.add(diff);
        } else if(autonameConfig != null && otherDataLine.autonameConfig != null && !autonameConfig.equals(otherDataLine.autonameConfig)) {
            diffList.addAll(autonameConfig.diff(otherDataLine.autonameConfig));
        }        

        if((color == null ^ otherDataLine.color == null) ||
           (color != null && otherDataLine.color != null && !color.equals(otherDataLine.color))) {
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
                MetadataConfig dummyMetadata =
                    new MetadataConfig(
                        new FileConfig(
                            "", logger, fileConfig.getFileReader()),
                            otherDataLine.getConfigPath()
                        );
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
                DerivativeLine otherDerivativeLine = otherDataLine.derivativeLines.get(i);
                DerivativeLine derivativeLine = DerivativeLineFactory.getInstance().create(configPath, otherDerivativeLine, false, logger);
                if(derivativeLine != null) {
                    diffList.addAll(derivativeLine.diff(otherDerivativeLine));
                }
            }
        }

        if(pointFilterConfig == null ^ otherDataLine.pointFilterConfig == null) {
            WatchrDiff<DataFilterConfig> diff = new WatchrDiff<>(configPath, DiffCategory.POINT_FILTER_CONFIG);
            diff.setBeforeValue(pointFilterConfig);
            diff.setNowValue(otherDataLine.pointFilterConfig);
            diffList.add(diff);
        } else if(pointFilterConfig != null && otherDataLine.pointFilterConfig != null && !pointFilterConfig.equals(otherDataLine.pointFilterConfig)) {
            diffList.addAll(pointFilterConfig.diff(otherDataLine.pointFilterConfig));
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
            equals = equals && templateName.equals(otherDataLine.templateName);
            equals = equals && inheritTemplate.equals(otherDataLine.inheritTemplate);
            equals = equals && metadataList.equals(otherDataLine.metadataList);
            equals = equals && derivativeLines.equals(otherDataLine.derivativeLines);

            if(color != null ^ otherDataLine.color != null) {
                equals = equals && color == null && otherDataLine.color == null;
            } else if(color != null && otherDataLine.color != null) {
                equals = equals && color.equals(otherDataLine.color);
            }

            equals = equals &&
                ((autonameConfig == null && otherDataLine.autonameConfig == null) ||
                 (autonameConfig != null && otherDataLine.autonameConfig != null &&
                  autonameConfig.equals(otherDataLine.autonameConfig)));            

            equals = equals &&
                ((pointFilterConfig == null && otherDataLine.pointFilterConfig == null) ||
                 (pointFilterConfig != null && otherDataLine.pointFilterConfig != null &&
                  pointFilterConfig.equals(otherDataLine.pointFilterConfig)));
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * (hash + name.hashCode());
        if(autonameConfig != null) hash = 31 * (hash + autonameConfig.hashCode());
        hash = 31 * (hash + templateName.hashCode());
        hash = 31 * (hash + inheritTemplate.hashCode());
        hash = 31 * (hash + xExtractor.hashCode());
        hash = 31 * (hash + yExtractor.hashCode());
        hash = 31 * (hash + metadataList.hashCode());
        hash = 31 * (hash + derivativeLines.hashCode());
        if(pointFilterConfig != null) hash = 31 * (hash + pointFilterConfig.hashCode());
        if(color != null) hash = 31 * (hash + color.hashCode());
        return hash;
    }
}
