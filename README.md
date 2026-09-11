# Personal Bookmark Organizer API

A Spring Boot REST API for organizing private bookmarks into collections and
attaching reusable tags. Supabase provides authentication and PostgreSQL
hosting, while Spring Boot owns application data, authorization, and business
rules.

## Stack

- Java 21
- Spring Boot 4.1
- Spring Web MVC
- Spring Security OAuth2 Resource Server
- Spring Data JPA and Hibernate
- Supabase Auth and PostgreSQL
- Flyway
- Maven
- Docker

## Main Features

- Create and manage a local profile linked to a Supabase Auth identity
- Create private bookmark collections
- Create, update, delete, filter, and paginate bookmarks
- Create reusable user-owned tags
- Attach and remove tags from bookmarks
- Enforce resource ownership in the service layer
- Fetch paginated bookmarks and tags without an N+1 query

## Domain Model

```text
Supabase Auth user
        |
        | JWT subject UUID
        v
Application profile
        |
        | 1:N
        v
Collection
        |
        | 1:N
        v
Bookmark
        |
        | 1:N
        v
BookmarkTag
        |
        | N:1
        v
Tag
```

`BookmarkTag` is an explicit join entity between bookmarks and tags. This keeps
the relationship extensible and allows the join table to store its own
`created_at` timestamp.

Bookmark statuses are `UNREAD`, `READ`, and `ARCHIVED`.

## Authentication and Authorization

Supabase owns signup, login, password management, and token issuance. Spring
Boot acts as an OAuth2 Resource Server:

- Reads the bearer token from the request
- Verifies the ES256 signature using Supabase JWKS
- Validates the issuer, audience, and expiration
- Reads the authenticated user UUID from the JWT subject
- Enforces collection, bookmark, and tag ownership in the service layer

```text
Client signs in through Supabase
        |
        | access-token JWT
        v
Spring Security validates the token
        |
        | verified JWT subject
        v
Service layer applies ownership rules
```

The client sends the Supabase access token with protected requests:

```http
Authorization: Bearer <access-token>
```

The backend has no password column, login endpoint, custom JWT generator, or
custom JWT filter.

## Database

Flyway owns the database schema and Hibernate validates it at startup:

- `V1__create_tables.sql`: users, collections, bookmarks, tags, and
  bookmark-tag links
- `V2__enable_rls.sql`: Row Level Security on application tables
- `V3__add_updated_at_triggers.sql`: database-managed update timestamps
- `V4__add_unique_collection_name_per_user.sql`: unique collection names per
  user

The application connects through the Supabase PostgreSQL session pooler. RLS
has no client-facing policies; application data is accessed through the API and
its service-layer authorization checks.

## Bookmark Tag Fetching

Bookmark responses include their tags. The repository uses an entity graph
with these Java relationship paths:

```text
bookmarkTags
bookmarkTags.tag
```

This tells Hibernate to load the `BookmarkTag` links and each linked `Tag` as
part of the bookmark query's fetch plan. DTO mapping can then read those
relationships without issuing one additional query per bookmark. Hibernate 7.4
can paginate the bookmarks before joining the tag collection on PostgreSQL.

## Configuration

The committed `application.yml` reads these environment variables:

| Variable | Purpose | Required |
|---|---|---|
| `SPRING_DATASOURCE_URL` | Supabase PostgreSQL JDBC URL | Yes |
| `SPRING_DATASOURCE_USERNAME` | Supabase pooler username | Yes |
| `SPRING_DATASOURCE_PASSWORD` | Database password | Yes |
| `DB_POOL_SIZE` | Hikari maximum pool size | No; defaults to `5` |
| `SUPABASE_JWT_ISSUER` | Expected JWT issuer | Yes |
| `SUPABASE_JWKS_URI` | Public signing-key endpoint | Yes |
| `SUPABASE_JWT_AUDIENCE` | Expected JWT audience | No; defaults to `authenticated` |
| `SUPABASE_JWT_ALGORITHM` | Expected signature algorithm | No; defaults to `ES256` |
| `PORT` | HTTP server port | No; defaults to `8080` |

