package com.pollaminllc.crs.model;

/**
 * Model for the appointmentFields object in OpenDock webhook requests.
 * Contains details about the scheduled appointment.
 */
public class AppointmentFields {

    private String refNumber;      // PO number or reference number
    private String start;          // Appointment start datetime (ISO 8601)
    private String end;            // Appointment end datetime
    private String loadTypeId;     // OpenDock load type ID
    private String status;         // Appointment status (e.g., "Scheduled", "Cancelled")
    private String dockId;         // OpenDock dock ID
    private String warehouseId;    // OpenDock warehouse ID

    public AppointmentFields() {
    }

    public String getRefNumber() {
        return refNumber;
    }

    public void setRefNumber(String refNumber) {
        this.refNumber = refNumber;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getLoadTypeId() {
        return loadTypeId;
    }

    public void setLoadTypeId(String loadTypeId) {
        this.loadTypeId = loadTypeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDockId() {
        return dockId;
    }

    public void setDockId(String dockId) {
        this.dockId = dockId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    /**
     * Check if this appointment has a cancelled status.
     */
    public boolean isCancelled() {
        if (status == null || status.isEmpty()) {
            return false;
        }
        String lower = status.toLowerCase();
        return lower.equals("cancelled") || lower.equals("canceled");
    }

    @Override
    public String toString() {
        return String.format(
            "AppointmentFields{refNumber='%s', start='%s', status='%s'}",
            refNumber, start, status
        );
    }
}
