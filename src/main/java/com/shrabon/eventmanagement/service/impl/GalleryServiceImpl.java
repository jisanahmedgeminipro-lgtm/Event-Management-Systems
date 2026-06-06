package com.shrabon.eventmanagement.service.impl;

import com.shrabon.eventmanagement.model.GalleryItem;
import com.shrabon.eventmanagement.repository.GalleryItemRepository;
import com.shrabon.eventmanagement.service.GalleryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GalleryServiceImpl implements GalleryService {

    private final GalleryItemRepository repository;

    public GalleryServiceImpl(GalleryItemRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<GalleryItem> findAll() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public List<GalleryItem> findLatest(int limit) {
        return repository.findByOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }

    @Override
    @Transactional
    public GalleryItem save(GalleryItem item) {
        return repository.save(item);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
