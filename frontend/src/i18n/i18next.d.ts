import "i18next";
import type { defaultNS, resources } from "./i18n.ts";

declare module "i18next" {
  // Module augmentation requires an interface
  // eslint-disable-next-line @typescript-eslint/consistent-type-definitions
  interface CustomTypeOptions {
    defaultNS: typeof defaultNS;
    resources: (typeof resources)["en"];
  }
}
