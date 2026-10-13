import { Button, Flex, Result, Spin } from "antd";

import { tokens } from "#/theme/tokens.ts";
import { SectionHeader } from "#/common/components/SectionHeader.tsx";
import type { CounterUiDTO } from "#/generated/interface-api.ts";
import { useApi } from "../../api/ApiContext.tsx";
import { useFetchOnce } from "#/common/hooks/useFetchOnce.ts";
import { useMutation } from "#/common/hooks/useMutation.ts";
import { useNotification } from "../../common/hooks/useNotification.tsx";
import { useEffectOnce } from "#/common/hooks/useEffectOnce.ts";

export const CounteringTheCounter = () => {
  const api = useApi();
  const notify = useNotification();

  const [createdCounter, createLoading, createError] = useFetchOnce(() =>
    api.counter.create(),
  );
  const [update, updatedCounter, updateLoading, updateError] = useMutation(
    (dto: CounterUiDTO) => api.counter.update(dto),
  );

  // When the side loads the useFetchOnce fetch is executed.
  // At this time the updatedCounter is still undefined.
  // When the counter is updated the updatedCounter gets a value and is now the value to use.
  const counter = updatedCounter ?? createdCounter;
  const error = createError ?? updateError;

  // Display all errors
  useEffectOnce(() => {
    notify.success({
      title: "Success",
      description: "Example success",
    });
    notify.info({
      title: "Info",
      description: "Example info",
    });
    notify.warn({
      title: "Warn",
      description: "Example warn",
    });
    notify.error({
      title: "Error",
      description: "Example error",
    });
  });

  if (error) {
    notify.error({
      title: "Counter konnte nicht geladen werden",
      description: "D:",
    });
  }

  return (
    <>
      <SectionHeader
        title="Example page"
        description="Counter aus dem Backend"
      />
      {createLoading || !counter ? (
        <Spin />
      ) : (
        <Flex vertical align="center" gap={tokens.gap.medium}>
          <div>Current count: {counter.count}</div>
          <Button
            type="primary"
            loading={updateLoading}
            onClick={() =>
              update({
                ...counter, // ... "spreaded" the counter object -> adds all props
                count: counter.count + 1, // when the spreading operator is the first prop all following ones overwrite the props. Here I set the count manually
              }).then(() =>
                notify.success({
                  title: "wohoo it updated",
                  description: ":D",
                }),
              )
            }
          >
            Increment
          </Button>
        </Flex>
      )}
    </>
  );
};
