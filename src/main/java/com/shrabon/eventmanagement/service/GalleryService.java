package com.shrabon.eventmanagement.service;

import com.shrabon.eventmanagement.model.GalleryItem;

import java.util.List;

public interface GalleryService {

    List<GalleryItem> findAll();

    List<GalleryItem> findLatest(int limit);

    GalleryItem save(GalleryItem item);

    void delete(Long id);
}
