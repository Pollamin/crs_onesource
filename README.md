# CRS OneSource OpenDock PO Validator

A webhook server that validates purchase orders from OpenDock against CRS's internal purchase order database. The service ensures that appointments scheduled in OpenDock correspond to valid purchase orders before allowing them to proceed.

## Overview

When an appointment is scheduled in OpenDock, this validator receives a webhook request, extracts the PO reference number, validates it against the database, and returns a response to allow or block the appointment.

## Tech Stack

- **Java 11+** - Runtime environment
- **com.sun.net.httpserver** - Built-in HTTP server
- **Gson 2.10.1** - JSON processing
- **DB2/IBM i** - Target database (via JT400 JDBC driver)

## Project Structure

```
crs-opendock-validator/
├── src/main/java/com/pollaminllc/crs/
│   ├── Main.java                    # Application entry point
│   ├── ValidatorHandler.java        # HTTP request handler
│   ├── ValidatorService.java        # Core validation logic
│   ├── model/
│   │   ├── WebhookRequest.java      # OpenDock webhook request
│   │   ├── AppointmentFields.java   # Appointment details
│   │   ├── ValidationResult.java    # Response model
│   │   └── PurchaseOrder.java       # PO data model
│   ├── data/
│   │   ├── PurchaseOrderRepository.java  # Repository interface
│   │   ├── StubRepository.java           # Mock implementation
│   │   └── Db2Repository.java            # DB2/IBM i (placeholder)
│   └── util/
│       ├── Config.java              # Configuration loader
│       └── JsonUtil.java            # JSON utilities
├── build.sh                         # Build script
├── run.sh                           # Run script
├── config.properties                # Configuration file
├── lib/                             # Dependencies
└── dist/                            # Distribution artifacts
```

## Configuration

Edit `config.properties`:

```properties
server.port=8080
auth.secret_token=your-secret-token-here

# DB2 settings (for production)
db.server=your-ibm-i-server
db.user=username
db.password=password
db.name=database
```

Environment variables can override config values:
- `SERVER_PORT` - Override server port
- `SECRET_TOKEN` - Override authentication token

## Build & Run

### Build

```bash
./build.sh
```

This compiles the source, creates a JAR, and packages everything into `dist/`.

### Run

```bash
cd dist && ./run.sh
```

Or from project root:

```bash
./run.sh
```

## API Endpoints

### POST /validate

Main webhook endpoint for PO validation.

**Request:**
```json
{
  "action": "appointment_created",
  "appointmentFields": {
    "poNumber": "PO-12345",
    "appointmentDate": "2024-01-15",
    "carrierName": "Carrier Inc"
  }
}
```

**Headers:**
```
Authorization: Bearer <secret_token>
Content-Type: application/json
```

**Response (Success - 200):**
```json
{
  "data": "PO validated successfully: PO-12345"
}
```

**Response (Error):**
```json
{
  "errorMessage": "PO number not found in system"
}
```

### GET /health

Health check endpoint.

**Response:**
```json
{
  "data": "CRS OneSource PO Validator v1.0 - OK"
}
```

## Response Codes

| Code | Description |
|------|-------------|
| 200  | PO validated successfully |
| 400  | Missing or invalid request fields |
| 401  | Invalid or missing authentication token |
| 404  | PO number not found in database |
| 409  | Multiple PO records found (conflict) |
| 503  | Database connection error |

## Testing with Stub Data

The `StubRepository` provides test PO numbers:

| PO Number | Behavior |
|-----------|----------|
| `PO-001` to `PO-010` | Valid POs |
| `NOTFOUND` | Returns 404 |
| `MULTI` | Returns 409 conflict |
| `ERROR` | Returns 503 service error |
| Any alphanumeric | Returns valid PO |

## Validation Rules

1. **Authentication** - Bearer token must match configured secret
2. **Required Fields** - `action` and `appointmentFields` must be present
3. **Cancellation Bypass** - Cancellation actions are always allowed
4. **PO Format** - Must be alphanumeric, 1-50 characters
5. **Database Lookup** - PO must exist in the system

## Architecture

```
HTTP Request → ValidatorHandler → ValidatorService → Repository → Database
                    ↓                    ↓
              Authentication        Business Logic
              JSON Parsing          PO Validation
              Response Building     Result Mapping
```

## Future Enhancements

- [ ] Implement DB2/IBM i connectivity via JT400
- [ ] Add PO status validation (Open/Closed/Cancelled)
- [ ] Appointment date validation against expected delivery
- [ ] Connection pooling
- [ ] Metrics and monitoring

## License

Proprietary - Pollam in LLC
