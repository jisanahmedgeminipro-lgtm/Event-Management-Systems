package com.shrabon.eventmanagement.service;

import com.shrabon.eventmanagement.dto.VendorForm;
import com.shrabon.eventmanagement.model.Vendor;

import java.util.List;

public interface VendorService {

    List<Vendor> findAll();

    Vendor getById(Long id);

    Vendor save(VendorForm form);

    void delete(Long id);
}
