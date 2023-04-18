package gov.sandia.watchr.db;

public class PlotDatabaseSearchCriteria {

    protected String name;
    protected String category;

    public PlotDatabaseSearchCriteria(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
}
