package com.shrabon.eventmanagement.service.impl;

import com.shrabon.eventmanagement.dto.CategoryForm;
import com.shrabon.eventmanagement.exception.ResourceNotFoundException;
import com.shrabon.eventmanagement.model.EventCategory;
import com.shrabon.eventmanagement.repository.EventCategoryRepository;
import com.shrabon.eventmanagement.service.EventCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EventCategoryServiceImpl implements EventCategoryService {

    private final EventCategoryRepository repository;

    public EventCategoryServiceImpl(EventCategoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<EventCategory> findAll() {
        return repository.findAll();
    }

    @Override
    public List<EventCategory> findActive() {
        return repository.findByActiveTrue();
    }

    @Override
    public EventCategory getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event category not found: " + id));
    }

    @Override
    @Transactional
    public EventCategory save(CategoryForm form) {
        EventCategory category = form.getId() != null ? getById(form.getId()) : new EventCategory();
        category.setName(form.getName());
        category.setDescription(form.getDescription());
        category.setIcon(form.getIcon());
        category.setActive(form.isActive());
        return repository.save(category);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
