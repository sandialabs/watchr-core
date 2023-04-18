# Configuration Documentation: filters

The "filters" section of a Watchr config file provides a convenient, generic way to selectively filter points.

The "filters" section must be an array of elements, which means that multiple individual filters can appear next to each other in the same "filters" block.

## Properties

- **expression** : *string* : The most important field of a filter, this field specifies the expression by which points are filtered. These expressions are styled after the logical expressions seen in most programming languages. Some examples are shown below under "Filter Expression Examples."
- **type** : *enumerated string* : This field can either be "point" or "metadata". If "point", the filter expression specified will filter data points based on their x, y, or z values. If "metadata", the filter expression specified will filter data points based on associated metadata values.
- **policy** : *enumerated string* : This field can either be "whitelist" or "blacklist". If "blacklist", then data points will be removed if they match the filter expression. If "whitelist", then data points will be removed if they *don't* match the filter expression.

### Filter Expression Examples

    expression: "y == 4.0"

The previous filter expression will match on any point whose y-value is precisely equal to 4.0.

    expression: "x >= 1.0"

The previous filter expression will match on any point whose x-value is greater than or equal to 1.0.

    expression: "x == 1.0 && y < 4.0"

The expression "&&" shown above is an example of a boolean "and" operation. The previous filter expression will match on any point whose x-value is equal to 1.0 AND whose y-value is less than 4.0.

    expression: "z < 1.0 || x > 1.0"

The expression "||" shown above is an example of a boolean "or" operation. The previous filter expression will match on any point whose z-value is less than 1.0 OR whose x-value is greater than 1.0.

    type: "metadata"    
    expression: "branch == master"

The previous expression will only work for metadata filtering, as evidenced by ```type: "metadata"```. For the previous expression, any point containing metadata with a key of "branch" and a value of "master" will be matched by the filter expression.

### Scope Options

A "filters" section may be associated with:

- A single data line (i.e. it is nested under a specific [**data line**](dataLines.html))
- A single plot (i.e. it is nested under a specific [**plot**](plot.html))
- The entire configuration, also known as a "global filter" (i.e. it is nested under the [**"plots"**](plots.html) section).

### Special Notes About Combining Filters

- If multiple filters appear together in the same "filters" block in a configuration file, they are combined using OR logic.
- Multiple filters appearing at different levels of the configuration file are also combined using OR logic. That is, any filters applied to a data line will be OR'd with any filters that are applied to the entire plot, which will be OR'd with any filters that are applied at the global level. 
- If a child data line that inherits from a parent template has no filters of its own, it will inherit all of its parent's filters.
- If a child data line that inherits from a parent template has filters of its own, it will override all of its parent's filters.

## Child Elements

None.

## Parent Element

- [**data line**](dataLines.html), [**plot**](plot.html), or [**plots**](plots.html) 