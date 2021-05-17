# Configuration Documentation: fileFilter

In addition to the "fileName" field provided in the top-level [**files**](files.html) section of a Watchr config JSON file, which provides a whitelist to Watchr of files to look at for performance report data, you can also provide a filename whitelist filter at the level of individual plots.  This is achieved with the "fileFilter" section.

## Properties

* **namePattern** : *string* : Allows you to specify a pattern for allowing only files that contain certain characters to be used for gathering plot data.  This field recognizes simple wildcards (i.e. asterisks).

## Child Elements

None.

## Parent Element

- [**plot**](plot.html)