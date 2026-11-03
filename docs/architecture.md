# Architecture

## Components

### Watch applications
Small native applications designed for the Fit3's constrained display, memory, input methods and battery. Native execution and APIs must be verified against the exact target firmware and a documented SDK.

Planned modules:
- LeafNote: short notes and drafts
- LeafTask: compact task lists
- LeafRoutine: routine check-ins
- LeafSettings: device-side preferences
- LeafLink: transfer status and pairing state

### Android bridge
An Android companion component will own BLE scanning, pairing, GATT interactions, permissions, transfer retries and user-visible synchronization status. It will integrate with the existing LeafVibe Android application rather than duplicate it.

### Shared protocol
Versioned JSON messages describe notes, tasks, acknowledgements and synchronization state. Payloads must be validated and bounded before storage or transfer.

### LeafCore integration
LeafCore remains the higher-level automation and organization engine. LeafWatch should communicate through a documented bridge contract, not depend directly on LeafCore internals.

## Offline-first behavior

- The watch stores drafts locally where supported.
- Each item receives a stable ID and revision.
- Transfers are queued while disconnected.
- Android acknowledges accepted items.
- Retries must be idempotent.
- Conflicts are surfaced rather than silently overwriting user content.

## Constraints

The Fit3 is not a Wear OS device. Do not assume Android APIs, APK installation, Linux access, arbitrary background services, or a general-purpose filesystem are available on the watch.
