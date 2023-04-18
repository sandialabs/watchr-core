package gov.sandia.watchr.parse.generators.rule.actors;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import gov.sandia.watchr.config.file.DefaultFileReader;
import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.db.TestDatabase;
import gov.sandia.watchr.log.StringOutputLogger;

public class RuleDatabaseFailActorTest {
    
    private TestDatabase db;
    private StringOutputLogger testLogger;
    private IFileReader fileReader;

    @Before
    public void setup() {
        testLogger = new StringOutputLogger();
        fileReader = new DefaultFileReader(testLogger);
        db = new TestDatabase(testLogger, fileReader);
    }

    @Test
    public void testRuleWorks() {
        assertEquals(10, db.getMetadata().getHealth());

        RuleDatabaseFailActor ruleActor = new RuleDatabaseFailActor(db, new StringOutputLogger(), new HashMap<>());
        ruleActor.act();
        assertEquals(9, db.getMetadata().getHealth());

        ruleActor.act();
        ruleActor.act();
        assertEquals(7, db.getMetadata().getHealth());        
    }
}
