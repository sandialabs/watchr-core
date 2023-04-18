# Configuration Documentation: rules

Rules refer to pairings of conditions and actions, which can be used to monitor specific changes to Watchr's database across multiple runs.

Some example uses of rules:

 - Whenever a new data point is added to a graph, each rule applied to that graph is re-evaluated.  If a rule is found to be true, its action will be applied.
 - The number of new datasets since last running Watchr can be counted. If the count is found to be greater than, equal to, or less than some other number, the rule's action will be applied.

For more information about what text specifically can be used to create rules, refer to the [Rule Expression Glossary.](ruleExpressionGlossary.html)

## Properties

- **condition** : *string* : The condition statement for the rule being evaluated as true.
- **action** : *string* : The action that the rule should apply if the condition is evaluated as true.

## Child Elements

- **actionProperties** : An arbitrary collection of key-value pairs, which can be used to further configure the action that needs to be taken if the condition is true. This child block is optional for most rules in Watchr.

## Parent Element

- May be [**plot**](plot.html), but rules can appear at multiple levels.