package com.shrabon.eventmanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Event type / category (Wedding, Reception, Corporate Event, Custom Event ...).
 * Admin can create unlimited categories.
 */
@Entity
@Table(name = "event_categories", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Getter
@Setter
@NoArgsConstructor
public class EventCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 80)
    private String icon;

    @Column(nullable = false)
    private boolean active = true;
}
