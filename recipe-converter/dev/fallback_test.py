"""Live test of the schema-correction round with a forced, real schema failure. Development tooling only.

The recipe schema is tightened for this test (no "description" allowed), so the extractor's normal answer is
truly invalid, the error message is true, and the correction runs through the production path.

Usage: uv run python -m dev.fallback_test slug [slug ...]
"""

import copy
import json
import sys
from pathlib import Path

from jsonschema import Draft202012Validator

import pipeline
import validation
from reduce import reduce_dump

ROOT = Path(__file__).resolve().parent.parent


def main(slugs: list[str]) -> None:
    schema = copy.deepcopy(validation._validator("recipe").schema)
    del schema["properties"]["description"]
    strict = Draft202012Validator(schema)
    original = validation._validator

    def patched(name: str):
        return strict if name == "recipe" else original(name)

    validation._validator = patched
    config = pipeline.Config(evaluate=False)
    for slug in slugs:
        result = pipeline.convert(reduce_dump(ROOT / "dumps" / slug), config)
        print(f"{slug}: {result.status}, rounds={len(result.rounds)}")
        for number, (round_, usage) in enumerate(zip(result.rounds, result.usage), 1):
            print(
                f"  round {number}: schema errors={round_.schema_errors} cache_read={usage['cache_read_input_tokens']} "
                f"cache_write={usage['cache_creation_input_tokens']} out={usage['output_tokens']} ${usage['cost_usd']:.4f}"
            )
        if len(result.rounds) == 2 and result.recipe:
            first = pipeline.extract_last_json(result.rounds[0].raw)
            changed = sorted(k for k in set(first) | set(result.recipe) if first.get(k) != result.recipe.get(k))
            print(f"  properties changed by the correction: {changed}")
        (ROOT / "out" / f"fallback_{slug}.json").write_text(
            json.dumps({"status": result.status, "rounds": [r.__dict__ for r in result.rounds], "usage": result.usage},
                       ensure_ascii=False, indent=2), encoding="utf-8")  # fmt: skip


if __name__ == "__main__":
    main(sys.argv[1:])
