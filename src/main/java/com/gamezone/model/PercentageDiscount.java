package com.gamezone.model;
 
import java.time.LocalDate;
 
/**
 * Represents a percentage discount promotion commercialized by GameZone Unicesar.
 *
 * A percentage discount applies a fixed percentage to the total amount of
 * the sale, regardless of which products it contains.
 */
public class PercentageDiscount extends PercentageBasedPromotion {
 
 
    /**
     * Creates a new percentage discount promotion.
     *
     * @param id         unique identifier of the promotion
     * @param name       name of the promotion
     * @param startDate  first date on which the promotion is valid
     * @param endDate    last date on which the promotion is valid
     * @param percentage discount percentage, between 0 and 100
     * @throws IllegalArgumentException if the percentage is not between 0 and 100
     */
    public PercentageDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                              double percentage) {
        super(id, name, startDate, endDate);
        setPercentage(percentage);
    }
 
 
 
    /**
     * Calculates the discount as the configured percentage of the sale's total.
     *
     * @param sale the sale to evaluate
     * @return the discount amount in pesos
     */
    @Override
    public double calculateDiscount(Sale sale) {
        return applyPercentage(sale.calculateTotal());
    }
}