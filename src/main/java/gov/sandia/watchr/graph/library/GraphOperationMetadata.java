package gov.sandia.watchr.graph.library;

public enum GraphOperationMetadata {
    PLOT_DB_LOCATION("plotDbLocation"),
    PLOT_NAME_MANIFEST("plotNameManifest"),
    NUMBER_OF_GRAPHS("numberOfGraphs");

    private final String label;

    private GraphOperationMetadata(String label) {
        this.label = label;
    }

    public String get() {
        return label;
    }
}
