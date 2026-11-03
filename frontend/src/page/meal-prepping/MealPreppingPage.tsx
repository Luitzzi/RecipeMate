import { useMemo, useState } from "react";
import { LeftOutlined, RightOutlined } from "@ant-design/icons";
import {
  Button,
  Col,
  type DatePickerProps,
  Flex,
  Row,
  Space,
  Spin,
  Tooltip,
  Typography,
} from "antd";
import dayjs, { type Dayjs } from "dayjs";
import { useTranslation } from "react-i18next";
import { useApi } from "#/api/apiContext.ts";
import { SectionHeader } from "#/common/components/SectionHeader.tsx";
import { useFetch } from "#/common/hooks/useFetch.ts";
import { useMutation } from "#/common/hooks/useMutation.ts";
import { useNotification } from "#/common/hooks/useNotification.tsx";
import { DatePicker } from "antd";
import {
  Language,
  type MealPlanDayUiDTO,
  type RecipeUiDTO,
} from "#/generated/interface-api.ts";
import { tokens } from "#/theme/tokens.ts";
import { MealPlanDayCard } from "./MealPlanDayCard.tsx";

const { RangePicker } = DatePicker;

const DATE_FORMAT = "YYYY-MM-DD";
const WEEK_FORMAT = "DD.MMM";
const MONTH_FORMAT = "YYYY/MM";

const startOfWeek = (date: Dayjs) =>
  date.startOf("day").subtract((date.day() + 6) % 7, "day");

const emptyDay = (date: string): MealPlanDayUiDTO => ({
  date,
  goShopping: false,
  recipeIds: [],
});

export const MealPreppingPage = () => {
  const { t, i18n } = useTranslation("mealPrepping");
  const { recipe, mealPrepping } = useApi();
  const notification = useNotification();
  const language = i18n.resolvedLanguage === "de" ? Language.DE : Language.EN;

  const [weekStart, setWeekStart] = useState(() => startOfWeek(dayjs()));
  const weekDays = useMemo(
    () => Array.from({ length: 7 }, (_, i) => weekStart.add(i, "day")),
    [weekStart],
  );

  const [recipes] = useFetch(() => recipe.getAll({}), [language]);
  const [fetchedDays, loadingDays, loadError] = useFetch(
    () =>
      mealPrepping.getRange({
        from: weekDays[0].format(DATE_FORMAT),
        to: weekDays[6].format(DATE_FORMAT),
      }),
    [weekDays],
  );

  const [editedDays, setEditedDays] = useState<Map<string, MealPlanDayUiDTO>>(
    new Map(),
  );
  const [saveDay] = useMutation(mealPrepping.save.bind(mealPrepping));

  const recipesById = useMemo(
    () =>
      new Map<number, RecipeUiDTO>(
        (recipes ?? []).map((recipe) => [recipe.id as number, recipe]),
      ),
    [recipes],
  );

  const daysByDate = useMemo(() => {
    const days = new Map(fetchedDays?.map((day) => [day.date, day]));
    editedDays.forEach((day, date) => days.set(date, day));
    return days;
  }, [fetchedDays, editedDays]);

  const getDay = (date: string) => daysByDate.get(date) ?? emptyDay(date);

  const setEditedDay = (day: MealPlanDayUiDTO) =>
    setEditedDays((prev) => new Map(prev).set(day.date, day));

  const changeDay = async (day: MealPlanDayUiDTO) => {
    const previous = getDay(day.date);
    setEditedDay(day);
    const saved = await saveDay(day.date, day);
    if (saved) {
      setEditedDay(saved);
    } else {
      setEditedDay(previous);
      notification.error({ title: t("saveFailed") });
    }
  };

  const plannedDays = weekDays.map((date) => getDay(date.format(DATE_FORMAT)));
  const mealCount = plannedDays.reduce(
    (count, day) => count + day.recipeIds.length,
    0,
  );
  const shoppingCount = plannedDays.filter((day) => day.goShopping).length;

  const customWeekStartEndFormat: DatePickerProps["format"] = (value) =>
    `${dayjs(value).startOf("week").format(WEEK_FORMAT)} - ${dayjs(value)
      .endOf("week")
      .format(WEEK_FORMAT)}`;

  return (
    <Flex vertical gap={tokens.gap.medium}>
      <SectionHeader title={t("title")} />
      <Flex justify="space-between" align="center" wrap gap={tokens.gap.small}>
        <Space.Compact>
          <Tooltip title={t("previousWeek")}>
            <Button
              icon={<LeftOutlined />}
              onClick={() => setWeekStart(weekStart.subtract(7, "day"))}
            />
          </Tooltip>
          <Button onClick={() => setWeekStart(startOfWeek(dayjs()))}>
            {t("today")}
          </Button>
          <Tooltip title={t("nextWeek")}>
            <Button
              icon={<RightOutlined />}
              onClick={() => setWeekStart(weekStart.add(7, "day"))}
            />
          </Tooltip>
        </Space.Compact>

        <DatePicker
          defaultValue={dayjs(weekDays[0])}
          picker="week"
          format={customWeekStartEndFormat}
          onPickerValueChange={(date) => setWeekStart(date)}
        />
      </Flex>
      {loadError ? (
        <Typography.Text type="danger">{t("loadFailed")}</Typography.Text>
      ) : (
        <Spin spinning={loadingDays}>
          <Row gutter={[tokens.gap.medium, tokens.gap.medium]}>
            {weekDays.map((date) => {
              const key = date.format(DATE_FORMAT);
              return (
                <Col key={key} xs={24} md={12} xl={8}>
                  <MealPlanDayCard
                    date={date}
                    day={getDay(key)}
                    recipes={recipes ?? []}
                    recipesById={recipesById}
                    onChange={changeDay}
                  />
                </Col>
              );
            })}
          </Row>
        </Spin>
      )}
    </Flex>
  );
};
