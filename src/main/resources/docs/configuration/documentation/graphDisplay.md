# Configuration Documentation: graphDisplay

The graphDisplay section of a Watchr config document is concerned with properties of the graphs that Watchr can render.  Unless configured otherwise, Watchr will output its graphs to a series of HTML files. 

## Properties

* **dbLocation** : *string* : The location within the Watchr database to render plots.  This property can either be:
 * Blank, in which case, Watchr will render the root of the database (i.e. any top-level plots that have no parents).  Using the reserved word "root" for location will produce the same result.
 * The name of a specific plot in your database.  This will cause any children of the given plot name to be rendered.
* **displayCategory** : *string* : The category of plots to render.  This property can be used to filter out all plots that do not belong to the given category.  If left blank, all plots will be rendered, regardless of category.
* **displayRange** : *int* : The number of horizontal points to display on a given graph.  If sorted by time, the range refers to the most recent N number of points.
* **displayedDecimalPlaces** : *int* : The number of decimal points to display for data on a given graph.
* **graphWidth** : *int* : The number of pixels used for a single graph's width.
* **graphHeight** : *int* : The number of pixels used for a single graph's height.
* **graphsPerRow** : *int* : Since Watchr lays out rendered plots in a grid, this property can be used to dictate the number of plots in a row of the grid.
* **graphsPerPage** : *int* : Watchr also has the ability to divvy graphs across multiple HTML pages, if the number of plots on a single page becomes too cumbersome.  This property dictates how many graphs will display on a single page.  

## Child Elements

None.

## Parent Element

None.