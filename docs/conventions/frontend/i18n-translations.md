# I18next translation strategy

### Libs
- i18next
- react-i18next
- i18next-browser-languagedetector

### Setup
- Configuration: `frontend/src/i18n/i18n.ts` (imported once in `main.tsx`)
- Types: `frontend/src/i18n/i18next.d.ts` – keys passed to `t()` are type checked
  against the english resources. Unknown keys fail the build.
- Supported languages: `de`, `en` (fallback: `en`). Regional variants like
  `de-DE` are mapped to `de`.
- Detection order: `localStorage` (key `lang`) → browser language → `<html lang>`.
- The active language is synced to `<html lang>`, `dayjs` and the Ant Design
  `ConfigProvider` locale.

### File structure
```
frontend/locales/<language>/<namespace>.json
```
- `common` holds reusable texts (buttons, generic messages). It is the default namespace.
- Add one namespace per feature (e.g. `recipe.json`) instead of merging everything into `common`.

### Keys
- Nested, camelCase and semantic: `buttons.save`, `recipe.form.title`
- Never use the english text itself as key.
- English is the reference language: every key must exist in `en` first.

### Usage
```tsx
const { t } = useTranslation();          // default namespace "common"
const { t } = useTranslation("recipe");  // feature namespace

t("buttons.save");
t("recipe.greeting", { name });          // "Hallo {{name}}"
t("recipe.portions", { count });         // keys "portions_one" / "portions_other"
```
Switch the language with `i18n.changeLanguage("de")`. The choice is persisted in `localStorage`.

### Adding a namespace or language
1. Create the JSON file(s) in `frontend/locales/<language>/`.
2. Import them in `i18n.ts` and register them in `resources` (and `ns` for a new namespace).
3. For a new language additionally extend `supportedLanguages`, the `antdLocales`
   map in `App.tsx` and import the matching `dayjs/locale/<language>`.
