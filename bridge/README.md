# Android bridge

The bridge will be implemented as a module in the existing LeafVibe Android application.

Responsibilities:
- BLE permissions and connection lifecycle;
- bounded message transfer;
- acknowledgements and retries;
- local queue and synchronization status;
- import into the existing LeafVibe document store.

No Android BLE implementation is included until the Fit3's actual supported transport and service characteristics are confirmed.
