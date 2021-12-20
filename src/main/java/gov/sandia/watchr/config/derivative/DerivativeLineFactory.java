package gov.sandia.watchr.config.derivative;

import gov.sandia.watchr.log.ILogger;

public class DerivativeLineFactory {
 
    private DerivativeLineFactory() {}

    private static DerivativeLineFactory INSTANCE;

    public static DerivativeLineFactory getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new DerivativeLineFactory();
        }
        return INSTANCE;
    }

    public DerivativeLine create(DerivativeLineType type, String configPathPrefix, ILogger logger) {
        DerivativeLine derivativeLine = null;
        if(type == DerivativeLineType.SLOPE) {
            derivativeLine = new SlopeDerivativeLine(configPathPrefix, logger);
            derivativeLine.setName("slope");
        } else if(type == DerivativeLineType.AVERAGE) {
            derivativeLine = new AverageDerivativeLine(configPathPrefix, logger);
            derivativeLine.setName("average");
        } else if(type == DerivativeLineType.STANDARD_DEVIATION) {
            derivativeLine = new StdDevDerivativeLine(configPathPrefix, logger);
            derivativeLine.setName("stdDev");
        } else if(type == DerivativeLineType.STANDARD_DEVIATION_OFFSET) {
            derivativeLine = new StdDevPositiveOffsetDerivativeLine(configPathPrefix, logger);
            derivativeLine.setName("stdDevOffset");
        } else if(type == DerivativeLineType.STANDARD_DEVIATION_NEG_OFFSET) {
            derivativeLine = new StdDevNegativeOffsetDerivativeLine(configPathPrefix, logger);
            derivativeLine.setName("stdDevNegativeOffset");
        } else if(type == DerivativeLineType.CHILD_PREVIEW) {
            derivativeLine = new ChildPreviewDerivativeLine(configPathPrefix, logger);
            derivativeLine.setName("childPreview");
        }
        return derivativeLine;
    }

    public DerivativeLine create(String configPathPrefix, DerivativeLine copy, boolean copyProperties, ILogger logger) {
        DerivativeLine derivativeLine = null;
        if(copy instanceof SlopeDerivativeLine) {
            if(copyProperties) {
                derivativeLine = new SlopeDerivativeLine((SlopeDerivativeLine) copy);
            } else {
                derivativeLine = new SlopeDerivativeLine(configPathPrefix, logger);
            }
        } else if(copy instanceof AverageDerivativeLine) {
            if(copyProperties) {
                derivativeLine = new AverageDerivativeLine((AverageDerivativeLine) copy);
            } else {
                derivativeLine = new AverageDerivativeLine(configPathPrefix, logger);
            }
        } else if(copy instanceof StdDevDerivativeLine) {
            if(copyProperties) {
                derivativeLine = new StdDevDerivativeLine((StdDevDerivativeLine) copy);
            } else {
                derivativeLine = new StdDevDerivativeLine(configPathPrefix, logger);
            }
        } else if(copy instanceof StdDevPositiveOffsetDerivativeLine) {
            if(copyProperties) {
                derivativeLine = new StdDevPositiveOffsetDerivativeLine((StdDevPositiveOffsetDerivativeLine) copy);
            } else {
                derivativeLine = new StdDevPositiveOffsetDerivativeLine(configPathPrefix, logger);
            }
        } else if(copy instanceof StdDevNegativeOffsetDerivativeLine) {
            if(copyProperties) {
                derivativeLine = new StdDevNegativeOffsetDerivativeLine((StdDevNegativeOffsetDerivativeLine) copy);
            } else {
                derivativeLine = new StdDevNegativeOffsetDerivativeLine(configPathPrefix, logger);
            }
        } else if(copy instanceof ChildPreviewDerivativeLine) {
            if(copyProperties) {
                derivativeLine = new ChildPreviewDerivativeLine((ChildPreviewDerivativeLine) copy);
            } else {
                derivativeLine = new ChildPreviewDerivativeLine(configPathPrefix, logger);
            }
        }
        return derivativeLine;
    }

    public DerivativeLineType getTypeFromObject(DerivativeLine line) {
        if(line instanceof AverageDerivativeLine) return DerivativeLineType.AVERAGE;
        else if(line instanceof StdDevDerivativeLine) return DerivativeLineType.STANDARD_DEVIATION;
        else if(line instanceof StdDevNegativeOffsetDerivativeLine) return DerivativeLineType.STANDARD_DEVIATION_NEG_OFFSET;
        else if(line instanceof StdDevPositiveOffsetDerivativeLine) return DerivativeLineType.STANDARD_DEVIATION_OFFSET;
        else if(line instanceof SlopeDerivativeLine) return DerivativeLineType.SLOPE;
        else if(line instanceof ChildPreviewDerivativeLine) return DerivativeLineType.CHILD_PREVIEW;
        else return null;
    }
}
