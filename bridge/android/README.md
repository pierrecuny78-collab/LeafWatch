# LeafWatch Android bridge — offline-first foundation

This directory contains the first Android-side building block for LeafWatch. It is designed to be integrated into the existing LeafVibe Android application; it is not a standalone APK.

## Offline contract

- Creating a message never requires Internet access.
- The complete protocol envelope is persisted in the Android app's private SQLite database before any transfer is attempted.
- Pending messages survive app restarts and phone reboots.
- A message remains pending until the transport reports an acknowledgement containing the same `message_id`.
- Retries reuse the original message ID so the receiver can safely deduplicate them.
- The first proof of concept must use a harmless test message only. Do not send private notes or documents until authenticated transport, encryption and key handling have been implemented and reviewed.

## Files

- `OfflineOutbox.kt`: durable local queue using Android's built-in SQLite APIs; no network or third-party library.
- `BridgeContract.kt`: transport boundary. A real BLE implementation must be supplied only after the Fit3 service/characteristics and authentication behavior are verified on the exact watch firmware.

## Current limitation

This is not yet a working Bluetooth sender. The Fit3's supported GATT service, characteristics, framing and acknowledgement mechanism are not confirmed in this repository. The transport interface deliberately prevents us from inventing UUIDs or sending data through an unverified service.

The existing Fit3 is not Wear OS. This Android code does not install an APK on the watch and does not create a watch-side receiver by itself.

## Integration sequence

1. Add these Kotlin sources to the LeafVibe Android app.
2. Create the outbox with the app's application context.
3. Enqueue a non-sensitive `leafwatch.transfer.v1` test envelope.
4. When a verified transport is available, send pending messages.
5. Mark a message delivered only after a matching acknowledgement is received.
6. Test airplane mode / Bluetooth off, force-stop and restart, then reconnect. The queued message must remain pending until acknowledged.

No Internet permission, cloud endpoint, analytics SDK or remote service is required by this foundation.
