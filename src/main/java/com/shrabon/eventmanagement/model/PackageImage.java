package com.shrabon.eventmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "package_images")
@Getter
@Setter
@NoArgsConstructor
public class PackageImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package_id", nullable = false)
    private EventPackage eventPackage;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    public PackageImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
