package gov.sandia.watchr.config;

import java.util.ArrayList;
import java.util.List;

import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.log.ILogger;

public class LogConfig implements IConfig {

    ////////////
    // FIELDS //
    ////////////

    private String configPath;
    private ILogger logger;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public LogConfig(String configPathPrefix, ILogger logger) {
        this.configPath = configPathPrefix + "/logConfig";
        this.logger = logger;
    }

    public LogConfig(LogConfig copy) {
        this.configPath = copy.getConfigPath();
        this.logger = copy.getLogger();
    }
    
    /////////////
    // GETTERS //
    /////////////

    @Override
    public ILogger getLogger() {
        return logger;
    }

    @Override
    public String getConfigPath() {
        return configPath;
    }

    public ErrorLevel getLoggingLevel() {
        return logger.getLoggingLevel();
    }

    /////////////
    // SETTERS //
    /////////////

    public void setLoggingLevel(String loggingLevelStr) {
        ErrorLevel loggingLevel = ErrorLevel.valueOf(loggingLevelStr.toUpperCase());
        if(loggingLevel != null) {
            this.logger.setLoggingLevel(loggingLevel);
        } else {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "Log level " + loggingLevelStr + " is not recognized!"));
        }
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void validate() {
        if(logger.getLoggingLevel() == null) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "Logging level is undefined."));
        }
    }

    @Override
    public List<WatchrDiff<?>> diff(IConfig other) {
        LogConfig otherLogConfig = (LogConfig) other;
        List<WatchrDiff<?>> diffList = new ArrayList<>();

        if(logger.getLoggingLevel() != otherLogConfig.logger.getLoggingLevel()) {
            WatchrDiff<String> diff = new WatchrDiff<>(configPath, DiffCategory.LOGGING_LEVEL);
            diff.setBeforeValue(logger.getLoggingLevel().toString());
            diff.setNowValue(otherLogConfig.logger.getLoggingLevel().toString());
            diffList.add(diff);
        }
        return diffList;
    }
    
}
