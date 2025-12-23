package com.pollaminllc.crs.model;

/**
 * Model for OpenDock webhook request payload.
 *
 * Example payload:
 * {
 *   "action": "create",
 *   "appointmentFields": {
 *     "refNumber": "PO12345",
 *     "start": "2024-03-15T10:00:00Z",
 *     "status": "Scheduled"
 *   },
 *   "existingAppointment": { ... }
 * }
 */
public class WebhookRequest {

    private String action;                         // "create" or "update"
    private AppointmentFields appointmentFields;   // New/updated appointment data
    private AppointmentFields existingAppointment; // Previous appointment data (for updates)

    public WebhookRequest() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public AppointmentFields getAppointmentFields() {
        return appointmentFields;
    }

    public void setAppointmentFields(AppointmentFields appointmentFields) {
        this.appointmentFields = appointmentFields;
    }

    public AppointmentFields getExistingAppointment() {
        return existingAppointment;
    }

    public void setExistingAppointment(AppointmentFields existingAppointment) {
        this.existingAppointment = existingAppointment;
    }

    /**
     * Check if this is a create action.
     */
    public boolean isCreate() {
        return "create".equalsIgnoreCase(action);
    }

    /**
     * Check if this is an update action.
     */
    public boolean isUpdate() {
        return "update".equalsIgnoreCase(action);
    }

    /**
     * Get the reference number from either appointmentFields or existingAppointment.
     * Prefers appointmentFields if available.
     */
    public String getRefNumber() {
        if (appointmentFields != null && appointmentFields.getRefNumber() != null) {
            return appointmentFields.getRefNumber();
        }
        if (existingAppointment != null && existingAppointment.getRefNumber() != null) {
            return existingAppointment.getRefNumber();
        }
        return null;
    }

    /**
     * Check if the appointment is being cancelled.
     */
    public boolean isCancellation() {
        return isUpdate() &&
               appointmentFields != null &&
               appointmentFields.isCancelled();
    }

    @Override
    public String toString() {
        return String.format(
            "WebhookRequest{action='%s', refNumber='%s', isCancellation=%s}",
            action, getRefNumber(), isCancellation()
        );
    }
}
