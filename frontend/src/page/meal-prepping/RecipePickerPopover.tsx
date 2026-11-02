import { useState, type ReactNode } from "react";
import { SearchOutlined, ThunderboltOutlined } from "@ant-design/icons";
import { App, Button, Divider, Empty, Flex, Input, Menu, Popover } from "antd";
import { useTranslation } from "react-i18next";
import type { RecipeUiDTO } from "#/generated/interface-api.ts";
import { tokens } from "#/theme/tokens.ts";

type RecipePickerPopoverProps = {
  recipes: RecipeUiDTO[];
  onPick: (recipe: RecipeUiDTO) => void;
  /** Element that opens the popover on click. */
  children: ReactNode;
};

// No width/height tokens exist, so the sizes are derived from the control height.
const POPOVER_WIDTH = 7 * tokens.controlHeight.large;
const LIST_MAX_HEIGHT = 5 * tokens.controlHeight.large;

/** Popover to search a recipe of the user or pick a random one. */
export const RecipePickerPopover = ({
  recipes,
  onPick,
  children,
}: RecipePickerPopoverProps) => {
  const { t } = useTranslation("mealPrepping");
  const { message } = App.useApp();
  const [open, setOpen] = useState(false);
  const [search, setSearch] = useState("");

  const normalizedSearch = search.trim().toLowerCase();
  const matches = recipes.filter((recipe) =>
    recipe.title.toLowerCase().includes(normalizedSearch),
  );

  const pick = (recipe: RecipeUiDTO) => {
    onPick(recipe);
    setOpen(false);
    setSearch("");
  };

  const pickRandom = () => {
    const candidates = matches.length ? matches : recipes;
    if (!candidates.length) {
      message.warning(t("noRecipes"));
      return;
    }
    const recipe = candidates[Math.floor(Math.random() * candidates.length)];
    pick(recipe);
    message.success(t("randomPicked", { title: recipe.title }));
  };

  const content = (
    <Flex vertical gap={tokens.gap.small} style={{ width: POPOVER_WIDTH }}>
      <Input
        autoFocus
        allowClear
        prefix={<SearchOutlined />}
        placeholder={t("searchPlaceholder")}
        value={search}
        onChange={(event) => setSearch(event.target.value)}
        onPressEnter={() => matches.length === 1 && pick(matches[0])}
      />
      {matches.length ? (
        <Menu
          selectable={false}
          items={matches.map((recipe) => ({
            key: String(recipe.id),
            label: recipe.title,
          }))}
          onClick={({ key }) => {
            const recipe = matches.find((match) => String(match.id) === key);
            if (recipe) pick(recipe);
          }}
          style={{
            maxHeight: LIST_MAX_HEIGHT,
            overflowY: "auto",
            borderInlineEnd: "none",
          }}
        />
      ) : (
        <Empty
          image={Empty.PRESENTED_IMAGE_SIMPLE}
          description={t("noMatch")}
        />
      )}
      <Divider style={{ margin: 0 }} />
      <Button block icon={<ThunderboltOutlined />} onClick={pickRandom}>
        {t("random")}
      </Button>
    </Flex>
  );

  return (
    <Popover
      content={content}
      trigger="click"
      open={open}
      onOpenChange={setOpen}
      placement="bottom"
      arrow={false}
    >
      {children}
    </Popover>
  );
};
