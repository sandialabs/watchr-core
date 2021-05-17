# Rule Expression Glossary

## Recognized Boolean Operators

Currently, Watchr rule expressions recognize <, =, and >.

* *Note*: Double-equals (==) is also available if you wish, and it means the same thing as a single equals.

## Condition Elements

* **average** - Refers to the last point on a "rolling average" derivative line on any graph.  See the [**derivativeLines**](configuration/documentation/derivativeLines.html) section for more information. 
* **dataLine** - Refers to the last point on the main data line of any graph.
* **standardDeviation** - Refers to the last point on a "rolling standard deviation" derivative line on any graph.  See the [**derivativeLines**](configuration/documentation/derivativeLines.html) section for more information.
* **standardDeviationOffset**  - Refers to the last point on a "standard deviation offsete" derivative line on any graph.  A "standard deviation offset" line is the average line + 1 standard deviation.  See the [**derivativeLines**](configuration/documentation/derivativeLines.html) section for more information.

## Actions

* **fail** - The "fail" action will color the background of the graph red, and also indicate that the graph is in a fail state.  This information can be acquired by any external processes that are able to query Watchr's database.
* **warn** - The "warn" action will color the background of the graph yellow, and also indicate that the graph is in a warning state.  This information can be acquired by any external processes that are able to query Watchr's database.