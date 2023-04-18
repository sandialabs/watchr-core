package gov.sandia.watchr.config;

import java.util.ArrayList;
import java.util.List;

import gov.sandia.watchr.config.WatchrConfigError.ErrorLevel;
import gov.sandia.watchr.config.diff.DiffCategory;
import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.log.ILogger;

public class PlotypusConfig implements IConfig {

    ////////////
    // FIELDS //
    ////////////

    private int numberOfTentacles = 10;
    private int payloadTimeWarning = 5000;
    private int payloadTimeout = 30000;

    private final String configPath;
    private final ILogger logger;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public PlotypusConfig(String configPathPrefix, ILogger logger) {
        this.configPath = configPathPrefix + "/plotypusConfig";
        this.logger = logger;
    }

    public PlotypusConfig(PlotypusConfig plotypusConfig) {
        this.configPath = plotypusConfig.getConfigPath();
        this.logger = plotypusConfig.getLogger();        
        this.numberOfTentacles = plotypusConfig.getNumberOfTentacles();
        this.payloadTimeWarning = plotypusConfig.getPayloadTimeWarning();
        this.payloadTimeout = plotypusConfig.getPayloadTimeout();
    }

    /////////////
    // GETTERS //
    /////////////

    public int getNumberOfTentacles() {
        return numberOfTentacles;
    }

    public int getPayloadTimeWarning() {
        return payloadTimeWarning;
    }

    public int getPayloadTimeout() {
        return payloadTimeout;
    }

    @Override
    public ILogger getLogger() {
        return logger;
    }

    @Override
    public String getConfigPath() {
        return configPath;
    }

    /////////////
    // SETTERS //
    /////////////

    public void setNumberOfTentacles(int numberOfTentacles) {
        this.numberOfTentacles = numberOfTentacles;
    }

    public void setPayloadTimeWarning(int payloadTimeWarning) {
        this.payloadTimeWarning = payloadTimeWarning;
    }

    public void setPayloadTimeout(int payloadTimeout) {
        this.payloadTimeout = payloadTimeout;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public void validate() {
        if(numberOfTentacles <= 0) {
            logger.log(new WatchrConfigError(ErrorLevel.ERROR, "Plotypus needs at least one tentacle."));
        }
        if(payloadTimeWarning > payloadTimeout) {
            logger.log(new WatchrConfigError(ErrorLevel.WARNING, "Warning time is longer than timeout time."));
        }
    }

    @Override
    public List<WatchrDiff<?>> diff(IConfig other) {
        List<WatchrDiff<?>> diffList = new ArrayList<>();
        if(other instanceof PlotypusConfig) {
            PlotypusConfig otherPlotypusConfig = (PlotypusConfig) other;
            if(otherPlotypusConfig.getNumberOfTentacles() != getNumberOfTentacles()) {
                WatchrDiff<Integer> diff = new WatchrDiff<>(configPath, DiffCategory.TENTACLES);
                diff.setBeforeValue(numberOfTentacles);
                diff.setNowValue(otherPlotypusConfig.numberOfTentacles);
                diffList.add(diff);
            }
            if(otherPlotypusConfig.getPayloadTimeWarning() != getPayloadTimeWarning()) {
                WatchrDiff<Integer> diff = new WatchrDiff<>(configPath, DiffCategory.TIME_WARNING);
                diff.setBeforeValue(payloadTimeWarning);
                diff.setNowValue(otherPlotypusConfig.payloadTimeWarning);
                diffList.add(diff);
            }
            if(otherPlotypusConfig.getPayloadTimeout() != getPayloadTimeout()) {
                WatchrDiff<Integer> diff = new WatchrDiff<>(configPath, DiffCategory.TIMEOUT);
                diff.setBeforeValue(payloadTimeout);
                diff.setNowValue(otherPlotypusConfig.payloadTimeout);
                diffList.add(diff);
            }
        }

        return diffList;
    }
    
}
