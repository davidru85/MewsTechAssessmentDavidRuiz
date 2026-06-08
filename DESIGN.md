# Design Specification: Serene Stay

> **Source of Truth:** This document defines the high-fidelity UI design for the Serene Stay guest room control app. It serves as the canonical reference for the Android implementation.

## Design Principles

1. **Atmospheric Depth:** Use subtle gradients, soft shadows, and layered surfaces to create a premium, "living" room feel.
2. **Tactile Precision:** Interactive controls (sliders, switches, chips) must have clear, high-contrast active states and intuitive affordances.
3. **Information Elegance:** Content is organized into clear cards with a strong hierarchy, making room state glanceable and control effortless.
4. **Perfect Symmetry:** Light and Dark modes are functionally and structurally identical; only the color mapping changes.
5. **Brand Continuity:** The teal brand accent (#006A6A / #54D7D4) is the primary thread throughout both themes.

---

## Design System: "Serene Stay"

### Typography
- **Primary Font:** Manrope
- **Headlines:** Bold, tracking -0.02em (e.g., Suite 402, Temperature display).
- **Subheaders:** Semi-bold, tracking +0.01em (e.g., Climate Control, Lighting).
- **Body & Labels:** Medium weight, optimized for legibility (e.g., Main Lights, Target temp).

### Color Palette

| Role | Light Mode | Dark Mode | Intent |
|---|---|---|---|
| **Primary** | `#005050` | `#54D7D4` | Active states, brand accents, sliders |
| **Surface** | `#F8FAF9` | `#101414` | Main app background |
| **Surface-Container** | `#FFFFFF` | `#1A1F1F` | Section card backgrounds |
| **On-Surface** | `#191C1C` | `#E1E3E3` | Primary text and headings |
| **On-Surface-Variant** | `#404948` | `#BFC8C7` | Secondary text, icons, inactive states |
| **Outline-Variant** | `#DAE5E3` | `#3F4948` | Dividers and subtle borders |

### Shapes & Spacing
- **Corner Radii:** 24dp for Section Cards, 12dp for internal buttons/chips, 8dp for small affordances.
- **Outer Margins:** 16dp horizontal padding for the main container.
- **Card Spacing:** 16dp vertical gap between sections.
- **Inner Padding:** 20dp padding inside section cards.

---

## Layout & Components

### 1. Top App Bar
- **Leading:** Brand Logo/Icon and "Suite 402".
- **Status:** "IN SYNC" pill with a pulsing green indicator.
- **Trailing:** Notifications (bell) and Connection Strength icons.

### 2. Climate Control (Section)
- **Climate Dial:** A premium circular interactive dial interface replacing the horizontal slider (16–28°C range).
- **Primary Display:** Centered inside the dial, showing the current/inside temperature (e.g., 23°C) and the target temperature (e.g., "Target 21°C").
- **Track & Sweep:** The circular track represents the active progress to target. In Light Mode, it sweeps from 12 o'clock clockwise. In Dark Mode, it sweeps similarly with a modern glow accent.
- **Action Buttons:** Large 50/50 split buttons for "Auto Fan" and "Cooling" (High-fill primary color when active).

### 3. Atmosphere / Moods (Section)
- **Control:** Segmented chip row.
- **Items:** Auto (Sparkle icon), Day (Sun icon), Night (Moon icon).
- **Active State:** Solid fill with contrasting icon/text color.

### 4. Lighting (Section)
- **Items:** Ceiling, Bedside, Bathroom.
- **Layout:** Icon (left), Label (center), Switch (right).
- **Detail:** Subtle dividers between rows; consistent 48dp touch targets.
- **Faulty device state:** a row whose device is unreachable shows a small warning icon (error tint) immediately left of the switch, and its switch is **disabled** (non-interactive). No caption text. (Mock: the Bathroom light is the deliberate fault.)

### 5. Privacy & View (Section)
- **Control:** Segmented tab control (Open / Half / Closed).
- **Visual Reference:** High-quality image of the "Sunrise Vista" room view, integrated directly into the card with rounded corners and centered text overlay.

### 6. Bottom Navigation
- **Destinations:** Room (Active), Services, Keys.
- **Styling:** Pill-shaped active indicator; semantic icons with clear labels.
- **Persistence:** the bar is an app-level shell shared by all destinations; the active item follows the current route.

### 7. Placeholder Screens (Services / Keys)
- **Purpose:** lightweight "Coming soon" destinations for tabs that are not yet built.
- **Layout:** centered tab icon (RoomService / VpnKey), the tab title, and a "Coming soon" caption, on the standard background.
- **Navigation:** reached from the bottom nav; the bar persists with the active tab highlighted.

---

## Technical Constraints
- **Android Implementation:** Use Jetpack Compose with `MaterialTheme` mapping the tokens above.
- **State Management:** Reflect "Sync" status in real-time; animate slider thumb position and chip state transitions.
- **Asset Preservation:** "Sunrise Vista" image must be used verbatim across all themes.