package gov.sandia.watchr.config.derivative;

import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.util.StatUtil;

public class StdDevNegativeOffsetDerivativeLine extends RollingDerivativeLine {

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public StdDevNegativeOffsetDerivativeLine(String configPathPrefix, ILogger logger) {
        super(configPathPrefix + "/derivativeLine/stdDevNegativeOffset", logger);
    }

    public StdDevNegativeOffsetDerivativeLine(StdDevNegativeOffsetDerivativeLine copy) {
        super(copy);
    }
    
    //////////////
    // OVERRIDE //
    //////////////

    @Override
    protected double calculateValue(double[] data) {
        return StatUtil.avg(data) - StatUtil.stdDev(data);
    }

    @Override
    public DerivativeLine applyOverTemplate(DerivativeLine template) {
        if(template instanceof StdDevNegativeOffsetDerivativeLine) {
            StdDevNegativeOffsetDerivativeLine newDerivativeLine =
                new StdDevNegativeOffsetDerivativeLine((StdDevNegativeOffsetDerivativeLine)template);
            newDerivativeLine.setColor(getColor());
            newDerivativeLine.setName(getName());
            newDerivativeLine.setRollingRange(getRollingRange());
            newDerivativeLine.setNumberFormat(getNumberFormat());
            newDerivativeLine.setIgnoreFilteredData(shouldIgnoreFilteredData());
            return newDerivativeLine;
        }
        return null;
    }
    
    @Override
    public ILogger getLogger() {
        return logger;
    }
}
