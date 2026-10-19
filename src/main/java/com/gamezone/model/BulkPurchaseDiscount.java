package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents a bulk purchase discount promotion commercialized by GameZone Unicesar.
 *
 * A bulk purchase discount applies a percentage discount to the total amount
 * of the sale only when the sale includes at least a minimum number of
 * product units.
 */
public class BulkPurchaseDiscount extends Promotion {

    private int minimumQuantity;
    private double percentage;

    /**
     * Creates a new bulk purchase discount promotion.
     *
     * @param id              unique identifier of the promotion
     * @param name            name of the promotion
     * @param startDate       first date on which the promotion is valid
     * @param endDate         last date on which the promotion is valid
     * @param minimumQuantity minimum number of product units required, greater than zero
     * @param percentage      discount percentage, between 0 and 100
     * @throws IllegalArgumentException if the minimum quantity is not greater than zero,
     *                                  or if the percentage is not between 0 and 100
     */
    public BulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                                int minimumQuantity, double percentage) {
        super(id, name, startDate, endDate);
        setMinimumQuantity(minimumQuantity);
        setPercentage(percentage);
    }

    /**
     * Returns the minimum number of product units required for the discount to apply.
     *
     * @return the minimum quantity
     */
    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    /**
     * Sets the minimum number of product units required for the discount to apply.
     *
     * @param minimumQuantity the new minimum quantity, greater than zero
     * @throws IllegalArgumentException if the minimum quantity is not greater than zero
     */
    public void setMinimumQuantity(int minimumQuantity) {
        if (minimumQuantity <= 0) {
            throw new IllegalArgumentException("Minimum quantity must be greater than zero");
        }
        this.minimumQuantity = minimumQuantity;
    }

    /**
     * Returns the discount percentage.
     *
     * @return the discount percentage
     */
    public double getPercentage() {
        return percentage;
    }

    /**
     * Sets the discount percentage.
     *
     * @param percentage the new discount percentage, between 0 and 100
     * @throws IllegalArgumentException if the percentage is not between 0 and 100
     */
    public void setPercentage(double percentage) {
        if (percentage < 0 || percentage > 100) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        this.percentage = percentage;
    }

    /**
     * Calculates the discount as the configured percentage of the sale's
     * total, but only when the sale includes at least the minimum required
     * number of product units.
     *
     * @param sale the sale to evaluate
     * @return the discount amount in pesos, or zero if the minimum quantity is not met
     */
    @Override
    public double calculateDiscount(Sale sale) {
        int totalQuantity = 0;
        for (SaleItem item : sale.getItems()) {
            totalQuantity += item.getQuantity();
        }
        if (totalQuantity < minimumQuantity) {
            return 0.0;
        }
        return sale.calculateTotal() * percentage / 100;
    }
}