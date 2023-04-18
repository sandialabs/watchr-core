package gov.sandia.watchr.parse.generators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.watchr.config.DataFilterConfig;
import gov.sandia.watchr.config.HierarchicalExtractor;
import gov.sandia.watchr.config.MetadataConfig;
import gov.sandia.watchr.config.derivative.DerivativeLine;
import gov.sandia.watchr.config.rule.RuleConfig;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.generators.line.extractors.strategy.AmbiguityStrategy;

public abstract class AbstractTemplateGenerator {

    protected final ILogger logger;

    protected AbstractTemplateGenerator(ILogger logger) {
        this.logger = logger;
    }

    ///////////////
    // PROTECTED //
    ///////////////
    
    protected void applyChildExtractorOverTemplate(HierarchicalExtractor newExtractor, HierarchicalExtractor childExtractor) {
        if(newExtractor != null && childExtractor != null) {
            if(childExtractor.getAmbiguityStrategy() != null && newExtractor.getAmbiguityStrategy() == null) {
                AmbiguityStrategy newStrategy = new AmbiguityStrategy(childExtractor.getConfigPath());

                newStrategy.setIterateWithOtherExtractor(
                    childExtractor.getAmbiguityStrategy().getIterateWithOtherExtractor());
                newStrategy.setShouldGetFirstMatchOnly(
                    childExtractor.getAmbiguityStrategy().shouldGetFirstMatchOnly());
                newStrategy.setShouldRecurseToChildGraphs(
                    childExtractor.getAmbiguityStrategy().shouldRecurseToChildGraphs());

                newExtractor.setAmbiguityStrategy(newStrategy);
            }
        
            for(Entry<String,String> entry : childExtractor.getProperties().entrySet()) {
                newExtractor.setProperty(entry.getKey(), entry.getValue());
            }
        }
    }

    protected void applyDerivativeLinesOverTemplate(List<DerivativeLine> templateDerivativeLines, List<DerivativeLine> childDerivativeLines) {
        List<DerivativeLine> newDerivativeLines = new ArrayList<>();

        for(DerivativeLine childDerivativeLine : childDerivativeLines) {
            boolean found = false;
            for(DerivativeLine templateDerivativeLine : templateDerivativeLines) {
                if(templateDerivativeLine.getClass() == childDerivativeLine.getClass() &&
                   templateDerivativeLine.getName().equals(childDerivativeLine.getName())) {
                    DerivativeLine appliedDerivativeLine = childDerivativeLine.applyOverTemplate(templateDerivativeLine);
                    newDerivativeLines.add(appliedDerivativeLine);
                    found = true;
                    break;
                }
            }
            if(!found) {
                newDerivativeLines.add(childDerivativeLine);
            }
        }

        templateDerivativeLines.addAll(newDerivativeLines);
    } 
    
    protected void applyRulesOverTemplate(List<RuleConfig> templateRules, List<RuleConfig> childRules) {
        List<RuleConfig> newRules = new ArrayList<>();

        for(RuleConfig childRule : childRules) {
            boolean found = false;
            for(RuleConfig templateRule : templateRules) {
                if(templateRule.getCondition().equals(childRule.getCondition())) {
                    templateRule.setAction(childRule.getAction());
                    found = true;
                    break;
                }
            }
            if(!found) {
                newRules.add(childRule);
            }
        }

        templateRules.addAll(newRules);
    }

    protected void applyMetadataOverTemplate(List<MetadataConfig> templateMetadataConfigs, List<MetadataConfig> childMetadataConfigs) {
        for(int i = 0; i < templateMetadataConfigs.size() && i < childMetadataConfigs.size(); i++) {
            MetadataConfig newMetadataConfig = new MetadataConfig(templateMetadataConfigs.get(i));
            MetadataConfig childMetadataConfig = childMetadataConfigs.get(i);

            if(StringUtils.isNotBlank(childMetadataConfig.getName())) {
                newMetadataConfig.setName(childMetadataConfig.getName());
            }
            applyChildExtractorOverTemplate(newMetadataConfig.getMetadataExtractor(), childMetadataConfig.getMetadataExtractor());
        }
    }

    protected void applyFiltersOverTemplate(DataFilterConfig templateFilterConfig, DataFilterConfig childFilterConfig) {
        if(templateFilterConfig != null && childFilterConfig != null && !childFilterConfig.getFilters().isEmpty()) {
            templateFilterConfig.getFilters().clear();
            templateFilterConfig.getFilters().addAll(childFilterConfig.getFilters());
        }
    }
}
