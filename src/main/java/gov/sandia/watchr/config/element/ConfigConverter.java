package gov.sandia.watchr.config.element;

public interface ConfigConverter {
    
    public String asString(Object value);

    public Integer asInt(Object value);

    public Double asDouble(Object value);

    public Boolean asBoolean(Object value);

    public ConfigElement asChild(Object value);
}
