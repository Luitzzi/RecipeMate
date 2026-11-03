import { BrowserRouter, Route, Routes } from "react-router";

import FeedPage from "#/page/feed/FeedPage.tsx";
import ProfilePage from "#/page/profile/ProfilePage.tsx";
import RecipeOverviewPage from "#/page/recipe/RecipeOverviewPage.tsx";
import { RecipeCreationPage } from "#/page/recipe_creation/RecipeCreationPage.tsx";
import { RecipeSearchPage } from "#/page/recipe_search/RecipeSearchPage.tsx";
import ShoppingListPage from "#/page/shopping-list/ShoppingListPage.tsx";
import { TestPage } from "#/page/test/TestPage.tsx";

import { ComponentsDocsPage } from "./page/docs/ComponentsDocsPage.tsx";
import { DocsPage } from "./page/docs/DocsPage.tsx";
import { RootLayout } from "./page/layout/RootLayout.tsx";
import {MealPreppingPage} from "#/page/meal-prepping/MealPreppingPage.tsx";

export const AllRoutes = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<RootLayout />}>
          <Route path={"/"} element={<FeedPage />} />
          <Route path={"/my-recipes"} element={<RecipeOverviewPage />} />
          <Route path={"/recipe/:id"} element={<RecipeOverviewPage />} />
          <Route path={"/recipe-search"} element={<RecipeSearchPage />} />
          <Route path={"/recipe-creation"} element={<RecipeCreationPage />} />
          <Route path={"/meal-prepping"} element={<MealPreppingPage />} />
          <Route path={"/shopping-list"} element={<ShoppingListPage />} />
          <Route path={"/profile"} element={<ProfilePage />} />

          <Route path={"/test"} element={<TestPage />} />
          <Route path={"/docs"} element={<DocsPage />} />
          <Route path={"/docs/components"} element={<ComponentsDocsPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};
