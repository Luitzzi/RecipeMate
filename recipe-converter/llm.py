"""Thin wrapper around the headless `claude` CLI."""

import json
import subprocess
import tempfile
import time
from dataclasses import dataclass, field
from pathlib import Path

# Sessions are stored per working directory. A neutral one keeps project instructions out of the context.
WORKDIR = Path(tempfile.gettempdir()) / "recipe-converter-llm"
TIMEOUT_SECONDS = 600
ATTEMPTS = 3


class LlmError(RuntimeError):
    pass


@dataclass
class Reply:
    text: str
    session_id: str
    usage: dict = field(default_factory=dict)


def complete(
    system_prompt: str, message: str, model: str, resume: str | None = None, effort: str | None = None
) -> Reply:
    """Sends one user message. With `resume`, the message continues that session, so the earlier turns are cached.

    `effort` is the CLI effort level. Whether the model reasons before answering is its own decision and cannot be
    switched on or off from here.
    """
    WORKDIR.mkdir(parents=True, exist_ok=True)
    command = [
        "claude", "--print", "--model", model, "--system-prompt", system_prompt, "--output-format", "json",
        "--tools", "", "--strict-mcp-config", "--disable-slash-commands", "--setting-sources", "",
    ]  # fmt: skip
    if resume:
        command += ["--resume", resume]
    if effort:
        command += ["--effort", effort]

    error = "no attempt made"
    for attempt in range(ATTEMPTS):
        if attempt:
            time.sleep(5 * attempt)
        try:
            process = subprocess.run(
                command, input=message, capture_output=True, text=True, cwd=WORKDIR, timeout=TIMEOUT_SECONDS
            )
        except subprocess.TimeoutExpired:
            error = f"timeout after {TIMEOUT_SECONDS}s"
            continue
        try:
            result = json.loads(process.stdout)
        except json.JSONDecodeError:
            error = f"exit {process.returncode}: {(process.stderr or process.stdout).strip()[:500]}"
            continue
        if result.get("is_error") or not isinstance(result.get("result"), str):
            error = str(result.get("result") or result.get("subtype"))[:500]
            continue
        usage = result.get("usage") or {}
        return Reply(
            text=result["result"],
            session_id=result["session_id"],
            usage={
                "input_tokens": usage.get("input_tokens", 0),
                "cache_creation_input_tokens": usage.get("cache_creation_input_tokens", 0),
                "cache_read_input_tokens": usage.get("cache_read_input_tokens", 0),
                "output_tokens": usage.get("output_tokens", 0),
                "thinking_tokens": (usage.get("output_tokens_details") or {}).get("thinking_tokens", 0),
                "cost_usd": result.get("total_cost_usd", 0.0),
                "model": next(iter(result.get("modelUsage") or {}), model),
                "stop_reason": result.get("stop_reason"),
            },
        )
    raise LlmError(error)
