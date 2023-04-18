package gov.sandia.watchr.parse.generators.rule;

public enum RuleTarget {
    
    LAST_POINT_ON_DATA_LINE,
    LAST_POINT_ON_AVERAGE_LINE,
    LAST_POINT_ON_STD_DEV_LINE,
    LAST_POINT_ON_STD_DEV_OFFSET_LINE,
    LAST_POINT_ON_STD_DEV_NEG_OFFSET_LINE,
    NUMBER_OF_NEW_DATASETS;

    public static RuleTarget getTargetForShortLabel(String shortLabel) {
        if(shortLabel.equals("dataLine")) {
            return RuleTarget.LAST_POINT_ON_DATA_LINE;
        } else if(shortLabel.equals("average")) {
            return RuleTarget.LAST_POINT_ON_AVERAGE_LINE;
        } else if(shortLabel.equals("standardDeviation")) {
            return RuleTarget.LAST_POINT_ON_STD_DEV_LINE;
        } else if(shortLabel.equals("standardDeviationOffset")) {
            return RuleTarget.LAST_POINT_ON_STD_DEV_OFFSET_LINE;
        } else if(shortLabel.equals("newDatasets")) {
            return RuleTarget.NUMBER_OF_NEW_DATASETS;
        } else {
            return null;
        }
    }
}
