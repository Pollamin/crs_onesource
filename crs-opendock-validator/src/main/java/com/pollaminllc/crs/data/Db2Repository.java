package com.pollaminllc.crs.data;

import com.pollaminllc.crs.model.PurchaseOrder;
import com.pollaminllc.crs.util.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DB2 implementation of PurchaseOrderRepository.
 * Connects to Power Enterprise on IBM i via JDBC.
 *
 * TODO: This is a placeholder. Implementation requires:
 * 1. Power Enterprise table schema from CRS team
 * 2. JT400 JDBC driver (jt400.jar)
 * 3. Network connectivity to IBM i
 *
 * JDBC Connection String Format (IBM i / AS400):
 *   jdbc:as400://hostname;libraries=LIBRARYNAME;prompt=false
 *
 * Required JAR:
 *   jt400.jar (IBM Toolbox for Java)
 *   Download: https://sourceforge.net/projects/jt400/
 */
public class Db2Repository implements PurchaseOrderRepository {

    private final Config config;
    private Connection connection;

    // TODO: Update this query based on actual Power Enterprise schema
    // This is a placeholder based on typical PO table structures
    private static final String PO_QUERY =
        "SELECT " +
        "    PO_NUMBER, " +
        "    VENDOR_ID, " +
        "    VENDOR_NAME, " +
        "    ORDER_DATE, " +
        "    EXPECTED_DATE, " +
        "    STATUS, " +
        "    LOCATION_CODE " +
        "FROM PURCHASE_ORDERS " +  // TODO: Replace with actual table name
        "WHERE PO_NUMBER = ?";

    public Db2Repository(Config config) {
        this.config = config;
    }

    /**
     * Get or create database connection.
     * Uses JT400 JDBC driver for IBM i / AS400 connectivity.
     */
    private Connection getConnection() throws Exception {
        if (connection != null && !connection.isClosed()) {
            return connection;
        }

        // Load JT400 driver
        Class.forName("com.ibm.as400.access.AS400JDBCDriver");

        // Build connection string
        // Format: jdbc:as400://hostname;libraries=LIBRARYNAME;prompt=false
        String url = String.format(
            "jdbc:as400://%s;libraries=%s;prompt=false",
            config.getDbServer(),
            config.getDbName()
        );

        System.out.println("[Db2Repository] Connecting to: " + config.getDbServer());

        connection = DriverManager.getConnection(
            url,
            config.getDbUser(),
            config.getDbPassword()
        );

        System.out.println("[Db2Repository] Connected successfully");
        return connection;
    }

    @Override
    public List<PurchaseOrder> findByPoNumber(String poNumber) throws Exception {
        List<PurchaseOrder> results = new ArrayList<>();

        // TODO: Remove this exception once schema is known
        throw new UnsupportedOperationException(
            "Db2Repository is not yet implemented. " +
            "Please use StubRepository for testing until Power Enterprise schema is provided."
        );

        /*
        // Uncomment and update once schema is known:

        try (PreparedStatement stmt = getConnection().prepareStatement(PO_QUERY)) {
            stmt.setString(1, poNumber);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PurchaseOrder po = new PurchaseOrder();
                    po.setPoNumber(rs.getString("PO_NUMBER"));
                    po.setVendorId(rs.getString("VENDOR_ID"));
                    po.setVendorName(rs.getString("VENDOR_NAME"));

                    java.sql.Date orderDate = rs.getDate("ORDER_DATE");
                    if (orderDate != null) {
                        po.setOrderDate(orderDate.toLocalDate());
                    }

                    java.sql.Date expectedDate = rs.getDate("EXPECTED_DATE");
                    if (expectedDate != null) {
                        po.setExpectedDate(expectedDate.toLocalDate());
                    }

                    po.setStatus(rs.getString("STATUS"));
                    po.setLocationCode(rs.getString("LOCATION_CODE"));

                    results.add(po);
                }
            }
        }

        return results;
        */
    }

    @Override
    public boolean isHealthy() {
        try {
            Connection conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[Db2Repository] Connection closed");
            } catch (Exception e) {
                System.err.println("[Db2Repository] Error closing connection: " + e.getMessage());
            }
        }
    }
}
