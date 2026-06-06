package com.shrabon.eventmanagement.service;

import com.shrabon.eventmanagement.dto.CategoryForm;
import com.shrabon.eventmanagement.model.EventCategory;

import java.util.List;

public interface EventCategoryService {

    List<EventCategory> findAll();

    List<EventCategory> findActive();

    EventCategory getById(Long id);

    EventCategory save(CategoryForm form);

    void delete(Long id);
}
