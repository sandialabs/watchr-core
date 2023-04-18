package gov.sandia.watchr.db;

import gov.sandia.watchr.config.NameConfig;
import gov.sandia.watchr.parse.generators.line.extractors.ExtractionResult;

public class NewPlotDatabaseSearchCriteria extends PlotDatabaseSearchCriteria {

    private NameConfig nameConfig;
    private ExtractionResult xResult;
    private ExtractionResult yResult;
    private int resultIndex;

    public NewPlotDatabaseSearchCriteria(String name, String category) {
        super(name, category);
    }

    public ExtractionResult getXResult() {
        return xResult;
    }
    
    public void setXResult(ExtractionResult xResult) {
        this.xResult = xResult;
    }    

    public NameConfig getNameConfig() {
        return nameConfig;
    }
    
    public void setNameConfig(NameConfig nameConfig) {
        this.nameConfig = nameConfig;
    }

    public ExtractionResult getYResult() {
        return yResult;
    }
    
    public void setYResult(ExtractionResult yResult) {
        this.yResult = yResult;
    }    

    public int getResultIndex() {
        return resultIndex;
    }
    
    public void setResultIndex(int resultIndex) {
        this.resultIndex = resultIndex;
    }
}
