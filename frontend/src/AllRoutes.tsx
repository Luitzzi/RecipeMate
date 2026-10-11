import { BrowserRouter, Route, Routes } from "react-router";
import { RootLayout } from "./page/layout/RootLayout.tsx";
import {DocsPage} from "./page/docs/DocsPage.tsx";
import {ComponentsDocsPage} from "./page/docs/ComponentsDocsPage.tsx";
import {ExamplePage} from "./page/example/ExamplePage.tsx";
export const AllRoutes = () => {
    return (
        <BrowserRouter>
            <Routes>
                <Route element={<RootLayout />}>
                    <Route path={"/"} element={<ExamplePage />} />
                    <Route path={"/docs"} element={<DocsPage />} />
                    <Route path={"/docs/components"} element={<ComponentsDocsPage />} />
                </Route>
            </Routes>
        </BrowserRouter>
    );
};
