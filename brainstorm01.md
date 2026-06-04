### 1. The Problem: In-Room Friction and Energy Waste

**The goal here is to show that you're solving a dual problem: the user's (UX) and the business's (ROI).**

* **For the guest (User Pain):** Hotel rooms typically have confusing HVAC controls and unintuitive light switches. Often, the room key card is left in the slot simply to keep devices charging, leaving lights on unnecessarily.
* **For the hotel (Business Pain):** HVAC and electricity spend in empty rooms is one of the largest operational costs.
* **The current opportunity:** Hotels are already installing Bluetooth locks and sensor networks (Zigbee/Matter), but interaction is limited to maintenance staff. Exposing simplified local control directly to the guest's phone democratizes that infrastructure.

### 2. The Discovery and Validation Process

**Here you show that you don't start coding blindly, but validate the impact first.**

* **Quantitative Signals:** Analyze the hotel's electricity consumption curve (kWh) cross-referenced with check-in/check-out data from presence sensors or door locks.
* **Qualitative Signals:** Interview the maintenance team (how often do they have to fix/explain a thermostat?) and read online reviews looking for mentions of room temperature or comfort.
* **Lightweight Experiment (Fake Door Test):** Place a QR code (or an NFC tag) on the nightstand of 10 pilot rooms reading: *"Control your room from your phone."* When scanned, the user lands on a web page indicating the service is "in beta" and logs the intent to use it. If the scan rate is high, you have validation at zero development cost.

### 3. The Android Prototype: Craft and Architecture

**This is your home turf. You need to demonstrate technical excellence in Kotlin, but focused on prototyping speed.**

* **Technical Approach:** You'll build the app simulating a local control ecosystem. Instead of traditional REST calls, the data layer will simulate subscribing to a messaging broker (MQTT-style), emitting real-time state updates.
* **Architecture (Clean + MVVM):**
* **Data:** A repository using `StateFlow` or `SharedFlow` with Kotlin Coroutines to emit simulated changes (e.g., the ambient temperature gradually drops while the AC is on).
* **Domain:** Clear use cases (`ToggleLightUseCase`, `SetTemperatureUseCase`).
* **UI (Jetpack Compose):** Reactive screens that observe state.


* **The Core Flow to showcase:** A main screen (Dashboard) with the overall room state, quick toggles for energy modes (e.g., "Sleep Mode" turns off lights and lowers the blinds), and a dial for the thermostat.

### 4. Delivery Strategy: MVP vs. Production

**Demonstrate pragmatism. How you balance delivery speed with future maintainability.**

* **Speed (For the Prototype):** All state lives in memory on the device. Zero backend infrastructure. Dependency injection configured to inject mocks.
* **Robustness (For Production):** Thanks to the layered separation (Clean Architecture), scaling to production simply means creating a new repository implementation in the data layer (to connect to the hotel's backend or gateway) and injecting it. The UI and business logic remain untouched.

### 5. Adoption Plan and Metrics

**A brilliant app is worthless if no one uses it.**

* **Adoption:** The key moment is *Check-in*. The strategy is **zero friction**. When the user connects to the hotel Wi-Fi (Captive Portal), they're prompted to open/download the app. Alternatively, the physical room card can include an NFC chip for instant pairing.
* **Success Metrics:**
* *Engagement:* % of guests who use the app at least once a day.
* *Business:* Estimated kWh reduction per automated room vs. a standard room.



### 6. Working Without a PM or Designer

**How you maintain focus and visual quality without support.**

* **Focus and Prioritization:** Use an ICE matrix (Impact, Confidence, Ease) or MoSCoW. If a feature (such as adding TV control) has low impact on energy savings and high technical complexity, it's dropped from the MVP.
* **UX/UI Quality:** Rely strictly on **Material Design 3** components and guidelines. Use automatic tonal color palettes, ensure native *Dark Mode* support, and prioritize accessibility (touch target sizes, contrast) instead of trying to invent a complex visual design from scratch.

### 7. Use of AI Tools (AI-First Development)

**This directly addresses one of the key requirements of the assessment.**

* Document how you'll use AI for this project:
* Rapid generation of the entire folder structure and architecture boilerplate.
* Creation of mock data scripts (e.g., JSONs simulating complex room configurations).
* Assistance in designing specific UI components in Jetpack Compose by passing it functional descriptions.