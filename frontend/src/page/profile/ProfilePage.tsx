import { Flex } from "antd";
import { useTranslation } from "react-i18next";
import { SectionHeader } from "#/common/components/SectionHeader.tsx";
import { ProfileInfo } from "#/page/profile/components/ProfileInfo.tsx";
import { PreferencesForm } from "#/page/profile/components/PreferencesForm.tsx";

export const ProfilePage = () => {
  const { t } = useTranslation();

  return (
    <Flex vertical style={{ minHeight: "100%" }}>
      <SectionHeader
        title={t("profile.title")}
        description={t("profile.description")}
      />
      <ProfileInfo />
      <PreferencesForm />
    </Flex>
  );
};
