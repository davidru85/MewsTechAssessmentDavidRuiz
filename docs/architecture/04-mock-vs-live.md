# Mock vs. Live Data

Mock/live swapping happens inside the owning feature data module.

For any selected feature:

| | mock | live |
|---|---|---|
| Data source | `Mock<Feature>DataSource` | `Live<Feature>DataSource` |
| Repository | `<Feature>RepositoryImpl` | `<Feature>RepositoryImpl` |
| Contract | `<Feature>Repository` in `:feature:<name>:domain` | same |
| Presentation | unchanged | unchanged |

Production migration should replace or complete the live data source without
moving feature models or use cases into shared core modules.
