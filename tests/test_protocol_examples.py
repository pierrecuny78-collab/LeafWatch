import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

def test_note_example_uses_expected_protocol():
    example = json.loads((ROOT / "shared/examples/note-created.json").read_text(encoding="utf-8"))
    assert example["protocol"] == "leafwatch.transfer.v1"
    assert example["kind"] == "note.created"
    assert example["payload"]["format"] == "text/plain"
    assert example["payload"]["revision"] == 1
