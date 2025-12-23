# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build the project (compiles Java, creates JAR in dist/)
cd crs-opendock-validator && ./build.sh

# Run the server
cd crs-opendock-validator/dist && ./run.sh

# Test the health endpoint
curl http://localhost:8080/health

# Test validation endpoint (with auth)
curl -X POST http://localhost:8080/validate \
  -H "Authorization: Bearer your-secret-token-here" \
  -H "Content-Type: application/json" \
  -d '{"action":"appointment_created","appointmentFields":{"refNumber":"PO-001"}}'
```

## Architecture

This is an OpenDock PO validation webhook server. The flow is:

```
OpenDock → POST /validate → ValidatorHandler → ValidatorService → Repository → Response
```

**Key layers:**
- `ValidatorHandler` - HTTP layer: auth, JSON parsing, response formatting
- `ValidatorService` - Business logic: validation rules, PO lookup orchestration
- `PurchaseOrderRepository` - Data layer interface with two implementations:
  - `StubRepository` - Mock data for testing (currently active in Main.java)
  - `Db2Repository` - Placeholder for IBM i/DB2 production database

## Test PO Numbers (StubRepository)

| PO Number | Behavior |
|-----------|----------|
| `PO-001` to `PO-010` | Returns valid PO (200) |
| `NOTFOUND` | Returns 404 |
| `MULTI` | Returns 409 conflict |
| `ERROR` | Returns 503 service error |
| Any other alphanumeric | Returns generic valid PO |

## Configuration

Edit `crs-opendock-validator/config.properties` or use environment variables:
- `SERVER_PORT` overrides `server.port`
- `SECRET_TOKEN` overrides `auth.secret_token`

## Response Format

- Success: `{"data":"message"}`
- Error: `{"errorMessage":"message"}`

HTTP codes: 200 (valid), 400 (bad request), 401 (auth failed), 404 (PO not found), 409 (multiple POs), 503 (DB error)
