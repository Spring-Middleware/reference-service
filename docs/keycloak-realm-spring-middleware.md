# Keycloak realm: `spring-middleware`

This document describes the expected **Keycloak configuration** for the
`spring-middleware` realm used by the reference services. It is based on the
sanitized export provided in `docker/keycloak/spring-middleware-realm_sanitized.json`.

The goal is to explain **what must exist** in Keycloak (realm, clients, client
scopes, roles and role mappings) so that the reference services can integrate
with OAuth2 / OpenID Connect.

> **Important:** The JSON file in `docker/keycloak/` is **sanitized**. It does
> not contain any private keys, certificates or client secrets. You must
> generate all secrets and cryptographic material in your own environment.

---

## 1. Realm overview

- **Realm name:** `spring-middleware`
- **Default signature algorithm:** `RS256`
- Standard realm roles such as:
  - `default-roles-spring-middleware` (composite)
  - `offline_access`
  - `uma_authorization`

The reference services assume that they authenticate against a realm with this
name. If you use a different realm name, update your service configuration
accordingly.

---

## 2. Clients

The sanitized export defines several clients. The most relevant for this
repository are:

- `catalog-service`
- `product-service`
- (standard administration clients such as `realm-management`, `account`,
  `security-admin-console`, etc.)

For each of the service clients (`catalog-service` and `product-service`) you
must ensure:

- The client exists in the `spring-middleware` realm.
- It uses the **OpenID Connect** protocol.
- It has appropriate **redirect URIs** and **web origins** for your
  environment (e.g. `http://localhost:8070/*`, `http://localhost:8090/*`, or
  the URLs used by your API gateway or UI).
- You configure the access type and credentials according to your deployment
  (confidential vs. public, client secret or MTLS, etc.). Those values are
  intentionally not part of the sanitized export.

The important part from the sanitized export is the **roles** defined for each
client, described below.

---

## 3. Roles

### 3.1 Realm roles

The realm contains a small set of standard realm roles, for example:

- `default-roles-spring-middleware` (composite)
- `offline_access`
- `uma_authorization`

These are mostly Keycloak defaults and provide basic capabilities (offline
access, UMA authorization, etc.).

### 3.2 Client roles for `catalog-service`

The `catalog-service` client defines several **client roles** that represent
permissions over catalog resources. From the sanitized export:

- `CREATE_CATALOG`
- `GET_CATALOG`
- `LIST_CATALOGS`
- `UPDATE_CATALOG`
- `DELETE_CATALOG`
- `ADD_PRODUCTS_TO_CATALOG`
- `REMOVE_PRODUCTS_FROM_CATALOG`
- `REPLACE_PRODUCTS_FROM_CATALOG`
- `LIST_CATALOG_PRODUCTS`

Typical usage pattern:

- A user or technical client that needs to **query** catalogs can be granted
  roles such as `GET_CATALOG` and `LIST_CATALOGS`.
- A user or technical client that needs to **modify** catalogs and catalog
  products can be granted roles such as `CREATE_CATALOG`, `UPDATE_CATALOG`,
  `DELETE_CATALOG`, `ADD_PRODUCTS_TO_CATALOG`, `REMOVE_PRODUCTS_FROM_CATALOG`,
  `REPLACE_PRODUCTS_FROM_CATALOG`, etc.

### 3.3 Client roles for `product-service`

The `product-service` client defines roles focused on product operations. From
the sanitized export (truncated here to the most relevant):

- `PRODUCT_READER`
- `PRODUCT_WRITER`

Typical usage pattern:

- **Read-only clients** get `PRODUCT_READER`.
- **Write-capable clients** (create, update, delete products) get
  `PRODUCT_WRITER` (and possibly also `PRODUCT_READER`).

You can inspect `docker/keycloak/spring-middleware-realm_sanitized.json` to see
all client roles defined under the `product-service` client if you need the
complete list.

---

## 4. Client scopes

Client scopes allow grouping common protocol mappers and role mappings that can
then be attached to multiple clients.

In the `spring-middleware` realm export you will find:

- Standard Keycloak scopes such as `profile`, `email`, etc.
- Custom scopes that can be used to represent **business capabilities** for the
  reference services (for example, a scope that implies catalog or product
  permissions).

Each client scope can:

- define protocol mappers (claims to add to the token), and
- be associated with specific **roles** (realm roles or client roles).

The association between a client scope and roles is what allows you to say:

> “If a client requests this scope, the resulting token will contain the
>  corresponding roles.”

The sanitized realm export shows these mappings under the `clientScopes` and
`scopeMappings` sections.

---

## 5. Role mappings between client scopes and roles

### 5.1 Concept

A **scope mapping** in Keycloak links a **client scope** (or client) with one or
more **roles**. When a token is issued for a client that includes that scope,
Keycloak adds the mapped roles to the token.

