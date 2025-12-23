package com.pollaminllc.crs.data;

import com.pollaminllc.crs.model.PurchaseOrder;

import java.util.List;

/**
 * Repository interface for accessing Purchase Order data.
 *
 * Implementations:
 * - StubRepository: Returns mock data for testing
 * - Db2Repository: Connects to Power Enterprise on IBM i (to be implemented)
 */
public interface PurchaseOrderRepository {

    /**
     * Find purchase orders by PO number.
     *
     * @param poNumber The purchase order number to search for
     * @return List of matching purchase orders (usually 0 or 1)
     * @throws Exception if database connection fails
     */
    List<PurchaseOrder> findByPoNumber(String poNumber) throws Exception;

    /**
     * Check if the repository connection is healthy.
     *
     * @return true if connection is working, false otherwise
     */
    default boolean isHealthy() {
        return true;
    }

    /**
     * Close any open connections.
     */
    default void close() {
        // Default implementation does nothing
    }
}
