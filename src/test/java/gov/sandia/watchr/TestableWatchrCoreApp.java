package gov.sandia.watchr;

public class TestableWatchrCoreApp extends WatchrCoreApp {
    
    /**
     * Instantiates a new WatchrCoreApp. This method should not be used in practice
     * - it is required by unit tests that need to start multiple Watchr processes.
     * 
     * @return A new WatchrCoreApp instance.
     */
    public static WatchrCoreApp initWatchrAppForTests() {
        return new WatchrCoreApp();
    } 

    /**
     * This method should not be used in practice
     * - it is required by unit tests that need access to the database subsystem.
     * @return
     */
    public WatchrCoreAppDatabaseSubsystem getDatabaseSubsystemForTests() {
        return dbSubsystem;
    }
}
