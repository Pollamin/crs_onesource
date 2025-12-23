# CRS OneSource OpenDock PO Validator

A webhook server that validates purchase orders from OpenDock against CRS's internal purchase order database. When an appointment is scheduled in OpenDock, this service validates the PO reference number and allows or blocks the appointment accordingly.

## Tech Stack

- **Java 11+** - Runtime environment
- **com.sun.net.httpserver** - Built-in HTTP server (no external framework)
- **Gson 2.10.1** - JSON processing
- **DB2/IBM i** - Target production database (via JT400 JDBC driver)

## Quick Start

```bash
# Build
cd crs-opendock-validator && ./build.sh

# Run
cd dist && ./run.sh

# Test health
curl http://localhost:8080/health

# Test validation
curl -X POST http://localhost:8080/validate \
  -H "Authorization: Bearer your-secret-token-here" \
  -H "Content-Type: application/json" \
  -d '{"action":"appointment_created","appointmentFields":{"refNumber":"PO-001"}}'
```

## Configuration

Edit `crs-opendock-validator/config.properties`:

```properties
server.port=8080
auth.secret_token=your-secret-token-here

# DB2 settings (for production)
db.server=your-ibm-i-server
db.user=username
db.password=password
db.name=database
```

Environment variables override config: `SERVER_PORT`, `SECRET_TOKEN`

## API Endpoints

### POST /validate

Validates a PO number from an OpenDock webhook.

**Request:**
```json
{
  "action": "appointment_created",
  "appointmentFields": {
    "refNumber": "PO-12345"
  }
}
```

**Headers:**
```
Authorization: Bearer <secret_token>
Content-Type: application/json
```

**Success (200):**
```json
{
  "data": "Appointment with PO Number PO-12345 is valid"
}
```

**Error:**
```json
{
  "errorMessage": "No records found for PO Number: PO-12345. Please verify the PO number and try again."
}
```

### GET /health

```json
{"version":"1.0.0","status":"healthy"}
```

## Response Codes

| Code | Description |
|------|-------------|
| 200  | PO validated successfully |
| 400  | Missing/invalid request fields or PO format |
| 401  | Invalid or missing Bearer token |
| 404  | PO number not found |
| 409  | Multiple PO records found |
| 503  | Database connection error |

## Testing with Stub Data

The application runs with `StubRepository` by default for testing:

| PO Number | Result |
|-----------|--------|
| `PO-001` to `PO-010` | Valid (200) |
| `NOTFOUND` | Not found (404) |
| `MULTI` | Conflict (409) |
| `ERROR` | DB error (503) |
| Any other alphanumeric | Valid (200) |

## Validation Flow

1. **Auth check** - Verify Bearer token matches configured secret
2. **Parse request** - Extract `action` and `appointmentFields`
3. **Cancellation bypass** - Cancellations always allowed without PO check
4. **Format validation** - `refNumber` must be alphanumeric, 1-50 chars
5. **Database lookup** - Query repository for matching PO
6. **Business rules** - Apply additional validation rules (extensible)

## Architecture

```
OpenDock Webhook
       ↓
ValidatorHandler (auth, JSON parsing, response formatting)
       ↓
ValidatorService (validation rules, orchestration)
       ↓
PurchaseOrderRepository
   ├── StubRepository (testing - active by default)
   └── Db2Repository (production - placeholder)
```

## License

Proprietary - Pollam in LLC
