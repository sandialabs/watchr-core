# Configuration Documentation: filter

The "filter" section of a Watchr config file provides a convenient way to selectively filter "bad" points that have already entered the database of previously-parsed data files.

A "filter" section may be associated with a single plot (i.e. it is nested under a specific [**plot**](plot.html)), or it may be associated with all plots specified by a given configuration file (i.e. it is nested under the [**"plots"**](plots.html) section).

## Properties

A filter section contains two JSON arrays - "x" and "y", for filtering x and y data points respectively.  For example:

	"filter": {
		"x": [ "2.0" ],
		"y": [ "-1.0" ]
	}

The previous example would filter all x data points that matched the string "2.0".  Likewise, the previous example would filter all y data points that matched the string "-1.0."

[Regular expressions](regex.html) may be used to indicate partial matches for filtered values.

## Child Elements

None.

## Parent Element

- [**plot**](plot.html) or [**plots**](plots.html) 