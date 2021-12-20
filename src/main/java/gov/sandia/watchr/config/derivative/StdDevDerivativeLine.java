package gov.sandia.watchr.config.derivative;

import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.util.StatUtil;

public class StdDevDerivativeLine extends RollingDerivativeLine {

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public StdDevDerivativeLine(String configPathPrefix, ILogger logger) {
        super(configPathPrefix + "/derivativeLine/stdDev", logger);
    }

    public StdDevDerivativeLine(RollingDerivativeLine copy) {
        super(copy);
    }
    
    //////////////
    // OVERRIDE //
    //////////////

    @Override
    protected double calculateValue(double[] data) {
        return StatUtil.stdDev(data);
    }

    @Override
    public DerivativeLine applyOverTemplate(DerivativeLine template) {
        if(template instanceof StdDevDerivativeLine) {
            StdDevDerivativeLine newDerivativeLine = new StdDevDerivativeLine((StdDevDerivativeLine)template);
            newDerivativeLine.setColor(getColor());
            newDerivativeLine.setName(getName());
            newDerivativeLine.setRollingRange(getRollingRange());
            newDerivativeLine.setNumberFormat(getNumberFormat());
            newDerivativeLine.setIgnoreFilteredData(shouldIgnoreFilteredData());
        }
        return null;
    }

    @Override
    public ILogger getLogger() {
        return logger;
    }
    
}
