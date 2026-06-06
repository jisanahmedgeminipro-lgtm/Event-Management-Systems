package com.shrabon.eventmanagement.repository;

import com.shrabon.eventmanagement.model.Client;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByUserId(Long userId);

    Optional<Client> findByUserEmail(String email);

    @Query("""
            SELECT c FROM Client c
            WHERE LOWER(c.user.fullName) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(c.user.email)    LIKE LOWER(CONCAT('%', :q, '%'))
               OR c.user.phone           LIKE CONCAT('%', :q, '%')
               OR LOWER(c.city)          LIKE LOWER(CONCAT('%', :q, '%'))
            """)
    List<Client> search(@Param("q") String query);
}
