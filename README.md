# LeafWatch

**LeafWatch** is an independent open-development project exploring a custom software environment and companion applications for the Samsung Galaxy Fit3 (SM-R390).

The project aims to:
- investigate documented native-development possibilities for the Fit3;
- build original, lightweight watch applications;
- create a Bluetooth companion bridge for Android;
- support offline-first notes, tasks and synchronization;
- preserve the ability to return to the original watch software.

> **Status: early research and architecture stage.** No custom firmware is provided here. Hardware compatibility, boot/recovery procedures and native application deployment must be validated before any device modification.

## Target device

| Item | Current target |
|---|---|
| Device | Samsung Galaxy Fit3 |
| Model | SM-R390 |
| Reported firmware | `R390XXU0AZA3` |
| Region | France / Europe |
| Firmware verification | Pending confirmation from the watch's software-information screen |

## Project layout

- `docs/` — architecture, compatibility, security and recovery notes
- `watch/` — original watch-side application code and SDK integration
- `bridge/` — Android/Bluetooth bridge design
- `shared/` — protocol and data schemas shared by devices
- `simulator/` — host-side development and UI simulation
- `tests/` — automated tests
- `tools/` — validation and build helpers

## Safety

Do not flash firmware, unlock boot components, or install unverified binaries based only on this repository. First establish an exact firmware match, a verified recovery route, and a repeatable test procedure. A project or SDK that targets one firmware revision may not work on another.

## Development principles

1. Keep original firmware untouched during initial development.
2. Prefer documented SDK interfaces over reverse-engineered internals.
3. Keep watch-side features small and power-aware.
4. Store unsent data locally and synchronize when a trusted connection returns.
5. Use explicit versioning and schema validation for every exchanged document.
6. Never commit credentials, personal documents, device identifiers or proprietary firmware dumps.

## Initial milestones

- [ ] Confirm firmware identifier from the watch itself.
- [ ] Review third-party SDK and project licences.
- [ ] Build a desktop simulator and protocol tests.
- [ ] Prototype the shared data format.
- [ ] Implement an Android BLE proof of concept.
- [ ] Validate one minimal native watch application.
- [ ] Design a tested recovery procedure before firmware experiments.

## Licence

Original source code in this repository is released under the MIT License. Third-party components retain their own licences; see `NOTICE.md` and the relevant upstream projects. This licence does not grant rights to Samsung firmware, trademarks, or proprietary SDK material.
