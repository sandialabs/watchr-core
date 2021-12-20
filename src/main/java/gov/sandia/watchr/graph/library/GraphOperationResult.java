package gov.sandia.watchr.graph.library;

import java.util.HashMap;
import java.util.Map;

public class GraphOperationResult {
    
    private String graphRenderingLog;

    private String html;
    private Map<String, String> metadata = new HashMap<>();

    public String getHtml() {
        return html;
    }

    public Map<String,String> getMetadata() {
        return metadata;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getLog() {
        return graphRenderingLog;
    }

    public void setLog(String log) {
        this.graphRenderingLog = log;
    }
}
