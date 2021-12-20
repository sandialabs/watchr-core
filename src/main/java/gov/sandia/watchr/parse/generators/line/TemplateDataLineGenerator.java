package gov.sandia.watchr.parse.generators.line;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.config.DataLine;
import gov.sandia.watchr.config.NameConfig;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.generators.AbstractTemplateGenerator;

public class TemplateDataLineGenerator extends AbstractTemplateGenerator {
    
    ////////////
    // FIELDS //
    ////////////

    private final List<DataLine> allDataLines;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public TemplateDataLineGenerator(List<DataLine> allDataLines, ILogger logger) {
        super(logger);
        this.allDataLines = new ArrayList<>();
        this.allDataLines.addAll(allDataLines);
    }

    ////////////
    // PUBLIC //
    ////////////
    
    public DataLine handleDataLineGenerationForTemplate(DataLine childDataLine) {
        DataLine templateDataLine = getTemplateDataLine(childDataLine.getInheritTemplate());
        if(templateDataLine != null) {
            return applyChildOverTemplate(templateDataLine, childDataLine);
        } else {
            logger.logError("Data line depends on template " + childDataLine.getInheritTemplate() + ", but this template does not exist in the configuration.");
        }
        return null;
    }

    /////////////
    // PRIVATE //
    /////////////

    private DataLine getTemplateDataLine(String templateName) {
        if(StringUtils.isNotBlank(templateName)) {
            for(DataLine dataLine : allDataLines) {
                if(dataLine.getTemplateName().equals(templateName)) {
                    return dataLine;
                }
            }
        }
        return null;
    }    


    private DataLine applyChildOverTemplate(DataLine templateDataLine, DataLine childDataLine) {
        DataLine newDataLine = new DataLine(templateDataLine);
        if(StringUtils.isNotBlank(childDataLine.getName())) {
            newDataLine.setName(childDataLine.getName());
        }
        if(childDataLine.getNameConfig() != null) {
            newDataLine.setNameConfig(new NameConfig(childDataLine.getNameConfig()));
        }
        if(childDataLine.getColor() != null) {
            newDataLine.setColor(childDataLine.getColor());
        }

        applyChildExtractorOverTemplate(newDataLine.getXExtractor(), childDataLine.getXExtractor());
        applyChildExtractorOverTemplate(newDataLine.getYExtractor(), childDataLine.getYExtractor());
        applyDerivativeLinesOverTemplate(newDataLine.getDerivativeLines(), childDataLine.getDerivativeLines());
        applyMetadataOverTemplate(newDataLine.getMetadata(), childDataLine.getMetadata());

        return newDataLine;
    }
}
