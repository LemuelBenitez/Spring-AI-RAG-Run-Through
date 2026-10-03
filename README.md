# Spring AI RAG Demo

A compact Spring Boot application that demonstrates a local retrieval-augmented generation (RAG) workflow using Spring AI, Elasticsearch, and Ollama. The project is a backend-focused prototype for experimenting with AI chat, embeddings, and vector search in a Java service without relying on cloud-hosted model infrastructure.

This repository is best understood as a learning and prototyping project for:

- local LLM integration with Ollama
- embedding-based document retrieval
- vector search using Elasticsearch
- Spring AI configuration patterns in Java
- simple API-driven AI workflows for experimentation and portfolio use

## Project Overview

The application loads a set of sample text documents into an Elasticsearch vector index, retrieves the most relevant documents for a user query, and then sends the query plus retrieved context to a local Ollama chat model. In practice, this is a minimal RAG pipeline implemented in Java.

The project serves developers and learners who want to understand how a small AI-enabled backend is wired together. There is no frontend application or user-facing dashboard in this repository; the service is exposed as a REST API and is intended to be exercised via HTTP requests or CLI tooling.

## Key Features

- Local chat model integration using Ollama
- Embedding model integration using Ollama
- Elasticsearch-backed vector search
- Startup data ingestion into a vector index
- Prompt templating with retrieved documents as context
- Simple REST endpoint for chat-style queries
- External service configuration via environment variables and `application.yml`

## Results and Output
The application is designed to return grounded responses from the chat model based on the retrieved documents.
For example, if a user queries "What is a blockchain?", the service will return a response that is informed by the documents in the vector store, or reply with "I don't know."
If the answer is not supported by the context.

- Currently, the llm model is configured to `llama3.2:1b` and the embedding model is `nomic-embed-text`. (Output may vary based on model performance and configuration. Example `1B` & `1C` image  is incorrect, but `1A` image is correct.)
    - The embedding model is used to convert documents and user queries into vector representations for similarity search.
    - Due to small model size, the responses may be limited in depth and accuracy, but the workflow demonstrates the RAG pattern effectively.
    - The output isn't deterministic and may vary based on the model's internal state and the retrieved context.
## Architecture

### Backend-only service

This project uses a single Spring Boot service with a layered MVC-style backend structure:

- `RAGController` handles HTTP requests
- `ChatClientConfig` builds the configured chat client
- `RandomDataLoader` seeds the vector store on startup
- `VectorStore` provides similarity search capabilities
- `application.yml` centralizes model and service configuration

### Runtime workflow

Based on the actual code, the application flow is:

1. The Spring Boot app starts.
2. `RandomDataLoader` runs after initialization.
3. A fixed list of sentence-based documents is converted to Spring AI `Document` objects.
4. Those documents are added to the configured Elasticsearch vector store.
5. A request is sent to `GET /basic-chat`.
6. The controller runs a similarity search using the user input.
7. The most relevant documents are converted into a text context.
8. A prompt template combines the user input with the retrieved documents.
9. The Ollama chat model responds using that context.

This is a classic retrieval-augmented generation pattern implemented in Java and Spring AI.

### Infrastructure and integrations

The repository clearly includes these runtime components:

- Java 25
- Spring Boot 4.1.0
- Spring AI 2.0.0
- Ollama for chat and embedding models
- Elasticsearch for vector storage and retrieval
- Spring Web MVC for the REST API
- Springdoc OpenAPI for API documentation support

The codebase does not provide evidence of:

- a separate frontend service
- a message broker or event bus
- microservice decomposition
- container orchestration manifests
- database persistence beyond the Elasticsearch vector store usage

## Technology Stack

| Component | Evidence in repository |
| --- | --- |
| Language | Java |
| Java version | 25 |
| Framework | Spring Boot 4.1.0 |
| AI framework | Spring AI 2.0.0 |
| Web layer | Spring Web MVC |
| LLM runtime | Ollama |
| Vector database | Elasticsearch |
| API docs | Springdoc OpenAPI (`springdoc-openapi-starter-webmvc-ui`) |
| ORM/data access | Spring Data Elasticsearch |
| Build tool | Maven Wrapper (`mvnw`) |
| Test stack | JUnit 5, Spring Boot Test, AssertJ |
| Additional runtime | PostgreSQL JDBC driver present in `pom.xml` but not used in the application code |

## AI Functionality

This project contains real AI functionality.

### Chat model
The application configures Ollama as the chat model:

- model: `llama3.2:1b`

This is configured in `src/main/resources/application.yml`.

### Embedding model
The application configures Ollama as the embedding model:

- model: `nomic-embed-text`
- vector dimensions: `768`

### Retrieval workflow
The controller uses Spring AI `VectorStore` and `SearchRequest` to perform a similarity search. The relevant code is in `RAGController.java`:

- `vectorStore.similaritySearch(searchRequest)`
- `topK(3)`
- `similarityThreshold(0.5)`

This demonstrates a simple retrieval step before the model generates a final answer.

