# Reference Service – Spring Middleware Demo

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.spring-middleware/bom.svg)](https://central.sonatype.com/artifact/io.github.spring-middleware/bom)
[![Status](https://img.shields.io/badge/status-active%20development-brightgreen)](#)
[![Architecture](https://img.shields.io/badge/Architecture-Spring%20Middleware%20Reference-blueviolet.svg)](#)

## Quickstart (TL;DR)

If you just want to see the reference services running:

```bash
# From the root of this module
cd reference-service

# Build all modules (catalog-service + product-service)
mvn clean install

# Start local infrastructure (MongoDB, etc.)
docker-compose -f docker-compose.local.yml up -d

# In separate terminals, start each service
cd catalog-service/boot
mvn spring-boot:run

cd product-service/boot
mvn spring-boot:run
```

Make sure a compatible **Spring Middleware Registry Service** is running and
reachable with the configuration in the services `application.yml`.

Then call a catalog endpoint (see below) and observe how it calls Product Service
through a declarative `@MiddlewareClient`.

---

## What is this repository?

This repository contains the **reference microservices** used to exercise and validate
[Spring Middleware](https://central.sonatype.com/artifact/io.github.spring-middleware/bom)
with a realistic, but compact, architecture.

The main goal of this project is to **showcase how to connect services using
Spring Middleware declarative HTTP clients and the Registry**, together with
context propagation, structured errors and GraphQL integration.

---

## Goals of this repository

This project is not a generic business application. It is intentionally focused on
infrastructure behavior and answers questions like:

- How do I call another microservice using **`@MiddlewareClient`** instead of
  manually building `WebClient` calls?
- How does the **Registry** discover services and their nodes?
- How are **`X-Request-ID`** and **`X-Span-ID`** propagated across services?
- What do **remote errors** look like when they travel across a call chain?
- How do **GraphQL schemas** get registered and exposed through the Registry?

Use this repository as:

- a **demo** of Spring Middleware features,
- a **sandbox** to experiment with declarative clients and registry‑driven topology,
- a **starting point** for new services that want to adopt the framework.

---

## Spring Middleware in a nutshell

[Spring Middleware](https://github.com/spring-middleware) is a modular framework
for building distributed microservice platforms on top of Spring Boot.

It provides infrastructure for:

- service discovery and registry‑driven topology
- declarative HTTP clients (`@MiddlewareClient`)
- GraphQL schema registration and federation foundation
- structured error propagation (`ServiceException`, `ErrorMessage`, …)
- request / span context propagation (`X-Request-ID`, `X-Span-ID`)
- data integrations (Mongo, JPA, Redis, cache)
- messaging integration (RabbitMQ)

This reference project focuses mainly on:

- **Registry + declarative HTTP clients**
- **Request / span context propagation**
- **Standardized error model**
- **GraphQL schema registration**

---

## Architecture of the reference-service

The repository is a **multi‑module Maven project** that simulates a small
microservice landscape:

```text
reference-service/
├── pom.xml                # Parent POM: dependency management & shared config
├── catalog-service/       # Catalog microservice (API + CORE + BOOT)
└── product-service/       # Product microservice (API + CORE + BOOT)
```

Each service follows the typical Spring Middleware layering:

```text
boot   →  core   →  api
```

- `api`  – Contracts only (DTOs, interfaces), no infrastructure
- `core` – Business logic and middleware integrations (Mongo, mappings, etc.)
- `boot` – Spring Boot application (controllers, configuration, wiring)

### Services in this demo

#### Registry (external dependency)

The **Registry Service** is a central component provided by Spring Middleware
(not implemented in this repository). It is responsible for:

- keeping the **global topology** (clusters and nodes)
- registering REST resources annotated with `@Register`
- registering GraphQL schemas and their locations
- performing basic health / liveness checks on nodes

Both `catalog-service` and `product-service` register themselves and use the
Registry to communicate.

#### Catalog Service (`catalog-service`)

Service responsible for **catalog‑level APIs**. It demonstrates:

- integration with Spring Middleware **boot/runtime** modules
- exposure of REST controllers annotated with `@Register`
- **outbound calls** to Product Service using a **declarative middleware client**
- consumption of Registry information to discover the `product` cluster

Typical role in the platform:

- calls Product Service to obtain product data
- reshapes data into catalog views
- exposes REST endpoints that are visible in the Registry

#### Product Service (`product-service`)

Service responsible for **product management**, used by the catalog.
It demonstrates:

- **MongoDB integration** via Spring Middleware Mongo modules
- **GraphQL support**, with schemas registered in the Registry
- structured error handling and propagation from the data layer upwards

Typical role in the platform:

- owns product entities and persistence
- exposes REST and/or GraphQL APIs for product operations
- registers its GraphQL schema so other services can discover it

#### Infrastructure

For local development the project relies on:

- **MongoDB** – primary backing store used by Product Service
- **Docker Compose** – local orchestration for infrastructure pieces

The file `docker-compose.local.yml` contains the services required to run the
stack on a developer machine.

---

## How services communicate

The high‑level interaction looks like this:

```text
+------------------+          +------------------+
|  Catalog Service |  ---->   |  Product Service |
|  (REST / HTTP)   |          |  (REST/GraphQL)  |
+------------------+          +------------------+
          \                          /
           \                        /
                   +-----------+
                   | Registry  |
                   | Topology  |
                   +-----------+
```

Key points:

- The Catalog Service **does not** hardcode `http://product-service:port`.
- Instead, it declares a client like:

  ```java
  @MiddlewareClient(service = "product")
  public interface ProductApi {
      // e.g. @GetMapping("/api/v1/products/{id}")
      // ProductDto getProduct(@PathVariable String id);
  }
  ```

- Spring Middleware resolves the `product` cluster in the Registry, selects a
  healthy node and performs the HTTP call.
- Request context headers (`X-Request-ID`, `X-Span-ID`) are automatically
  propagated across services.
- Errors are converted into a structured **error model** that is consistent
  across the whole platform.

---

## Request context and error model

Every incoming request to a middleware‑enabled service is associated with:

- a **Request ID** – `X-Request-ID`
- a **Span ID** – `X-Span-ID`

When one service calls another via a `@MiddlewareClient`:

- these headers are forwarded automatically,
- downstream logs can be correlated using the same `X-Request-ID`,
- a new span is created for each hop in the call chain.

Errors are propagated using the Spring Middleware **error model**, for example:

```json
{
  "statusCode": 404,
  "statusMessage": "Not Found",
  "code": "PRODUCT:NOT_FOUND",
  "message": "Product not found",
  "extensions": {
    "requestId": "F4D29AAFE7FC4844A1FF8794F186B102"
  }
}
```

This reference project is a convenient place to see these payloads and headers
in action when you trigger real requests between services.

---

## Requirements

To build and run this project you will need:

- **Java 21** (with preview features enabled)
- **Maven 3.8+**
- **Docker** and **Docker Compose**
- A running **Registry Service** compatible with the Spring Middleware
  versions defined in the parent `pom.xml`

---

## Running the project locally

### 1. Build all modules

From the root of the repository:

```bash
mvn clean install
```

This compiles all modules (`catalog-service` and `product-service`) and ensures
Spring Middleware dependencies are resolved correctly.

### 2. Start infrastructure (MongoDB, etc.)

Use Docker Compose to bring up infrastructure services:

```bash
docker-compose -f docker-compose.local.yml up -d
```

> Adjust the command if you use a different compose file or profile.

### 3. Run the microservices

Each microservice can be started from its `boot` module.

**Catalog Service**

```bash
cd catalog-service/boot
mvn spring-boot:run
```

**Product Service**

```bash
cd product-service/boot
mvn spring-boot:run
```

Make sure the Registry Service is up and reachable with the configuration
specified in the services `application.yml`.

---

## Example calls

Once the stack is running (Registry, Mongo, Redis, Product, Catalog) you can hit the services directly.

### Service base URLs

**Local host (running services on your machine):**

- Registry: `http://localhost:8080/registry`
- Catalog Service: `http://localhost:8070/catalog`
- Product Service (debug): `http://localhost:8090/product`

**Inside Docker network (from other containers):**

- Registry: `http://registry:8080/registry`
- Catalog Service: `http://catalog:8080/catalog`
- Product Service: `http://product:8080/product`

### REST – Catalog → Product via `@MiddlewareClient`

Catalog REST endpoints are served under the `/catalog` context path and the `/api/v1/catalogs` resource path.

For a quick happy path, from your host machine:

```bash
# List catalogs
curl -v "http://localhost:8070/catalog/api/v1/catalogs?page=0&size=20"

# Create a catalog
curl -v -X POST "http://localhost:8070/catalog/api/v1/catalogs" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Summer Collection 2026",
    "status": "ACTIVE"
  }'

# Get catalog by ID (replace {id} with the returned id)
curl -v "http://localhost:8070/catalog/api/v1/catalogs/{id}"

# Get catalog with products expanded
curl -v "http://localhost:8070/catalog/api/v1/catalogs/{id}?expand=products"
```

These calls:

- hit **Catalog Service** on `localhost:8070/catalog`,
- which in turn calls **Product Service** via a declarative `@MiddlewareClient`,
- resolving the `product` cluster through the Registry at `http://localhost:8080/registry`.

To see error propagation, request a non‑existing catalog or product ID and inspect the structured error payload.

### GraphQL – Product Service

Product Service exposes GraphQL under the `/product` context path (default Spring GraphQL endpoint `/graphql`). From your host machine:

```bash
curl -X POST "http://localhost:8090/product/graphql" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "{ products { id name } }"
  }'
```

This will exercise:

- the Product GraphQL schema,
- Spring Middleware GraphQL error handling,
- and schema registration into the Registry.

---

## How to test Spring Middleware features

### 1. Declarative HTTP clients + Registry

1. Start **Registry**, **MongoDB**, `product-service` and `catalog-service`.
2. Call one of the REST endpoints exposed by `catalog-service` (see its
   controllers in the `boot` module).
3. Observe:
   - `catalog-service` calls `product-service` via a `@MiddlewareClient`.
   - No hardcoded URLs in the client.
   - The Registry shows both services and their nodes registered.

### 2. Context propagation

1. Send a request to `catalog-service` including a custom `X-Request-ID` header.
2. Inspect logs for both `catalog-service` and `product-service`.
3. You should see the same `X-Request-ID` flowing through, with different
   span IDs (`X-Span-ID`) for each hop.

### 3. Error propagation

1. Trigger a scenario where `product-service` returns an error
   (for example, ask for a non‑existing product).
2. Check the response from `catalog-service`:
   - it should be a structured error JSON,
   - it should preserve relevant metadata (request id, error code, etc.).

### 4. GraphQL schema registration

1. Start `product-service`.
2. Ensure its GraphQL schema files (e.g. `schema.graphql`, `queries.graphql`)
   are loaded by the application.
3. Check the Registry to see the schema namespace and locations associated
   with the product service.

---

## Extending this reference

Possible extensions if you want to experiment further:

- Add another service that also consumes Product APIs to create longer
  call chains and more complex error propagation scenarios.
- Integrate **Redis** or **RabbitMQ** using the corresponding Spring Middleware
  modules to test cache and messaging capabilities.
- Add additional **GraphQL queries and mutations** and explore
  cross‑service GraphQL composition.
- Deploy the same layout into **Kubernetes** to validate how Registry‑driven
  topology behaves in a real cluster.

---

## Further reading

For a complete overview of all modules and capabilities, refer to the
main Spring Middleware documentation and the BOM published on Maven Central:

- Maven BOM: `io.github.spring-middleware:bom`
- Core modules: `commons`, `api`, `app`, `model`, `view`
- Data modules: `mongo`, `jpa`, `redis`, `cache`
- Messaging: `rabbitmq`
- Platform: `registry`, `graphql`
