import { useEffect } from "react";
import { Button, Flex, Result, Spin } from "antd";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import { tokens } from "#/theme/tokens.ts";
import { SectionHeader } from "#/common/components/SectionHeader.tsx";
import { useApi } from "#/api/ApiContext.tsx";

export const CounterPage = () => {
  const api = useApi();
  const queryClient = useQueryClient();

  // --- Create a counter once on load ---
  const create = useMutation({
    mutationFn: () => api.counter.create(),
  });

  useEffect(() => {
    create.mutate();
  }, [create.mutate]);

  const id = create.data?.id;

  // --- Query the counter ---
  const counter = useQuery({
    queryKey: ["counter", id],
    queryFn: () => api.counter.getById(id!),
    enabled: id != null,
  });

  // --- Increment the counter ---
  const increment = useMutation({
    mutationFn: () => api.counter.increment(id!, { increment: 1 }),
    onSuccess: (updated) => queryClient.setQueryData(["counter", id], updated),
  });

  // --- Render the page ---
  const error = create.error ?? counter.error;
  if (error)
    return (
      <Result status="error" title="Counter error" subTitle={error.message} />
    );
  if (!counter.data) return <Spin />;

  return (
    <>
      <SectionHeader
        title="Example page"
        description="Counter aus dem Backend"
      />
      <Flex vertical align="center" gap={tokens.gap.medium}>
        <div>Current count: {counter.data.count}</div>
        <Button
          type="primary"
          loading={increment.isPending}
          onClick={() => increment.mutate()}
        >
          Increment
        </Button>
      </Flex>
    </>
  );
};
