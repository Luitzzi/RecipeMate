// api/ApiContext.tsx
import { createContext, useContext, type ReactNode } from "react";
import { createApi, type Api } from "./createApi";
import type { HttpClient } from "#/generated/interface-api.ts";

const ApiContext = createContext<Api | null>(null);

export function useApi(): Api {
    const api = useContext(ApiContext);
    if (!api) throw new Error("useApi must be used inside <ApiProvider>");
    return api;
}

export function ApiProvider({ httpClient, children, }: { httpClient: HttpClient; children: ReactNode; }) {
    const api = createApi(httpClient);
    return <ApiContext.Provider value={api}>{children}</ApiContext.Provider>;
}
