package com.shrabon.eventmanagement.model.enums;

public enum PaymentStatus {
    PENDING("Pending"),
    PARTIAL("Partial"),
    PAID("Paid");

    private final String label;

    PaymentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
