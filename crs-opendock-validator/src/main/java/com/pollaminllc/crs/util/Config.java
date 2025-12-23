package com.pollaminllc.crs.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration loader for the CRS OpenDock Validator.
 * Loads settings from config.properties file.
 */
public class Config {

    private static final String CONFIG_FILE = "config.properties";

    private final int port;
    private final String secretToken;

    // Database settings (for future DB2 connection)
    private final String dbServer;
    private final String dbUser;
    private final String dbPassword;
    private final String dbName;

    private Config(Properties props) {
        this.port = Integer.parseInt(props.getProperty("server.port", "8080"));
        this.secretToken = props.getProperty("auth.secret_token", "");

        // DB settings (will be used when DB2Repository is implemented)
        this.dbServer = props.getProperty("db.server", "");
        this.dbUser = props.getProperty("db.user", "");
        this.dbPassword = props.getProperty("db.password", "");
        this.dbName = props.getProperty("db.name", "");
    }

    /**
     * Load configuration from config.properties file.
     * First checks current directory, then classpath.
     */
    public static Config load() throws IOException {
        Properties props = new Properties();

        // Try loading from current directory first
        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            props.load(input);
            System.out.println("Loaded config from: " + CONFIG_FILE);
        } catch (IOException e) {
            // Try loading from classpath
            try (InputStream input = Config.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
                if (input != null) {
                    props.load(input);
                    System.out.println("Loaded config from classpath: " + CONFIG_FILE);
                } else {
                    System.out.println("No config file found, using defaults");
                }
            }
        }

        // Also check environment variables (override properties)
        String envPort = System.getenv("SERVER_PORT");
        if (envPort != null) {
            props.setProperty("server.port", envPort);
        }

        String envToken = System.getenv("SECRET_TOKEN");
        if (envToken != null) {
            props.setProperty("auth.secret_token", envToken);
        }

        return new Config(props);
    }

    public int getPort() {
        return port;
    }

    public String getSecretToken() {
        return secretToken;
    }

    public String getDbServer() {
        return dbServer;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbName() {
        return dbName;
    }

    public boolean hasSecretToken() {
        return secretToken != null && !secretToken.isEmpty();
    }

    @Override
    public String toString() {
        return String.format(
            "Config{port=%d, hasToken=%s, dbServer=%s, dbName=%s}",
            port,
            hasSecretToken(),
            dbServer.isEmpty() ? "(not set)" : dbServer,
            dbName.isEmpty() ? "(not set)" : dbName
        );
    }
}
