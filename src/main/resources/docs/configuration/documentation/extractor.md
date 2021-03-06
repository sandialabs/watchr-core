# Configuration Documentation: extractor

An extractor is one of the central workhorses of a Watchr config file.  The extractor is responsible for applying an approach to locating a piece of plottable information inside of a data file (provided by you) with unknown contents.  It is not required for Watchr to know anything specific about your file's contents; it only needs to have a reliable, repeatable technique for finding the relevant information inside your file.

Currently, Watchr supports extracting data out of the following file types:

- XML files
- JSON files

Sometimes, Watchr will arrive at ambiguous locations in your data file, where it's necessary to provide additional information to Watchr for determining where to go next.  In these situations, you should add a [strategy](strategy.html) section to your extractor.

## Properties

- **getPath** : *string* : Use this property to give Watchr a path through your data file to the data of interest.  Forward slashes are used to indicate segments of your path.  In addition, [regular expressions](regex.html) may be used to indicate that Watchr should either partially match a single path segment, or entirely skip the matching step of a given path segment. 
- **getPathAttribute** : *string* : This is used in conjunction with the *getPath* property to tell Watchr which *attribute* should be looked at to match the next path segement.  **This property is unique to extractors for XML files.**
- **getElement** : *string* : Use this property to restrict the *types of elements* that Watchr is allowed to traverse through your data file. **This property is unique to extractors for XML files.**
- **getKey** : *string* : Use this property to specify the "key" for the final value that will ultimately be returned by Watchr.  This could also be thought of as the terminating section of the path, although it should not be included as part of the *getPath* path.
- **unit** : *string* : The unit of your extracted value (for example, "inches").  This is useful for displaying text on the axes of the graph that will be generated by Watchr.  Units are simply arbitrary text and do not have any impact on the display of the final graphs.
- **formatAs** : *enumerated string* : Once your data has been extracted using one or more of the previous properties, you can use this property to format the retrieved data.  This property may use one of the following values:
 - *timestamp* : Interpret a number as an [**epoch timestamp**](https://www.epochconverter.com/) in seconds, and format it as a human-readable time string.
 - *timestamp_ms* : Interpret a number as an [**epoch timestamp**](https://www.epochconverter.com/) in milliseconds, and format it as a human-readable time string.

## Child Elements

- [**strategy**](strategy.html)

## Parent Element

- [**dataLines**](dataLines.html)
- [**metadata**](metadata.html)

## See Also

- [**Special JSON array syntax**](configuration/documentation/jsonArrays.html)