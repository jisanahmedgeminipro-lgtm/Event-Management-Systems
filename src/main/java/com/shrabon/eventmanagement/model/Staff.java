package com.shrabon.eventmanagement.model;

import com.shrabon.eventmanagement.model.enums.StaffAvailability;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Profile extension for STAFF users.
 */
@Entity
@Table(name = "staff")
@Getter
@Setter
@NoArgsConstructor
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 100)
    private String position;

    @Column(precision = 12, scale = 2)
    private BigDecimal salary = BigDecimal.ZERO;

    @Column(length = 500)
    private String skills;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StaffAvailability availability = StaffAvailability.AVAILABLE;

    @Column(name = "join_date")
    private LocalDate joinDate;
}
