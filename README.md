# Reference Service – Spring Middleware Demo

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.spring-middleware/bom.svg)](https://central.sonatype.com/artifact/io.github.spring-middleware/bom)
[![Status](https://img.shields.io/badge/status-active%20development-brightgreen)](#)
[![Architecture](https://img.shields.io/badge/Architecture-Spring%20Middleware%20Reference-blueviolet.svg)](#)

## Table of contents

- [Quickstart (TL;DR)](#quickstart-tldr)
- [What is this repository?](#what-is-this-repository)
- [Goals of this repository](#goals-of-this-repository)
- [Spring Middleware in a nutshell](#spring-middleware-in-a-nutshell)
- [Architecture of the reference-service](#architecture-of-the-reference-service)
  - [GraphQL in a real platform](#graphql-in-a-real-platform)
  - [GraphQL topology: platform vs. local](#graphql-topology-platform-vs-local)
  - [Services in this demo](#services-in-this-demo)
  - [How services communicate](#how-services-communicate)
- [Request context and error model](#request-context-and-error-model)
- [Requirements](#requirements)
- [Running the project locally](#running-the-project-locally)
- [Example calls](#example-calls)
  - [Service base URLs](#service-base-urls)
  - [REST – Catalog → Product via @MiddlewareClient](#rest--catalog--product-via-middlewareclient)
  - [GraphQL – Product Service](#graphql--product-service)
- [Security & Keycloak (optional)](#security--keycloak-optional)
- [How to test Spring Middleware features](#how-to-test-spring-middleware-features)
- [Extending this reference](#extending-this-reference)
- [Further reading](#further-reading)

## Quickstart (TL;DR)

If you just want to see the reference services running:

```bash
# From the root of this module
cd reference-service

# Build all modules (catalog-service + product-service)
mvn clean install

# Start local infrastructure (MongoDB, etc.)
docker-compose -f docker-compose.yml up -d

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
- **GraphQL schema registration and gateway composition**

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

### GraphQL in a real platform

In a typical platform deployment you don’t expose multiple GraphQL endpoints
per microservice directly to clients. Instead, the platform offers a **single
front GraphQL endpoint**, backed by the Spring Middleware **GraphQL Gateway**,
for example:

```text
http://localhost:8060/graphql
```

That front endpoint:

- receives **all GraphQL queries and mutations** from clients,
- uses the **Registry** and the registered schemas to know which service
  implements each type/field,
- routes the operation internally to the responsible microservice (for example,
  `product-service` or `catalog-service`),
- aggregates the results and returns **a single GraphQL response** to the client.

In this repository, **both** `product-service` and `catalog-service` register
their GraphQL schemas into the Registry. The Spring Middleware **graphql-gateway
module** queries the Registry, composes a single merged schema from all
registered services and exposes it through the front endpoint
`http://localhost:8060/graphql`.

### GraphQL topology: platform vs. local

This project is designed to fit into that **single-endpoint topology**, while
still making local development easy with direct service endpoints.

- **In a platform environment**:
  - Public endpoint: `http://localhost:8060/graphql` (via GraphQL Gateway)
  - The gateway/router receives the operation and, with the help of the Registry,
    routes it to `product-service`, `catalog-service`, etc.
  - Clients only know this front endpoint, not the internal services.

- **In a local environment (this repo)**:
  - You can still call the GraphQL endpoint of `product-service` directly,
    for example `http://localhost:8090/product/graphql`, when debugging
    that service in isolation.
  - This is simpler for debugging and testing service changes.
  - The design remains compatible with the front GraphQL endpoint exposed
    by the GraphQL Gateway.

The rest of this README shows direct local URLs to simplify testing individual
services, but the recommended entry point in a full platform setup is the
**unified GraphQL endpoint** exposed by the GraphQL Gateway.

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

#### GraphQL Gateway (`graphql-gateway`)

The **GraphQL Gateway** is the service responsible for exposing a **single
front GraphQL endpoint** for the whole platform (in this demo:
`http://localhost:8060/graphql`). It:

- queries the **Registry** to discover which services expose GraphQL schemas
  and where their GraphQL endpoints are located,
- downloads the registered schemas from each service (for example,
  `product-service` and `catalog-service`),
- composes a **single merged GraphQL schema** from all registered services,
- exposes that unified schema through the `/graphql` endpoint,
- routes incoming queries and mutations to the **proper backend service**
  based on the composed schema and the information stored in the Registry.

For clients, this means they only need to know one URL (`/graphql`), while the
GraphQL Gateway takes care of discovering services, composing the schema and
routing operations to the right microservice.

#### Catalog Service (`catalog-service`)

Service responsible for **catalog‑level APIs**. It demonstrates:

- integration with Spring Middleware **boot/runtime** modules
- exposure of REST controllers annotated with `@Register`
- **outbound calls** to Product Service using a **declarative middleware client**
- consumption of Registry information to discover the `product` cluster
- registration of its **GraphQL schema** in the Registry so the GraphQL
  Gateway can compose it

Typical role in the platform:

- calls Product Service to obtain product data
- reshapes data into catalog views
- exposes REST endpoints that are visible in the Registry
- contributes its GraphQL schema to the platform‑wide GraphQL API

#### Product Service (`product-service`)

Service responsible for **product management**, used by the catalog.
It demonstrates:

- **MongoDB integration** via Spring Middleware Mongo modules
- **GraphQL support**, with schemas registered in the Registry
- structured error handling and propagation from the data layer upwards

Typical role in the platform:

- owns product entities and persistence
- exposes REST and/or GraphQL APIs for product operations
- registers its GraphQL schema so other services and the GraphQL Gateway
  can discover and consume it

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
                             \
                              \
                        +-------------+
                        | GraphQL     |
                        | Gateway     |
                        | (/graphql)  |
                        +-------------+
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

- When Catalog Service starts, Spring Middleware queries the Registry and
  configures the declarative clients (like `ProductApi`) with the **cluster
  endpoint** information for the `product` service. Calls made through
  `ProductApi` do **not** need to go back to the Registry on every request:
  they simply use the already configured cluster endpoint, very much like a
  Kubernetes `Service` fronting multiple pods behind it.
- If all nodes of the `product` cluster go down or are stopped, the Registry
  detects that state and notifies Catalog Service. Catalog then **deconfigures
  or disables** its clients for `product` so calls fail fast instead of
  hanging or retrying blindly.
- As soon as healthy `product` nodes come back and register again, the
  Registry pushes an update to the Catalog Service and the declarative
  clients are **reconfigured automatically**, so calls to `ProductApi` start
  working again without any manual change or restart.
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
docker-compose -f docker-compose.yml up -d
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

Once the stack is running (Registry, Mongo, Redis, Product, Catalog, GraphQL Gateway, and optionally Keycloak for OAuth2 security) you can hit the services directly. See also [Security & Keycloak (optional)](#security--keycloak-optional) for details about the sanitized Keycloak realm export and how to configure Keycloak.

### Service base URLs

**Local host (running services on your machine):**

- Registry: `http://localhost:8080/registry`
- Catalog Service: `http://localhost:8070/catalog`
- Product Service (debug): `http://localhost:8090/product`
- GraphQL Gateway (unified schema): `http://localhost:8060/graphql`

In a real platform deployment you typically **do not expose** the internal
GraphQL endpoints of each microservice directly. Instead, the Spring Middleware
GraphQL Gateway exposes a **single front endpoint** (`http://localhost:8060/graphql`)
that composes schemas from all registered services and routes operations to
`product-service`, `catalog-service` and others. The URLs for individual
services above are intended for **local development and debugging**.

**Inside Docker network (from other containers):**

- Registry: `http://registry:8080/registry`
- Catalog Service: `http://catalog:8080/catalog`
- Product Service: `http://product:8080/product`
- GraphQL Gateway: `http://graphql-gateway:8060/graphql`

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

Product Service exposes GraphQL under the `/product` context path (default Spring GraphQL endpoint `/graphql`).
In a **local environment** you can call it directly, but in a **real platform
setup** clients usually hit the **single front GraphQL endpoint** exposed by
the GraphQL Gateway (`http://localhost:8060/graphql`), which routes internally
to this service and others.

From your host machine, calling Product Service directly:

```bash
curl -X POST "http://localhost:8090/product/graphql" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "{ products { id name } }"
  }'
```

Or, using the **unified schema** exposed by the GraphQL Gateway:

```bash
curl -X POST "http://localhost:8060/graphql" \
  -H "Content-Type: application/json" \
  -d '{
    "query": "{ products { id name } }"
  }'
```

Both options exercise:

- the Product GraphQL schema,
- Spring Middleware GraphQL error handling,
- and schema registration into the Registry and composition by the GraphQL Gateway.

---

## Security & Keycloak (optional)

The reference services can be secured with OAuth2 / OpenID Connect using **Keycloak**. This repository does **not** ship a fully configured, ready‑to‑use Keycloak instance, but it does include a **sanitized realm export** that you can use as a blueprint for your own setup.

For a detailed, step-by-step description of the expected realm, clients, client scopes, roles and role mappings, see:

- `docs/keycloak-realm-spring-middleware.md`

### Sanitized realm export for `spring-middleware`

Under `docker/keycloak/` you will find a sanitized Keycloak realm export:

- `docker/keycloak/spring-middleware-realm_sanitized.json`

This file represents a Keycloak realm named `spring-middleware` that has been **sanitized**:

- all sensitive information (secrets, keys, certificates, user data) has been removed,
- only structural configuration is kept (clients, client scopes, roles, mappings, etc.).

Use this file as a **reference** for how the realm used by the demo is structured, not as a drop‑in production realm.

### Keycloak startup requirements

When you run Keycloak for this demo, you have two main options:

1. **Create a realm named `spring-middleware`** in your Keycloak instance, using the sanitized export as a blueprint, and generate new secrets/keys as appropriate for your environment.
2. **Import an adapted export** into Keycloak, still ensuring that:
   - the realm is named `spring-middleware`, and
   - all keys, certificates and client secrets are **generated by you**, not taken from any non‑sanitized source.

The repository does **not** include any private keys or usable secrets. Operators are responsible for:

- generating signing keys and certificates under their own security policies,
- creating client secrets or configuring public clients as needed,
- aligning redirect URIs and endpoints with their deployment environment.

### Using the sanitized realm to derive configuration

You can inspect `docker/keycloak/spring-middleware-realm_sanitized.json` to understand what Keycloak configuration is expected by the services. In particular, you can derive:

- **clients** (client IDs, protocols, redirect URIs, access type),
- **client scopes** and their mappings,
- **realm roles** and **client roles**,
- **role mappings** between users/groups/clients and roles,
- protocol mappers and claims required by the services.

Platform or security teams can take this JSON file as a **blueprint** to build or adjust their own `spring-middleware` realm in a secure environment while still generating fresh secrets and keys. Once such a realm exists in Keycloak, the reference services can be configured to use it for OAuth2 / OpenID Connect authentication and authorization.

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

### 4. GraphQL through a front endpoint (optional)

In this repository, you can:

- call the GraphQL endpoint of `product-service` directly
  (`http://localhost:8090/product/graphql`) to keep local service‑level
  development simple, or
- call the **unified GraphQL endpoint** exposed by the GraphQL Gateway
  (`http://localhost:8060/graphql`) to exercise the full path: schema
  registration in the Registry, schema composition and routing through
  the gateway.

In a real platform with a front GraphQL gateway, clients are expected to use
only the unified `/graphql` endpoint; direct service endpoints remain an
implementation detail.

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
- Platform: `registry`, `graphql`, `graphql-gateway`
