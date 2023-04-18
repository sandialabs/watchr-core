package gov.sandia.watchr.parse.generators.rule.actors;

import java.util.Map;

import gov.sandia.watchr.db.DatabaseMetadata;
import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.generators.rule.RuleAction;

public class RuleDatabaseFailActor extends RuleDatabaseActor {

    public RuleDatabaseFailActor(IDatabase db, ILogger logger, Map<String, String> properties) {
        super(db, logger, properties);
    }

    @Override
    public void act() {
        logger.logInfo("Acting upon rule " + RuleAction.FAIL_DATABASE.toString() + "...");
        DatabaseMetadata metadata = db.getMetadata();
        int newHealth = Math.max(0, metadata.getHealth() - 1);
        metadata.setHealth(newHealth);
    }

    @Override
    public void undo() {
        DatabaseMetadata metadata = db.getMetadata();
        metadata.setHealth(DatabaseMetadata.HEALTH_MAX);
    }
}
