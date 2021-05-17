# Configuration Documentation: derivativeLines

Given a [data line](dataLines.html) on a Watchr graph, additional "derivative lines" can be added.  These lines are derivative in the sense that they do not contain their own independent data, but rather are processed versions of data from an original line.  One example of a derivative line would be the rolling average of data from an original line.

## Properties

* **type** : *enum* :  The type of derivative line.  Current available types are "average", "standardDeviation", or "standardDeviationOffset", which is the average line + 1 standard deviation.  These strings can be case-insensitive in your config file.
* **range** : *int* : The range of data points to use if your derivative line type is rolling.
* **color** : *RGB* : The color of the data line.  The color should be specified by providing the three number values for red, green, and blue.
* **ignoreFilteredData** : *boolean* : If true, any points filtered from the main data line will not be used in calculating the derivative values that comprise this line.

## Child Elements

None.

## Parent Element

* [**dataLines**](dataLines.html)