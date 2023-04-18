package gov.sandia.watchr.parse.generators.line.extractors;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.config.HierarchicalExtractor;
import gov.sandia.watchr.config.NameConfig;
import gov.sandia.watchr.config.reader.Shorthand;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.util.OsUtil;
import gov.sandia.watchr.util.StringUtil;

public class ExtractionResultNameResolver {
    
    ////////////
    // FIELDS //
    ////////////

    private static final String CLASSNAME = ExtractionResultNameResolver.class.getSimpleName();

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

    public String getName(ExtractionResult xResult, ExtractionResult yResult, int resultIndex) {
        String targetName = "";

        if((nameConfig != null && !nameConfig.isBlank()) && xResult != null && yResult != null) {
            targetName = determineTargetName(nameConfig, xResult, yResult, resultIndex);
        }
        if(StringUtils.isBlank(targetName)) {
            StringBuilder errorSb = new StringBuilder();
            errorSb.append("Could not find an existing plot using your specified naming configuration.");
            if(xResult == null && yResult == null) {
                errorSb.append(OsUtil.getOSLineBreak());
                errorSb.append("Cause: xResult and yResult are both null!");
            } else if(xResult != null && yResult == null) {
                errorSb.append(OsUtil.getOSLineBreak());
                errorSb.append("Cause: yResult is null. xResult was \"").append(xResult.toString()).append("\"");
            } else if(xResult == null) {
                errorSb.append(OsUtil.getOSLineBreak());
                errorSb.append("Cause: xResult is null. yResult was \"").append(yResult.toString()).append("\"");
            } else {
                errorSb.append(OsUtil.getOSLineBreak());
                errorSb.append("xResult was \"").append(xResult.toString());
                errorSb.append("\" and yResult was \"").append(yResult.toString());
                errorSb.append("\". Review your configuration file if these are not the results you want.");
            }
            logger.logError(errorSb.toString());
            return null;
        } else {
            return targetName;
        }
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

        logger.logDebug("determineTargetName()", CLASSNAME);
        String targetName = "";

        String nameUseProperty = nameConfig.getNameUseProperty();
        HierarchicalExtractor nameUseExtractor = nameConfig.getNameUseExtractor();

        if(StringUtils.isNotBlank(nameUseProperty)) {
            logger.logDebug("Use name property", CLASSNAME);
            targetName = determineTargetNameUsingProperties(nameUseProperty, xResult, yResult);
            logger.logDebug("Target name is " + targetName, CLASSNAME);
        } else if(!nameUseExtractor.getProperties().isEmpty() && xResult != null) {
            logger.logDebug("Use name extractor", CLASSNAME);
            try {
                targetName = determineTargetNameUsingExtractor(xResult.getSourceFile(), nameUseExtractor, resultIndex);
            } catch(WatchrParseException e) {
                nameConfig.getLogger().logError("Could not extract plot name from data file.", e);
            }
            logger.logDebug("Target name is " + targetName, CLASSNAME);
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

        logger.logDebug("Final targetName is " + targetName, CLASSNAME);
        logger.logDebug("DONE: determineTargetName()", CLASSNAME);
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
