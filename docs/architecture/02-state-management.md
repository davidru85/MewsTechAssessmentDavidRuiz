# State Management

- Stable screen state uses `StateFlow<UiState>`.
- One-shot events use `SharedFlow<Event>` with `replay = 0`.
- ViewModels expose immutable flows and public intent functions.
- Domain use cases return domain values or `DataResult<T>`.
- Feature data sources expose streams where the underlying capability is
  stateful, such as door-lock state or access audit history.

Example shape:

```text
FeatureScreen
  collects FeatureUiState
  calls FeatureViewModel.onUserIntent()

FeatureViewModel
  observes feature-owned use cases
  calls feature-owned action use cases
```
