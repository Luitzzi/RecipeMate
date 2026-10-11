"""JSON Schema validation with error messages a model can act on."""

import json
from functools import cache
from pathlib import Path

from jsonschema import Draft202012Validator

SCHEMAS = Path(__file__).resolve().parent / "schemas"
MAX_ERRORS = 20


@cache
def _validator(name: str) -> Draft202012Validator:
    schema = json.loads((SCHEMAS / f"{name}.schema.json").read_text(encoding="utf-8"))
    Draft202012Validator.check_schema(schema)
    return Draft202012Validator(schema)


def _leaves(error):
    """Errors of if/then/else and combinators carry the actual cause in their context."""
    if error.context:
        for child in error.context:
            yield from _leaves(child)
    else:
        yield error


def schema_errors(value, name: str) -> list[str]:
    """Returns one line per violation, empty if the value is valid."""
    if not isinstance(value, dict):
        return [f"$: expected a JSON object, got {type(value).__name__}"]
    messages = []
    for error in sorted(_validator(name).iter_errors(value), key=lambda e: list(map(str, e.absolute_path))):
        for leaf in _leaves(error):
            path = "$" + "".join(f"[{p}]" if isinstance(p, int) else f".{p}" for p in leaf.absolute_path)
            message = leaf.message if len(leaf.message) <= 300 else leaf.message[:300] + "…"
            messages.append(f"{path}: {message}")
    return list(dict.fromkeys(messages))[:MAX_ERRORS]
