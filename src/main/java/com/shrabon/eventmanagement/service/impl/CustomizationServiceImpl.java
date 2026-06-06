package com.shrabon.eventmanagement.service.impl;

import com.shrabon.eventmanagement.dto.CustomizationItemForm;
import com.shrabon.eventmanagement.dto.QuoteResult;
import com.shrabon.eventmanagement.exception.ResourceNotFoundException;
import com.shrabon.eventmanagement.model.CustomizationItem;
import com.shrabon.eventmanagement.model.EventPackage;
import com.shrabon.eventmanagement.model.enums.CustomizationType;
import com.shrabon.eventmanagement.repository.CustomizationItemRepository;
import com.shrabon.eventmanagement.repository.EventPackageRepository;
import com.shrabon.eventmanagement.service.CustomizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class CustomizationServiceImpl implements CustomizationService {

    private final CustomizationItemRepository itemRepository;
    private final EventPackageRepository packageRepository;

    public CustomizationServiceImpl(CustomizationItemRepository itemRepository,
                                    EventPackageRepository packageRepository) {
        this.itemRepository = itemRepository;
        this.packageRepository = packageRepository;
    }

    @Override
    public List<CustomizationItem> findAll() {
        return itemRepository.findAll();
    }

    @Override
    public List<CustomizationItem> findActive() {
        return itemRepository.findByActiveTrue();
    }

    @Override
    public Map<CustomizationType, List<CustomizationItem>> findActiveGroupedByType() {
        Map<CustomizationType, List<CustomizationItem>> grouped = new LinkedHashMap<>();
        for (CustomizationType type : CustomizationType.values()) {
            List<CustomizationItem> items = itemRepository.findByTypeAndActiveTrue(type);
            if (!items.isEmpty()) {
                grouped.put(type, items);
            }
        }
        return grouped;
    }

    @Override
    public CustomizationItem getById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customization item not found: " + id));
    }

    @Override
    @Transactional
    public CustomizationItem save(CustomizationItemForm form) {
        CustomizationItem item = form.getId() != null ? getById(form.getId()) : new CustomizationItem();
        item.setName(form.getName());
        item.setType(form.getType());
        item.setDescription(form.getDescription());
        item.setPrice(form.getPrice());
        item.setUnit(form.getUnit());
        item.setActive(form.isActive());
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public QuoteResult calculateQuote(Long packageId, List<Long> selectedItemIds) {
        BigDecimal basePrice = BigDecimal.ZERO;
        BigDecimal discountPercent = BigDecimal.ZERO;

        if (packageId != null) {
            EventPackage pkg = packageRepository.findById(packageId).orElse(null);
            if (pkg != null) {
                basePrice = pkg.getBasePrice() != null ? pkg.getBasePrice() : BigDecimal.ZERO;
                discountPercent = pkg.getDiscountPercent() != null ? pkg.getDiscountPercent() : BigDecimal.ZERO;
            }
        }

        BigDecimal additional = BigDecimal.ZERO;
        if (selectedItemIds != null && !selectedItemIds.isEmpty()) {
            List<CustomizationItem> items = itemRepository.findAllById(selectedItemIds);
            for (CustomizationItem item : items) {
                if (item.isActive() && item.getPrice() != null) {
                    additional = additional.add(item.getPrice());
                }
            }
        }

        BigDecimal subtotal = basePrice.add(additional);
        // Discount only applies to the package base price.
        BigDecimal discount = basePrice
                .multiply(discountPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal finalPrice = subtotal.subtract(discount).setScale(2, RoundingMode.HALF_UP);

        return new QuoteResult(
                basePrice.setScale(2, RoundingMode.HALF_UP),
                additional.setScale(2, RoundingMode.HALF_UP),
                discount.setScale(2, RoundingMode.HALF_UP),
                finalPrice);
    }
}
