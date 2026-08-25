# Dex Insights Mini

A small full-stack application built with Java, Spring Boot, and Angular to analyze store operational data and provide grounded Q&A with citations.

## Tech Stack

- Java 21
- Spring Boot 3.5.16
- Maven
- Angular 18+
- TypeScript

## Run Backend

```bash
cd backend
mvn clean test
mvn spring-boot:run
```

Backend runs at:

```text
http://localhost:8080
```

## Run UI

```bash
cd ui
npm install
npm start
```

By default, the UI runs at:

```text
http://localhost:4200
```

If port `4200` is already in use, Angular may start on another available port. Check the terminal output for the actual URL.

## APIs

```text
GET  /v1/stores
GET  /v1/stores/{storeId}
GET  /v1/insights/overview
POST /v1/chat
```

`/v1/stores` supports filtering by brand and status, and sorting by offline pumps.

## Chat / RAG

The chat endpoint uses a lightweight retrieval-based approach.

Relevant store, incident, and transaction records are retrieved from the provided JSON dataset. The answer is generated only from the retrieved context and includes citations to the records used.

No external LLM or paid credentials are required.

## Design Decisions

- JSON data is loaded into memory because the supplied dataset is small.
- No database was added in order to keep the implementation simple and suitable for the exercise timebox.
- Chat retrieval uses lightweight keyword and store-based matching rather than embeddings or a vector database.
- Answers are generated only from retrieved dataset context.
- Unsupported questions are handled without inventing information.

