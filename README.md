# Software Engineering Template

## Configuration

```sh
install -m 0600 .env.example .env
sed -i "s/^POSTGRES_PASSWORD=.*/POSTGRES_PASSWORD=$(openssl rand -hex 16)/" .env
```

Get an Anthropic API key and paste it after `ANTHROPIC_API_KEY=` in `.env`.

## Production

Requires only **Docker & Docker Compose v2**.

To build the containers and run the services (frontend at `localhost:8080`), do:

```sh
docker compose -f compose.prod.yaml up -d --wait
```

## Development

### Setup

Requires:

- **Docker & Docker Compose v2** for the Postgres database
- **[mise](https://mise.jdx.dev/getting-started.html)** (installed and [activated](https://mise.jdx.dev/getting-started.html#activate-mise) in your shell) for Java and Node
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
# Starts the DB, backend and frontend (localhost:5173)
mise run dev

# Generates the frontend interfaces from all *UiDTO backend classes.
mise run ts-gen
```

## Docs

```sh
# Preview the docs
uv run mkdocs serve

# Build the docs
uv run mkdocs build
```
