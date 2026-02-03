# Output Quotes - AI Agent Reference Guide

## Project Overview
**Output Quotes** is a full-stack web application that allows users to submit quotes, vote for submissions, and publish them. The application is deployed at https://output.center.

- **Backend**: Spring Boot application (Java 17) - professionally written
- **Frontend**: Vue.js 3 application with Vite - mostly "vibe-coded" (rapid prototyping style)
- **Database**: MariaDB with Liquibase migrations
- **Repository**: suohcnamiD/output-quotes (main branch)

---

## Technology Stack

### Backend
- **Framework**: Spring Boot 4.0.2
- **Language**: Java 17
- **Build Tool**: Gradle (Kotlin DSL)
- **ORM**: Spring Data JPA with Hibernate
- **Security**: Spring Security with session-based authentication
- **Database**: MariaDB
- **Migrations**: Liquibase
- **Mapping**: MapStruct 1.6.3
- **Validation**: Jakarta Validation
- **Testing**: JUnit 5, Testcontainers (MariaDB)

### Frontend
- **Framework**: Vue.js 3.4.15
- **Build Tool**: Vite 5.0.11
- **Router**: Vue Router 4.2.5
- **State Management**: Pinia 2.1.7
- **HTTP Client**: Axios 1.6.5
- **Type**: ES Modules

---

## Architecture & Project Structure

### Backend Structure (`src/main/java/me/dimanchous/quotesapp/`)

#### Core Components
- **`QuotesAppApplication.java`**: Main Spring Boot application entry point
  - Excludes `ErrorMvcAutoConfiguration` (custom error handling)

#### Configuration (`configuration/`)
- **`SecurityConfiguration.java`**: Spring Security setup
  - Session-based authentication
  - CORS configuration (localhost:5173, localhost:5174, output.center)
  - Role-based access control (USER, ADMIN)
  - Custom error handlers for 401/403
- **`MainConfiguration.java`**: General application configuration
- **`WebConfiguration.java`**: Web-specific configuration

#### Component Layer (`component/`)
- **`ExceptionHandlerProvider.java`**: Global exception handling
- **`Utilities.java`**: Common utility methods and response building

