# GLOSSARY.md

> Shared vocabulary for the project — hospitality domain, technical architecture, and product/process terms. Keeps docs and code unambiguous.

---

## Hospitality & Domain

| Term | Meaning |
|---|---|
| **PMS** | Property Management System — the hotel's core software for reservations, check-in/out, room status. Source of reservation windows. |
| **BMS** | Building Management System — controls/monitors HVAC, lighting, energy at the building level. |
| **HVAC** | Heating, Ventilation, Air Conditioning — the room climate system; a primary energy cost driver. |
| **OPEX / CAPEX** | Operating vs. Capital expenditure. Energy is OPEX; IoT hardware is CAPEX. |
| **NOI** | Net Operating Income — hotel profitability metric energy savings improve. |
| **Keyless entry** | Unlocking the room with the guest's phone (BLE/NFC) instead of a key card. |
| **Captive portal** | The Wi-Fi login splash page; used here as a zero-friction app-onboarding entry point. |
| **Fake-door test** | Validation experiment offering a not-yet-built feature to measure real demand at near-zero cost. |
| **Wizard-of-Oz** | Experiment where humans manually fulfil what will later be automated, to test desirability. |
| **Control room** | A non-automated room used as the baseline in A/B energy/satisfaction comparisons. |

---

## IoT & Connectivity

| Term | Meaning |
|---|---|
| **Matter** | Modern smart-home interoperability standard for in-room devices. |
| **Zigbee** | Low-power mesh protocol common for lights/sensors/blinds. |
| **BLE** | Bluetooth Low Energy — used for phone-as-key and local device control. |
| **MQTT** | Lightweight publish/subscribe messaging; how live room state would stream in production. |
| **NFC / NTAG215-216** | Near-Field Communication; tap-to-launch onboarding via a programmed room card (<$1/chip). |
| **App Links** | Verified Android deep links (`assetlinks.json`) that route a tap straight into the app. |
| **Room hub / gateway** | Device bridging in-room protocols (Matter/Zigbee/BLE) into one management plane. |

---

## Architecture & Android

| Term | Meaning |
|---|---|
| **Feature-isolated Clean Architecture** | Each product feature owns its `domain`/`data`/`presentation`; no shared business core. |
| **Vertical slice** | One feature built end-to-end through all layers, vs. a horizontal layer across features. |
| **`domain` / `data` / `presentation`** | Pure business logic / implementations + data sources / Compose UI + ViewModels. |
| **Mock vs. live** | In-memory simulated data source vs. real integration; swapped via product flavor inside the feature `data` module. |
| **Product flavor (`mock` / `live`)** | Gradle build variant selecting the data implementation; the prototype↔production seam. |
| **`StateFlow` / `SharedFlow`** | Hot streams for stable screen state / one-shot events. |
| **`DataResult<T>`** | Result wrapper in `:core:common` carrying success/failure across layers. |
| **Hilt** | Compile-time dependency injection framework used project-wide. |
| **ADR** | Architecture Decision Record — a dated note capturing a decision and its consequences ([docs/decisions/](docs/decisions/)). |
| **`api/impl`** | Optional future module split exposing a stable public contract while hiding implementation. Not used by default. |

---

## Product & Process

| Term | Meaning |
|---|---|
| **MVP** | Minimum Viable Product — the smallest real release that proves value (here: one live feature in a pilot). |
| **ICE** | Prioritisation score = avg(Impact, Confidence, Ease). |
| **MoSCoW** | Must / Should / Could / Won't scope categorisation. |
| **NPS** | Net Promoter Score — guest-satisfaction metric. |
| **DAU** | Daily Active Users — engagement metric during a stay. |
| **Adoption funnel** | Portal click-through → install → first use → daily use → feature breadth. |
| **Definition of Done** | The merge gate every feature PR must satisfy ([AGENTS.md](AGENTS.md)). |
