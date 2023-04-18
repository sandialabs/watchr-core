package gov.sandia.watchr.parse.plotypus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.parse.generators.AbstractGenerator;

public class TentaclePayload<I> {

    ////////////
    // FIELDS //
    ////////////

    private final String uuid;
    private AbstractGenerator<I> generator;
    private I config;
    private List<WatchrDiff<?>> diffs;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public TentaclePayload(AbstractGenerator<I> generator, I config, List<WatchrDiff<?>> diffs) {
        this.uuid = UUID.randomUUID().toString();
        this.generator = generator;
        this.config = config;
        this.diffs = new ArrayList<>();
        this.diffs.addAll(diffs);
    }

    /////////////
    // GETTERS //
    /////////////

    public String getUUID() {
        return uuid;
    }

    public AbstractGenerator<I> getGenerator() {
        return generator;
    }

    public I getConfig() {
        return config;
    }

    public List<WatchrDiff<?>> getDiffs() {
        return Collections.unmodifiableList(diffs);
    }
    
    public String getProblemStatus() {
        return generator.getProblemStatus();
    }
}
