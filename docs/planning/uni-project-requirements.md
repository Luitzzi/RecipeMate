# Uni Project Requirements

Overview of the required features for the uni project.

The requirements are listet [here](../assets/se-project-requirements.pdf).
Following are all requirements by their number in the requirements-pdf.

## 1. Four features for every person with all necessary CRUDs

| Feature                                                    | Specification                                                                            | Priority | Complexity | Assignee |
| ---------------------------------------------------------- | ---------------------------------------------------------------------------------------- | -------- | ---------- | -------- |
| 1. Create the data structure for recipes                   | CRUDs for RecipeEntity, IngredientEntity, StepEntity                                     | Must     | High       | Luis     |
| 2. Implement the Recipe-Creation Page                      | Implement the UI for the Recipe-Creation Page and post the created recipe to the backend | Must     | Medium     | Luis     |
| 3. Save images for recipes                                 | Save images on a dedicated object storage and make them accessible to the Backend        | Must     | Medium     | Luis     |
| 4. Implement a search page for recipes with filter options | Uses the get-Endpoint of the RecipeController and provides the filter options            | Must     | Medium     | Luis     |
| 5. Implement AI recipe creation via image                  | Create a recipe from an image                                                            | Could    | High       | Luis     |
|                                                            |                                                                                          |          |            |          |
| 1. AI "Mate" Chat                                          | See AI-Features 1-3                                                                      | Must     | High       | Johannes |
| 2. Share a recipe via E-Mail                               | See Communication 2                                                                      | Could    | Low        | Johannes |
| 3. Recipe Folder Entity & Folder Overview                  | Per-user folders to allow grouping recipes                                               | Should   | Low        | Johannes |
| 4. Project Documentation & Application Build via CI/CD     | See Nonfunctional 5                                                                      | Should   | Medium     | Johannes |
|                                                            |                                                                                          |          |            |          |

### Entities and their CRUD-Operations

| Entity             | Usage                                                                                      | Assignee |
|--------------------|--------------------------------------------------------------------------------------------|----------|
| UserEntity         | Create/Read/Update/Delete app-user specific information. Personal data is saved in OAuth.  | Florian  |
| RecipeEntity       | Create/Read/Update/Delete recipes with their steps and ingredients                         | Luis     |
| FolderEntity       | Create/Read/Update/Delete folders containing recipes. A folder belongs to exactly one user | Johannes |
| ShoppingListEntity | Add/Delete ingredients and items to the user specific shopping list                        | Oskar    |
| MealPreppingEntity | Contains recipes for exactly one user                                                      | Oskar    |

### For at least two user-profiles

- **User:** Default user of the application. Can only see his own recipes
- **Admin:** Can see all users and the recipes they created.

## 2. One Rest-API for every person

**Internal Rest-APIs:**

See 1. Entities and their CRUD-Operations

**External Rest-APIs:**

| API                              | Usage                                     | Assignee |
|----------------------------------|-------------------------------------------|----------|
| 1. Anthropic API (via Spring AI) | LLM for all AI features                   | Johannes |
| 2. Resend                        | E-mail sending API                        | Oskar    |
| 3. Zitadel Cloud                 | Authentication (OAuth 2.0 / OIDC) and MFA | Florian  |
| 4. Cloudflare R2 (has 10GB free) | Object storage for recipe images          | Luis     |

## 3. Pagination for every person

| Component                        | Usage                                                      | Assignee |
|----------------------------------|------------------------------------------------------------|----------|
| 1. Folder Pagination             | Display the folders a user created in pages                | Johannes |
| 2. Recipe Pagination             | Display the result of the recipe search in pages           | Luis     |
| 3. Endless-scroll of recipes     | Display the suggested recipes in a vertical endless-scroll | Oskar    |
| 4. Admin user overview           | Display all users in pages                                 | Florian  |
| 5. Admin user's recipes overview | Display all recipes of a user in pages                     | Florian  |

## 4. Communication for every person

Channel: E-Mail

| Communication                | Description                                                                                                             | Assignee |
| ---------------------------- | ----------------------------------------------------------------------------------------------------------------------- | -------- |
| 1. E-mail confirmation & MFA | Confirmation mail after registration and one-time codes for multi-factor authentication. (via Zitadel Cloud)            | Florian  |
| 2. Share a recipe via E-Mail | Share a recipe to an E-Mail. They get a static version of the recipe.                                                   | Johannes |
| 3. Newsletter                | Weekly "Check out these recipes" mail with new and best-rated recipes. Users subscribe/unsubscribe in the user settings | Luis     |
| 4. Welcome back mail         | If the user did not login for x days he gets a welcome back email                                                       | Oskar    |

## 5. Frontend

We use Typescript with React and Ant Design as the design system.

Further specifications are defined in the [conventions](../../conventions/frontend/design-system)

## 6. Login with Spring Security

**Authentication service:** OAuth2 with Zitadel Cloud

The user specific properties are managed in the profile page. The research is documented in
the [epic specs](../epic-specs/login-oauth) to be presented
to the team members.

## 7. Non-functional requirements for every person

| Non-functional requirement                             | Usage                                                                                                                                                                       | Assignee |
| ------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------- |
| 1. Internationalisation                                | Translate the UI in the frontend                                                                                                                                            | Oskar    |
| 2. Central authentication service                      | The authentication service should be easy to scale and to use for other applications. For example HolidayMate.                                                              | Florian  |
| 3. Consistent design system in the frontend            | All styling related values should be consistent. The colors predefined and easy to use in the code                                                                          | Luis     |
| 4. Consistent coding style in the backend              | Consistent coding style specified in the [conventions](../conventions/backend/bce-pattern-and-data-flow)                                                                    | Luis     |
| 5. Project Documentation & Application Build via CI/CD | The docs are rendered as mkdocs and automatically hosted on GitHub pages via a CI/CD pipeline. The application is hosted on a server for presentation and testing purposes. | Johannes |

## 8. AI-Feature for every person

| Feature                           | Description                                                                             | Assignee |
| --------------------------------- | --------------------------------------------------------------------------------------- | -------- |
| 1. AI "Mate" Chat                 | Chat assistant with tool-call support                                                   | Johannes |
| 2. Context                        | Mate knows context about which page the user is currently viewing                       | Johannes |
| 3. Recipe creation & modification | Mate creates or changes recipes; a writer/evaluator agent loop checks quality           | Johannes |
| 4. Query tools                    | Mate has tools for: Search (with parameters, like in recipe search page); Folders; ...  | Oskar    |
| 5. Recipe scraping & conversion   | Website content pasted into the chat is converted into a recipe                         | Florian  |
| 6. Recipe creation from images    | Convert an image via OCR to markdown and pass it to the agent. Used for recipe creation | Luis     |
