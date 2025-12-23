package com.pollaminllc.crs;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.pollaminllc.crs.model.ValidationResult;
import com.pollaminllc.crs.model.WebhookRequest;
import com.pollaminllc.crs.util.Config;
import com.pollaminllc.crs.util.JsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * HTTP handler for OpenDock validation webhook requests.
 * Handles authentication, request parsing, and response formatting.
 */
public class ValidatorHandler implements HttpHandler {

    private final ValidatorService validatorService;
    private final Config config;
    private static final DateTimeFormatter LOG_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ValidatorHandler(ValidatorService validatorService, Config config) {
        this.validatorService = validatorService;
        this.config = config;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String timestamp = LocalDateTime.now().format(LOG_FORMAT);

        log("Request: %s /validate from %s", method, exchange.getRemoteAddress());

        try {
            // Handle GET for health/status check
            if ("GET".equalsIgnoreCase(method)) {
                handleGet(exchange);
                return;
            }

            // Only POST is allowed for validation
            if (!"POST".equalsIgnoreCase(method)) {
                sendError(exchange, 405, "Method not allowed. Use POST.");
                return;
            }

            // Check authorization
            if (!isAuthorized(exchange)) {
                sendError(exchange, 401, "Unauthorized. Invalid or missing Bearer token.");
                return;
            }

            // Parse request body
            String requestBody = readRequestBody(exchange);
            log("Request body: %s", truncate(requestBody, 500));

            WebhookRequest request;
            try {
                request = JsonUtil.fromJson(requestBody, WebhookRequest.class);
            } catch (Exception e) {
                sendError(exchange, 400, "Bad Request: Invalid JSON - " + e.getMessage());
                return;
            }

            // Validate the request
            ValidationResult result = validatorService.validate(request);

            // Send response
            sendResponse(exchange, result);

        } catch (Exception e) {
            log("Error processing request: %s", e.getMessage());
            e.printStackTrace();
            sendError(exchange, 500, "Internal server error: " + e.getMessage());
        }
    }

    /**
     * Handle GET request - returns version info.
     */
    private void handleGet(HttpExchange exchange) throws IOException {
        String response = "{\"message\":\"CRS OneSource PO Validator\",\"status\":\"ready\"}";
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Check if request has valid Bearer token authorization.
     */
    private boolean isAuthorized(HttpExchange exchange) {
        // If no token configured, skip auth (for testing)
        if (!config.hasSecretToken()) {
            log("Warning: No secret token configured, skipping auth check");
            return true;
        }

        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || authHeader.isEmpty()) {
            log("Missing Authorization header");
            return false;
        }

        String[] parts = authHeader.split(" ");
        if (parts.length != 2 || !"Bearer".equalsIgnoreCase(parts[0])) {
            log("Invalid Authorization header format");
            return false;
        }

        String token = parts[1];
        boolean valid = config.getSecretToken().equals(token);
        if (!valid) {
            log("Invalid Bearer token");
        }
        return valid;
    }

    /**
     * Read the full request body as a string.
     */
    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            byte[] bytes = is.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    /**
     * Send a validation result response.
     */
    private void sendResponse(HttpExchange exchange, ValidationResult result) throws IOException {
        String json = result.toJson();
        log("Response: %d - %s", result.getHttpStatus(), truncate(json, 200));

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] responseBytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(result.getHttpStatus(), responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    /**
     * Send an error response.
     */
    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        String json = String.format("{\"errorMessage\":\"%s\"}", escapeJson(message));
        log("Error response: %d - %s", statusCode, message);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] responseBytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    /**
     * Escape special characters for JSON string.
     */
    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Truncate string for logging.
     */
    private String truncate(String s, int maxLen) {
        if (s == null) return "null";
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen) + "...";
    }

    /**
     * Log a message with timestamp.
     */
    private void log(String format, Object... args) {
        String timestamp = LocalDateTime.now().format(LOG_FORMAT);
        System.out.printf("[%s] %s%n", timestamp, String.format(format, args));
    }
}
