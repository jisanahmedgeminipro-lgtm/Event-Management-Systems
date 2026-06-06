package com.shrabon.eventmanagement.repository;

import com.shrabon.eventmanagement.model.WebsiteContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WebsiteContentRepository extends JpaRepository<WebsiteContent, Long> {

    Optional<WebsiteContent> findByContentKey(String contentKey);
}
