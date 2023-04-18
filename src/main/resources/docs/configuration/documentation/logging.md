# Configuration Documentation: logging

The logging section of a Watchr config document makes it possible for the user to control the amount of output that Watchr logs to its log file while it runs.

This is an optional section of the Watchr configuration. 

## Properties

* **level** : *enumerated string* : Describes the level of granularity for logging messages. This string is allowed to be "ERROR", "WARNING", "INFO", or "DEBUG". If not specified, "INFO" is the default logging level. Note that, by default, "DEBUG" logging does not enable a finer level of logging. You must additionally specify a list of classes in the "loggableClasses" array (see below) to turn on debug logging in specific areas of Watchr.
* **loggableClasses** : *array* : If you know the exact Java class name of the component you would like to turn DEBUG logging on for, you may provide the names of such classes in a JSON array here. This property value should only be the name of the class itself, not the fully-qualified package name leading up to the class name.

## Child Elements

None.

## Parent Element

None.