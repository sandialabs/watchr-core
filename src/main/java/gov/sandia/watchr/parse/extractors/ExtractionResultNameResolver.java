package gov.sandia.watchr.parse.extractors;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.config.HierarchicalExtractor;
import gov.sandia.watchr.config.NameConfig;
import gov.sandia.watchr.config.reader.Shorthand;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.util.StringUtil;

public class ExtractionResultNameResolver {
    
    ////////////
    // FIELDS //
    ////////////

    private final NameConfig nameConfig;
    private final ILogger logger;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public ExtractionResultNameResolver(NameConfig nameConfig, ILogger logger) {
        this.nameConfig = nameConfig;
        this.logger = logger;
    }

    ////////////
    // PUBLIC //
    ////////////

    public String getName(ExtractionResult xResult, ExtractionResult yResult, String prefix) {
        return getName(xResult, yResult, prefix, -1);
    }

    public String getName(ExtractionResult xResult, ExtractionResult yResult, String prefix, int resultIndex) {
        String targetName = "";

        if((nameConfig != null && !nameConfig.isBlank()) && xResult != null && yResult != null) {
            targetName = determineTargetName(nameConfig, xResult, yResult, resultIndex);
        }
        if(StringUtils.isBlank(targetName)) {
            logger.logWarning("Could not find an existing plot using your specified naming configuration. A target plot name consisting of hash codes will be used, which is a fallback option and may not be what you want.");
            if(xResult == null && yResult == null) {
                throw new IllegalStateException("xResult and yResult are both null!");
            } else if(xResult != null && yResult == null) {
                targetName = prefix + xResult.hashCode();
            } else if(xResult == null) {
                targetName = prefix + yResult.hashCode();
            } else {
                targetName = prefix + xResult.hashCode() + "_" + yResult.hashCode();
            }
            
        }
        return targetName;
    }

    public String getChildName(String prefix, ExtractionResult childResult, int extractionResultIndex) {
        String childPlotName = "";
        
        if(!nameConfig.isBlank()) {
            childPlotName = determineTargetName(nameConfig, childResult, childResult, extractionResultIndex);
        } else if(StringUtils.isNotBlank(prefix)) {
            childPlotName = prefix + "_plot_" + childResult.hashCode();
        }

        return childPlotName;
    }

    public String determineTargetName(
            NameConfig nameConfig, ExtractionResult xResult, ExtractionResult yResult, int resultIndex) {

        logger.logDebug("determineTargetName()");
        String targetName = "";

        String nameUseProperty = nameConfig.getNameUseProperty();
        HierarchicalExtractor nameUseExtractor = nameConfig.getNameUseExtractor();

        if(StringUtils.isNotBlank(nameUseProperty)) {
            logger.logDebug("Use name property");
            targetName = determineTargetNameUsingProperties(nameUseProperty, xResult, yResult);
            logger.logDebug("Target name is " + targetName);
        } else if(!nameUseExtractor.getProperties().isEmpty() && xResult != null) {
            logger.logDebug("Use name extractor");
            try {
                targetName = determineTargetNameUsingExtractor(xResult.getSourceFile(), nameUseExtractor, resultIndex);
            } catch(WatchrParseException e) {
                nameConfig.getLogger().logError("Could not extract plot name from data file.", e);
            }
            logger.logDebug("Target name is " + targetName);
        }

        if(StringUtils.isNotBlank(nameConfig.getNameFormatRemovePrefix())) {
            String regex = StringUtil.convertToRegex(nameConfig.getNameFormatRemovePrefix());
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(targetName);
            if (matcher.find()) {
                int endIndex = matcher.end();
                targetName = targetName.substring(endIndex);
            }
        }

        logger.logDebug("Final targetName is " + targetName);
        logger.logDebug("DONE: determineTargetName()");
        return targetName;
    }

    /////////////
    // PRIVATE //
    /////////////    

    private String determineTargetNameUsingProperties(String nameUse, ExtractionResult xResult, ExtractionResult yResult) {
        Shorthand shorthand = new Shorthand(nameUse);
        if (shorthand.getAxis().equals("x")) {
            if (shorthand.getGroupingField().equals("key")) {
                return xResult.getValue();
            } else if (shorthand.getGroupingField().equals("path")) {
                return xResult.getPath();
            }
        } else if (shorthand.getAxis().equals("y")) {
            if (shorthand.getGroupingField().equals("key")) {
                return yResult.getValue();
            } else if (shorthand.getGroupingField().equals("path")) {
                return yResult.getPath();
            }
        } else {
            // Error state?
        }
        return "";
    }

    private String determineTargetNameUsingExtractor(
            String reportAbsPath, HierarchicalExtractor nameUseExtractor, int resultIndex) throws WatchrParseException {

        boolean iterate = StringUtils.isNotBlank(nameUseExtractor.getAmbiguityStrategy().getIterateWithOtherExtractor());

        List<ExtractionResult> results = nameUseExtractor.extract(reportAbsPath);
        if(resultIndex == -1 && !results.isEmpty()) {
            return results.get(0).getValue();
        } else if(iterate && results.size() > resultIndex) {
            ExtractionResult result = results.get(resultIndex);
            return result.getValue();
        }
        return "";
    }    
}
