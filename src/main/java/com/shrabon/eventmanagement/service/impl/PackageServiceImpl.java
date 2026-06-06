package com.shrabon.eventmanagement.service.impl;

import com.shrabon.eventmanagement.dto.PackageForm;
import com.shrabon.eventmanagement.exception.ResourceNotFoundException;
import com.shrabon.eventmanagement.model.EventCategory;
import com.shrabon.eventmanagement.model.EventPackage;
import com.shrabon.eventmanagement.model.PackageImage;
import com.shrabon.eventmanagement.repository.EventCategoryRepository;
import com.shrabon.eventmanagement.repository.EventPackageRepository;
import com.shrabon.eventmanagement.service.PackageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PackageServiceImpl implements PackageService {

    private final EventPackageRepository packageRepository;
    private final EventCategoryRepository categoryRepository;

    public PackageServiceImpl(EventPackageRepository packageRepository,
                              EventCategoryRepository categoryRepository) {
        this.packageRepository = packageRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<EventPackage> findAll() {
        return packageRepository.findAll();
    }

    @Override
    public List<EventPackage> findActive() {
        return packageRepository.findByActiveTrue();
    }

    @Override
    public List<EventPackage> findFeatured() {
        return packageRepository.findByActiveTrueAndFeaturedTrue();
    }

    @Override
    public EventPackage getById(Long id) {
        return packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package not found: " + id));
    }

    @Override
    @Transactional
    public EventPackage save(PackageForm form) {
        EventPackage pkg = form.getId() != null ? getById(form.getId()) : new EventPackage();
        pkg.setName(form.getName());
        pkg.setDescription(form.getDescription());
        pkg.setBasePrice(form.getBasePrice());
        pkg.setDiscountPercent(form.getDiscountPercent());
        pkg.setDecoration(form.isDecoration());
        pkg.setCatering(form.isCatering());
        pkg.setPhotography(form.isPhotography());
        pkg.setVideography(form.isVideography());
        pkg.setLighting(form.isLighting());
        pkg.setSoundSystem(form.isSoundSystem());
        pkg.setStageSetup(form.isStageSetup());
        pkg.setGuestCapacity(form.getGuestCapacity());
        pkg.setFeatured(form.isFeatured());
        pkg.setActive(form.isActive());

        if (form.getCategoryId() != null) {
            EventCategory category = categoryRepository.findById(form.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + form.getCategoryId()));
            pkg.setCategory(category);
        } else {
            pkg.setCategory(null);
        }

        pkg.getFeatures().clear();
        pkg.getFeatures().addAll(splitLines(form.getFeaturesText()));

        pkg.getImages().clear();
        for (String url : splitLines(form.getImageUrls())) {
            pkg.addImage(new PackageImage(url));
        }

        return packageRepository.save(pkg);
    }

    @Override
    public PackageForm toForm(EventPackage pkg) {
        PackageForm form = new PackageForm();
        form.setId(pkg.getId());
        form.setName(pkg.getName());
        form.setCategoryId(pkg.getCategory() != null ? pkg.getCategory().getId() : null);
        form.setDescription(pkg.getDescription());
        form.setBasePrice(pkg.getBasePrice());
        form.setDiscountPercent(pkg.getDiscountPercent());
        form.setDecoration(pkg.isDecoration());
        form.setCatering(pkg.isCatering());
        form.setPhotography(pkg.isPhotography());
        form.setVideography(pkg.isVideography());
        form.setLighting(pkg.isLighting());
        form.setSoundSystem(pkg.isSoundSystem());
        form.setStageSetup(pkg.isStageSetup());
        form.setGuestCapacity(pkg.getGuestCapacity());
        form.setFeatured(pkg.isFeatured());
        form.setActive(pkg.isActive());
        form.setFeaturesText(String.join("\n", pkg.getFeatures()));
        form.setImageUrls(pkg.getImages().stream()
                .map(PackageImage::getImageUrl)
                .collect(Collectors.joining("\n")));
        return form;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        packageRepository.deleteById(id);
    }

    @Override
    public long count() {
        return packageRepository.count();
    }

    private List<String> splitLines(String text) {
        if (!StringUtils.hasText(text)) {
            return List.of();
        }
        return Arrays.stream(text.split("\\r?\\n"))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }
}
