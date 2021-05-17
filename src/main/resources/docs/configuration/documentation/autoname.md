# Configuration Documentation: autoname

The "autoname" section can help with automatically naming many plots if the Watchr specification for a single plot will actually result in more than one rendered graph.  This can happen if a "strategy" section is specified, and the "getFirstMatchOnly" field is false, or the "recurseChildGraphs" setting is true.

## Properties

* **useProperty** : *string* : Informs Watchr which property should be used to autoname the resulting graph.  Currently supported options are "x/key", "x/path", "y/key", or "y/path."
 * "x" and "y" refer to the extractor used to generate the main data line.
 * "key" refers to the specific field used to identify the plottable data.
 * "path" refers to the entire path taken through the data file to arrive at the plottable data.
* **formatByRemovingPrefix** : *string* : Allows Watchr to remove the beginning portion of the string returned by "useProperty."  This field accepts fully-featured Java regular expressions.

## Child Elements

None.

## Parent Element

- [**plot**](plot.html)