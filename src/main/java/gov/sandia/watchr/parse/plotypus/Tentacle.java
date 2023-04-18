package gov.sandia.watchr.parse.plotypus;

import java.util.List;

import gov.sandia.watchr.config.diff.WatchrDiff;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.generators.AbstractGenerator;

public class Tentacle<I> implements Runnable {

    ////////////
    // FIELDS //
    ////////////

    private static final String CLASSNAME = Tentacle.class.getSimpleName();

    private final int tentacleId;
    private int payloadTimeWarning;
    private int payloadTimeout;
    private long payloadStartTime;

    private Plotypus<I> parent;
    private TentaclePayload<I> payload;
    private ILogger logger;

    private Thread thread;

    /////////////////
    // CONSTRUCTOR //
    /////////////////

    public Tentacle(Plotypus<I> parent, int tentacleId, ILogger logger) {
        this.tentacleId = tentacleId;
        this.parent = parent;
        this.logger = logger;

        this.payloadStartTime = System.currentTimeMillis();

        thread = new Thread(this);
        thread.setName("Watchr Plotypus Tentacle "+tentacleId+" ["+thread.getId()+"]");
    }

    /////////////
    // GETTERS //
    /////////////

    public int getId() {
        return tentacleId;
    }

    public long getThreadId() {
        return thread == null ? -1 : thread.getId();
    }

    public boolean isWorking() {
        return payload != null;
    }

    public boolean isInterrupted() {
        return thread == null || thread.isInterrupted();
    }

    public boolean isStale() {
        long currentTime = System.currentTimeMillis();
        return payload != null && (currentTime - payloadStartTime > payloadTimeWarning);
    }

    public boolean isTimeout() {
        long currentTime = System.currentTimeMillis();
        return payload != null && (currentTime - payloadStartTime > payloadTimeout);
    }

    public double getWorkTimeSeconds() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - payloadStartTime) / 1000.0;
    }

    public String getPayloadUUID() {
        if(payload != null) {
            return payload.getUUID();
        }
        return "NO PAYLOAD";
    }

    public String getProblemStatus() {
        if(payload != null) {
            return payload.getProblemStatus();
        }
        return "";
    }

    /////////////
    // SETTERS //
    /////////////

    public void returnPayloadToParent() {
        parent.addFailedPayload(payload); // Return the unfinished payload to the parent.
        payload = null;
    }

    public void killTentacle() {
        try {
            if(thread != null) {
                thread.interrupt();            
                thread.join();
            }
        } catch(InterruptedException e1) {
            logger.logDebug("Tentacle " + getId() + " Thread Interrupt: ", CLASSNAME);
            Thread.currentThread().interrupt();
        } catch(Exception e2) {
            logger.logError("Payload could not complete. Reason:", e2);
        }
        
        thread = null;
        payload = null;
    }

    /////////
    // RUN //
    /////////

    public void begin() {
        payloadTimeWarning = parent.getPayloadTimeWarning();
        payloadTimeout = parent.getPayloadTimeout();

        logger.logDebug("Starting tentacle " + thread.getName(), CLASSNAME);
        thread.start();
    }

    @Override
    public void run() {
        thread = Thread.currentThread();
        try {
            while(!isInterrupted() && parent.isAlive() && parent.hasMorePayloads()) {
                payload = parent.getNextPayload();
                if(payload != null) {
                    logger.logDebug("Tentacle #" + getThreadId() + " says, \"I got the next payload...\"", CLASSNAME);
                    processPayload();
                    payload = null;                
                    logger.logDebug("Tentacle #" + getThreadId() + " says, \"I finished the payload.\"", CLASSNAME);
                }
                Thread.sleep(1);
            }
        } catch(InterruptedException e1) {
            logger.logDebug("Tentacle " + getId() + " Thread Interrupt: ", CLASSNAME);
            returnPayloadToParent();
            killTentacle();
        } catch(WatchrParseException e2) {
            logger.logError("Payload could not complete. Reason:", e2);
            logger.logError("Original exception:", e2.getOriginalException());
            returnPayloadToParent();
            killTentacle();
        } catch(Exception e3) {
            logger.logError("Payload could not complete. Reason:", e3);
            returnPayloadToParent();
            killTentacle();
        }
    }

    /////////////
    // PRIVATE //
    /////////////

    private void processPayload() throws WatchrParseException {
        payloadStartTime = System.currentTimeMillis();
        AbstractGenerator<I> generator = payload.getGenerator();
        I config = payload.getConfig();
        List<WatchrDiff<?>> diffs = payload.getDiffs();
        generator.generate(config, diffs);
    }
}
