# Navigation

`:app` owns the root `NavHost`.

Feature presentation modules expose small navigation entry points, such as:

```kotlin
fun NavGraphBuilder.featureGraph()
```

Feature modules must not navigate directly to other feature modules. Cross
feature journeys are coordinated by `:app`.
