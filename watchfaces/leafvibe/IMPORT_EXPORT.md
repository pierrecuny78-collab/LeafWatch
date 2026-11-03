# LeafVibe / LeafWatch — import, export and offline installation

This is the quickest safe route to show LeafVibe on a real Galaxy Fit3 (SM-R390) without modifying firmware.

## Tools

- R390 Studio: https://yuriyurin.github.io/r390-studio/
- R390 Studio source: https://github.com/yuriyurin/r390-studio
- FitFace Studio (Android): https://github.com/satvikgosai/fitface-studio
- Alternative companion Fit3 App (Android): https://github.com/yuriyurin/Fit3-App/releases

R390 Studio v0.6.18 documents local browser editing, ZIP project import/export and BIN export. Its ZIP contains project JSON and embedded assets, so it is portable and editable. The editor's local ZIP library is bundled; web fonts may be unavailable offline, but system fallback fonts should remain usable.

FitFace Studio and Fit3 App are independent community applications. Do not operate Fit3 App and Samsung's official Fit3 plugin as simultaneous controllers of the same band. Use one connection workflow at a time and follow the chosen app's current instructions.

## A. Prepare the design

1. Download assets/leafvibe-leafwatch-background.png from this repository to the phone or computer.
2. Open R390 Studio. For an offline session, download/clone its source repository first and open the local index.html; the app's ZIP library is bundled locally. Its optional Google Fonts import may require Internet; choose a system font if offline.
3. Create a new face/project using a supported base face or an empty panel.
4. Import the LeafVibe PNG as a background/image layer. Keep it at the 256×402 portrait ratio and position it to fill the screen.
5. Add a **native/system time widget** over the central reserved area. Use a large, high-contrast style.
6. Add a **native/system date widget** underneath. Do not use a static text layer for the clock or date.
7. Optionally add native battery/steps widgets only if the editor supports them for the selected base face. Keep the first version simple.
8. Check the preview, layer order, AOD separately, and estimated BIN size. Keep the BIN under 4 MiB.

The included preview PNG is only a visual reference. It has a sample time and must not be used as the installed face.

## B. Save an editable offline project

1. In R390 Studio, press **ZIP** to export a .r390face.zip project.
2. Save it somewhere local (phone storage, computer or USB drive).
3. To continue later or on another device, open R390 Studio and use **Open** to import that ZIP.
4. Keep this ZIP as the editable source. It contains project JSON and embedded image assets.

The R390 Studio ZIP is not the installable watch-face BIN. Keep both files.

## C. Export the installable face

1. Use R390 Studio's **BIN export** action.
2. Save the resulting .bin file.
3. Confirm the size is below 4 MiB and that the editor reports no validation errors.
4. Keep the BIN next to the editable ZIP. The ZIP is the project; the BIN is the compiled watch face.

## D. Install on the Fit3

Preferred route: use FitFace Studio's documented local-file install workflow or Fit3 App's **Band → Installed** flow.

- Fit3 App README says to add/import a BIN, then tap the face to install it. It also warns not to use Fit3 App and the official plugin to control the same watch at the same time.
- FitFace Studio documents BLE discovery while the stock plugin is connected, then releasing the plugin's Bluetooth channel before transfer. Follow its in-app checklist exactly; "request sent" is not proof of successful installation—check the watch display.
- If a transfer fails, do not repeatedly send blindly. Reconnect, rediscover and check the watch-face list.

No firmware flashing is required for this watch-face path.

## E. Verify offline behavior

After the face is installed:

1. Disconnect the phone Bluetooth temporarily (do not reset or unpair the watch).
2. Wake the Fit3 and verify the face still appears.
3. Verify that the time and date continue updating on the watch.
4. Reconnect the phone afterward.

A successful result is: LeafVibe face remains visible with live system time/date while the phone is disconnected. This proves the **watch-face milestone**, not yet a native LeafWatch inbox or message receiver.

## Safety and limitations

- Do not flash firmware for this milestone.
- Do not use Fit3 Toolkit's native-app loader as part of this first display test.
- Do not install a BIN intended for another model.
- Preserve the original face and know how to select it again.
- Do not include private notes, documents or personal data in the face.
- A successful BLE transfer request must be confirmed on the watch itself.

## Sources

- R390 Studio: https://github.com/yuriyurin/r390-studio
- FitFace Studio installation instructions: https://github.com/satvikgosai/fitface-studio
- Fit3 App import/install instructions: https://github.com/yuriyurin/Fit3-App
- Fit3 Toolkit (native apps; experimental, firmware-specific): https://github.com/yksen/fit3-toolkit
