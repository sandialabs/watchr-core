#Watchr on the Command Line

## Command Glossary

 * **start**
  * Start Watchr.
  * This command should always be executed first.
  * In practice, this prepares Watchr to run on the command line by creating intermediate data files behind the scenes. 
 * **config**
  * Connect Watchr to a pre-existing JSON configuration file. This configuration file  contains all the instructions to tell Watchr how to run.
  * This should always be executed prior to a `watchr run` command.
  * This command is required to be followed by a filepath to the location of the configuration JSON file.
 * **put**
  * The "put" command is optional. This provides additional specification for where Watchr should put its output data (database folder, plot export folder, or both).
  * If not specified, Watchr will put these output files in default locations.
  * For example, `put db`, followed by a filepath, will tell Watchr where to put database files.
  * The other option is `put plots`, also followed by a filepath, which will tell Watchr where to export graph files to.
 * **add**
  * The "add" command tells Watchr to add either a data file or a data folder to its queue of data files to process.
  * This should always be executed prior to a `watchr run` command.
  * This command is required to be followed by a filepath to the location of the data file or data folder.
  * If a folder is specified as the location instead of a file, every file (and every subfolder) inside that folder will be processed by Watchr recursively. 
 * **run**
  * The "run" command will actually run Watchr, according to the configuration specified by the previous commands.
  * There is no secondary argument for "run." 
 * **stop**
  * Stops Watchr.
  * This should be the last command you execute.
  * In practice, this simply deletes the intermediate data files that Watchr was using while running. 

## Example Program

	watchr start 
    watchr config config.json
    watchr add performance_day_1.xml
    watchr put db DatabaseFolder
    watchr put plots PlotExportFolder
    watchr run
    watchr stop