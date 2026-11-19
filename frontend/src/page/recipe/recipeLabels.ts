import { useMemo } from "react";
import { useTranslation } from "react-i18next";

import type { Unit } from "#/generated/interface-api.ts";

// TODO: LGE, OSA Difficulties als enum aus dem Backend
export const isKnownDifficulty = (
  difficulty: number,
): difficulty is 1 | 2 | 3 =>
  difficulty === 1 || difficulty === 2 || difficulty === 3;

/** Localized display labels and formatters for the recipe enums of the backend. */
export const useRecipeFormats = () => {
  const { t, i18n } = useTranslation("enums");

  const amountFormat = useMemo(
    () => new Intl.NumberFormat(i18n.language, { maximumFractionDigits: 2 }),
    [i18n.language],
  );

  const formatAmount = (amount: number | undefined, unit: Unit) => {
    // Without an amount (e.g. "to taste") the singular form is used
    const unitLabel = t(`unit.${unit}`, { count: amount ?? 1 });
    if (amount == null) return unitLabel;
    return `${amountFormat.format(amount)} ${unitLabel}`;
  };

  const formatMinutes = (minutes: number) => t("minutes", { count: minutes });

  return { formatAmount, formatMinutes };
};
