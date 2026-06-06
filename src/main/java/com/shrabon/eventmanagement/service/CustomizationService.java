package com.shrabon.eventmanagement.service;

import com.shrabon.eventmanagement.dto.CustomizationItemForm;
import com.shrabon.eventmanagement.dto.QuoteResult;
import com.shrabon.eventmanagement.model.CustomizationItem;
import com.shrabon.eventmanagement.model.enums.CustomizationType;

import java.util.List;
import java.util.Map;

public interface CustomizationService {

    List<CustomizationItem> findAll();

    List<CustomizationItem> findActive();

    /** Active items grouped by their {@link CustomizationType} (for the builder UI). */
    Map<CustomizationType, List<CustomizationItem>> findActiveGroupedByType();

    CustomizationItem getById(Long id);

    CustomizationItem save(CustomizationItemForm form);

    void delete(Long id);

    /**
     * Compute the automatic price breakdown for a custom package: base price
     * (from the selected package, if any) + the sum of selected add-ons,
     * with the package discount applied.
     */
    QuoteResult calculateQuote(Long packageId, List<Long> selectedItemIds);
}
