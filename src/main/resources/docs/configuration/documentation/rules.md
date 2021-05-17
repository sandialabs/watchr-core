# Configuration Documentation: rules

Rules refer to pairs of conditional statements and actions that can be applied to specific Watchr graphs when their data changes.  Whenever a new data point is added to a graph, each rule applied to that graph is re-evaluated.  If a rule is found to be true, its action will be applied.

For more information about what text specifically can be used to create rules, refer to the [Rule Expression Glossary.](ruleExpressionGlossary.html)

## Properties

- **condition** : *string* : The condition statement for the rule being evaluated as true.
- **action** : *string* : The action that the rule should apply if the condition is evaluated as true.

## Child Elements

None.

## Parent Element

- [**plot**](plot.html)