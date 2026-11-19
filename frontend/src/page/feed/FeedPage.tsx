import { SearchOutlined } from "@ant-design/icons";
import { Button, Empty, Flex, Result, Spin } from "antd";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router";

import { useApi } from "#/api/apiContext.ts";
import { SectionHeader } from "#/common/components/SectionHeader.tsx";
import { useFetchOnce } from "#/common/hooks/useFetchOnce.ts";
import { FeedRow } from "#/page/feed/FeedRow.tsx";
import { tokens } from "#/theme/tokens.ts";

export default function FeedPage() {
  const navigate = useNavigate();
  const { t } = useTranslation("feed");
  const api = useApi();

  // The backend defines the sections and omits empty ones
  const [sections, loading, error] = useFetchOnce(() => api.feed.getSections());

  const renderSections = () => {
    if (error) {
      return (
        <Result
          status="error"
          title={t("loadError.title")}
          subTitle={t("loadError.subTitle")}
        />
      );
    }

    if (loading || !sections) {
      return (
        <Flex align="center" justify="center" style={{ height: "50vh" }}>
          <Spin />
        </Flex>
      );
    }

    if (sections.length === 0) {
      return (
        <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description={t("empty")} />
      );
    }

    return sections.map((section) => (
      <FeedRow key={`${section.type}-${section.property}`} section={section} />
    ));
  };

  return (
    <Flex
      vertical
      gap={tokens.gap.large}
      style={{ paddingBottom: tokens.padding.large }}
    >
      <SectionHeader
        title={t("title")}
        extra={
          <Button
            icon={<SearchOutlined />}
            aria-label={t("search")}
            onClick={() => navigate("/recipe-search")}
          />
        }
      />
      {renderSections()}
    </Flex>
  );
}
