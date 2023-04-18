package gov.sandia.watchr.config.element;

import java.util.List;
import java.util.Map;

public interface ConfigElement {

    public List<Object> getValueAsList();

    public Map<String, Object> getValueAsMap();

    public ConfigConverter getConverter();
}
