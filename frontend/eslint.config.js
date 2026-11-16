import js from "@eslint/js";
import globals from "globals";
import reactHooks from "eslint-plugin-react-hooks";
import reactRefresh from "eslint-plugin-react-refresh";
import simpleImportSort from "eslint-plugin-simple-import-sort";
import tseslint from "typescript-eslint";
import prettier from "eslint-config-prettier/flat";
import { defineConfig, globalIgnores } from "eslint/config";

export default defineConfig([
  globalIgnores(["dist"]),
  {
    files: ["**/*.{ts,tsx}"],
    extends: [
      js.configs.recommended,
      tseslint.configs.recommended,
      tseslint.configs.stylistic,
      reactHooks.configs.flat.recommended,
      reactRefresh.configs.vite,
    ],
    plugins: {
      "simple-import-sort": simpleImportSort,
    },
    languageOptions: {
      globals: globals.browser,
    },
    rules: {
      "func-style": ["warn", "expression"], // prefer arrow functions over functions
      "prefer-arrow-callback": "warn",
      "@typescript-eslint/consistent-type-definitions": ["warn", "type"],
      "@typescript-eslint/no-unused-vars": "warn",
      "simple-import-sort/imports": "warn",
      "simple-import-sort/exports": "warn",
      "no-restricted-imports": [
        "warn",
        {
          patterns: [
            {
              group: ["..", "../*", "../**"],
              message: 'Use the "#/" alias instead of parent-relative imports.',
            },
          ],
        },
      ],
    },
  },
  prettier, // disable ESLint rules that conflict with prettier
]);
