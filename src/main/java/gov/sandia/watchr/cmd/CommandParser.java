package gov.sandia.watchr.cmd;

import java.io.IOException;
import java.util.Arrays;

import gov.sandia.watchr.WatchrCoreApp;
import gov.sandia.watchr.cmd.impl.WatchrAddCommand;
import gov.sandia.watchr.cmd.impl.WatchrConfigCommand;
import gov.sandia.watchr.cmd.impl.WatchrPutDatabaseCommand;
import gov.sandia.watchr.cmd.impl.WatchrPutPlotsCommand;
import gov.sandia.watchr.cmd.impl.WatchrRunCommand;
import gov.sandia.watchr.cmd.impl.WatchrStartCommand;
import gov.sandia.watchr.cmd.impl.WatchrStopCommand;

public class CommandParser {
    
    private static final String HELP = "help";

    private static final String START = "start";
    private static final String RESTART = "restart";
    private static final String STOP = "stop";
    
    private static final String CONFIG = "config";
    private static final String PUT = "put";
    private static final String ADD = "add";
    private static final String RUN = "run";

    private static final String DB = "db";
    private static final String PLOTS = "plots";

    private final WatchrCoreApp app;

    public CommandParser(WatchrCoreApp app) {
        this.app = app;
    }

    public void parse(String[] tokens) throws IOException {
        WatchrCommand command = null;

        String primaryCommand = "";
        String secondaryCommand = "";
        String argument = "";

        if(tokens.length == 1) {
            primaryCommand = tokens[0];
        } else if(tokens.length == 2) {
            primaryCommand = tokens[0];
            argument = tokens[1];
        } else if(tokens.length == 3) {
            primaryCommand = tokens[0];
            secondaryCommand = tokens[1];
            argument = tokens[2];
        }

        if(primaryCommand.equals(HELP)) {
            printHelp();
            return;
        } else if(primaryCommand.equals(START) || primaryCommand.equals(RESTART)) {
            command = new WatchrStartCommand();
        } else if(primaryCommand.equals(STOP)) {
            command = new WatchrStopCommand();
        } else if(primaryCommand.equals(CONFIG)) {
            command = new WatchrConfigCommand();
        } else if(primaryCommand.equals(ADD)) {
            command = new WatchrAddCommand();
        } else if(primaryCommand.equals(PUT)) {
            if(secondaryCommand.equals(DB)) {
                command = new WatchrPutDatabaseCommand();
            } else if(secondaryCommand.equals(PLOTS)) {
                command = new WatchrPutPlotsCommand();
            } 
        } else if(primaryCommand.equals(RUN)) {
            command = new WatchrRunCommand();
        }

        if(command != null) {
            command.execute(argument, app);
        } else {
            throw new IllegalStateException("Unrecognized command: " + Arrays.toString(tokens));
        }
    }

    public void printHelp() {
        System.out.println("Watchr v" + WatchrCoreApp.getVersion() + "");
        System.out.println("=============");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  help    - Print Watchr's command-line commands.");
        System.out.println("  start   - Start a new Watchr session.");
        System.out.println("  restart - Clear previous Watchr session information.");
        System.out.println();
        System.out.println("  config <file>         - Provide the configuration file.");
        System.out.println("  add <file>            - Add a data file that Watchr should read.");
        System.out.println("  add <directory>       - Add a directory containing data files that Watchr should read.");
        System.out.println("  put db <directory>    - Provide the path to the directory where Watchr should put its database.");
        System.out.println("  put plots <directory> - Provide the path to the directory where Watchr should export graphs.");
        System.out.println();
        System.out.println("  run - Run Watchr according to configuration set up by previous commands.");
    }
}
