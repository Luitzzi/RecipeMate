import { BrowserRouter, Route, Routes } from "react-router";
import { RootLayout } from "./page/layout/RootLayout.tsx";
import { DocsPage } from "./page/docs/DocsPage.tsx";
import { ComponentsDocsPage } from "./page/docs/ComponentsDocsPage.tsx";
import { CounteringTheCounter } from "#/page/counteringTheCounter/CounteringTheCounter.tsx";
import { TestPage } from "#/page/test/TestPage.tsx";
import { RecipeOverviewPage } from "#/page/recipe/overview/RecipeOverviewPage.tsx";
export const AllRoutes = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<RootLayout />}>
          <Route path={"/"} element={<CounteringTheCounter />} />
          <Route path={"/test"} element={<TestPage />} />
          <Route path={"/recipes/:recipeId"} element={<RecipeOverviewPage />} />
          <Route path={"/docs"} element={<DocsPage />} />
          <Route path={"/docs/components"} element={<ComponentsDocsPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};
