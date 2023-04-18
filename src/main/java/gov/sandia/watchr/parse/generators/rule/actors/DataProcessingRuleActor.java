package gov.sandia.watchr.parse.generators.rule.actors;

public interface DataProcessingRuleActor {
    
    public Object getDataToProcess();

    public void setDataToProcess(Object data);
}
