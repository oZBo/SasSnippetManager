# SAS Snippet Manager

A cross-platform application for managing SAS code snippets, macros, and reusable code blocks. Built with Kotlin Multiplatform and Compose Multiplatform, targeting Android, iOS, and Web from a single codebase.

## Overview

SAS developers often struggle with scattered code snippets across local files, wikis, and emails. SAS Snippet Manager provides a centralized repository where teams can store, search, and reuse SAS code — from PROC SQL joins to complex macros.

## Features

- **Browse snippets** — scrollable list with title, type badge, description, and tags
- **Search** — find snippets by keyword, description, or tag
- **Detail view** — full snippet details with monospace code display and one-click copy
- **Create snippets** — add new snippets with title, type, description, code, and comma-separated tags
- **Edit snippets** — update any snippet field via a dedicated edit screen
- **Delete snippets** — remove snippets with a confirmation dialog
- **Snippet types** — MACRO, DATA\_STEP, PROC\_SQL, REPORT, OTHER
- **Reactive updates** — list refreshes automatically after create, edit, or delete via `SnippetEventBus`

## Tech Stack

### Client (Kotlin Multiplatform)
- **Kotlin Multiplatform** — shared business logic across Android, iOS, Web
- **Compose Multiplatform** — shared UI across all platforms
- **Ktor Client** — HTTP communication with the backend
- **Kotlin Coroutines + StateFlow + SharedFlow** — async operations and reactive state

### Server
- **Ktor** — lightweight Kotlin backend framework
- **Exposed ORM** — type-safe SQL with Kotlin DSL
- **PostgreSQL** — relational database
- **HikariCP** — high-performance connection pooling
- **Docker Compose** — local database setup

## Project Structure

```
SasSnippetManager/
├── composeApp/          # Shared Compose Multiplatform UI
│   └── src/
│       └── commonMain/
│           └── ui/
│               ├── list/       # Snippet list screen + ViewModel
│               ├── detail/     # Snippet detail screen + ViewModel
│               ├── create/     # Create snippet screen + ViewModel
│               └── edit/       # Edit snippet screen + ViewModel
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
│       └── routes/             # REST API endpoints
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
| DELETE | `/api/snippets/{id}` | Delete snippet by ID |

### Request / Response

**Snippet model** (used in POST and PUT request body):
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

## Navigation

| Route | Screen |
|-------|--------|
| `/` | Snippet list |
| `detail/{id}` | Snippet detail (with Edit / Delete actions in TopAppBar) |
| `create` | Create new snippet |
| `edit/{id}` | Edit existing snippet |

## Getting Started

### Prerequisites
- Java 21 (LTS)
- Docker Desktop
- Android Studio or IntelliJ IDEA

### Run the backend

```bash
# Start PostgreSQL
docker-compose up -d

# Run Ktor server
./gradlew :server:run
```

### Run Android
Open the project in IntelliJ IDEA and run the `composeApp` configuration on an Android emulator.

> **Note:** Android requires cleartext traffic to be enabled in the manifest for HTTP connections to a local emulator host.

### Run Web

```bash
./gradlew kotlinWasmUpgradeYarnLock
./gradlew :composeApp:wasmJsBrowserDevelopmentRun
```

## Architecture Notes

- **SnippetEventBus** — `SharedFlow`-based event bus that propagates `SnippetCreated`, `SnippetUpdated`, and `SnippetDeleted` events across screens without tight coupling.
- **ViewModel + StateFlow + Result pattern** — each screen has its own ViewModel exposing UI state as `StateFlow<Result<T>>`.
- **Repository layer** — shared module abstracts HTTP calls behind a repository interface, keeping ViewModels platform-agnostic.

## Roadmap

- [ ] Offline cache with SQLDelight
- [ ] User authentication
- [ ] AI-powered snippet tagging and explanation
