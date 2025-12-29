# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**izgw-core** is the IZ Gateway Core library containing core functionality for the IZ Gateway Hub and Transformation services. This is a Maven-based Java 17 Spring Boot library that provides shared infrastructure for processing HL7 immunization messages, managing security, routing, and logging for immunization information systems.

## Build and Development Commands

### Building and Testing
```bash
# Clean build with tests
mvn clean test

# Build and package (skip tests)
mvn clean package -DskipTests

# Build and install locally
mvn clean install

# Run a single test class
mvn test -Dtest=TestClassName

# Run tests with specific test method
mvn test -Dtest=TestClassName#testMethodName
```

### Security and Dependency Checks
```bash
# Run OWASP dependency check
mvn org.owasp:dependency-check-maven:check

# Check for SNAPSHOT dependencies (excluding parent BOM)
mvn dependency:list | grep SNAPSHOT | grep -v izgw-bom

# View dependency tree
mvn dependency:tree
```

### Version Management
```bash
# Check current version
mvn help:evaluate -Dexpression=project.version -q -DforceStdout

# Update version
mvn versions:set -DnewVersion=2.4.0-SNAPSHOT -DgenerateBackupPoms=false
```

### Deployment
```bash
# Deploy to GitHub Packages (requires authentication)
mvn clean deploy -DskipTests
```

## Architecture

### Core Package Structure

- **`gov.cdc.izgateway.common`** - Shared constants, exceptions, health monitoring
- **`gov.cdc.izgateway.configuration`** - Spring Boot configuration classes for DynamoDB, clients, servers
- **`gov.cdc.izgateway.logging`** - Request logging, event tracking, MDC management via Tomcat valves
- **`gov.cdc.izgateway.model`** - Core data models and interfaces (destinations, message headers, endpoints)
- **`gov.cdc.izgateway.principal`** - Principal provider implementations for authentication
- **`gov.cdc.izgateway.repository`** - Data access layer (JPA, DynamoDB)
- **`gov.cdc.izgateway.security`** - Authentication and authorization infrastructure
  - **`security/principal`** - JWT and certificate-based authentication providers
  - **`security/crypto`** - Cryptographic utilities
  - **`security/oauth`** - OAuth2 integration
  - **`security/ocsp`** - Certificate status checking
  - **`security/filter`** - Security filters for request processing
  - **`security/service`** - Security-related services
- **`gov.cdc.izgateway.service`** - Business logic services and interfaces
- **`gov.cdc.izgateway.soap`** - SOAP message processing and mock message support
- **`gov.cdc.izgateway.utils`** - Utilities for HL7, JSON, XML, dates, reflection, X.500, certificates

### Key Architectural Components

#### Tomcat Valves
This library uses custom Tomcat Valves for cross-cutting concerns:
- **`LoggingValve`** (highest precedence) - Attaches event IDs to MDC for request tracing
- **`AccessControlValve`** - Validates client certificates and IP addresses for access control

#### Security Architecture
Multi-layered security model supporting:
- **Certificate-based authentication** via `CertificatePrincipalProvider`
- **JWT authentication** via `JwtPrincipalProvider` and `JwtSharedSecretPrincipalProvider`
- **OAuth2 resource server** integration
- **FIPS-compliant cryptography** using BouncyCastle FIPS libraries
- **Access control** via IP allowlists/denylists and certificate validation
- **OCSP certificate status checking**

#### Data Models
The architecture uses interface-based models for flexibility:
- **`IDestination`** - Endpoint configuration (IIS, ADS, Azure)
- **`IMessageHeader`** - HL7 message routing metadata
- **`IEndpoint`** - Base endpoint interface
- **`IAccessControl`** - IP-based access control rules

#### Health Monitoring
Centralized health service (`HealthService`) tracks:
- Server health status and changes
- Request volume and success metrics
- Database and egress DNS configuration
- Last exception and change reason

## Development Workflow

