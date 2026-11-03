import "./i18n/i18n.ts";

import { StrictMode } from "react";
import { createRoot } from "react-dom/client";

import { ApiProvider } from "./api/ApiProvider.tsx";
import { fetchHttpClient } from "./api/httpClient.ts";
import App from "./App.tsx";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <ApiProvider httpClient={fetchHttpClient}>
      <App />
    </ApiProvider>
  </StrictMode>,
);
