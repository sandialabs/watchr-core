/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.plotypus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.util.OsUtil;

/**
 * A plotypus is a data management object that allows Watchr configurations to
 * be processed against a set of report files in multithreaded fashion. A
 * plotypus has one of more {@link Tentacle}s, which are the threaded objects it
 * uses to process report files in parallel. A plotypus processes
 * {@link TentaclePayload}s, which bundle the configurations with their
 * associated {@link AbstractGenerator}s.
 * 
 * @param <I> The type of {@link IConfig} that the plotypus is configured to
 *            process.
 */
public class Plotypus<I> {

    ////////////
    // FIELDS //
    ////////////

    private static final String CLASSNAME = Plotypus.class.getSimpleName();

    private boolean dead = false;
    private Set<Tentacle<I>> tentacles = new HashSet<>();
    private List<TentaclePayload<I>> payloads;
    private List<TentaclePayload<I>> failedPayloads;
    private ILogger logger;

    private int payloadTimeWarning = 5000;
    private int payloadTimeout = 30000;

    private int tentacleId = 0;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    /**
     * The constructor.
     * 
     * @param tentacleCount The number of tentacles (i.e. the number of threads).
     * @param logger        The logger for the plotypus.
     */
    public Plotypus(int tentacleCount, ILogger logger) {
        this.logger = logger;
        this.payloads = new ArrayList<>();
        this.failedPayloads = new ArrayList<>();
        
        for(int i = 0; i < tentacleCount; i++) {
            tentacles.add(new Tentacle<>(this, ++tentacleId, logger));
        }
        logger.logDebug("A plotypus is born!", CLASSNAME);
    }

    public void begin() {
        for(Tentacle<I> tentacle : tentacles) {
            tentacle.begin();
        }
    }

    /////////////
    // GETTERS //
    /////////////

    public int getPayloadTimeout() {
        return payloadTimeout;
    }

    public int getPayloadTimeWarning() {
        return payloadTimeWarning;
    }

    public void setPayloadTimeout(int payloadTimeout) {
        this.payloadTimeout = payloadTimeout;
    }

    public void setPayloadTimeWarning(int payloadTimeWarning) {
        this.payloadTimeWarning = payloadTimeWarning;
    }

    /**
     * 
     * @return Whether the plotypus is alive.
     */
    public boolean isAlive() {
        return !dead;
    }

    /**
     * 
     * @return Whether the plotypus is working. This is determined by two things: 1)
     *         whether the plotypus still has unprocessed payloads, and 2) whether
     *         the tentacles are still working on existing payloads.
     */
    public boolean isWorking() {
        if(hasMorePayloads()) {
            logger.logDebug("The plotypus says, \"I still have payloads to handle...\"", CLASSNAME);
            return true;
        }
        for(Tentacle<I> tentacle : tentacles) {
            if(tentacle.isWorking()) {
                logger.logDebug("The plotypus says, \"Tentacle " + tentacle.getThreadId() + " is still busy...\"", CLASSNAME);
                return true;
            }
        }
        return false;
    }

    /**
     * 
     */
    public void respawnInterruptedTentacles() {
        int removedTentacles = 0;
        for(Iterator<Tentacle<I>> iter = tentacles.iterator(); iter.hasNext();) {
            Tentacle<?> tentacle = iter.next();
            if(tentacle.isInterrupted()) {                
                iter.remove();
                removedTentacles++;
            }
        }

        for(int i = 0; i < removedTentacles; i++) {
            Tentacle<I> tentacle = new Tentacle<>(this, ++tentacleId, logger);
            tentacles.add(tentacle);
            tentacle.begin();
        }
    }

