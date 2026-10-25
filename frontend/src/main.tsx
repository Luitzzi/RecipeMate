import "#/i18n/i18n.ts";

import { StrictMode } from "react";
import { createRoot } from "react-dom/client";

import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

import App from "./App.tsx";
import { ApiProvider } from "./api/ApiContext.tsx";
import { fetchHttpClient } from "./api/httpClient.ts";

const queryClient = new QueryClient();

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <ApiProvider httpClient={fetchHttpClient}>
        <App />
      </ApiProvider>
    </QueryClientProvider>
  </StrictMode>,
);
