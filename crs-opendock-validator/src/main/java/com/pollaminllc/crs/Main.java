package com.pollaminllc.crs;

import com.sun.net.httpserver.HttpServer;
import com.pollaminllc.crs.data.PurchaseOrderRepository;
import com.pollaminllc.crs.data.StubRepository;
import com.pollaminllc.crs.util.Config;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Main entry point for CRS OneSource OpenDock PO Validator.
 * Starts an embedded HTTP server to receive webhook requests from OpenDock.
 */
public class Main {

    private static final String VERSION = "1.0.0";

    public static void main(String[] args) {
        try {
            // Load configuration
            Config config = Config.load();

            // Initialize repository (stub for now, DB2 later)
            PurchaseOrderRepository repository = new StubRepository();

            // Create validator service
            ValidatorService validatorService = new ValidatorService(repository);

            // Create HTTP handler
            ValidatorHandler handler = new ValidatorHandler(validatorService, config);

            // Start HTTP server
            HttpServer server = HttpServer.create(
                new InetSocketAddress(config.getPort()),
                0  // backlog
            );

            server.createContext("/validate", handler);
            server.createContext("/health", exchange -> {
                String response = String.format(
                    "{\"version\":\"%s\",\"status\":\"healthy\"}",
                    VERSION
                );
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length());
                exchange.getResponseBody().write(response.getBytes());
                exchange.getResponseBody().close();
            });

            server.setExecutor(Executors.newFixedThreadPool(10));
            server.start();

            System.out.println("===========================================");
            System.out.println("CRS OneSource OpenDock Validator v" + VERSION);
            System.out.println("===========================================");
            System.out.println("Server started on port " + config.getPort());
            System.out.println("Endpoints:");
            System.out.println("  POST /validate - PO validation webhook");
            System.out.println("  GET  /health   - Health check");
            System.out.println("===========================================");

            // Add shutdown hook for graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down server...");
                server.stop(5);
            }));

        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