    /**
     * Report problems with payloads being processed. This is useful if the plotypus
     * appears to be stuck in deadlock.
     * 
     * @return Any problem status reported by generators that are inside tentacle
     *         payloads that are actively being processed.
     */
    public String getProblemStatus() {
        StringBuilder sb = new StringBuilder();
        for (Tentacle<I> tentacle : tentacles) {
            if(tentacle.isStale()) {
                sb.append("Tentacle #").append(tentacle.getId());
                sb.append(" has been working on payload ").append(tentacle.getPayloadUUID());
                sb.append(" for ").append(tentacle.getWorkTimeSeconds()).append(" seconds.")
                        .append(OsUtil.getOSLineBreak());
                sb.append("\t" + tentacle.getProblemStatus());
            }
        }
        return sb.toString();
    }

    /**
     * If a tentacle has been working on a particular payload for too long,
     * interrupt the thread and force it to return the payload to the parent for
     * later processing.
     * 
     * @return The status of the interrupted tentacles.
     */
    public String interruptTentacles() {
        StringBuilder sb = new StringBuilder();
        for (Tentacle<I> tentacle : tentacles) {
            if(tentacle.isTimeout() && tentacle.isWorking()) {
                sb.append("Tentacle #").append(tentacle.getId());
                sb.append(" (Thread #").append(tentacle.getThreadId()).append(")");
                sb.append(" has timed out trying to work on payload ").append(tentacle.getPayloadUUID());
                sb.append(OsUtil.getOSLineBreak());
                tentacle.returnPayloadToParent();
                tentacle.killTentacle();                
            }
        }
        return sb.toString();
    }

    /**
     * 
     * @return Whether there are more payloads left to process.
     */
    public boolean hasMorePayloads() {
        return !payloads.isEmpty();
    }

    /**
     * 
     * @return The number of remaining payloads. This does not include
     *         currently-processed payloads.
     */
    public int getPayloadCount() {
        return payloads.size();
    }

    /**
     * Get the next unprocessed payload. Note that this will remove the payload from
     * the plotypus' internal list of unprocessed payloads.
     * 
     * @return The next unprocessed payload.
     */
    public TentaclePayload<I> getNextPayload() {
        logger.logDebug("A tentacle has asked the plotypus for the next payload...", CLASSNAME);
        TentaclePayload<I> nextPayload = null;
        synchronized(payloads) {
            if(!payloads.isEmpty()) {
                nextPayload = payloads.remove(0);
            }
        }
        return nextPayload;
    }

    public List<TentaclePayload<I>> getFailedPayloads() {
        return failedPayloads;
    }

    /**
     * 
     * @param payload The payload to add to the plotypus' list of payloads.
     */
    public void addPayload(TentaclePayload<I> payload) {
        logger.logDebug("Adding the next payload to the plotypus...", CLASSNAME);
        payloads.add(payload);
    }

    /**
     * 
     * @param payload The payload to add to the plotypus' list of failed payloads.
     */
    public void addFailedPayload(TentaclePayload<I> payload) {
        if(payload != null) {
            failedPayloads.add(payload);
        }
    }

    public void waitToFinish() throws InterruptedException {
        int lastPayloadCount = -1;
        while(isWorking()) {
            int thisPayloadCount = getPayloadCount();
            if (thisPayloadCount != lastPayloadCount) {
                lastPayloadCount = thisPayloadCount;
                logger.logInfo("Remaining plot payloads: " + thisPayloadCount);
            } else {
                String problemStatus = getProblemStatus();
                if(!problemStatus.isEmpty()) {
                    logger.logWarning(OsUtil.getOSLineBreak() + problemStatus);
                }
                String interruptionStatus = interruptTentacles();
                if(!interruptionStatus.isEmpty()) {
                    logger.logError(OsUtil.getOSLineBreak() + interruptionStatus);
                }
                respawnInterruptedTentacles();
            }
            Thread.sleep(1000);
        }
    }

    /**
     * Kill the plotypus.
     */
    public void kill() {
        for(Tentacle<I> tentacle : tentacles) {
            tentacle.returnPayloadToParent();
            tentacle.killTentacle();
        }
        tentacles.clear();
        dead = true;
    }
}
