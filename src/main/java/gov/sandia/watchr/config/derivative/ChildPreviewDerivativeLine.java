package gov.sandia.watchr.config.derivative;

import gov.sandia.watchr.log.ILogger;

public class ChildPreviewDerivativeLine extends DerivativeLine {

    protected ChildPreviewDerivativeLine(String configPathPrefix, ILogger logger) {
        super(configPathPrefix + "/derivativeLine/child", logger);
    }

    protected ChildPreviewDerivativeLine(ChildPreviewDerivativeLine copy) {
        super(copy);
    }

    @Override
    public DerivativeLine applyOverTemplate(DerivativeLine template) {
        if(template instanceof ChildPreviewDerivativeLine) {
            ChildPreviewDerivativeLine newDerivativeLine = new ChildPreviewDerivativeLine((ChildPreviewDerivativeLine)template);
            newDerivativeLine.setColor(getColor());
            newDerivativeLine.setName(getName());
        }
        return null;
    }

    @Override
    public ILogger getLogger() {
        return logger;
    }
    
}
