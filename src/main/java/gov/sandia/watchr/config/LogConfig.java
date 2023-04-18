package gov.sandia.watchr.config;

import java.util.ArrayList;
import java.util.Collections;
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

    public List<String> getLoggableDebugClasses() {
        return Collections.unmodifiableList(logger.getLoggableDebugClasses());
    }

    /////////////
    // SETTERS //
    /////////////

    public void setLoggingLevel(String loggingLevelStr) {
        ErrorLevel loggingLevel = ErrorLevel.valueOf(loggingLevelStr.toUpperCase());
        if(loggingLevel != null) {
            this.logger.setLoggingLevel(loggingLevel);
        } else {
            logger.log(
                new WatchrConfigError(
                    ErrorLevel.ERROR, "Log level " + loggingLevelStr + " is not recognized!",
                    LogConfig.class.getSimpleName()));
        }
    }

    public void setLoggableDebugClasses(List<String> loggableDebugClasses) {
        logger.getLoggableDebugClasses().clear();
        for(String debugClass : loggableDebugClasses) {
            logger.getLoggableDebugClasses().add(debugClass);
        }
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void validate() {
        if(logger.getLoggingLevel() == null) {
            logger.log(new WatchrConfigError(
                ErrorLevel.ERROR, "Logging level is undefined.",
                LogConfig.class.getSimpleName())
            );
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

        if(!logger.getLoggableDebugClasses().equals(otherLogConfig.logger.getLoggableDebugClasses())) {
            WatchrDiff<List<String>> diff = new WatchrDiff<>(configPath, DiffCategory.LOGGABLE_CLASSES);
            diff.setBeforeValue(logger.getLoggableDebugClasses());
            diff.setNowValue(otherLogConfig.logger.getLoggableDebugClasses());
            diffList.add(diff);
        }

        return diffList;
    }
    
}
