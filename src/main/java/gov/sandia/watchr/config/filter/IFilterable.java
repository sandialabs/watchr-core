package gov.sandia.watchr.config.filter;

import java.util.Collection;

public interface IFilterable {
    
    public void setFilterValues(Collection<DataFilter> filters);

    public void addFilterValues(Collection<DataFilter> filters);
}
