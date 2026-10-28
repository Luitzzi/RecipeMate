import { App as AntApp, ConfigProvider } from "antd";
import deDE from "antd/locale/de_DE";
import enUS from "antd/locale/en_US";
import { useTranslation } from "react-i18next";
import { AllRoutes } from "./AllRoutes.tsx";
import { themeConfig } from "./theme/tokens.ts";
import "./css/index.css";

const antdLocales = { de: deDE, en: enUS };

const App = () => {
  const { i18n } = useTranslation();
  const antdLocale =
    antdLocales[i18n.resolvedLanguage as keyof typeof antdLocales] ?? enUS;

  return (
    <ConfigProvider theme={themeConfig} locale={antdLocale}>
      <AntApp>
        <AllRoutes />
      </AntApp>
    </ConfigProvider>
  );
};

export default App;
