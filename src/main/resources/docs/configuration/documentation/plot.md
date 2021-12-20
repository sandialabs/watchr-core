# Configuration Documentation: plot

The "plot" section of a Watchr config file represents the specification for one or more graphs that Watchr will create.

The "plot" section holds a JSON array, with each element representing one or more graphs.  In the simple case, one "plot" element specification results in one rendered graph.

At minimum, a single "plot" element should have information about a [data line](dataLines.html) it wishes to display.  Plots may also contain:

 - [Derivative lines](derivativeLines.html) (such as rolling averages or rolling standard deviations)
 - Legends
 - Metadata displayed in hover text

Plots may be subject to:

 - [Rules](rules.html)
 - [Point filters](filter.html)
 - [File name filters](fileFilter.html)

More complicated specifications may result in many rendered graphs (for example, setting the "getFirstMatchOnly" element to false, or setting "recurseChildGraphs" to true).

*Pro Tip*:  Watchr provides two shortcuts for handling multiple plots quickly, depending on what you want to do.

* **The "template" and "inherit" properties** may be used to define a single template plot, and then have other plots inherit all the properties of this parent template plot, overriding properties with their own settings only when necessary.  See the page on [templates](templates.html) for more information.
* **The "autoname" section** can help with automatically naming many plots if the Watchr specification for a single plot will actually result in more than one rendered graph.  See the page on [autonaming](autoname.html) for more information.

## Properties

* **name** : *string* : The name of the plot.  If using [auto-naming](autoname.html), it is not necessary to provide a name for each plot.
* **type** : *enumerated string* : The type of plot that Watchr will generate.  If left blank, Watchr will create scatter plots.
 * *Allowed types*:  areaPlot, scatterPlot, treeMap 
* **canvasLayout** : *enumerated string* : If multiple data lines are present on your graph, you can include this property to cause them to be laid out across multiple canvases, where a "canvas" describes a single set of X/Y axes.  The value for this property can be one of the following strings:
 * *shared* : This is the default option.  All lines will be drawn on the same canvas with a shared scale.
 * *independent* : All lines will be drawn overlaid on the same canvas, but each line will be drawn at a unique scale to maximize its visibility.  Any scaling information needed to differentiate the relative size of each line will be included off to the side of the canvas.
 * *grid* : Each line will have its own canvas, and the canvases will be arranged in a grid.  You can specify the length of a row in the grid with the extra property **canvasPerRow**.
 * *stackX* : Each line will have its own canvas, and the canvases will be arranged in a horizontal row.
 * *stackY* : Each line will have its own canvas, and the canvases will be arranged in a vertical column.
* **canvasPerRow** : *integer* : If "canvasLayout" is specified with a value of "grid", you can use this property to specify how many canvases should occupy one row of the grid.  This property has a default value of "1" if not specified.
* **category** : *string* : The specific category that this plot belongs to.  Categories may be used to filter or group plots together.  See the [categories](categories.html) section for more information.
* **legend** : *boolean* : Whether or not to display a legend next to the plot.
* **template** : *string* : An arbitrary label you may apply to a single plot to indicate that it is a template for other plots.  Other plots may inherit this plot's properties using the *inherit* field.  See the page on [templates](templates.html) for more information.
* **inherit** : *string* : Use this property to refer to the *template* field on another plot.  The current plot will inherit all of the properties of the template plot, but you still have the ability to override specific properties on the current plot.  See the page on [templates](templates.html) for more information.

## Child Elements

- [**dataLines**](dataLines.html)
- [**rules**](rules.html)
- [**filter**](filter.html)
- [**fileFilter**](fileFilter.html)
- [**autoname**](autoname.html)

## Parent Element

- [**plots**](plots.html)