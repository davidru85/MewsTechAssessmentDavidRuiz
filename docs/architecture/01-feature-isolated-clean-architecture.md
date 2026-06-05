# Feature-Isolated Clean Architecture

The project uses feature-isolated Clean Architecture. Each product feature owns
its own `domain`, `data`, and `presentation` modules.

```text
:app
  -> :feature:<name>:presentation
  -> :feature:<name>:data

:feature:<name>:presentation -> :feature:<name>:domain
:feature:<name>:data         -> :feature:<name>:domain
:feature:<name>:*            -> :core:common
:feature:<name>:presentation -> :core:ui
```

There is no shared `:core:domain` and no shared `:core:data`.

`core` is technical only:

- `:core:common`: results, dispatchers, clock, logging.
- `:core:ui`: generic theme and UI primitives.
- `:core:testing`: reusable test utilities.

Business concepts belong to the feature that owns them. For example, if the
Access feature is selected, `UnlockDoorUseCase`, `DoorLockState`, and
`AccessRepository` live in `:feature:access:domain`.
