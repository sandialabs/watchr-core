# Using Regular Expressions in Watchr

Certain Watchr configuration fields recognize "regular expressions."  Most users are familiar with the idea of using asterisks to indicate "wildcards", which enables a partial match of a piece of text.  An asterisk/wildcard expression is a simple example of a regular expression, but in general, regular expressions can be very complex.  

Two forms of regular expressions are recognized by Watchr.

 - **Simple**: Isolated asterisks are recognized by default, to indicate wildcards.  There is no need to use the standard regex form **.*** to indicate wildcards.
 - **Full**: A full regular expression may be used simply by placing the special token **R$** at the beginning of your expression.

Note that for full regular expressions, double-backslashes are required in place of a single backslash (see [this Stack Overflow thread](https://stackoverflow.com/questions/54478948/regular-expressions-with-double-backslash-in-java) for a more detailed explanation).

## Further Reading

[Java documentation on regular expression syntax](https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html)