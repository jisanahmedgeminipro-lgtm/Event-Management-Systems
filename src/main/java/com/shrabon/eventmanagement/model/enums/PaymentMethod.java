package com.shrabon.eventmanagement.model.enums;

public enum PaymentMethod {
    CASH("Cash"),
    BKASH("bKash"),
    NAGAD("Nagad"),
    ROCKET("Rocket"),
    CARD("Card"),
    BANK("Bank Transfer");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
