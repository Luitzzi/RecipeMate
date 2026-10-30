# Design tokens

All visual values live in `src/theme/tokens.ts`. Components read them from
the exported `tokens` object. The values originate from
`docs/antd-docs/ant-d-design-system.md`.

## Principles

1. **One source.** Every color, spacing, size, radius and shadow in
   component code comes from `tokens`. This is the source of truth.
   Only use hex codes for specific once occurring problems.
2. **No antd tokens in components.** Do not use `theme.useToken()` or
   `var(--ant-*)`. antd only receives our values through `themeConfig`
   (seed tokens), so its components stay consistent with ours.
3. **Three sizes.** Scales use `small` / `medium` / `large`. If a
   design seems to need a fourth step, revisit the design first.
4. **Role over value.** Pick a token by its purpose (`color.text.secondary`), hex values again only for very specific
   problems.
5. **4 px grid.** All spacing values are multiples of 4.

## Token overview

### Spacing — `margin`, `padding`, `gap`

All three share one scale, so changing a step changes it everywhere.

| Token    | Value | Typical use                                 |
|----------|-------|---------------------------------------------|
| `small`  | 8 px  | Between related elements (input rows, tags) |
| `medium` | 16 px | Inside containers, between form fields      |
| `large`  | 24 px | Between sections, separating page blocks    |

Use the group that matches the CSS property:
`style={{ marginTop: tokens.margin.large }}`, `gap={tokens.gap.small}`.

### Layout — `layout`

The page frame is the only exception to the three-step scale.

| Token            | Value   | Use                                     |
|------------------|---------|-----------------------------------------|
| `gutter.mobile`  | 16 px   | Page inner padding on phones            |
| `gutter.desktop` | 32 px   | Page inner padding from `md` breakpoint |
| `frameMargin`    | 32 px   | Space around the framed page box        |
| `maxWidth`       | 1200 px | Maximum width of the page box           |

### Widths — `width`

Widths of form fields. Named by role, because they do not form a scale.

| Token         | Value  | Use                               |
|---------------|--------|-----------------------------------|
| `numberField` | 80 px  | Short numeric inputs in rows      |
| `selectField` | 160 px | Selects with short values in rows |

Use `flex: 1` instead of a width for the main input of a row.

### Colors — `color`

| Group        | Tokens                                           | Use                                             |
|--------------|--------------------------------------------------|-------------------------------------------------|
| Brand/status | `primary`, `success`, `warning`, `error`, `info` | Actions, links, feedback states                 |
| `text`       | `primary`, `secondary`, `tertiary`, `disabled`   | Body, descriptions, hints, disabled/placeholder |
| `background` | `layout`, `container`, `subtle`                  | Page, cards/forms, table headers/hover          |
| `border`     | `default`, `subtle`                              | Control outlines, dividers/frames               |
| `fill`       | —                                                | Inactive indicators (e.g. empty badge)          |

Rules:

- `primary` marks the single most important action per screen.
- Text colors are transparent black, so they blend on tinted surfaces.
- Surfaces follow the layer model: `layout` (page) → `container` (content).

### Typography — `fontSize`, `fontWeight`, `fontFamily`

| Token      | small | medium | large |
|------------|-------|--------|-------|
| `fontSize` | 12 px | 14 px  | 16 px |

`fontWeight` has only `regular` (400) and `strong` (600). Headings use
`Typography.Title` levels instead of custom sizes.

### Shape and elevation

| Token           | small | medium | large | Use                                                     |
|-----------------|-------|--------|-------|---------------------------------------------------------|
| `radius`        | 4 px  | 6 px   | 8 px  | Tags/tooltips · controls · surfaces (cards, page frame) |
| `controlHeight` | 24 px | 32 px  | 40 px | Matches antd `size="small"` / default / `"large"`       |

- `borderWidth`: 1 px, always `solid`.
- `border`: complete border for frames (`borderWidth`, `solid`,
  `color.border.subtle`), used as `style={{ border: tokens.border }}`.
- `shadow.raised`: surfaces resting on the page (page frame).
- `shadow.popup`: floating layers (dropdowns, popovers).
  The design is flat-first; use borders before shadows.

## Extending tokens

1. Check whether an existing token fits the role.
2. If not, take the value from `ant-d-design-system.md`; do not invent one.
3. Add it to `tokens.ts` with a role-based name and document it here.
4. If antd components must follow the change, map it in `themeConfig`.
