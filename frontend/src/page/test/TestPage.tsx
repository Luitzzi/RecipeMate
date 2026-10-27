import { useMemo, useState } from "react";
import {
  type SelectOption,
  toSelectOption,
  toSelectOptions,
} from "#/common/components/input/SelectOption.ts";
import { Flex, Select } from "antd";
import { AddableSelect } from "#/common/components/input/AddableSelect.tsx";
import { tokens } from "#/theme/tokens.ts";

type Element = {
  id?: number;
  name: string;
};

const normalizeString = (value: string) => {
  return value.trim().toLowerCase();
};

export const TestPage = () => {
  // Elements could be fetched
  const [elements, setElements] = useState<Element[]>([
    { id: 1, name: "A" },
    { id: 2, name: "B" },
    { id: 3, name: "C" },
  ]);
  const [newElementsByName, setNewElementsByName] = useState<
    Map<string, Element>
  >(new Map());
  // Either the id of an existing element or the name of a new element
  const [selectedValue, setSelectedValue] = useState<number | string>();

  const addElement = (element: Element) => {
    setNewElementsByName((prev) =>
      new Map(prev).set(normalizeString(element.name), {
        name: element.name,
      }),
    );
  };

  // Transform the elements to SelectOptions to use them in the Select as options
  // Map them by their normalized name to be able to check if a element already exist (of course only if the name should be unique)
  // The added elements are ignored. Idea: The user only inputs something if he wants to create.
  // Afterwards switching to another Select to input it not necessary (Technical the new elements would need ids, which only the backend generates)
  const elementOptionsByName = useMemo(
    () =>
      new Map<string, SelectOption<number>>(
        elements.map((element) => [
          normalizeString(element.name),
          toSelectOption(
            element,
            (element) => element.id as number,
            (element) => element.name,
          ),
        ]),
      ),
    [elements],
  );

  const isElementValid = (newName: string) => {
    const normalizedNewName = normalizeString(newName);
    return (
      !elementOptionsByName.has(normalizedNewName) &&
      !newElementsByName.has(normalizedNewName)
    );
  };

  return (
    <Flex vertical gap={tokens.gap.medium} style={{ width: "50%" }}>
      <Select
        placeholder={"Ich bin ein normales Select"}
        options={toSelectOptions(
          elements,
          (element) => element.id as number,
          (element) => element.name,
        )}
      />
      <AddableSelect
        placeholder={"Ich bin ein Select in dem man was hinzufügen kann"}
        selectOptions={[...elementOptionsByName.values()]}
        value={selectedValue}
        onChange={setSelectedValue}
        onCreate={(newName) => addElement({ name: newName })}
        isElementValid={isElementValid}
      />
    </Flex>
  );
};
