# LeafWatch — installation package status

## Device target

- Samsung Galaxy Fit3, model SM-R390
- Firmware reported by the user: R390XXU0AZA3
- Display: 256 × 402 pixels
- Battery specification supplied by the user: 208 mAh typical
- Charging input supplied by the user: DC 5 V / 1 A

## What is ready in this repository

- LeafVibe background artwork: `watchfaces/leafvibe/assets/leafvibe-leafwatch-background.png`
- Static visual preview: `watchfaces/leafvibe/assets/leafvibe-leafwatch-preview.png`
- Build and installation instructions: `watchfaces/leafvibe/IMPORT_EXPORT.md`

## Important: not yet an installable package

The repository does **not** currently contain a validated `.bin` watch-face file or an editable `.r390face.zip` project. The PNG files are artwork only. They cannot be copied to the Fit3 and expected to become a watch face.

A valid package must be assembled and exported with R390 Studio, then checked on the actual SM-R390. The project will only be called "ready to install" after both the editable ZIP and the exported BIN have been generated and checked. Never rename a PNG to .bin or install a file built for another model.

## Safe next steps

1. Download the background PNG from this repository.
2. Open R390 Studio on a computer or supported browser.
3. Create a Fit3-compatible face, import the PNG as its background, and add native/system time and date widgets.
4. Export both the editable `.r390face.zip` and installable `.bin`.
5. Check the BIN validation and size (less than 4 MiB).
6. Transfer it using one supported Fit3 installation workflow, following that tool's current instructions.
7. Confirm the face and live time/date on the physical watch. Then disconnect Bluetooth briefly and confirm the face remains functional offline.

## Safety boundary

This first milestone must not flash firmware, patch Bluetooth services, unlock boot components, or install experimental native applications. The reported firmware R390XXU0AZA3 is a useful compatibility reference, not permission to modify firmware. Any later firmware-level work requires a verified stock backup/recovery path and the user's explicit approval.

## Later application milestone

A watch face is not an application launcher. LeafNote, LeafTask, LeafRoutine, LeafLink and LeafSettings require a separate native-app feasibility/build/deployment milestone. No claim is made that these modules are currently installed or executable on the Fit3.
