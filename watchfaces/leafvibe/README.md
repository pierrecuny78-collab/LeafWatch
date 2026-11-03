# LeafVibe / LeafWatch — first Fit3 watchface

**Goal:** get a real LeafVibe identity onto the user's Galaxy Fit3 tonight without flashing firmware or installing experimental native code.

## What this milestone is

This is a **custom watch-face milestone**, not yet the full native LeafWatch application. The face is built from a local background image plus native/system watch widgets (time, date, and optionally battery/steps). Once installed, the face is stored on the watch and its clock/date continue to work offline.

The visual concept is a dark AMOLED background with a green LeafVibe leaf mark, a central area reserved for the system clock/date, and a small local-ready indicator. The sample preview uses a fixed example time only; it is not live watch data.

## Files

- assets/leafvibe-leafwatch-background.png — 256×402 background intended for R390 Studio.
- assets/leafvibe-leafwatch-preview.png — static design preview with example clock/date; never install this preview as the watch face.
- IMPORT_EXPORT.md — build, export, import and install procedure.

## Important distinction

- **PNG background:** a visual layer, not a watch face by itself.
- **R390 Studio BIN export:** the installable watch-face file.
- **R390 Studio .r390face.zip:** editable source project with layers and embedded assets; use it to reopen/edit the design.
- **BIN on the watch:** the installed face; time/date are rendered by native watch widgets, so no Internet is required during normal use.

Do not replace native time/date widgets with text baked into the PNG. That would freeze the displayed time.

## Compatibility and safety

The watch-face route does not require custom firmware, a bootloader patch, or an experimental app loader. It is the safest practical way to get a real LeafVibe visual onto the current Fit3 while native LeafWatch app research continues.

The exported BIN must pass R390 Studio validation and remain below the Fit3's 4 MiB face-container limit. Always check the preview and the watch itself; a desktop preview is not proof of live widget behavior.

## Current state

- [x] 256×402 LeafVibe background asset created.
- [x] Static preview created.
- [x] Import/export workflow documented.
- [ ] Assemble the editable R390 Studio project on the user's device.
- [ ] Export and validate the BIN.
- [ ] Install it on the physical Fit3 and confirm time/date.
- [ ] Capture a real photo of the installed face.
