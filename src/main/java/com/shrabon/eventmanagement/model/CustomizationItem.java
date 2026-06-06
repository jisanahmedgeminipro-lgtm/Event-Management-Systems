package com.shrabon.eventmanagement.model;

import com.shrabon.eventmanagement.model.enums.CustomizationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * A catalog item that clients can add to a package using the
 * Custom Package Builder (e.g. "Premium Stage Decor", "Chicken Roast / plate").
 */
@Entity
@Table(name = "customization_items")
@Getter
@Setter
@NoArgsConstructor
public class CustomizationItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CustomizationType type;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Column(length = 40)
    private String unit = "package";

    @Column(nullable = false)
    private boolean active = true;
}
