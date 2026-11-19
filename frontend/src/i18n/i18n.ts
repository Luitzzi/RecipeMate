import "dayjs/locale/de";

import dayjs from "dayjs";
import i18n from "i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import { initReactI18next } from "react-i18next";

import commonDE from "##/locales/de/common.json";
import enumsDE from "##/locales/de/enums.json";
import feedDE from "##/locales/de/feed.json";
import recipeOverviewDE from "##/locales/de/recipe-overview.json";
import commonEN from "##/locales/en/common.json";
import enumsEN from "##/locales/en/enums.json";
import feedEN from "##/locales/en/feed.json";
import recipeOverviewEN from "##/locales/en/recipe-overview.json";

import type { EnumTranslations } from "./enumTranslations.ts";

export const defaultNS = "common";
export const supportedLanguages = ["de", "en"] as const;
export type SupportedLanguage = (typeof supportedLanguages)[number];

export const resources = {
  de: {
    common: commonDE,
    feed: feedDE,
    recipeOverview: recipeOverviewDE,
    enums: enumsDE satisfies EnumTranslations,
  },
  en: {
    common: commonEN,
    feed: feedEN,
    recipeOverview: recipeOverviewEN,
    enums: enumsEN satisfies EnumTranslations,
  },
} as const;

i18n.on("languageChanged", (lng) => {
  document.documentElement.lang = lng;
  dayjs.locale(lng);
});

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    resources,
    ns: Object.keys(resources.en),
    defaultNS,
    fallbackLng: "en",
    supportedLngs: supportedLanguages,
    load: "languageOnly", // de-DE -> de
    debug: import.meta.env.DEV,
    interpolation: { escapeValue: false }, // React already escapes values
    detection: {
      order: ["localStorage", "navigator", "htmlTag"],
      caches: ["localStorage"],
      lookupLocalStorage: "lang",
    },
  })
  .catch((error: unknown) =>
    console.error("i18n initialisation failed", error),
  );

export default i18n;
