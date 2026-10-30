import { useParams } from "react-router";

/* /recipe/:id */
export default function RecipeOverviewPage() {
  const { id } = useParams();

  return <>recipe: {id}</>;
}
