package com.shrabon.eventmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Editable key/value content blocks for the public website
 * (managed via Admin → Website Content Management).
 */
@Entity
@Table(name = "website_content", uniqueConstraints = @UniqueConstraint(columnNames = "content_key"))
@Getter
@Setter
@NoArgsConstructor
public class WebsiteContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_key", nullable = false, unique = true, length = 100)
    private String contentKey;

    @Column(name = "content_value", columnDefinition = "TEXT")
    private String contentValue;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public WebsiteContent(String contentKey, String contentValue) {
        this.contentKey = contentKey;
        this.contentValue = contentValue;
    }

    @PrePersist
    @PreUpdate
    public void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}
