import { Empty, Flex, Spin } from "antd";
import { Children, type ReactNode, useEffect, useRef } from "react";

import { tokens } from "#/theme/tokens.ts";

export type EndlessScrollGridProps = {
  children: ReactNode;
  emptyDescription: string;
  itemWidth?: number;
  loading?: boolean;
  hasMore: boolean;
  onLoadMore: () => void;
};

// inspired by https://juntao.substack.com/p/react-code-interview-002-infinite
export const EndlessScrollGrid = ({
  children,
  emptyDescription,
  itemWidth = 200,
  loading,
  hasMore,
  onLoadMore,
}: EndlessScrollGridProps) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const triggerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!hasMore || loading || !triggerRef.current) return;

    const observerOptions = {
      root: containerRef.current,
      rootMargin: `${itemWidth}px`,
    };
    const observer = new IntersectionObserver(
      (entries) => entries[0].isIntersecting && onLoadMore(),
      observerOptions,
    );

    observer.observe(triggerRef.current);
    return () => observer.disconnect();
  }, [hasMore, loading, itemWidth, onLoadMore]);

  const isEmpty = Children.count(children) === 0 && !loading;

  return (
    <div
      ref={containerRef}
      style={{ flex: 1, minHeight: 0, overflowY: "auto" }}
    >
      {isEmpty ? (
        <Empty
          image={Empty.PRESENTED_IMAGE_SIMPLE}
          description={emptyDescription}
        />
      ) : (
        <div
          style={{
            display: "grid",
            gap: tokens.gap.medium,
            gridTemplateColumns: `repeat(auto-fill, ${itemWidth}px)`,
            justifyContent: "center",
          }}
        >
          {children}
        </div>
      )}

      <div ref={triggerRef} />

      {loading && (
        <Flex
          justify={"center"}
          style={{ paddingBlock: tokens.padding.medium }}
        >
          <Spin />
        </Flex>
      )}
    </div>
  );
};
