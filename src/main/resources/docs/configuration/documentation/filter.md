# Configuration Documentation: filter

The "filter" section of a Watchr config file provides a convenient way to selectively filter "bad" points that have already entered the database of previously-parsed data files.

## Properties

A filter section contains two JSON arrays - "x" and "y", for filtering x and y data points respectively.  For example:

	"filter": {
		"x": [ "2.0" ],
		"y": [ "-1.0" ]
	}

The previous example would filter all x data points that matched the string "2.0".  Likewise, the previous example would filter all y data points that matched the string "-1.0."

Simple wildcards (i.e. asterisks) are observed for the specific values in the JSON arrays.

## Child Elements

None.

## Parent Element

- [**plot**](plot.html)