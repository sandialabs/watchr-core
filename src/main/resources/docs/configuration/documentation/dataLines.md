# Configuration Documentation: dataLines

A data line is just what it sounds like - it is a line of data displayed on a plot provided by Watchr.  A data line must be driven by two [**extractors**](extractor.html), one for the X data and one for the Y data.

## Properties

* **name** : *string* : The name of the data line (distinct from the name of the plot containing the line).
* **color** : *RGB* : The color of the data line.  The color should be specified by providing the three number values for red, green, and blue.

## Child Elements

- [**X extractor**](extractor.html)
- [**Y extractor**](extractor.html)
- [**derivativeLines**](derivativeLines.html)
- [**metadata**](metadata.html)

## Parent Element

- [**plot**](plot.html)