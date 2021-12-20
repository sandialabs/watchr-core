package gov.sandia.watchr.config.derivative;

public enum DerivativeLineType {
    AVERAGE("Average"),
    CHILD_PREVIEW("Child Preview"),
    STANDARD_DEVIATION("Std. Dev."),
    STANDARD_DEVIATION_OFFSET("Average + Std. Dev."),
    STANDARD_DEVIATION_NEG_OFFSET("Average - Std. Dev."),
    SLOPE("Slope");

    private final String label;

    private DerivativeLineType(String label) {
        this.label = label;
    }

    public String get() {
        return label;
    }
}
