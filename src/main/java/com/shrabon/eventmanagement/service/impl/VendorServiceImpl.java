package com.shrabon.eventmanagement.service.impl;

import com.shrabon.eventmanagement.dto.VendorForm;
import com.shrabon.eventmanagement.exception.ResourceNotFoundException;
import com.shrabon.eventmanagement.model.Vendor;
import com.shrabon.eventmanagement.repository.VendorRepository;
import com.shrabon.eventmanagement.service.VendorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class VendorServiceImpl implements VendorService {

    private final VendorRepository repository;

    public VendorServiceImpl(VendorRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Vendor> findAll() {
        return repository.findAll();
    }

    @Override
    public Vendor getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found: " + id));
    }

    @Override
    @Transactional
    public Vendor save(VendorForm form) {
        Vendor vendor = form.getId() != null ? getById(form.getId()) : new Vendor();
        vendor.setName(form.getName());
        vendor.setCategory(form.getCategory());
        vendor.setContactPerson(form.getContactPerson());
        vendor.setPhone(form.getPhone());
        vendor.setEmail(form.getEmail());
        vendor.setAddress(form.getAddress());
        vendor.setServiceDetails(form.getServiceDetails());
        vendor.setActive(form.isActive());
        return repository.save(vendor);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
