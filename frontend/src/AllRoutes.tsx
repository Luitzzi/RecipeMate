import { BrowserRouter, Route, Routes } from "react-router";
import { RootLayout } from "./page/layout/RootLayout.tsx";
import { DocsPage } from "./page/docs/DocsPage.tsx";
import { ComponentsDocsPage } from "./page/docs/ComponentsDocsPage.tsx";
import { TestPage } from "#/page/test/TestPage.tsx";
import { RecipeCreationPage } from "#/page/recipe_creation/RecipeCreationPage.tsx";
export const AllRoutes = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<RootLayout />}>
          <Route path={"/recipe-creation"} element={<RecipeCreationPage />} />
          <Route path={"/test"} element={<TestPage />} />
          <Route path={"/docs"} element={<DocsPage />} />
          <Route path={"/docs/components"} element={<ComponentsDocsPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};
