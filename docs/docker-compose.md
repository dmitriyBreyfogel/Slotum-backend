# Docker Compose

The project can now be started fully through Docker Compose.

## First-time setup

Copy the template:

```powershell
Copy-Item .env.example .env
```

Generate a JWT secret:

```powershell
.\docs\generate-jwt-secret.ps1
```

Put the generated value into `.env`:

```dotenv
JWT_SECRET=<your-secret>
```

## Start the whole project

From the repository root:

```powershell
docker compose up --build
```

Detached mode:

```powershell
docker compose up -d --build
```

This starts:

- `postgres`
- `backend`

The backend will be available on `http://localhost:8081`.

## Useful commands

Show containers:

```powershell
docker compose ps
```

Show backend logs:

```powershell
docker compose logs -f backend
```

Show postgres logs:

```powershell
docker compose logs -f postgres
```

Stop everything:

```powershell
docker compose down
```

Stop and remove database data:

```powershell
docker compose down -v
```

## Notes

- The backend waits for PostgreSQL through `depends_on` with a healthcheck.
- Inside Compose the backend connects to PostgreSQL with `DB_HOST=postgres`.
- If you change application source code, rebuild with `docker compose up --build`.
