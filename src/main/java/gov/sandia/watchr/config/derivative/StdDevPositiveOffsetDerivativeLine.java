package gov.sandia.watchr.config.derivative;

import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.util.StatUtil;

public class StdDevPositiveOffsetDerivativeLine extends RollingDerivativeLine {

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public StdDevPositiveOffsetDerivativeLine(String configPathPrefix, ILogger logger) {
        super(configPathPrefix + "/derivativeLine/stdDevPositiveOffset", logger);
    }

    public StdDevPositiveOffsetDerivativeLine(StdDevPositiveOffsetDerivativeLine copy) {
        super(copy);
    }
    
    //////////////
    // OVERRIDE //
    //////////////

    @Override
    protected double calculateValue(double[] data) {
        return StatUtil.avg(data) + StatUtil.stdDev(data);
    }

    @Override
    public DerivativeLine applyOverTemplate(DerivativeLine template) {
        if(template instanceof StdDevPositiveOffsetDerivativeLine) {
            StdDevPositiveOffsetDerivativeLine newDerivativeLine =
                new StdDevPositiveOffsetDerivativeLine((StdDevPositiveOffsetDerivativeLine)template);
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