### Local development without Docker

Create the ignored file:

```text
src/main/resources/application-local.yml
```

Then run:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Do not commit the local profile because it contains database credentials.

## Running with Docker

The Docker image contains the application only. PostgreSQL and authentication
remain hosted by Supabase, so this project does not need Docker Compose.

### 1. Prepare environment variables

Copy the example and replace its placeholders with your Supabase configuration:

```bash
cp .env.example .env
```

The `.env` file is ignored by Git and excluded from the Docker build context.
Never commit real database credentials.

### 2. Build the image

Run this from the directory containing the Dockerfile:

```bash
docker build -t personal-bookmark-organizer-api .
```

The final period is the build context. It gives Docker access to this project's
Maven wrapper, `pom.xml`, and source directory.

### 3. Start a container

```bash
docker run --name bookmark-api \
  --env-file .env \
  -p 8080:8080 \
  personal-bookmark-organizer-api
```

Port mapping follows `host:container`, so `-p 8080:8080` forwards requests from
port 8080 on the host to port 8080 inside the container.

### 4. Verify health

```bash
curl http://localhost:8080/health
```

Expected response:

```json
{"Status":"OK"}
```

### 5. Stop and remove the container

```bash
docker stop bookmark-api
docker rm bookmark-api
```

Stopping preserves the container. Removing deletes that container but does not
delete the reusable image.

## API Endpoints

`GET /health` is public. Every `/api/v1/**` endpoint requires a valid Supabase
access token.

### Profile

- `POST /api/v1/me/profile`
- `GET /api/v1/me/profile`
- `PATCH /api/v1/me/profile`
- `DELETE /api/v1/me/profile`

Create-profile body:

```json
{
  "displayName": "Mouad"
}
```

The profile ID and email come from the verified JWT.

### Collections

- `POST /api/v1/collections`
- `GET /api/v1/collections?page=0&size=20`
- `GET /api/v1/collections/{collectionId}`
- `PATCH /api/v1/collections/{collectionId}`
- `DELETE /api/v1/collections/{collectionId}`

Create-collection body:

```json
{
  "name": "Backend Engineering",
  "description": "Spring, databases, and infrastructure"
}
```

### Bookmarks

- `POST /api/v1/collections/{collectionId}/bookmarks`
- `GET /api/v1/collections/{collectionId}/bookmarks?page=0&size=20`
- `GET /api/v1/collections/{collectionId}/bookmarks?page=0&size=20&status=UNREAD`
- `GET /api/v1/collections/{collectionId}/bookmarks/{bookmarkId}`
- `PATCH /api/v1/collections/{collectionId}/bookmarks/{bookmarkId}`
- `DELETE /api/v1/collections/{collectionId}/bookmarks/{bookmarkId}`

Create-bookmark body:

```json
{
  "url": "https://docs.spring.io/spring-data/jpa/reference/",
  "title": "Spring Data JPA Reference",
  "notes": "Review entity graphs and pagination"
}
```

Update-bookmark fields are optional. For example:

```json
{
  "status": "READ"
}
```

### Tags

- `POST /api/v1/tags`
- `GET /api/v1/tags?page=0&size=20`
- `GET /api/v1/tags/{tagId}`
- `PATCH /api/v1/tags/{tagId}`
- `DELETE /api/v1/tags/{tagId}`

Create-tag body:

```json
{
  "name": "Spring"
}
```

Attach or remove a tag:

- `PUT /api/v1/collections/{collectionId}/bookmarks/{bookmarkId}/tags/{tagId}`
- `DELETE /api/v1/collections/{collectionId}/bookmarks/{bookmarkId}/tags/{tagId}`

## Error Handling

The API returns consistent JSON errors for:

- Validation and invalid PATCH values: `400 Bad Request`
- Missing or inaccessible resources: `404 Not Found`
- Duplicate profiles, collection names, or tag names: `409 Conflict`
- Missing or invalid access tokens: `401 Unauthorized`

Ownership-scoped lookups return `404` when another user's resource is requested,
avoiding disclosure that the resource exists.