### Testing
- Test files follow the pattern `**/*Tests.java`
- Tests use H2/HSQLDB for database integration
- System properties: `elastic.api.key` for Elasticsearch tests
- JVM args required: `--add-opens=java.base/java.lang.reflect=ALL-UNNAMED --add-opens=java.base/java.net=ALL-UNNAMED`

### Release Process
Use GitHub Actions workflows for releases (see `.github/workflows/README.md`):
- **Standard Release**: Use `release.yml` workflow (manual trigger from `develop` branch)
  - Specify release version (e.g., `2.3.0`)
  - Optionally specify next SNAPSHOT version (e.g., `2.4.0` or `3.0.0`)
    - If not specified, auto-increments minor version (e.g., `2.3.0` → `2.4.0-SNAPSHOT`)
  - Creates release branch, merges to main, tags, deploys to GitHub Packages
  - Updates develop with next SNAPSHOT version
- **Hotfix Release**: Use `hotfix.yml` workflow (manual trigger from `hotfix/*` branch)
  - For critical production fixes that can't wait for next standard release
  - Merges to main, tags, deploys, but does NOT bump develop version
- **CI/CD**: `maven.yml` runs on pushes to `main`, `develop`, or `Release*` branches

Version format:
- Development: `X.Y.Z-SNAPSHOT`
- Release: `X.Y.Z`

See `RELEASING.md` for detailed release procedures and `.github/workflows/README.md` for workflow documentation.

### Version Naming Conventions
- Working branches: `<major>.<minor>.<patch>-IGDD-<ticket#>_<ticket-title>-SNAPSHOT`
- Release branch: `<major>.<minor>.<patch>-SNAPSHOT`
- Main branch: `<major>.<minor>.<patch>`

### Code Standards
- **Java 17** with Lombok for reducing boilerplate
- **Spring Boot 3.5.4** framework
- **HL7 v2.5.1** message processing via HAPI
- **Hibernate/JPA** for persistence
- **FIPS-compliant** cryptography required
- **Logback** with Logstash encoder for JSON logging

## Key Dependencies

- **Spring Boot** 3.5.4 - Web framework, security, OAuth2
- **HAPI** - HL7 v2.5.1 message parsing
- **Hibernate/JPA** - Database ORM
- **AWS SDK** - DynamoDB, Secrets Manager
- **BouncyCastle FIPS** - Cryptographic operations
- **Jackson** - JSON processing
- **Logback + Logstash encoder** - Structured logging
- **JWT (jjwt)** - JWT token processing
- **SpringDoc OpenAPI** - API documentation
- **Apache Commons** - Utilities (Lang3, Text, Collections4, IO)
- **HikariCP** - Connection pooling
- **MySQL Connector** - Database driver

## Important Notes

### Security Considerations
- All dependencies must be release versions before creating a release (no SNAPSHOTs except `izgw-bom`)
- OWASP dependency check configured to fail on CVSS >= 7
- Use `dependency-suppression.xml` to suppress false positives
- Certificate validation and OCSP checking are critical security boundaries

### HL7 Message Processing
- Messages are HL7 v2.5.1 format
- Routing is based on `IDestination` configuration with multiple version schemas:
  - CDC Schema: `2011`
  - 2014 Schema: `2014`
  - HUB Schema: `HUB`
  - DEX 1.0/2.0: `DEX1.0`, `DEX2.0`
  - Azure NDLP: `V2022-12-31`
- Use `HL7Utils` for parsing and field extraction

### Logging and Tracing
- Request tracing uses event IDs in MDC (Mapped Diagnostic Context)
- All logging goes through Logback with Logstash JSON encoding
- Health events logged with special markers via `Markers2`
- Use `RequestContext` for request-scoped data

### Multi-Tenancy and Routing
- Destinations are identified by facility ID and destination ID
- Access control is per-destination based on certificates and IP addresses
- Message headers contain routing metadata (`IMessageHeader`)
