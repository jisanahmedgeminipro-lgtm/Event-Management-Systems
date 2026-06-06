package com.shrabon.eventmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Shrabon Decorator & Event Management System.
 *
 * <p>A production-ready Spring Boot + Thymeleaf + MySQL application providing
 * role-based portals for ADMIN, STAFF and CLIENT users along with a public
 * business website.</p>
 */
@SpringBootApplication
public class EventManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventManagementApplication.class, args);
    }
}
