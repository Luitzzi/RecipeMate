---
name: ant-design-consistency
description: Use when writing, reviewing, or restyling Ant Design v6
  components for token consistency, correct component APIs, or semantic
  styling. Covers component props/behavior and internal styling targets.
---

## Source of truth for styling

/design-docs/design-tokens.md is the source of truth for colors,
typography, and spacing — it's small, read it directly. Never invent a
value that isn't defined there; if something is missing, say so.

## Component behavior

/design-docs/antd-docs/ant-design-documentation.txt:
For props, variants, or behavior, search
docs/ant-design-documentation.txt for the specific component name.
Do not read the whole file — it is ~63,000 lines.

## Semantic styling (internals)

/design-docs/antd-docs/ant-design-semantic-documentation.txt:
For customizing a component's internals — targeting a specific part
like icon, title, or actions instead of the whole component — search
the file for that component's semantic
parts, and prefer the classNames/styles API over generic CSS overrides.
Do not read the whole file — it is ~11,400 lines.

If a component isn't covered in the semantic doc, it may not expose this
API yet — say so rather than assuming you missed it.
