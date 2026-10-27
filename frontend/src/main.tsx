import { StrictMode } from "react";
import { createRoot } from "react-dom/client";

import App from "./App.tsx";
import { ApiProvider } from "./api/ApiProvider.tsx";
import { fetchHttpClient } from "./api/httpClient.ts";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <ApiProvider httpClient={fetchHttpClient}>
      <App />
    </ApiProvider>
  </StrictMode>,
);
