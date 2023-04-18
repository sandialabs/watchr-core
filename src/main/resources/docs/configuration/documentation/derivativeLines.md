# Configuration Documentation: derivativeLines

Given a [data line](dataLines.html) on a Watchr graph, additional "derivative lines" can be added.  These lines are derivative in the sense that they do not contain their own independent data, but rather are processed versions of data from an original line.  One example of a derivative line would be the rolling average of data from an original line.

## Properties

* **type** : *enum* :  The type of derivative line.  These strings can be case-insensitive in your config file.  Current available types are:
 * *average* - rolling average line
 * *standardDeviation* - standard deviation line
 * *standardDeviationNegativeOffset* - the average line minus 1 standard deviation.
 * *standardDeviationOffset* - the average line plus 1 standard deviation.
 * *slope* - a line defined by the expression "y=mx+b".
* **range** : *int* : The range of data points to use if your derivative line type is rolling.
* **color** : *RGB* : The color of the data line.  The color should be specified by providing the three number values for red, green, and blue.  You can alternately provide a fourth value between 0.0 and 1.0 to specify opacity.
* **ignoreFilteredData** : *boolean* : If true, any points filtered from the main data line will not be used in calculating the derivative values that comprise this line.
* **numberFormat** : *string* : Provide Java number format syntax to apply digits-of-precision formatting to displayed values (i.e. the string #.#### will provide decimal numbers with four digits of precision).

## Child Elements

None.

## Parent Element

* [**dataLines**](dataLines.html)