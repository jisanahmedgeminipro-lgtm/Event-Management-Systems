package com.shrabon.eventmanagement.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Automatic price breakdown produced by the Custom Package Builder.
 */
@Getter
@Setter
public class QuoteResult {

    private BigDecimal basePrice = BigDecimal.ZERO;
    private BigDecimal additionalCost = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal finalPrice = BigDecimal.ZERO;

    public QuoteResult() {
    }

    public QuoteResult(BigDecimal basePrice, BigDecimal additionalCost, BigDecimal discount, BigDecimal finalPrice) {
        this.basePrice = basePrice;
        this.additionalCost = additionalCost;
        this.discount = discount;
        this.finalPrice = finalPrice;
    }
}
