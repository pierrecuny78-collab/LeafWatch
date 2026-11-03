# Security model

- Pair only with an explicitly authorized Android companion.
- Do not transmit credentials or authentication tokens in plaintext application payloads.
- Validate message type, schema version, lengths and identifiers.
- Apply strict size limits to notes and task payloads.
- Treat all received data as untrusted.
- Avoid executing downloaded code.
- Keep personal documents and health-related data out of public test fixtures.
- Do not commit BLE addresses, serial numbers, pairing secrets or device dumps.

The initial protocol is a development format, not a security certification.
