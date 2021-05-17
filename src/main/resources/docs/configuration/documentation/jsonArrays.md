# Configuration Documentation: Special JSON array syntax

Unlike XML files, JSON files can contain indexed child elements.  If you wish to indicate a specific index of a JSON element to Watchr, you may use curly braces with a number inside to indicate that one or more segments of a path will be an indexed JSON element.

A few examples have been provided below.

## Examples

The following will retrieve the "time" element from the first child element in a JSON array.

	"getPath" : "{0}"
	"getKey" : "time"

The following will dive into the second element of a JSON array.  From there, it will go to the child element beginning with the text "performance."

	"getPath": "{1}/performance*"

The following will dive into the second element of a JSON array.  From there, it will dive in to the third element within the JSON structure we're already in.  Finally, it will go to the child element beginning with the text "performance."

    "getPath": "{1}/{2}/performance*"