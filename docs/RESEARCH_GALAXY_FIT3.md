# Galaxy Fit3 research and LeafWatch technical direction

**Research snapshot:** 2026-11-03  
**Target:** Samsung Galaxy Fit3, model SM-R390

This document records a research-based direction for LeafWatch. It does not claim that every capability is available on every Fit3 firmware. The exact firmware on the physical watch must be read from the watch itself before any device-side experiment.

## 1. Executive findings

The Galaxy Fit3 is not a Wear OS watch. Samsung specifications identify FreeRTOS, a 1.6-inch 256x402 AMOLED display, Bluetooth 5.3, 16 MB memory, 256 MB storage and sensors including accelerometer, gyroscope, optical heart-rate and barometer. Samsung lists phone-notification viewing and preset replies as companion features; it does not publish a general third-party Wear OS application platform.

Consequences for LeafWatch:
- The primary product must be a watch-native application/runtime, not an Android notification wrapper.
- Android is a companion for provisioning, Bluetooth transport, backup and optional configuration. It must not be the only place where LeafWatch messages exist or are displayed.
- Watch-side code must be small, event-driven and power-aware.
- Community native-app loaders exist, but install and execution paths remain firmware-specific and experimental.
- Keep stock-firmware BLE experiments and custom-firmware/native-app experiments as separate tracks.

## 2. Device facts and compatibility gate

| Item | Research baseline | LeafWatch rule |
|---|---|---|
| Model | SM-R390 | Reject unknown models until separately tested |
| OS | FreeRTOS | Do not use Wear OS APIs or APK assumptions |
| Display | 256x402 AMOLED | Design for portrait 256x402 and large touch targets |
| Memory | 16 MB RAM; 256 MB storage (published specs) | Bounded buffers, compact records and storage quotas |
| Radio | Bluetooth 5.3 | BLE first; proprietary channels need validation |
| Sensors | Accelerometer, gyroscope, optical HR, barometer, light sensor | Sensor access remains unconfirmed until API/firmware test |
| Battery | 208 mAh published typical capacity | Avoid continuous radio, polling and unnecessary screen wakeups |

**Hard compatibility gate:** record the exact software/firmware string from Settings > About > Software information, region, hardware model and companion plugin version. Do not infer firmware from a previous report or another developer's watch. Fit3 Store explicitly limits its experiments to R390XXU0AZA3 and warns that mismatched packages can cause reboot loops.

## 3. Community projects worth learning from

### Fit3 Toolkit — native apps and firmware research
https://github.com/yksen/fit3-toolkit

Research value: native Fit3 app and launcher work; C/ARM toolchain direction; firmware packaging and transfer research; examples of extending a constrained FreeRTOS device.

LeafWatch use: study app entry points, UI/event loop, linker/build scripts and memory constraints. Isolate only minimum native-app techniques. Do not reuse firmware patches or flashing steps until exact compatibility and recovery are independently verified.

Status: unofficial community research. Treat firmware modifications and unverified install mechanisms as experimental.

### Fit3 Store — .f3a SDK and sample applications
https://github.com/Musicalista/fit3-store-apps-games

Research value: C SDK headers (f3app.h, f3game.h), linker script, .f3a application format, packaging tool, sample apps/games, app catalog/installer and on-watch recovery UI concepts.

Critical limitation stated by upstream: the project reports PC simulator/mock-watch testing; Bluetooth transfer from phone and execution of RAM-loaded apps were not yet verified on a real watch. It also specifies a firmware-specific target. Therefore .f3a is a research candidate, not yet a LeafWatch production package format.

LeafWatch use: inspect SDK and format docs; create a separate compatibility spike and harmless hello app; do not build core data integrity around an unverified loader.

### FitFace Studio — binary editing and Bluetooth workflow
https://github.com/satvikgosai/fitface-studio

Research value: Kotlin/Android companion architecture, Bluetooth discovery and transfer sequencing, local project storage and import/export, binary container validation and size limits, explicit user checklist and transfer-result verification.

LeafWatch use: borrow the workflow pattern: discover -> validate -> release conflicting companion channel if needed -> transfer -> verify on watch -> reconnect. Use project export/import and preflight validation in a future LeafWatch Manager. Watch-face editing is not proof of a general message or app transport.

The repository page identifies MIT licensing; third-party SDKs and face assets retain their own terms. Review NOTICE and exact dependencies before reuse.

### Fit3Free — standard BLE heart-rate profile and GATT observations
https://github.com/Astroisback/Fit3Free

Research value: documented standard Heart Rate service 0x180D, Heart Rate Measurement characteristic 0x2A37 with notifications, Device Information and Generic Access observations, practical BLE scan/dump/listen tools, observations about proprietary services and pairing/authentication boundaries.

