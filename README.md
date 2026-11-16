# RecipeMate

The documentation is hosted at [recipe.mate-universe.de](https://recipe.mate-universe.de/).

[UI-Planning](https://recipe.mate-universe.de/planning/ui-planning/) - [Project Specifications](https://recipe.mate-universe.de/planning/project-specifications/) - [Conventions](https://recipe.mate-universe.de/conventions/git-usage/)

## Configuration

```sh
install -m 0600 .env.example .env
sed -i "s/^POSTGRES_PASSWORD=.*/POSTGRES_PASSWORD=$(openssl rand -hex 16)/" .env
```

#### AI integration for the chatbot 'Mate'

Get an Anthropic API key and paste it after `ANTHROPIC_API_KEY=` in `.env`.

#### Credentials for the object storage Garage

Generate the S3 access key, secret key and the Garage secrets.
Garage creates the access key and bucket with these values on its first start.

```sh
sed -i \
  -e "s/^S3_ACCESS_KEY=.*/S3_ACCESS_KEY=GK$(openssl rand -hex 12)/" \
  -e "s/^S3_SECRET_KEY=.*/S3_SECRET_KEY=$(openssl rand -hex 32)/" \
  -e "s/^GARAGE_RPC_SECRET=.*/GARAGE_RPC_SECRET=$(openssl rand -hex 32)/" \
  -e "s/^GARAGE_ADMIN_TOKEN=.*/GARAGE_ADMIN_TOKEN=$(openssl rand -hex 32)/" \
  .env
```

| Variable             | Format                       |
|----------------------|------------------------------|
| `S3_ACCESS_KEY`      | `GK` + 24 hex characters     |
| `S3_SECRET_KEY`      | 64 hex characters            |
| `GARAGE_RPC_SECRET`  | 64 hex characters (32 bytes) |
| `GARAGE_ADMIN_TOKEN` | any random string            |

When changing the values later, reset the Garage volume with `docker compose down -v`
(also deletes the Postgres data).

## Production

Requires only **Docker & Docker Compose v2**.

To build the containers and run the services (frontend at `localhost:8080`), do:

```sh
docker compose -f compose.prod.yaml up -d --wait
```

## Development

### Setup

Requires:

- **Docker & Docker Compose v2** for the Postgres database and Garage object storage.
- **[mise](https://mise.jdx.dev/getting-started.html)** (installed
  and [activated](https://mise.jdx.dev/getting-started.html#activate-mise) in your shell) for Java and Node
- **[uv](https://docs.astral.sh/uv/getting-started/installation/)** for Python and the docs

Then install the pinned Java and Node versions from [mise.toml](mise.toml) and the npm dependencies:

```sh
mise trust
mise install
mise run install
```

### Tasks

All project tasks are defined in [mise.toml](mise.toml).
Run `mise tasks` to get a list of them.

Examples:

```sh
# Starts the DB, Garage (object storage), backend and frontend (localhost:5173)
mise run dev

# Generates the frontend interfaces from all *UiDTO backend classes
# and Api-Endpoints for *UiController backend classes.
mise run ts-gen

# List the number and size of images in garage
mise run s3:stats

# DESTRUCTIVE: deletes the DB data, Garage data, build outputs and dependencies
# docker compose down -v && rm -rf backend/target frontend/{node_modules,dist,src/generated} site .venv
mise run clean

# DESTRUCTIVE: clean + install + ts-gen
mise run reset
```

## Docs

```sh
# Preview the docs
uv run mkdocs serve

# Build the docs
uv run mkdocs build
```
