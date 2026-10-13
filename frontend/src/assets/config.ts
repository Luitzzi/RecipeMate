export const config = {
    // Relative so requests go through the Vite dev proxy (see vite.config.ts)
    backendBaseUrl: `${window.location.origin}/api`,
} as const;