For the `spring-middleware` realm, the typical pattern is:

- Define a **client scope** that represents an application-level capability
  (for example, `catalog-api` or `product-api`).
- Map that client scope to one or more **client roles** on `catalog-service`
  or `product-service`.
- Attach that client scope to the clients (or rely on it as a default scope),
  so that tokens carry the required roles.

In the sanitized export you can inspect the `scopeMappings` section to see
exactly which scopes are mapped to which roles. Even though secrets are
removed, the structural relationship (scope → roles) is preserved.

### 5.2 Example: mapping a scope to a catalog role

A typical configuration might look like this (conceptually):

- Client scope: `catalog-api`
- Mapped roles:
  - client `catalog-service` role `GET_CATALOG`
  - client `catalog-service` role `LIST_CATALOGS`

With this mapping:

- If a token is requested with the `catalog-api` scope, Keycloak adds the
  `GET_CATALOG` and `LIST_CATALOGS` roles to the token (under the
  `resource_access` for `catalog-service`).
- The Catalog Service can then check these roles in the token to authorize
  incoming requests.

You can use the same pattern for write operations (mapping a `catalog-api-write`
client scope to roles such as `CREATE_CATALOG`, `UPDATE_CATALOG`, etc.).

---

## 6. Hardcoded role in token (protocol mapper)

### 6.1 What “hardcoded role in token” means

Keycloak supports a **"Hardcoded role"** protocol mapper. This mapper adds a
predefined role to every token issued for a client (or client scope), regardless
of the user’s own role assignments.

This is useful when you want a specific client (for example, a backend service)
to always have a certain role when it calls another service, without having to
assign that role to each user individually.

### 6.2 How it is used in this realm

In the `spring-middleware` realm you can use hardcoded role mappers to ensure
that a technical client always receives the appropriate client roles for calling
Catalog or Product APIs. For example:

- A backend client representing the **Catalog Service** could have a hardcoded
  mapper that adds `PRODUCT_READER` on the `product-service` client so that
  every call from Catalog to Product carries that role.

Even if the sanitized export removes secrets, the presence of such mappers can
still be seen under the `protocolMappers` of the clients or client scopes. They
will look like:

- **Mapper type:** `oidc-hardcoded-role-mapper`
- **Config:** includes the target role name and where it should appear
  (realm or client role).

### 6.3 When to use hardcoded role mappers

Use a **hardcoded role mapper** when:

- A service-to-service client should **always** have a specific role when
  calling another service.
- You do not want to manage per-user role assignments for that technical
  client.

Avoid using hardcoded role mappers for end-user facing clients unless you fully
understand the security implications (because every token obtained through that
client will carry the hardcoded role).

---

## 7. How to reconstruct this setup in your own Keycloak

1. **Create the realm** `spring-middleware` (or choose another name and update
   your services accordingly).
2. **Create the service clients** `catalog-service` and `product-service`:
   - Set protocol to OpenID Connect.
   - Configure access type, redirect URIs and credentials for your environment.
3. **Create client roles** on each client:
   - For `catalog-service`: `CREATE_CATALOG`, `GET_CATALOG`, `LIST_CATALOGS`,
     `UPDATE_CATALOG`, `DELETE_CATALOG`, `ADD_PRODUCTS_TO_CATALOG`,
     `REMOVE_PRODUCTS_FROM_CATALOG`, `REPLACE_PRODUCTS_FROM_CATALOG`,
     `LIST_CATALOG_PRODUCTS`, etc.
   - For `product-service`: `PRODUCT_READER`, `PRODUCT_WRITER`, and any other
     roles present in the sanitized export.
4. **Define client scopes** that group permissions:
   - For example, `catalog-api`, `catalog-api-write`, `product-api`, etc.
5. **Create scope mappings** from the client scopes to the roles:
   - `catalog-api` → `GET_CATALOG`, `LIST_CATALOGS` on `catalog-service`.
   - `catalog-api-write` → `CREATE_CATALOG`, `UPDATE_CATALOG`, etc.
   - `product-api` → `PRODUCT_READER` and/or `PRODUCT_WRITER` on
     `product-service`.
6. (Optional) **Add hardcoded role mappers** to service-to-service clients:
   - For example, on a client used by Catalog to call Product, add a mapper
     that always injects `PRODUCT_READER` (client role on `product-service`).
7. **Attach the client scopes** to your clients (as default or optional scopes)
   so that tokens carry the expected roles.

By following these steps and using `docker/keycloak/spring-middleware-realm_sanitized.json`
as a reference, you can recreate a secure, production-ready version of the
`spring-middleware` realm that works with this reference project while still
relying on secrets and keys generated in your own environment.

