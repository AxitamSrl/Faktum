# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

### Backend (Spring Boot 3.4.1, Java 17)
```bash
# Run with dev profile (requires PostgreSQL on 5432, Redis on 6900)
cd backend && mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Build WAR (skipping tests)
cd backend && mvn package -DskipTests

# Docker build (multi-stage, embeds frontend)
cd backend && docker build -t faktum .
```

### Frontend (Angular 19, standalone components)
```bash
cd frontend && npm install
cd frontend && npm start        # Dev server on :4200, proxies to :8080
cd frontend && npm run build    # Prod build → dist/frontend/browser
cd frontend && npm test         # Karma/Jasmine tests
```

### Full stack (from root)
```bash
mvn clean install   # Builds frontend first, copies to backend/target/classes/static, then packages WAR
```

## Architecture

**Monorepo Maven multi-module** — frontend is embedded into the backend WAR for production deployment.

### Backend — `com.faktum`

| Layer | Key classes |
|-------|-------------|
| Controllers | `FicheController` (`/api/fiches`), `QuestionController` (`/api/questions`), `CategoryController`, `StatisticsController` |
| Services | `FicheService` (CRUD + publish/archive/translate), `QuestionService` (CRUD + approve/reject/answer), `SearchService` (Meilisearch), `CacheService`, `RevalidationService` |
| Models | `Fiche` → `FicheVersion` (1:N, versioned per locale), `Question`, `Category`, `CrawlLog` |
| Config | `CorsConfig` (dev only), `RedisConfig` (dev only, 30-60 min TTL), `MeilisearchConfig` (conditional), `SpaWebConfig` (fallback to index.html) |

**Key design decisions:**
- IDs generated via `CuidGenerator` (not DB sequences)
- `FicheVersion` stores localized content with JSONB columns (`proArgs`, `contraArgs`, `data`, `sources`) — unique constraint on `(ficheId, locale, version)`
- Four fiche types (`ANALYSE`, `PRATIQUE`, `RECETTE`, `GUIDE`) with type-specific content validation on create/update
- Profiles: `dev` (Redis + Meilisearch enabled, CORS for localhost:4200), `prod` (both disabled, relative API paths)

### Frontend

- **Standalone components** (no NgModules), lazy-loaded routes: Home, Fiche detail, Questions
- **i18n**: `I18nService` loads JSON files (`fr.json`, `nl.json`, `de.json`, `en.json`), persists locale in `localStorage` key `faktum-locale`, `dbLocale` computed property returns uppercase for API
- **LocaleInterceptor**: injects `Accept-Language` header on every HTTP request
- **Styling**: Tailwind CSS v4 with custom theme in `styles.scss` — colors: `faktum-dark`, `faktum-blue`, `faktum-accent`, `faktum-pro` (green), `faktum-contra` (red); fonts: Inter (sans), Merriweather (serif)
- **Environment**: dev → `http://localhost:8080/api`, prod → `/api` (same origin)

### Database (PostgreSQL + Flyway)

Custom PostgreSQL enums: `Locale` (FR/NL/DE/EN), `FicheStatus` (DRAFT/PUBLISHED/ARCHIVED), `FicheType` (ANALYSE/PRATIQUE/RECETTE/GUIDE), `QuestionStatus` (PENDING/APPROVED/ANSWERED/REJECTED).

Single baseline migration (`V1__baseline_prisma_schema.sql`). Flyway runs in baseline-on-migrate mode.

## Supported Locales

French (FR), Dutch (NL), German (DE), English (EN) — this is a multilingual fact-checking platform for Belgium.
