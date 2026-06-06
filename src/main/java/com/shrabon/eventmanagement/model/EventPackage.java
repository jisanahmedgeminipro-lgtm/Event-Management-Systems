package com.shrabon.eventmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A pre-defined event package. Named {@code EventPackage} because
 * {@code package} is a reserved Java keyword; maps to table "packages".
 */
@Entity
@Table(name = "packages")
@Getter
@Setter
@NoArgsConstructor
public class EventPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private EventCategory category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice = BigDecimal.ZERO;

    @Column(name = "discount_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercent = BigDecimal.ZERO;

    // Inclusions
    private boolean decoration;
    private boolean catering;
    private boolean photography;
    private boolean videography;
    private boolean lighting;

    @Column(name = "sound_system")
    private boolean soundSystem;

    @Column(name = "stage_setup")
    private boolean stageSetup;

    @Column(name = "guest_capacity", nullable = false)
    private int guestCapacity;

    @Column(nullable = false)
    private boolean featured;

    @Column(nullable = false)
    private boolean active = true;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "package_features", joinColumns = @JoinColumn(name = "package_id"))
    @Column(name = "feature", length = 255)
    private List<String> features = new ArrayList<>();

    @OneToMany(mappedBy = "eventPackage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PackageImage> images = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /** Final price after applying the discount percentage. */
    @Transient
    public BigDecimal getFinalPrice() {
        if (basePrice == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal pct = discountPercent == null ? BigDecimal.ZERO : discountPercent;
        BigDecimal discount = basePrice.multiply(pct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return basePrice.subtract(discount).setScale(2, RoundingMode.HALF_UP);
    }

    /** Monetary value of the discount. */
    @Transient
    public BigDecimal getDiscountAmount() {
        if (basePrice == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal pct = discountPercent == null ? BigDecimal.ZERO : discountPercent;
        return basePrice.multiply(pct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    /** Primary image url for listings, or a placeholder. */
    @Transient
    public String getCoverImage() {
        return images != null && !images.isEmpty() ? images.get(0).getImageUrl() : null;
    }

    public void addImage(PackageImage image) {
        image.setEventPackage(this);
        this.images.add(image);
    }
}
