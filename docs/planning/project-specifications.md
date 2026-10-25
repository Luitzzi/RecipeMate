# Project specifications

This section describes **what** RecipeMate does and **why**.

The current work status lives in the [GitHub Project](https://github.com/users/Luitzzi/projects/9), not here.
Here live the specifications of the epics and optionally their features.
The specs contain Ui-Drafts, research and useful aspects for presenting the epic.
It should not be an extra workload. Everything according work-planning should be in the Github project.

## Epics Overview

| Epics                            | Release | Priority | Spec                                                         | Epic                                                      | Assignee               |
|----------------------------------|---------|----------|--------------------------------------------------------------|-----------------------------------------------------------|------------------------|
| Page: Recipe Creation            | MVP     | Must     | [Recipe Creation](../epic-specs/recipe-creation)             | [GitHub](https://github.com/Luitzzi/RecipeMate/issues/11) | Luis                   |
| Cloudflare R2 Object Container   | MVP     | Must     | [Image Database](../epic-specs/image-database)               | [GitHub](https://github.com/Luitzzi/RecipeMate/issues/29) | Luis                   |
| Page: Recipe Search              | MVP     | Must     | -                                                            | -                                                         | Luis                   |
| Page: My-Recipes Folder overview | MVP     | Must     | -                                                            | -                                                         | Johannes               |
| Page: Rezepte Übersicht          | MVP     | Must     | [Recipe Overview](../epic-specs/recipe-overview)             | [GitHub](https://github.com/Luitzzi/RecipeMate/issues/19) | Oskar                  |
| Page: Rezepte Zubereitung        | MVP     | Must     | [Recipes Preparation](../epic-specs/recipe-preparation)      | [GitHub](https://github.com/Luitzzi/RecipeMate/issues/33) | Oskar                  |
| Page: Rezepte Feed               | -       | Must     | -                                                            | -                                                         |                        |
| Page: Meal prepping              | -       | Could    | -                                                            | -                                                         |                        |
| Page: Shopping Liste             | -       | Could    | [Shopping List](../epic-specs/shopping-list)                 | [GitHub](https://github.com/Luitzzi/RecipeMate/issues/32) | Oskar                  |
| Page: Profile Page               | -       | Must     | -                                                            | -                                                         | Florian                |
| Login: OAuth 2.0                 | -       | Must     | -                                                            | -                                                         | Florian                |
| AI recipe assistance - Chatbot   | -       | Must     | [AI recipe assistance](../epic-specs/ai-assistance)          | -                                                         | Johannes               |
| AI recipe assistance - Plugins   | -       | Should   | [AI recipe assistance](../epic-specs/ai-assistance)          | -                                                         | Oskar & Florian & Luis |
| Sharing, groups & feed           | -       | Could    | -                                                            | -                                                         |                        |

## Release plan

| Release                              | Goal                                                                          | Scope                                                                                                                     | Link                               |
|--------------------------------------|-------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| **MVP**</br>(Minimum viable product) | A single user can create, manage and cook recipes. He can ask a basic chatbot | **Pages:** Recipe Creation, Recipe List, Recipe Display, Recipe cooking </br> **Else:** Basic chatbot, Profile Page Draft | [Details](../releases/mvp-release) |
