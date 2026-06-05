# Smart Guest Room Management

Android prototype for a smart hotel-room control app.

The project uses feature-isolated Clean Architecture. Product features must own
their own `domain`, `data`, and `presentation` modules. Shared `core` modules
are technical only.

Current implementation status:

```text
:app
:core:common
:core:ui
```

No product feature has been implemented yet. When a feature is selected, it
should follow this shape:

```text
:feature:<name>:domain
:feature:<name>:data
:feature:<name>:presentation
```

Architecture references:

- [Project structure blueprint](project-structure-blueprint.md)
- [Architecture specification](specifications/spec-04-android-architecture.md)
- [Delivery strategy](specifications/spec-05-delivery-strategy.md)
- [Feature-isolated architecture notes](docs/architecture/01-feature-isolated-clean-architecture.md)

Process references:

- [Branching & pull request strategy](docs/development/branching-and-pull-requests.md)

