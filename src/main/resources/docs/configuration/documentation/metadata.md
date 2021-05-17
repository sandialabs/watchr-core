# Configuration Documentation: metadata

Arbitrary maps of metadata can be applied to individual points on Watchr graphs.  This is particularly useful if you have secondary, non-numeric information that you would like to record and visualize along with your main data line.  Watchr displays point-specific metadata when you hover the mouse over the associated point on the resulting graph.

Similar to extracting x and y data points, you must provide an [**extractor**](extractor.html) to indicate how Watchr should extract the metadata information from your data file.

The "metadata" section of a Watchr config file is an array.  You may provide a new array element for each piece of metadata you wish to create.

*Note:* On a given data file, if multiple x/y pairings are extracted, but only one piece of metadata information is extracted, that metadata will be applied uniformly across all x/y pairings. Conversely, if multiple pieces of metadata are extracted for a single x/y pairing, and those pieces of metadata are duplicates (i.e. using the same "name" value), the point will only contain the most recently extracted metadata information, and earlier extractions will be overwritten.

## Properties

* **name** : *string* : The name of your piece of metadata.  This will be displayed to the left of the metadata value in the hover text of your graph.

## Child Elements

- [**extractor**](extractor.html)

## Parent Element

- [**dataLines**](dataLines.html)