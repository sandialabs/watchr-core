package gov.sandia.watchr.parse.generators.rule.actors;

import java.util.Map;

import gov.sandia.watchr.db.IDatabase;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.generators.rule.RuleAction;

public class RuleDatabaseDeleteEverythingActor extends RuleDatabaseActor {

    public RuleDatabaseDeleteEverythingActor(IDatabase db, ILogger logger, Map<String, String> properties) {
        super(db, logger, properties);
    }

    @Override
    public void act() {
        logger.logInfo("Acting upon rule " + RuleAction.DELETE_EVERYTHING.toString() + "...");
        db.deleteAll();
    }

    @Override
    public void undo() {
        // Not implemented - You can't undo a delete, lol
    }
    
}
