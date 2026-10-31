import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
  RouterProvider,
} from "react-router";
import { RootLayout } from "./page/layout/RootLayout.tsx";
import { DocsPage } from "./page/docs/DocsPage.tsx";
import { ComponentsDocsPage } from "./page/docs/ComponentsDocsPage.tsx";
import { TestPage } from "#/page/test/TestPage.tsx";
import { RecipeCreationPage } from "#/page/recipe_creation/RecipeCreationPage.tsx";
import { JohannesTestPage } from "#/page/test/JohannesTestPage.tsx";

const router = createBrowserRouter(
  createRoutesFromElements(
    <Route element={<RootLayout />}>
      <Route path={"/"} element={<></>} />
      <Route path={"/recipe-creation"} element={<RecipeCreationPage />} />
      <Route path={"/test"} element={<TestPage />} />
      <Route
        path={"/joe-test"}
        element={<JohannesTestPage />}
        handle={{ aiChat: true }}
      />
      <Route path={"/docs"} element={<DocsPage />} />
      <Route path={"/docs/components"} element={<ComponentsDocsPage />} />
    </Route>,
  ),
);

export const AllRoutes = () => <RouterProvider router={router} />;