The README says HR notifications were observed without bonding, while useful proprietary payloads are gated. It also reports no standard Battery Service 0x180F or Current Time Service 0x1805 in its capture. These observations apply to the firmware tested by that project, not automatically to every Fit3 firmware.

Privacy note: a standard heart-rate service can expose sensitive health readings to nearby BLE clients if advertised without authentication. LeafWatch must not scan or collect health data unnecessarily, and must not forward it by default.

The repository currently states that it has no licence. Treat it as a study/reference, not code to copy or redistribute, unless the author grants permission.

### Galaxy Fit3 watch-face parser
https://github.com/Ahmadjerj/galaxy-fit3-parser

Research value: reverse-engineered binary container inspection, extracting image assets and reconstructing previews, understanding display widgets, fonts and styles.

Status: alpha/guessed fields are explicitly noted by the author. Use only as a format-research aid, not as a trusted production parser.

## 4. Architecture recommended for LeafWatch

Keep three independently testable layers.

### A. Watch-native runtime (the product)
Responsibilities: home screen and compact app launcher; local inbox/messages; notes and task summaries; reminder display and acknowledgement; small local database or append-only journal; haptic feedback where supported; local settings and diagnostics; message deduplication and acknowledgement.

Implementation direction:
- C for the native Fit3 target, subject to a verified SDK.
- No dynamic allocation in hot paths where avoidable.
- Fixed maximum message size and fixed queue capacity.
- No network dependency.
- No sensitive data until storage and transport protections are reviewed.

### B. Shared protocol and data model
Responsibilities: versioned envelope; stable message IDs; timestamps with explicit timezone/UTC semantics; type, priority, expiry, payload length and checksum; acknowledgement and deduplication; schema migration and compatibility rules.

Suggested envelope fields (not final wire format):
- protocol: leafwatch.transfer.v1
- message_id: random unique identifier
- created_at: UTC timestamp
- expires_at: optional UTC timestamp
- kind: test, note, task, reminder, status
- priority: bounded enum
- payload: small typed object
- checksum: integrity check over canonical bytes

Transport success must mean a matching acknowledgement from the watch, not merely a successful BLE write.

### C. Android companion bridge
Responsibilities: discover and connect to the watch; queue outbound data durably; send in small chunks with sequence numbers; receive acknowledgements; retry with backoff; expose diagnostics and manual export/backup; optionally import data from LeafVibe modules.

Android should be replaceable: the watch must retain its own inbox and settings when the phone is absent. Do not use Android notifications as the primary LeafWatch UI.

## 5. Bluetooth research and safe protocol plan

Use two distinct Bluetooth tracks.

**Track 1 — stock firmware / documented BLE**
- Start with passive service discovery and read-only tests.
- Verify the standard Heart Rate service only if the user explicitly enables a health-data feature.
- Capture the complete GATT tree on the user's exact firmware.
- Record UUID, properties, permissions, bonding requirement, MTU, connection interval, notification behavior and disconnect behavior.
- Never write to proprietary characteristics merely because they appear writable.

**Track 2 — native app / custom loader**
- Determine whether the native app runtime can receive a message from a companion app.
- Verify how the loader is installed, whether code executes from RAM or persistent storage, and how it is removed.
- Use a test-only payload and known-good stock recovery route.
- Keep this track isolated from normal user data until repeatable.

**Transport design after proof:** framed chunks containing protocol version, transfer ID, sequence number, total chunks, payload length and CRC. Receiver writes to a temporary record, validates all chunks and checksum, commits atomically, then acknowledges the exact message ID. Duplicate transfer IDs must be idempotent.

Do not invent service UUIDs, authentication keys, encryption handshakes, battery characteristics or direct-notification endpoints.

## 6. Offline storage and synchronization

Watch:
- retain a small rolling inbox and essential settings;
- keep a bounded pending-outbox queue;
- persist before acknowledging receipt;
- protect against power loss with journal/temporary-write/commit semantics;
- expose storage usage and allow explicit deletion/export.

Android:
- SQLite/Room private database for durable queue;
- pending -> sending -> acknowledged states;
- retry with exponential backoff and jitter;
- stable message IDs across retries;
- explicit conflict policy for edits made on both devices.

PC:
- optional backup/import/export tool;
- no always-on PC requirement for basic watch use;
- encrypted backups for private records.

Initial limits should be conservative and configurable only after measuring actual RAM, storage and BLE throughput.

## 7. Security and privacy requirements

