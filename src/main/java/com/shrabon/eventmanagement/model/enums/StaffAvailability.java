package com.shrabon.eventmanagement.model.enums;

public enum StaffAvailability {
    AVAILABLE("Available"),
    BUSY("Busy"),
    ON_LEAVE("On Leave");

    private final String label;

    StaffAvailability(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
