package com.pollaminllc.crs.model;

import java.time.LocalDate;

/**
 * Model representing a Purchase Order from Power Enterprise.
 * This model will be populated by the DB2 repository when implemented.
 */
public class PurchaseOrder {

    private String poNumber;           // Purchase Order number
    private String vendorId;           // Vendor/supplier ID
    private String vendorName;         // Vendor/supplier name
    private LocalDate orderDate;       // Date order was placed
    private LocalDate expectedDate;    // Expected delivery date
    private String status;             // PO status (Open, Closed, Cancelled, etc.)
    private String locationCode;       // Warehouse/location code

    public PurchaseOrder() {
    }

    public PurchaseOrder(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(LocalDate expectedDate) {
        this.expectedDate = expectedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLocationCode() {
        return locationCode;
    }

    public void setLocationCode(String locationCode) {
        this.locationCode = locationCode;
    }

    /**
     * Check if the PO is in an open/active status.
     */
    public boolean isOpen() {
        if (status == null) return false;
        String lower = status.toLowerCase();
        return lower.equals("open") || lower.equals("active") || lower.equals("pending");
    }

    @Override
    public String toString() {
        return String.format(
            "PurchaseOrder{poNumber='%s', vendorName='%s', status='%s', expectedDate=%s}",
            poNumber, vendorName, status, expectedDate
        );
    }
}