#### Feature Modules (`feature/`)
Each feature follows a consistent structure with:
- **Entity**: JPA entity class
- **Repository**: Spring Data JPA repository
- **Service**: Business logic layer
- **Controller**: REST API endpoints
- **Mapper**: MapStruct DTOs mapping
- **dto/**: Request/Response DTOs
- **exception/**: Feature-specific exceptions

##### Core Features:

1. **`authentication/`** - User authentication & registration
   - Login/Register/Logout endpoints
   - Session management
   - Password encryption (BCrypt)

2. **`authorization/`** - Role-based access control
   - `Role` enum (USER, ADMIN)

3. **`user/`** - User management
   - `ApplicationUser` entity
   - User repository and services

4. **`author/`** - Quote authors management
   - `Author` entity (firstName, lastName)
   - CRUD operations for authors

5. **`quote/`** - Quote entities
   - `Quote` entity (content, saidBy, saidAt)
   - Core quote data model

6. **`submission/`** - Quote submissions (pending approval)
   - Users submit quotes for review
   - Voting system for submissions
   - `SubmissionController`: GET /api/submissions, POST /api/submissions, POST /api/submissions/{id}/vote
   - State transitions: Draft → Published

7. **`publication/`** - Published quotes
   - Approved submissions become publications
   - Reaction system (like/love/etc.)
   - `PublicationController`: GET /api/publications, POST /api/publications/{id}/react
   - Public-facing quote display

8. **`vote/`** - Voting on submissions
   - `Vote` entity with composite key (`VoteId`)
   - Tracks user votes on submissions

9. **`reaction/`** - Reactions to publications
   - `Reaction` entity
   - User reactions (emojis/likes) on published quotes

10. **`administration/`** - Admin panel functionality
    - `AdministrationController`: Admin-only endpoints
    - Restricted to users with ADMIN role

#### Database (`src/main/resources/db/changelog/`)
- **`master.yaml`**: Liquibase changelog
  - Tables: authors, users, quotes, submissions, publications, votes, reactions
  - All use UUID primary keys

---

### Frontend Structure (`frontend/`)

#### Core Files
- **`index.html`**: Main HTML entry point
- **`vite.config.js`**: Vite build configuration
- **`package.json`**: Dependencies and scripts
  - `npm run dev`: Development server
  - `npm run build`: Production build
  - `npm run preview`: Preview production build

#### Source Structure (`src/`)

##### Main Files
- **`main.js`**: Application bootstrap (Vue app, Pinia, Router)
- **`App.vue`**: Root component
  - Top navigation bar
  - Profile menu with user avatar
  - Authentication state management
  - German language UI

##### Router (`router/`)
- **`index.js`**: Vue Router configuration
  - Routes: `/publications` (home), `/submit`, `/submissions`, `/admin`, `/login`, `/register`
  - Auth guards: `requiresAuth`, `requiresAdmin`, `guestOnly`

##### State Management (`stores/`)
- **`auth.js`**: Authentication state (Pinia)
  - User session, login/logout, role checks
- **`author.js`**: Authors data
- **`publication.js`**: Publications state
- **`quote.js`**: Quotes management
- **`submission.js`**: Submissions state

##### Services (`services/`)
- **`api.js`**: Axios configuration and base API setup
- **`authService.js`**: Authentication API calls
- **`authorService.js`**: Author CRUD operations
- **`publicationService.js`**: Publications API
- **`quoteService.js`**: Quotes API
- **`submissionService.js`**: Submissions API
- **`administrationService.js`**: Admin operations

##### Views (`views/`)
- **`PublicationsList.vue`**: Homepage - displays published quotes
- **`QuoteSubmit.vue`**: Submit new quote form
- **`SubmissionsList.vue`**: View all submissions with voting
- **`AdminPanel.vue`**: Admin dashboard (ADMIN only)
- **`Login.vue`**: Login page
- **`Register.vue`**: User registration

##### Components (`components/`)
- **`CustomCheckbox.vue`**: Reusable checkbox component
- **`CustomSelect.vue`**: Reusable select dropdown
- **`Toast.vue`**: Toast notification component

##### Composables (`composables/`)
- **`useToast.js`**: Toast notification logic

##### Assets (`assets/`)
- **`styles.css`**: Global styles

---

## Key Domain Concepts

### Quote Lifecycle
1. **Submission**: User submits a quote → creates `Submission` entity
2. **Voting**: Users vote on submissions → `Vote` entities track votes
3. **Publication**: Admin/system publishes approved submission → creates `Publication` entity
4. **Reaction**: Users react to published quotes → `Reaction` entities

### User Roles
- **USER**: Can submit quotes, vote on submissions, react to publications
- **ADMIN**: Can access admin panel, manage publications, moderate content

### Security Model
- **Session-based authentication** (no JWT)
- **BCrypt password hashing**
- **CORS enabled** for localhost and production domains
- **Endpoint security**:
  - Public: GET publications, GET submissions, GET authors, authentication endpoints
  - Authenticated: POST submissions, POST votes, POST reactions
  - Admin-only: Administration endpoints

---

## API Endpoints Reference

### Authentication (`/api/authentication/**`)
- All endpoints: `permitAll`
- POST `/api/authentication/login`
- POST `/api/authentication/register`
- POST `/api/authentication/logout`

### Submissions (`/api/submissions`)
- GET `/api/submissions`: `permitAll` - List submissions (paginated, sorted by votes)
- POST `/api/submissions`: `authenticated` - Create new submission
- POST `/api/submissions/{id}/vote`: `authenticated` - Vote for submission

### Publications (`/api/publications`)
- GET `/api/publications`: `permitAll` - List published quotes
- POST `/api/publications/{id}/react`: `authenticated` - React to publication

### Authors (`/api/authors`)
- GET `/api/authors`: `permitAll` - List authors

### Administration (`/api/administration/**`)
- All endpoints: `hasRole('ADMIN')` - Admin operations

---

## Development Patterns

### Backend Patterns
1. **Layered Architecture**: Controller → Service → Repository
2. **DTO Pattern**: Request/Response DTOs with MapStruct
3. **Exception Handling**: Custom exceptions with global handler
4. **Response Format**: Standardized via `Utilities.buildSuccess()` / `buildFailure()`
5. **Validation**: Jakarta Bean Validation on DTOs
6. **Pagination**: Spring Data's `Pageable` and `Page`

### Frontend Patterns
1. **Composition API**: Vue 3 Composition API style
2. **Pinia Stores**: Centralized state management
3. **Service Layer**: API calls abstracted in service files
4. **Route Guards**: Authentication/authorization checks
5. **Composables**: Reusable logic (e.g., `useToast`)

---

## Common Tasks & Workflows

### Adding a New Feature
**Backend**:
1. Create feature package under `feature/`
2. Define entity (JPA annotations)
3. Create repository (extend Spring Data repository)
4. Create DTOs in `dto/` subdirectory
5. Create mapper interface (MapStruct)
6. Implement service with business logic
7. Create controller with REST endpoints
8. Add custom exceptions in `exception/` subdirectory
9. Update `SecurityConfiguration` if needed
10. Create Liquibase changeset in `master.yaml`

**Frontend**:
1. Create view component in `views/`
2. Add route in `router/index.js`
3. Create service in `services/` for API calls
4. Create Pinia store in `stores/` if needed
5. Update navigation in `App.vue` if needed

### Database Changes
1. Add changeset to `src/main/resources/db/changelog/master.yaml`
2. Follow Liquibase best practices (incremental changesets)
3. Test with local MariaDB instance

### Running the Application
**Backend**:
```bash
./gradlew bootRun
```

**Frontend**:
```bash
cd frontend
npm run dev
```

---

## Code Style & Conventions

### Backend
- **Package naming**: `me.dimanchous.quotesapp.feature.<feature>`
- **Entity naming**: Singular nouns (e.g., `Quote`, `Author`)
- **Table naming**: Plural snake_case (e.g., `quotes`, `authors`)
- **ID type**: UUID for all entities
- **Exception naming**: `<Feature><Error>Exception` (e.g., `SubmissionNotFoundException`)

### Frontend
- **Component naming**: PascalCase (e.g., `CustomCheckbox.vue`)
- **File naming**: camelCase for JS, PascalCase for Vue components
- **Store naming**: Singular feature names (e.g., `auth.js`, `submission.js`)
- **Language**: UI text in German

---

## Important Notes for AI Agents

### When Working on This Project:
1. **Backend is production-quality**: Follow strict Java/Spring conventions
2. **Frontend is more casual**: "Vibe-coded" means pragmatic, rapid development style
3. **Security matters**: Always respect authentication/authorization rules
4. **Database migrations**: Never modify existing changesets, always add new ones
5. **API consistency**: Follow the established response format pattern
6. **German UI**: Frontend user-facing text is in German
7. **UUID usage**: All entities use UUID, not Long/Integer IDs
8. **MapStruct**: Use MapStruct for entity-DTO mapping, avoid manual mapping
9. **Repository ownership**: Backend by dimanchous, frontend is community-contributed
10. **Testing**: Backend has Testcontainers setup for integration tests

### Common Pitfalls to Avoid:
- Don't break existing Liquibase changesets
- Don't bypass Spring Security configuration
- Don't use JWT - this app uses sessions
- Don't forget CORS when adding new endpoints
- Don't mix German and English in the UI
- Don't use primitive IDs instead of UUIDs
- Don't skip validation on DTOs

### File Modification Guidelines:
- **High-risk files** (modify with caution):
  - `SecurityConfiguration.java`
  - `master.yaml` (Liquibase changelog)
  - `QuotesAppApplication.java`
- **Frequently modified files**:
  - Controllers (when adding endpoints)
  - Services (when adding business logic)
  - DTOs (when changing request/response shapes)
  - Vue views/components (UI changes)

---

## Testing Strategy

### Backend Testing
- **Unit tests**: Service layer business logic
- **Integration tests**: Controller endpoints with Testcontainers
- **Test database**: MariaDB via Testcontainers
- Run tests: `./gradlew test`

### Frontend Testing
- Currently minimal testing infrastructure
- Manual testing via development server

---

## Deployment

- **Production URL**: https://output.center
- **CORS**: Configured for localhost:5173, localhost:5174, and output.center
- **Database**: MariaDB (production instance details not in repository)

---

## Quick Reference Commands

```bash
# Backend - Build
./gradlew build

# Backend - Run
./gradlew bootRun

# Backend - Test
./gradlew test

# Frontend - Install dependencies
cd frontend && npm install

# Frontend - Development server
cd frontend && npm run dev

# Frontend - Build for production
cd frontend && npm run build

# Frontend - Preview production build
cd frontend && npm run preview
```

---

## Feature Matrix

| Feature | Backend Endpoint | Frontend Route | Auth Level | Status |
|---------|-----------------|----------------|------------|---------|
| User Registration | POST /api/authentication/register | /register | Guest | ✅ Active |
| User Login | POST /api/authentication/login | /login | Guest | ✅ Active |
| User Logout | POST /api/authentication/logout | N/A | Authenticated | ✅ Active |
| View Publications | GET /api/publications | /publications | Public | ✅ Active |
| React to Publication | POST /api/publications/{id}/react | N/A | Authenticated | ✅ Active |
| View Submissions | GET /api/submissions | /submissions | Public | ✅ Active |
| Submit Quote | POST /api/submissions | /submit | Authenticated | ✅ Active |
| Vote on Submission | POST /api/submissions/{id}/vote | N/A | Authenticated | ✅ Active |
| View Authors | GET /api/authors | N/A | Public | ✅ Active |
| Admin Panel | /api/administration/** | /admin | Admin | ✅ Active |

---

## Entity Relationships

```
User (ApplicationUser)
  ├─ has many → Submission (submittedBy)
  ├─ has many → Vote (votedBy)
  └─ has many → Reaction (reactedBy)

Author
  └─ has many → Quote (saidBy)

Quote
  ├─ belongs to → Author (saidBy)
  ├─ referenced by → Submission
  └─ referenced by → Publication

Submission
  ├─ belongs to → User (submittedBy)
  ├─ has many → Vote
  └─ references → Quote

Publication
  ├─ references → Quote
  └─ has many → Reaction

Vote (composite key: userId + submissionId)
  ├─ belongs to → User
  └─ belongs to → Submission

Reaction
  ├─ belongs to → User
  └─ belongs to → Publication
```

---

## Environment & Configuration

### Backend Configuration Files
- `application.properties` / `application.yml`: Database, server port, etc. (not in workspace view)
- `build.gradle.kts`: Dependencies and build configuration
- `settings.gradle.kts`: Gradle project settings

### Frontend Configuration Files
- `package.json`: Dependencies and scripts
- `vite.config.js`: Vite build configuration
- API base URL likely configured in `services/api.js`

---

This guide should be scanned at the start of each session to provide context about the Output Quotes application architecture, conventions, and best practices.