### Prompting
The prompt template is stored in:

- `src/main/resources/promptTemlates/systemPromptRandomDataTemplate.st`

It instructs the assistant to answer only from the supplied document context and reply with `I don't know.` when the answer is unsupported.

### Data ingestion
The project seeds the vector store with a hard-coded set of factual sentences in `RandomDataLoader.java`. These are converted to AI `Document` objects and added to the Elasticsearch vector store on startup.

### Important note
The repository demonstrates core AI fundamentals clearly, but it is intentionally small and focused on local experimentation rather than a production-ready AI platform.

## Visual Walkthrough

No `images/` directory or image assets were present in this repository at the time of review, so there are no screenshots or diagrams to embed in the README. This is noted accurately rather than guessed.

### Additional Screenshots

No additional screenshots were found in the repository.

## Local Setup and Running

### Prerequisites

The project expects the following local services to be running:

- Java 25
- Elasticsearch at `http://localhost:9200`
- Ollama at `http://localhost:11434`

### Environment variables

The application supports overrides through environment variables:

```sh
export OLLAMA_BASE_URL=http://localhost:11434
export ELASTICSEARCH_URL=http://localhost:9200
```

The defaults are defined in `src/main/resources/application.yml`.

### Pull Ollama models

```sh
ollama pull llama3.2:1b
ollama pull nomic-embed-text
```

### Build and run the application

```sh
./mvnw spring-boot:run
```

### Run tests

```sh
./mvnw test
```

## API Documentation

### Endpoint: `/basic-chat`

The application exposes a simple REST endpoint:

- Method: `GET`
- Path: `/basic-chat`
- Required header: `username`
- Required query parameter: `userInput`

Example request:

```sh
curl --get http://localhost:8080/basic-chat \
  --header 'username: demo' \
  --data-urlencode 'userInput=What is a blockchain?'
```

Because the app is a Spring Boot MVC service with Springdoc dependency, standard OpenAPI endpoints are expected to be available when running locally:

- `/v3/api-docs`
- `/swagger-ui/index.html`

These endpoints are driven by the configured Springdoc dependency rather than custom code in this repository.

## Project Structure

```text
spring-ai-rag/
├── HELP.md
├── README.md
├── mvnw
├── mvnw.cmd
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/project/spring_ai_rag/
│   │   │       ├── SpringAiRagApplication.java
│   │   │       ├── configs/
│   │   │       │   ├── ChatClientConfig.java
│   │   │       │   └── ElasticsearchAiConfig.java
│   │   │       ├── controller/
│   │   │       │   └── RAGController.java
│   │   │       └── rag/
│   │   │           └── RandomDataLoader.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── scripts.sql
│   │       ├── promptTemlates/
│   │       │   └── systemPromptRandomDataTemplate.st
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/
│           └── com/project/spring_ai_rag/
│               └── SpringAiRagApplicationTests.java
├── target/
└── .mvn/
```

### Notable files

- `pom.xml`: dependency and build configuration
- `src/main/resources/application.yml`: Spring Boot and Spring AI setup
- `src/main/java/com/project/spring_ai_rag/controller/RAGController.java`: API layer and retrieval logic
- `src/main/java/com/project/spring_ai_rag/rag/RandomDataLoader.java`: startup document seeding
- `src/main/java/com/project/spring_ai_rag/configs/ChatClientConfig.java`: chat client wiring
- `src/main/resources/promptTemlates/systemPromptRandomDataTemplate.st`: system prompt used for grounded responses
- `src/test/java/com/project/spring_ai_rag/SpringAiRagApplicationTests.java`: Spring context wiring test

## Testing and Quality

### Included test coverage

The repository contains one primary application test:

- `SpringAiRagApplicationTests`

This test verifies that:

- the Ollama embedding model is present
- the Ollama chat model is present
- the default chat model resolves to `llama3.2:1b`

The test specifically excludes the Elasticsearch vector store auto-configuration to avoid startup-time ingestion during simple context validation.

### Running tests

```sh
./mvnw test
```

### Quality tooling

At the time of review, there is no evidence of dedicated linting, formatting, or CI configuration in the repository. This project is minimal and focused on demonstrating working Spring AI and RAG wiring.

## Future Improvements

These are recommendations, not current functionality:

- Add a frontend interface for chat interaction
- Add upload and indexing endpoints for user-provided documents
- Add stored conversation history and session management
- Add health checks and observability for Ollama and Elasticsearch calls
- Add authentication and request throttling
- Add a richer knowledge-base workflow for custom datasets
- Add CI checks for build and test validation
- Extend the project to support hybrid search and more advanced retrieval strategies

## Summary

This repository is a concise demonstration of a local Java-based RAG application using Spring Boot, Spring AI, Ollama, and Elasticsearch. It is a practical example for understanding how chat models, embeddings, semantic retrieval, and context grounding work together in a small backend service. The code is intentionally simple and readable, making it appropriate for AI engineering prototypes, learning exercises, and portfolio projects.
