import type { SelectOption } from "#/common/components/input/SelectOption.ts";
import { Button, type RefSelectProps, Select, type SelectProps } from "antd";
import { PlusOutlined } from "@ant-design/icons";
import { useRef, useState } from "react";

export type AddableSelectProps<ValueT extends string | number> = Omit<
  SelectProps,
  "options"
> & {
  selectOptions: SelectOption<ValueT>[];
  onCreate: (newName: string) => void;
  isElementValid?: (newName: string) => boolean;
};

export const AddableSelect = <ValueT extends string | number>({
  selectOptions,
  onCreate,
  isElementValid,
  ...selectProps
}: AddableSelectProps<ValueT>) => {
  const selectRef = useRef<RefSelectProps>(null);
  const [open, setOpen] = useState<boolean>(false);
  const [search, setSearch] = useState<string>("");
  const newName = search.trim();
  const canCreate = isElementValid
    ? isElementValid(newName) && newName !== ""
    : !selectProps.disabled;

  const create = () => {
    onCreate(newName);
    selectProps.onChange?.(newName);
    setSearch("");
    setOpen(false);
    selectRef.current?.blur();
  };

  return (
    <Select
      {...selectProps}
      ref={selectRef}
      options={selectOptions}
      onSelect={(value, option) => {
        selectRef.current?.blur();
        selectProps.onSelect?.(value, option);
      }}
      open={open}
      onOpenChange={setOpen}
      notFoundContent={
        <Button
          type="text"
          disabled={!canCreate}
          block
          icon={<PlusOutlined />}
          onClick={create}
        >
          Add "{newName}"
        </Button>
      }
      showSearch={{
        searchValue: search,
        onSearch: setSearch,
        optionFilterProp: "label",
      }}
      onInputKeyDown={(event) => {
        if (
          event.key === "Enter" &&
          open &&
          canCreate &&
          !event.nativeEvent.isComposing // ignore Enter that confirms IME input
        ) {
          event.preventDefault(); // avoid submitting a surrounding form
          event.stopPropagation(); // keep the Select from selecting the highlighted option
          create();
          return;
        }
        selectProps.onInputKeyDown?.(event);
      }}
      style={{ width: "100%" }}
    />
  );
};
