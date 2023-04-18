package gov.sandia.watchr.config.derivative;

import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.util.StatUtil;

public class AverageDerivativeLine extends RollingDerivativeLine {

    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public AverageDerivativeLine(String configPathPrefix, ILogger logger) {
        super(configPathPrefix + "/derivativeLine/average", logger);
    }

    public AverageDerivativeLine(AverageDerivativeLine copy) {
        super(copy);
    }
    
    //////////////
    // OVERRIDE //
    //////////////

    @Override
    protected double calculateValue(double[] data) {
        return StatUtil.avg(data);
    }

    @Override
    public DerivativeLine applyOverTemplate(DerivativeLine template) {
        if(template instanceof AverageDerivativeLine) {
            AverageDerivativeLine newDerivativeLine = new AverageDerivativeLine((AverageDerivativeLine)template);
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
