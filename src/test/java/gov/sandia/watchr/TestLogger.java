package gov.sandia.watchr;

import java.util.ArrayList;
import java.util.List;

import gov.sandia.watchr.config.WatchrConfigError;
import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.log.ILogger;

public class TestLogger implements ILogger {

    private List<WatchrConfigError> errorList = new ArrayList<>();

    public List<WatchrConfigError> getErrors() {
        return errorList;
    }

    @Override
    public void log(WatchrConfigError errorObj) {
        errorList.add(errorObj);
    }

    @Override
    public void logInfo(String message) {
        errorList.add(new WatchrConfigError(ErrorLevel.INFO, message));
    }

    @Override
    public void logWarning(String message) {
        errorList.add(new WatchrConfigError(ErrorLevel.WARNING, message));
    }

    @Override
    public void logError(String error) {
        errorList.add(new WatchrConfigError(ErrorLevel.ERROR, error));
    }

    @Override
    public void logError(String error, Throwable t) {
        errorList.add(new WatchrConfigError(ErrorLevel.ERROR, error + "\n" + t.getMessage()));
    }
    
}
