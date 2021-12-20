# Configuration Documentation: dataLines

A data line is just what it sounds like - it is a line of data displayed on a plot provided by Watchr.  A data line must be driven by two [**extractors**](extractor.html), one for the X data and one for the Y data.

## Properties

* **name** : *string* : The name of the data line (distinct from the name of the plot containing the line).
* **color** : *RGB* : The color of the data line.  The color should be specified by providing the three number values for red, green, and blue.  You can alternately provide a fourth value between 0.0 and 1.0 to specify opacity.
* **template** : *string* : An arbitrary label you may apply to a single data line, which indicates that it is a template for other data lines.  Other data lines may inherit this data line's properties using the *inherit* field.  See the page on [templates](templates.html) for more information.
* **inherit** : *string* : Use this property to refer to the *template* field on another data line.  The current data line will inherit all of the properties of the template data line, but you still have the ability to override specific properties on the current data line.  See the page on [templates](templates.html) for more information.

## Child Elements

- [**X extractor**](extractor.html)
- [**Y extractor**](extractor.html)
- [**derivativeLines**](derivativeLines.html)
- [**metadata**](metadata.html)

## Parent Element

- [**plot**](plot.html)