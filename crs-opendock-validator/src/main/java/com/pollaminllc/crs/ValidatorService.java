package com.pollaminllc.crs;

import com.pollaminllc.crs.data.PurchaseOrderRepository;
import com.pollaminllc.crs.model.PurchaseOrder;
import com.pollaminllc.crs.model.ValidationResult;
import com.pollaminllc.crs.model.WebhookRequest;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Core validation service for PO validation.
 * Implements the business rules for validating purchase orders against OpenDock appointments.
 */
public class ValidatorService {

    private final PurchaseOrderRepository repository;

    // PO number format: alphanumeric, 1-50 characters
    // Adjust this pattern based on CRS's actual PO number format
    private static final Pattern PO_NUMBER_PATTERN = Pattern.compile("^[A-Za-z0-9\\-]{1,50}$");

    public ValidatorService(PurchaseOrderRepository repository) {
        this.repository = repository;
    }

    /**
     * Validate an OpenDock webhook request.
     *
     * @param request The webhook request from OpenDock
     * @return Validation result with success/error message
     */
    public ValidationResult validate(WebhookRequest request) {
        // Step 1: Basic request validation
        if (request == null) {
            return ValidationResult.badRequest("Missing request body");
        }

        if (request.getAction() == null || request.getAction().isEmpty()) {
            return ValidationResult.badRequest("Missing required field: 'action'");
        }

        if (request.getAppointmentFields() == null) {
            return ValidationResult.badRequest("Missing required field: 'appointmentFields'");
        }

        // Step 2: BYPASS - Always allow cancellations
        if (request.isCancellation()) {
            return ValidationResult.success("Appointments are always allowed to be cancelled.");
        }

        // Step 3: Extract and validate reference number
        String refNumber = request.getRefNumber();
        if (refNumber == null || refNumber.isEmpty()) {
            return ValidationResult.badRequest(
                "Missing required field: 'refNumber' in appointmentFields or existingAppointment"
            );
        }

        // Step 4: Validate reference number format
        if (!isValidRefFormat(refNumber)) {
            return ValidationResult.badRequest(
                String.format(
                    "Invalid PO Number format: '%s'. Must be alphanumeric, 1-50 characters.",
                    refNumber
                )
            );
        }

        // Step 5: Look up PO in database
        List<PurchaseOrder> purchaseOrders;
        try {
            purchaseOrders = repository.findByPoNumber(refNumber);
        } catch (Exception e) {
            return ValidationResult.serviceUnavailable(
                "Database connection error: " + e.getMessage()
            );
        }

        // Step 6: Check results
        if (purchaseOrders == null || purchaseOrders.isEmpty()) {
            return ValidationResult.notFound(
                String.format(
                    "No records found for PO Number: %s. " +
                    "Please verify the PO number and try again.",
                    refNumber
                )
            );
        }

        if (purchaseOrders.size() > 1) {
            return ValidationResult.conflict(
                String.format(
                    "Multiple records found for PO Number: %s. " +
                    "Please contact support for assistance.",
                    refNumber
                )
            );
        }

        // Step 7: Apply business rules
        PurchaseOrder po = purchaseOrders.get(0);
        ValidationResult businessRuleResult = applyBusinessRules(request, po);
        if (!businessRuleResult.isSuccess()) {
            return businessRuleResult;
        }

        // All validations passed
        return ValidationResult.success(
            String.format("Appointment with PO Number %s is valid", refNumber)
        );
    }

    /**
     * Validate PO number format.
     * Adjust this pattern based on CRS's actual PO number format requirements.
     */
    private boolean isValidRefFormat(String refNumber) {
        if (refNumber == null || refNumber.isEmpty()) {
            return false;
        }
        return PO_NUMBER_PATTERN.matcher(refNumber).matches();
    }

    /**
     * Apply business rules to validate the appointment against the PO.
     * TODO: Add more rules based on CRS requirements.
     */
    private ValidationResult applyBusinessRules(WebhookRequest request, PurchaseOrder po) {
        // Rule 1: Check if PO is in valid status
        // Uncomment when we know CRS's PO status rules
        /*
        if (!po.isOpen()) {
            return ValidationResult.badRequest(
                String.format(
                    "PO Number %s has status '%s' and is not available for scheduling. " +
                    "Only open POs can be scheduled.",
                    po.getPoNumber(),
                    po.getStatus()
                )
            );
        }
        */

        // Rule 2: Check if appointment date is on or after expected date
        // Uncomment when we know CRS's date validation requirements
        /*
        if (request.getAppointmentFields() != null &&
            request.getAppointmentFields().getStart() != null &&
            po.getExpectedDate() != null) {

            LocalDate apptDate = parseApptDate(request.getAppointmentFields().getStart());
            if (apptDate != null && apptDate.isBefore(po.getExpectedDate())) {
                return ValidationResult.badRequest(
                    String.format(
                        "Appointment must be scheduled on or after the expected delivery date of %s for PO Number: %s. " +
                        "Select a later date and try again.",
                        po.getExpectedDate(),
                        po.getPoNumber()
                    )
                );
            }
        }
        */

        // All business rules passed
        return ValidationResult.success("OK");
    }

    /**
     * Parse appointment start datetime to LocalDate.
     * Handles ISO 8601 format (e.g., "2024-03-15T10:00:00Z").
     */
    /*
    private LocalDate parseApptDate(String startDateTime) {
        if (startDateTime == null || startDateTime.isEmpty()) {
            return null;
        }
        try {
            // Handle ISO 8601 format
            if (startDateTime.contains("T")) {
                return LocalDate.parse(startDateTime.substring(0, 10));
            }
            return LocalDate.parse(startDateTime);
        } catch (Exception e) {
            return null;
        }
    }
    */
}
