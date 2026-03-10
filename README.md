# SAS Snippet Manager

A cross-platform application for managing SAS code snippets, macros, and reusable code blocks. Built with Kotlin Multiplatform and Compose Multiplatform, targeting Android, iOS, and Web from a single codebase.

## Overview

SAS developers often struggle with scattered code snippets across local files, wikis, and emails. SAS Snippet Manager provides a centralized repository where teams can store, search, and reuse SAS code — from PROC SQL joins to complex macros — with built-in AI-powered conversion to R.

## Features

- **Browse snippets** — scrollable list with title, type badge, description, and tags; light blue card styling
- **Search** — find snippets by keyword, description, or tag
- **Detail view** — full snippet details with syntax-highlighted SAS code and one-click copy
- **Create snippets** — add new snippets with title, type, description, code, and comma-separated tags
- **Edit snippets** — update any snippet field via a dedicated edit screen
- **Delete snippets** — remove snippets with a confirmation dialog
- **Snippet types** — `MACRO`, `DATA_STEP`, `PROC_SQL`, `REPORT`, `OTHER`
- **Reactive updates** — list refreshes automatically after create, edit, or delete via `SnippetEventBus`
- **SAS syntax highlighting** — keywords, comments, strings, numbers, and operators rendered in distinct colors
- **R syntax highlighting** — same color treatment for converted R code
- **AI-powered SAS → R conversion** — convert any snippet to a complete, runnable R program via Groq API (llama-3.3-70b-versatile); the generated R code includes all `library()` imports and inline comments
- **Save R code** — converted R code can be saved to the snippet and is displayed below the SAS code in the detail view with its own Copy button
- **Adaptive web layout** — list, detail, and create screens are capped at 900 dp and centered on wide displays; full-width on mobile

## Tech Stack

### Client (Kotlin Multiplatform)
- **Kotlin Multiplatform** — shared business logic across Android, iOS, Web (WASM + JS)
- **Compose Multiplatform** — shared UI across all platforms
- **Ktor Client** — HTTP communication with the backend
- **Koin** — dependency injection (shared + compose viewmodel modules)
- **Kotlin Coroutines + StateFlow** — async operations and reactive state
- **Redux / MVI architecture** — `Store → Reducer → Middleware → State → Intent` pattern

### Server
- **Ktor** — lightweight Kotlin backend framework
- **Exposed ORM** — type-safe SQL with Kotlin DSL
- **PostgreSQL** — relational database
- **HikariCP** — high-performance connection pooling
- **Groq API** — server-side proxy for LLM-powered SAS → R conversion
- **Docker Compose** — local database setup

### Deployment
- **Frontend** — Netlify (Kotlin/WASM static build)
- **Backend** — Railway (Ktor + PostgreSQL)

## Project Structure

```
SasSnippetManager/
├── composeApp/          # Shared Compose Multiplatform UI
│   └── src/
│       ├── commonMain/
│       │   └── ui/
│       │       ├── list/       # Snippet list screen + ViewModel + Middleware
│       │       ├── detail/     # Snippet detail screen + ViewModel + Middleware
│       │       ├── create/     # Create snippet screen + ViewModel + Middleware
│       │       ├── edit/       # Edit snippet screen + ViewModel
│       │       └── util/       # SasCodeFormatter, RCodeFormatter
│       └── webMain/            # WASM entry point (main.kt, Koin init)
├── shared/              # Shared KMP business logic
│   └── src/
│       └── commonMain/
│           ├── model/          # Data classes, enums, SnippetEventBus
│           ├── network/        # Ktor API client
│           └── repository/     # Repository layer
├── server/              # Ktor backend
│   └── src/main/kotlin/
│       ├── database/           # Exposed table definitions + DatabaseFactory
│       ├── models/             # Serializable data models
│       ├── repository/         # Database operations
│       └── routes/             # REST API endpoints + AI routes
├── iosApp/              # iOS entry point
└── docker-compose.yml   # PostgreSQL local setup
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/snippets` | Get all snippets |
| GET | `/api/snippets/{id}` | Get snippet by ID |
| GET | `/api/snippets/search?q=` | Search by keyword or tag |
| POST | `/api/snippets` | Create new snippet |
| PUT | `/api/snippets/{id}` | Update existing snippet |
| PATCH | `/api/snippets/{id}/r-code` | Save converted R code to a snippet |
| DELETE | `/api/snippets/{id}` | Delete snippet by ID |
| POST | `/api/ai/convert-to-r` | Convert SAS code to R via Groq LLM |

