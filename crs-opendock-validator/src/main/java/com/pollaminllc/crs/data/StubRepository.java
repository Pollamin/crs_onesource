package com.pollaminllc.crs.data;

import com.pollaminllc.crs.model.PurchaseOrder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stub implementation of PurchaseOrderRepository for testing.
 * Returns predefined mock data based on PO number patterns.
 *
 * Test PO numbers:
 * - "PO-001" through "PO-999": Valid, returns a mock PO
 * - "NOTFOUND": Returns empty list (404)
 * - "MULTI": Returns multiple records (409 conflict)
 * - "ERROR": Throws exception (503 service unavailable)
 * - Any other alphanumeric: Returns a generic valid PO
 */
public class StubRepository implements PurchaseOrderRepository {

    private final Map<String, List<PurchaseOrder>> mockData;

    public StubRepository() {
        mockData = new HashMap<>();
        initializeMockData();
    }

    private void initializeMockData() {
        // Sample valid POs
        for (int i = 1; i <= 10; i++) {
            String poNumber = String.format("PO-%03d", i);
            PurchaseOrder po = createMockPO(poNumber, "ACME Supplier " + i, "Open");
            mockData.put(poNumber, List.of(po));
        }

        // Special case: PO not found
        mockData.put("NOTFOUND", new ArrayList<>());

        // Special case: Multiple records (conflict)
        PurchaseOrder multi1 = createMockPO("MULTI", "Vendor A", "Open");
        PurchaseOrder multi2 = createMockPO("MULTI", "Vendor B", "Pending");
        mockData.put("MULTI", List.of(multi1, multi2));
    }

    private PurchaseOrder createMockPO(String poNumber, String vendorName, String status) {
        PurchaseOrder po = new PurchaseOrder(poNumber);
        po.setVendorId("V" + poNumber.hashCode());
        po.setVendorName(vendorName);
        po.setOrderDate(LocalDate.now().minusDays(7));
        po.setExpectedDate(LocalDate.now().plusDays(3));
        po.setStatus(status);
        po.setLocationCode("CRS-WH1");
        return po;
    }

    @Override
    public List<PurchaseOrder> findByPoNumber(String poNumber) throws Exception {
        System.out.println("[StubRepository] Looking up PO: " + poNumber);

        // Special case: simulate database error
        if ("ERROR".equalsIgnoreCase(poNumber)) {
            throw new Exception("Simulated database connection error");
        }

        // Check predefined mock data
        if (mockData.containsKey(poNumber)) {
            List<PurchaseOrder> result = mockData.get(poNumber);
            System.out.println("[StubRepository] Found " + result.size() + " record(s)");
            return result;
        }

        // For any other valid-looking PO, return a generic mock record
        // This allows testing with arbitrary PO numbers
        if (poNumber != null && !poNumber.isEmpty() && !poNumber.equalsIgnoreCase("NOTFOUND")) {
            PurchaseOrder po = createMockPO(poNumber, "Generic Vendor", "Open");
            System.out.println("[StubRepository] Created generic mock for: " + poNumber);
            return List.of(po);
        }

        // Not found
        System.out.println("[StubRepository] No records found for: " + poNumber);
        return new ArrayList<>();
    }

    @Override
    public boolean isHealthy() {
        return true;
    }

    @Override
    public void close() {
        System.out.println("[StubRepository] Closed (no-op for stub)");
    }
}