Before private notes/documents are enabled:
- authenticated pairing/identity for the LeafWatch application protocol;
- modern authenticated encryption (AEAD) with per-device keys and key rotation strategy;
- no hard-coded shared secret in the APK or watch binary;
- replay protection, sequence numbers and expiry;
- strict payload-size limits and schema validation;
- encrypted Android database/backups where feasible;
- no health-data collection by default;
- redact message content from logs;
- never transfer Excel/RTF or other private documents in the first milestone.

A checksum detects accidental corruption; it is not encryption or authentication.

## 8. UI and interaction design

The 256x402 portrait display demands short, glanceable screens.

Recommended first-level screens:
1. Home — time, connection state, pending count, battery only if verified.
2. Inbox — newest messages, unread marker, priority icon.
3. Message — title, compact body, received time, acknowledge/delete.
4. Tasks — today's next items and completion toggle.
5. Notes — short text snippets only in first release.
6. Sync — connection, queue count, last successful sync, retry.
7. Settings — vibration, display timeout, language, privacy.
8. Diagnostics — firmware compatibility, app version, storage, last error.

Interaction rules: large touch targets; no long paragraphs; clear back/home behavior; avoid hidden swipe-only controls; optional haptics; safe home state; dark UI suited to AMOLED.

## 9. Reliability, testing and recovery

Host-side tests before hardware integration:
- schema validation and version negotiation;
- duplicate message delivery;
- corrupted/truncated chunks;
- missing/out-of-order chunks;
- disconnect mid-transfer;
- reboot with queued messages;
- expired messages;
- clock drift;
- full storage;
- malformed/oversized payload;
- duplicate acknowledgement;
- Android process death.

Hardware test matrix:
- exact firmware and model;
- stock companion connected vs disconnected;
- Bluetooth reconnect and range loss;
- screen off/on;
- low battery;
- repeated app launch/exit;
- phone offline with Bluetooth enabled;
- phone absent;
- app crash and recovery.

Recovery:
- preserve stock firmware package and verified restoration procedure before any flashing;
- document exact steps and known failure modes;
- do not use experimental Bluetooth patches or boot modifications on the user's daily watch;
- never test a firmware package on the only working watch before recovery has been demonstrated.

## 10. Proposed roadmap

| Phase | Deliverable | Acceptance gate |
|---|---|---|
| 0 — Inventory | Exact model/firmware/plugin record; upstream licence register | Firmware string confirmed on physical watch |
| 1 — Host simulation | Shared schema, inbox/outbox simulator, unit tests | Corruption/retry/dedup tests pass |
| 2 — BLE observation | Read-only GATT capture on exact firmware | No unverified writes; observations reproducible |
| 3 — Native SDK spike | Tiny watch-native hello/status app | Built, installed and run on physical Fit3 with safe rollback |
| 4 — Watch inbox | Native screen showing locally stored test message | Message remains after phone disconnect/reboot |
| 5 — Companion transport | Chunked authenticated transfer and ACK | Offline-first end-to-end test passes |
| 6 — LeafVibe modules | Tasks, reminders, short notes | Schema/version and privacy review passed |
| 7 — Hardening | Power, storage, crash and recovery tests | Repeatable soak tests and documented recovery |

## 11. Immediate next actions for this repository

1. Confirm the actual firmware string on the watch; do not assume the earlier R390XXU0AZA3 report is still correct.
2. Add a third-party source/licence register and pin upstream commit hashes before borrowing code.
3. Create a host-side protocol simulator and tests; this can progress without touching the watch.
4. Inspect the Fit3 Toolkit and Fit3 Store SDK/build scripts, but do not import firmware images or flashing code into LeafWatch.
5. Build a minimal native-app proof only after compatibility and recovery are established.
6. Keep the existing Android outbox as a companion component, but remove the local Android notification test from the definition of "LeafWatch message delivery to the watch".

## 12. References

- Samsung Galaxy Fit3 specifications: https://www.samsung.com/fr/watches/galaxy-fit/galaxy-fit3-gray-bluetooth-sm-r390nzaaxef/
- Samsung support features/specifications: https://www.samsung.com/us/support/answer/ANS10004550/
- Fit3 Toolkit: https://github.com/yksen/fit3-toolkit
- Fit3 Store / .f3a SDK: https://github.com/Musicalista/fit3-store-apps-games
- FitFace Studio: https://github.com/satvikgosai/fitface-studio
- Fit3Free BLE research: https://github.com/Astroisback/Fit3Free
- Galaxy Fit3 parser: https://github.com/Ahmadjerj/galaxy-fit3-parser

**Status vocabulary:** "documented" means stated in a cited source; "observed" means an upstream project reports a capture/test; "experimental" means not validated on the user's physical watch; "verified by LeafWatch" is reserved for a reproducible test performed by this project on the target hardware.
