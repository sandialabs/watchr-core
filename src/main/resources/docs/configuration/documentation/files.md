# Configuration Documentation: files

The "files" section of a Watchr config file instructs Watchr on how to explore your filesystem and gather data for plots.  Once files have been located and read by Watchr, the "plot" section of your config file will dictate how Watchr should extract data from each file.

## Properties

* **fileName** : *string* : The name pattern for files that should be parsed by Watchr.  This field respects [regular expressions](regex.html).
* **type** : *string* : The file extension for files that Watchr should read.  No leading period should be used when specifying the file extension.  If left blank, file extension is not regarded by Watchr.
* **ignoreOldFiles** : *boolean* :  If true, Watchr will keep track of the files it has previously parsed and ignore those files on subsequent parses.  If false, Watchr will read every file (based on the other configuration of this section) and add data to its database, disregarding duplicate information.
* **recurseDirectories** : *boolean* : If true, Watchr will also explore child directories for more files, beginning from the starting point specified when Watchr was launched.  If false, Watchr will only explore the starting directory for files.

## Child Elements

None.

## Parent Element

- [**plots**](plots.html)