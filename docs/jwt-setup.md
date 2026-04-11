# JWT setup

## What this secret is

`JWT_SECRET` is the server-side signing key. It is not a user token.

The app uses it to:

- sign access tokens on `/api/v1/auth/login`
- verify incoming `Authorization: Bearer <token>` headers

Keep the same secret while existing tokens should remain valid. If you generate a new secret, all previously issued tokens become invalid.

## 1) Generate secret once per environment

Run in PowerShell from repo root:

```powershell
.\docs\generate-jwt-secret.ps1
```

This prints a Base64 string with 32 random bytes before Base64 encoding.

## 2) Provide secret to app

Set environment variable `JWT_SECRET`.

PowerShell (current session):

```powershell
$env:JWT_SECRET = "<paste here>"
```

IntelliJ IDEA:

- Run/Debug Configuration -> **Environment variables** -> add `JWT_SECRET=<paste here>`

The app reads it via `app.jwt.secret=${JWT_SECRET:CHANGE_ME}` in `src/main/resources/application.properties`.

If the value is missing, invalid, or left as `CHANGE_ME`, the app fails on startup.

## Request flow

1. Client sends `POST /api/v1/auth/login` with email and password.
2. Server responds with `accessToken` and `tokenType`.
3. Client sends protected requests with:

```http
Authorization: Bearer <accessToken>
```

By default, the access token TTL is configured in `app.jwt.accessTtl`.
