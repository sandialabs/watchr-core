# Configuration Documentation: files

The "files" section of a Watchr config file instructs Watchr on how to explore your filesystem and gather data for plots.  Once files have been located and read by Watchr, the "plot" section of your config file will dictate how Watchr should extract data from each file.

## Properties

* **fileName** : *string* : The name pattern for files that should be parsed by Watchr.  This field respects [regular expressions](regex.html).
* **type** : *string* : The file extension for files that Watchr should read.  No leading period should be used when specifying the file extension.  If left blank, file extension is not regarded by Watchr.
* **ignoreOldFiles** : *boolean* :  False by default. If true, Watchr will keep track of the files it has previously parsed and ignore those files on subsequent parses.  If false, Watchr will read every file (based on the other configuration of this section) and add data to its database, disregarding duplicate information.
* **recurseDirectories** : *boolean* : False by default. If true, Watchr will also explore child directories for more files, beginning from the starting point specified when Watchr was launched.  If false, Watchr will only explore the starting directory for files.
* **randomizeFileOrder** : *boolean* : False by default. If true, Watchr will read report files in random order. Setting this field to true can improve threading performance if there are many sets of report files that each result in edits being made to the same final plot; randomization can implicitly force Watchr to diversify which plots it is working on at any given time.

## Child Elements

None.

## Parent Element

- [**plots**](plots.html)