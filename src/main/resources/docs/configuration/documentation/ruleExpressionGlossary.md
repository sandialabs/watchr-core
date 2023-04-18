# Rule Expression Glossary

## Recognized Boolean Operators

Currently, Watchr rule expressions recognize <, =, and >.

* *Note*: Double-equals (==) is also available if you wish to use more programmer-like syntax. It means the same thing as a single equals.

## Condition Elements (aka "Rule Targets")

* **average** - Refers to the last point on a "rolling average" derivative line on any graph.  See the [**derivativeLines**](configuration/documentation/derivativeLines.html) section for more information. 
* **dataLine** - Refers to the last point on the main data line of any graph.
* **newDatasets** - Refers to the number of new plottable datasets discovered since the last run of Watchr.
* **standardDeviation** - Refers to the last point on a "rolling standard deviation" derivative line on any graph.  See the [**derivativeLines**](configuration/documentation/derivativeLines.html) section for more information.
* **standardDeviationOffset**  - Refers to the last point on a "standard deviation offsete" derivative line on any graph.  A "standard deviation offset" line is the average line + 1 standard deviation.  See the [**derivativeLines**](configuration/documentation/derivativeLines.html) section for more information.

## Special Conditions

* **always** - As the name implies, this condition can be used for a rule that should always be acted upon.

## Actions

* **deleteAllData** - Delete all data from the database.
* **deleteConditionalData** - Delete certain data from the database. The conditions for deleting data are determined by setting configuration in the "actionProperties" block for this rule.
* **failDatabase** - The entire database can be put into a failure state. The severity/meaning of "failing" the database is determined by the one who implemented this rule.

## Actions: Plot-Specific

* **fail** - The "fail" action will color the background of a particular graph red, and also indicate that the graph is in a fail state.  This information can be acquired by any external processes that are able to query Watchr's database.
* **warn** - The "warn" action will color the background of a particular graph yellow, and also indicate that the graph is in a warning state.  This information can be acquired by any external processes that are able to query Watchr's database.

## Action Properties

* **ageToDelete** - This property is used in conjunction with the action "deleteConditionalData." Use this property for deleting data that is older than a certain age. Age is counted by the number of points on a line. For example, "ageToDelete : 10" would indicate that only the most recent 10 points on a line should be preserved.