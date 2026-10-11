from extract_json import extract_last_json


def test_plain_json():
    assert extract_last_json('{"a": 1}') == {"a": 1}


def test_json_in_prose():
    assert extract_last_json('Here is your output: {"a": 1} Hope this helps.') == {"a": 1}


def test_fenced_block():
    assert extract_last_json('Result:\n```json\n{\n  "a": [1, 2]\n}\n```\n') == {"a": [1, 2]}


def test_last_version_wins():
    text = 'Here it is:\n```json\n{"a": 1}\n```\nNever mind, I made a mistake. Correct version:\n```json\n{"a": 2}\n```'
    assert extract_last_json(text) == {"a": 2}


def test_no_preference_for_content():
    assert extract_last_json('{"@type": "Recipe", "name": "x"}\nSummary: {"ok": true}') == {"ok": True}


def test_nested_structures_are_not_candidates():
    assert extract_last_json('{"steps": [{"text": "a"}, {"text": "b"}]}') == {"steps": [{"text": "a"}, {"text": "b"}]}


def test_cut_off_last_block_falls_back_to_previous():
    text = '```json\n{"a": 1}\n```\nCorrected:\n```json\n{"a": 2, "steps": [{"text": "x"}, {"text": "y'
    assert extract_last_json(text) == {"a": 1}


def test_invalid_block_is_skipped_as_a_whole():
    assert extract_last_json('{"a": 1}\n{"a": 2, "steps": [{"text": "x"},]}') == {"a": 1}


def test_braces_and_brackets_inside_strings():
    assert extract_last_json('{"text": "use a {large} bowl [or pan] \\"now\\""}') == {
        "text": 'use a {large} bowl [or pan] "now"'
    }


def test_braces_in_prose():
    assert extract_last_json('Placeholders like {name} are ignored. {"a": 1}') == {"a": 1}


def test_top_level_array():
    assert extract_last_json("[1, 2, 3]") == [1, 2, 3]


def test_no_json():
    assert extract_last_json("I could not find a recipe.") is None
    assert extract_last_json("") is None
