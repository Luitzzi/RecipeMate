# Uni Project Requirements

Overview of the required features for the uni project.

The requirements are listet [here](../assets/se-project-requirements.pdf).
Following are all requirements by their number in the requirements-pdf.

## 1. Four features for every person with all necessary CRUDs

The features are defined in the [project specifications](../project-specifications).

Necessary CRUDs for entities:

- UserBE
- RecipeBE
- RecipeProperties -> RecipeCuisineBE, RecipePropertyBE
- IngredientBE

### For at least two user-profiles

- **User:** Default user of the application. Can only see his own recipes
- **Admin:** Can see all users and the recipes they created.

## 2. One Rest-API for every person

**Internal Rest-APIs:**

- RecipeBE
- RecipeCuisineBE
- RecipePropertyBE
- IngredientBE

**External Rest-APIs:**

| API                              | Usage                                     | Assignee |
|----------------------------------|-------------------------------------------|----------|
| 1. Anthropic API (via Spring AI) | LLM for all AI features                   | Johannes |
| 2. Resend                        | E-mail sending API                        |          |
| 3. Zitadel Cloud                 | Authentication (OAuth 2.0 / OIDC) and MFA |          |
| 4. Cloudflare R2 (has 10GB free) | Object storage for recipe images          |          |

## 3. Pagination for every person

- Folders in the personal recipe overview
- Recipes in the recipe search
- Users in the admin overview page
- ?

## 4. Communication for every person

Channel: E-Mail

| Communication                     | Description                                                                                                             | Assignee |
|-----------------------------------|-------------------------------------------------------------------------------------------------------------------------|----------|
| 1. E-mail confirmation & MFA      | Confirmation mail after registration and one-time codes for multi-factor authentication. (via Zitadel Cloud)            |          |
| 2. Recipe conversion notification | Notifies the user when a recipe conversion (see AI feature #4) has finished                                             |          |
| 3. Newsletter                     | Weekly "Check out these recipes" mail with new and best-rated recipes. Users subscribe/unsubscribe in the user settings |          |
| 4. Welcome back mail              | If the user did not login for x days he gets a welcome back email                                                       |          |

## 5. Frontend

We use Typescript with React and Ant Design as the design system.

Further specifications are defined in the [conventions](../../conventions/frontend/design-system)

## 6. Login with Spring Security

**Authentication service:** OAuth2 with Zitadel Cloud

The user specific properties are managed in the profile page. The research is documented in
the [epic specs](../epic-specs/login-oauth) to be presented
to the team members.

## 7. Non-functional requirements for every person

| Non-functional requirement                  | Usage                                                                                                                                                                       | Assignee |
|---------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------|
| 1. Internationalisation                     | Translate the UI in the frontend                                                                                                                                            | Oskar    |
| 2. Central authentication service           | The authentication service should be easy to scale and to use for other applications. For example HolidayMate.                                                              |          |
| 3. Consistent design system in the frontend | All styling related values should be consistent. The colors predefined and easy to use in the code                                                                          |          |
| 4. Consistent coding style in the backend   | Consistent coding style specified in the [conventions](../conventions/backend/bce-pattern-and-data-flow)                                                                    |          |
| 5. Visible project state and documentation  | The docs are rendered as mkdocs and automatically hosted on GitHub pages via a CI/CD pipeline. The application is hosted on a server for presentation and testing purposes. |          |

## 8. AI-Feature for every person

| Feature                           | Description                                                                   | Issue                                                  | Assignee |
|-----------------------------------|-------------------------------------------------------------------------------|--------------------------------------------------------|----------|
| 1. AI "Mate" Chat                 | Chat assistant with tool-call support                                         | [#14](https://github.com/Luitzzi/RecipeMate/issues/14) | Johannes |
| 2. Context & query tools          | Mate knows the current page and can look up and search recipes                | [#15](https://github.com/Luitzzi/RecipeMate/issues/15) |          |
| 3. Recipe creation & modification | Mate creates or changes recipes; a writer/evaluator agent loop checks quality | [#16](https://github.com/Luitzzi/RecipeMate/issues/16) |          |
| 4. Recipe scraping & conversion   | Website content pasted into the chat is converted into a recipe               | [#17](https://github.com/Luitzzi/RecipeMate/issues/17) |          |
