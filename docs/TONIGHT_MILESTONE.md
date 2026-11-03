# Tonight's LeafWatch milestone — visible and offline on the Fit3

## Target result

Show a LeafVibe / LeafWatch-branded face on the user's actual Galaxy Fit3. The face must remain visible and keep its time/date when the phone is disconnected.

## Why this milestone first

There are now community reports of native .f3x applications running on a physical SM-R390, but the current Fit3 Toolkit supports only firmware R390XXU0AZA3, has a first-install factory-reset requirement, and its default Bluetooth patches leave the watch's file service open to unauthenticated nearby connections. The toolkit's own status says the first-flash path and a build without those patches are not yet verified on a physical watch.

That is too risky for a first evening milestone on the user's daily watch.

A custom watch face is the practical, non-firmware-modifying route:
- R390 Studio builds a real Fit3 watch-face BIN.
- The editor also exports/imports a portable .r390face.zip project for later editing.
- The watch renders its native time/date widgets offline after installation.
- No cloud or Internet is needed for the installed face to display.

## Deliverables in this repository

- watchfaces/leafvibe/assets/leafvibe-leafwatch-background.png
- watchfaces/leafvibe/assets/leafvibe-leafwatch-preview.png
- watchfaces/leafvibe/README.md
- watchfaces/leafvibe/IMPORT_EXPORT.md

## Definition of done

- [ ] R390 Studio project assembled and exported as .r390face.zip.
- [ ] BIN exported and validated under 4 MiB.
- [ ] BIN transferred to the physical SM-R390.
- [ ] Actual watch shows LeafVibe branding with native live time/date.
- [ ] Phone Bluetooth disconnected; face still displays and clock/date continue.
- [ ] User confirms result with a photo or description.

## Next native-app milestone

After the face milestone, resume native LeafWatch work with Fit3 Toolkit only if the exact firmware is confirmed as R390XXU0AZA3, a stock recovery route is understood, and the user explicitly accepts the factory-reset and security trade-offs. Build a harmless offline counter/inbox prototype before adding personal data or Bluetooth message transfer.

## Reference links

- R390 Studio: https://github.com/yuriyurin/r390-studio
- Fit3 Toolkit status: https://github.com/yksen/fit3-toolkit/blob/master/docs/status.md
- Fit3 Toolkit how-to and warnings: https://github.com/yksen/fit3-toolkit/blob/master/docs/howto.md
- FitFace Studio: https://github.com/satvikgosai/fitface-studio
- Fit3 App: https://github.com/yuriyurin/Fit3-App
