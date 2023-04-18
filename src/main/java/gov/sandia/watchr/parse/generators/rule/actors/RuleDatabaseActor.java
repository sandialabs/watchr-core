package gov.sandia.watchr.parse.generators.rule.actors;

import java.util.HashMap;
import java.util.Map;

import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.generators.rule.RuleActor;

public abstract class RuleDatabaseActor implements RuleActor {
    
    ////////////
    // FIELDS //
    ////////////

    protected final IDatabase db;
    protected final ILogger logger;
    protected final Map<String, String> properties;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    protected RuleDatabaseActor(IDatabase db, ILogger logger, Map<String, String> properties) {
        this.db = db;
        this.logger = logger;
        this.properties = new HashMap<>();
        this.properties.putAll(properties);
    }

    /////////////
    // GETTERS //
    /////////////

    @Override
    public Map<String, String> getProperties() {
        return properties;
    }

    public IDatabase getDatabase() {
        return db;
    }
}
