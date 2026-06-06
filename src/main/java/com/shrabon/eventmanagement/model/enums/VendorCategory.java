package com.shrabon.eventmanagement.model.enums;

public enum VendorCategory {
    DECORATOR("Decorator"),
    CATERER("Caterer"),
    PHOTOGRAPHER("Photographer"),
    VIDEOGRAPHER("Videographer"),
    SOUND_PROVIDER("Sound Provider"),
    LIGHTING_PROVIDER("Lighting Provider"),
    TRANSPORT_PROVIDER("Transport Provider"),
    OTHER("Other");

    private final String label;

    VendorCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
