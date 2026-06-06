package com.shrabon.eventmanagement.service;

import com.shrabon.eventmanagement.dto.PackageForm;
import com.shrabon.eventmanagement.model.EventPackage;

import java.util.List;

public interface PackageService {

    List<EventPackage> findAll();

    List<EventPackage> findActive();

    List<EventPackage> findFeatured();

    EventPackage getById(Long id);

    EventPackage save(PackageForm form);

    PackageForm toForm(EventPackage eventPackage);

    void delete(Long id);

    long count();
}
