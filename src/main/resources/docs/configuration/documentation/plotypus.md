# Configuration Documentation: plotypus

A "plotypus" is a data processing object that allows multiple data reports to be read / multiple database graphs to be updated simultaneously in multi-threaded fashion.

This is an optional section of the Watchr configuration. 

## Properties

* **tentacles** : *integer* : The number of tentacles (threads) that the plotypus has. The default value is 10.
* **timeWarning** : *integer* : In seconds, this is how long a tentacle is allowed to process a data file before warnings are sent to the log. This field is optional, and the default value is 5 seconds.
* **timeout** : *integer* : In seconds, this is how long a tentacle is allowed to process a data file before the plotypus gives up on processing the data file. The parent Watchr application process will be alerted to the fact that the data file failed to process, and Watchr will try again on a subsequent run (assuming the file is still available at that time). This field is optional, and the default value is 30 seconds.

## Child Elements

None.

## Parent Element

None.