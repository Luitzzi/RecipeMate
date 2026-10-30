import dayjs from "dayjs";
import "dayjs/locale/de";
import i18n from "i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import { initReactI18next } from "react-i18next";

import commonDE from "../../locales/de/common.json";
import commonEN from "../../locales/en/common.json";

export const defaultNS = "common";
export const supportedLanguages = ["de", "en"] as const;
export type SupportedLanguage = (typeof supportedLanguages)[number];

export const resources = {
  de: { common: commonDE },
  en: { common: commonEN },
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
    defaultNS,
    ns: [defaultNS],
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
