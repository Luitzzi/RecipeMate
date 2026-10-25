# Devlog: Johannes

## Backend
- `GlobalExceptionHandler` error handling

### "Mate" AI Companion
- **Chat:** API and service for talking to the assistant
- **Context:** the model knows what the user is viewing
- **Tools:**
  - recipe querying and creation (with AiDTOs and Persistance)
  - unit conversion
- **Prompts:**
  - `mate.md`: assistant behaviour
  - `recipe-validator.md`: rules for recipe validation

## User Mock
- `UserService` mock and `UserRepository` for seeding and querying mock users

## Frontend
- **API client:**
  - Typed TypeScript clients generated from the backend controllers and DTOs (`mise run ts-gen`)
  - Fetch-based request handler that the generated clients use
  - `ApiProvider` / `useApi` hook to access the API clients in components

## Infrastructure & Documentation
- Docs site with MkDocs & uv
- CI/CD: GitHub Actions workflows
  - Docs build and deploy on `main`
- Docker:
  - `Dockerfiles`: Postgres, Spring Boot backend and nginx-served frontend
  - `compose.yaml`: Postgres database for local development
  - `compose.prod.yaml`
- README setup steps (configuration, production, development and docs)
