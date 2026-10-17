# Uni Project Requirements

Overview of the required features for the uni project.

## 4x AI Features

| Feature                           | Description                                                                   | Issue                                                  | Assignee |
| --------------------------------- | ----------------------------------------------------------------------------- | ------------------------------------------------------ | -------- |
| 1. AI "Mate" Chat                 | Chat assistant with tool-call support                                         | [#14](https://github.com/Luitzzi/RecipeMate/issues/14) | Johannes |
| 2. Context & query tools          | Mate knows the current page and can look up and search recipes                | [#15](https://github.com/Luitzzi/RecipeMate/issues/15) |          |
| 3. Recipe creation & modification | Mate creates or changes recipes; a writer/evaluator agent loop checks quality | [#16](https://github.com/Luitzzi/RecipeMate/issues/16) |          |
| 4. Recipe scraping & conversion   | Website content pasted into the chat is converted into a recipe               | [#17](https://github.com/Luitzzi/RecipeMate/issues/17) |          |

## 4x External APIs

| API                              | Usage                                     | Assignee |
| -------------------------------- | ----------------------------------------- | -------- |
| 1. Anthropic API (via Spring AI) | LLM for all AI features                   | Johannes |
| 2. Resend                        | E-mail sending API                        |          |
| 3. Zitadel Cloud                 | Authentication (OAuth 2.0 / OIDC) and MFA | Luis     |
| 4. Cloudflare R2                 | Object storage for recipe images          |          |

## 4x Communication

Channel: E-Mail

| Communication                     | Description                                                                                                             | Assignee |
| --------------------------------- | ----------------------------------------------------------------------------------------------------------------------- | -------- |
| 1. E-mail confirmation & MFA      | Confirmation mail after registration and one-time codes for multi-factor authentication. (via Zitadel Cloud)            | Luis     |
| 2. Recipe conversion notification | Notifies the user when a recipe conversion (see AI feature #4) has finished                                             |          |
| 3. Newsletter                     | Weekly "Check out these recipes" mail with new and best-rated recipes. Users subscribe/unsubscribe in the user settings |          |
| 4. TBD                            |                                                                                                                         |          |
