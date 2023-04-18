package gov.sandia.watchr;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    WatchrCoreAppTest_BasicTests.class,
	WatchrCoreAppTest_JsonTests.class,
	WatchrCoreAppTest_XmlTests.class,
	WatchrCoreAppTest_YamlConfigTests.class,
	WatchrCoreAppTest_MultistageTests.class,
})

public class WatchrCoreAppTest {

}
