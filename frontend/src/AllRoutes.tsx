import { BrowserRouter, Route, Routes } from "react-router";
import { RootLayout } from "./page/layout/RootLayout.tsx";

import FeedPage from "#/page/feed/FeedPage.tsx";
import ProfilePage from "#/page/profile/ProfilePage.tsx";
import RecipeOverviewPage from "#/page/recipe/RecipeOverviewPage.tsx";
import ShoppingListPage from "#/page/shopping-list/ShoppingListPage.tsx";
import MealPreppingPage from "#/page/meal-prepping/ShoppingListPage.tsx";

export const AllRoutes = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<RootLayout />}>
          <Route path={"/shopping-list"} element={<ShoppingListPage />} />
          <Route path={"/meal-prepping"} element={<MealPreppingPage />} />
          <Route path={"/"} element={<FeedPage />} />
          <Route path={"/recipe/:id"} element={<RecipeOverviewPage />} />
          <Route path={"/profile"} element={<ProfilePage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
};
