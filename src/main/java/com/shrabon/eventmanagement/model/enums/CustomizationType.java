package com.shrabon.eventmanagement.model.enums;

/**
 * Categories used by the Custom Package Builder.
 */
public enum CustomizationType {
    DECORATION("Decoration"),
    FOOD("Food Item"),
    PHOTOGRAPHY("Photography"),
    VIDEOGRAPHY("Videography"),
    LIGHTING("Lighting"),
    SOUND("Sound System"),
    STAGE("Stage Setup");

    private final String label;

    CustomizationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
