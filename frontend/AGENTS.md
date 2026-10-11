# Frontend

Typescript + React application.
Additionally:
- Ant Design v6 as the design system.

## Commands
- `npm run dev` — start dev server
- `npm run build` — production build/
- `npm test` — run tests
- `npm run lint` — ESLint
- `npm run format` — Prettier

## Conventions

## Design system
The documentation for the design-tokens and antd usage is in /design-docs.
- **design-tokens.md:** \
    Source of truth for all design questions.
    Always prefer the predefined tokens for colors, typography and spacing - 
    never invent a value that isn't defined. If something is missing, say so rather than
    guessing.
- **Ant Design documentation:** \
  The component reference and semantic-styling docs in docs/antd-docs are large.
  Don't read them directly — use the `ant-design-consistency` skill, which
  knows how to search them.