### Snippet model

Used in POST / PUT request body:

```json
{
  "title": "My Macro",
  "code": "%macro hello; %put Hello; %mend;",
  "description": "A simple hello macro",
  "tags": ["macro", "utility"],
  "category": "MACRO"
}
```

**Category values:** `MACRO`, `DATA_STEP`, `PROC_SQL`, `REPORT`, `OTHER`

The `rCode` field is returned in GET responses once R code has been saved for that snippet.

### AI conversion request

```json
{ "sasCode": "%macro hello; %put Hello; %mend;" }
```

The server proxies this to Groq and returns the R code as plain text. Requires a `GROQ_API_KEY` environment variable on the server.

## Navigation

| Route | Screen |
|-------|--------|
| `/` | Snippet list |
| `detail/{id}` | Snippet detail (SAS code + optional R code, Edit / Delete in TopAppBar) |
| `create` | Create new snippet |
| `edit/{id}` | Edit existing snippet |

## Getting Started

### Prerequisites
- Java 21 (LTS)
- Docker Desktop (for local PostgreSQL)
- IntelliJ IDEA or Android Studio

### Run the backend locally

```bash
# Start PostgreSQL
docker-compose up -d

# Run Ktor server (port 8080)
./gradlew :server:run
```

> For AI conversion set the `GROQ_API_KEY` environment variable before starting the server.

### Run Android

Open the project in IntelliJ IDEA and run the `composeApp` configuration on an Android emulator.

> **Note:** Android requires cleartext traffic to be enabled in the manifest for HTTP connections to a local emulator host.

### Run Web (development)

```bash
./gradlew kotlinWasmUpgradeYarnLock
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

> The webpack dev server runs on port 8080 by default. If the Ktor backend is also running locally, point the WASM platform config to the Railway production URL instead to avoid conflicts.

### Build for production (Web)

```bash
./gradlew :composeApp:wasmJsBrowserDistribution
```

Output is placed in `composeApp/build/dist/wasmJs/productionExecutable/`. Deploy this folder to Netlify (or any static host). A `_redirects` file is included in `webMain/resources` to handle SPA routing on Netlify:

```
/*    /index.html   200
```

## Architecture Notes

- **MVI / Redux pattern** — each screen has an `Intent` (user actions), `Reducer` (pure state transitions), `Middleware` (side effects: network, AI calls), and `State`. The `Store` wires them together.
- **Middleware refactored** — each `when` branch in every middleware is extracted into a named private `suspend fun` (e.g. `handleLoad`, `handleSaveEdit`, `handleConvertToR`, `handleSaveRCode`) for clarity and testability.
- **SnippetEventBus** — `SharedFlow`-based event bus propagating `SnippetCreated`, `SnippetUpdated`, and `SnippetDeleted` events across screens without tight coupling.
- **Repository layer** — shared module abstracts HTTP calls behind a repository interface, keeping ViewModels platform-agnostic.
- **`SchemaUtils.createMissingTablesAndColumns`** — used instead of `create` so new DB columns (e.g. `r_code`) are automatically added to an existing production database on deploy.
- **CORS** — `allowNonSimpleContentTypes = true` is required in the Ktor CORS config for POST / PATCH requests with a JSON body from browser clients.

## Environment Variables (Railway / server)

| Variable | Description |
|----------|-------------|
| `DATABASE_URL` | PostgreSQL connection URL |
| `GROQ_API_KEY` | API key for Groq LLM service |
| `X_API_KEY` | Shared secret expected in `X-API-Key` header from the client |

## Roadmap

- [x] AI-powered SAS → R conversion with library imports and inline comments
- [x] Save and display converted R code per snippet
- [x] Adaptive desktop/web layout (max-width 900 dp, centered)
- [x] SAS and R syntax highlighting
- [ ] Offline cache with SQLDelight
- [ ] User authentication
- [ ] AI-powered snippet tagging and description generation
