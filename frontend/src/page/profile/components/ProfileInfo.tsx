import { Descriptions } from "antd";
import { useTranslation } from "react-i18next";
import { SectionHeader } from "#/common/components/SectionHeader.tsx";

// TODO: Replace with the name and email from the Zitadel token once OAuth is set up.
// Changing them will then happen in Zitadel, not in our database.
const TEST_USER_PROFILE = {
  name: "Test User",
  email: "test.user@example.com",
};

export const ProfileInfo = () => {
  const { t } = useTranslation();

  return (
    <>
      <SectionHeader title={t("profile.info.title")} />
      <Descriptions
        column={1}
        items={[
          {
            key: "name",
            label: t("profile.info.name"),
            children: TEST_USER_PROFILE.name,
          },
          {
            key: "email",
            label: t("profile.info.email"),
            children: TEST_USER_PROFILE.email,
          },
        ]}
      />
    </>
  );
};
