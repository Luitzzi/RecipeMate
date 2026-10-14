import type { ReactElement } from "react";

export type SelectOption<ValueT extends string | number> = {
    value: ValueT;
    label: string | ReactElement;
};

export const toSelectOptions = <DataT, ValueT extends string | number>(
    data: DataT[],
    valueMapper: (data: DataT) => ValueT,
    labelMapper: (data: DataT) => string | ReactElement,
) => {
    return data.flatMap((element) =>
        toSelectOption<DataT, ValueT>(element, valueMapper, labelMapper),
    );
};

export const toSelectOption = <DataT, ValueT extends string | number>(
    data: DataT,
    valueMapper: (data: DataT) => ValueT,
    labelMapper: (data: DataT) => string | ReactElement,
): SelectOption<ValueT> => {
    return {
        value: valueMapper(data),
        label: labelMapper(data),
    } satisfies SelectOption<ValueT>;
};
