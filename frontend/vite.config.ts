import react from "@vitejs/plugin-react";
import path from "path";
import { defineConfig, loadEnv } from "vite";

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, path.resolve(import.meta.dirname, ".."), "");

  return {
    plugins: [react()],
    server: {
      proxy: {
        "/api": env.BACKEND_URL ?? "http://localhost:8080",
      },
    },
  };
});