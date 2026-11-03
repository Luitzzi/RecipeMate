import { Button } from "antd";
import { useNavigate } from "react-router";

export default function RecipeOverviewPage() {
  const navigate = useNavigate();

  return (
    <Button type={"primary"} onClick={() => navigate("/recipe-creation")}>
      Create Recipe
    </Button>
  );
}
