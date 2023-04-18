package gov.sandia.watchr.parse.generators.rule.properties;

public interface RuleProperty<E> {
    
    public Object process(E objectToProcess, String propertyValue);
}
