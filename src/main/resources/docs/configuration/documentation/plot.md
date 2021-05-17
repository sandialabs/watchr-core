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