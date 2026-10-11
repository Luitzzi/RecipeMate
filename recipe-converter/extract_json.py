"""Extracts the JSON answer from free-form model output."""

import json

_DECODER = json.JSONDecoder()
_OPEN = {"{": "}", "[": "]"}


def _skip_broken(text: str, start: int) -> int:
    """Returns the position after the bracketed region opening at `start`, or the text length if it never closes.

    Parts of a broken or cut-off structure must not be mistaken for the answer.
    """
    depth = 0
    in_string = False
    position = start
    while position < len(text):
        char = text[position]
        if in_string:
            if char == "\\":
                position += 1
            elif char == '"':
                in_string = False
        elif char == '"':
            in_string = True
        elif char in _OPEN:
            depth += 1
        elif char in _OPEN.values():
            depth -= 1
            if depth == 0:
                return position + 1
        position += 1
    return len(text)


def extract_last_json(text: str):
    """Returns the last top-level JSON object or array in the text, or None.

    Models wrap JSON in prose and code fences and sometimes correct themselves, so the last complete structure is
    the answer.
    """
    last = None
    position = 0
    while position < len(text):
        if text[position] not in _OPEN:
            position += 1
            continue
        try:
            last, position = _DECODER.raw_decode(text, position)
        except json.JSONDecodeError:
            position = _skip_broken(text, position)
    return last
