# Reference Service - Spring Middleware Demo

[![Java](https://img.shields.io/badge/Java-21-blue)]()
[![License](https://img.shields.io/badge/license-MIT-green)]()
[![Docker](https://img.shields.io/badge/docker-compose-blue)]()
[![Spring Middleware](https://img.shields.io/badge/framework-spring--middleware-orange)](https://github.com/Spring-Middleware/spring-base)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.spring-middleware/app.svg)](https://central.sonatype.com/artifact/io.github.spring-middleware/app)

This repository contains the **reference implementation for Spring Middleware**, demonstrating how to build microservices using the framework infrastructure.

It simulates a realistic microservice environment where services communicate through the **Spring Middleware registry, declarative clients, and distributed request context propagation**.

This project is intended as a **technical demonstration and validation environment for the framework**, not as a production-ready application.

---

## What this project demonstrates

The reference services validate several capabilities provided by **Spring Middleware**:

- **Declarative HTTP Clients**  
  Service-to-service communication using annotated interfaces (`@MiddlewareClient`).

- **Service Registry**  
  Automatic node registration and service discovery.

- **Context Propagation**  
  Request identifiers (`X-Request-ID`, `X-Span-ID`) propagated across service calls.

- **Unified Error Handling**  
  Standardized exception propagation between microservices.

- **GraphQL Integration**  
  GraphQL schema registration and query execution through the middleware.

---

## Architecture

The system consists of multiple services communicating through the registry.
