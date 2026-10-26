package com.gamezone.model;
 
import java.time.LocalDate;
 
/**
 * Base class for the promotions whose discount is a percentage of an amount.
 * It keeps in a single place the percentage and its validation rule (a value
 * between 0 and 100), so the rule can change without editing each promotion.
 */
public abstract class PercentageBasedPromotion extends Promotion {
 
    private double percentage;
 
    /**
     * Creates the common part of a percentage-based promotion. Subclasses set
     * the percentage with {@link #setPercentage(double)} in the order their
     * own validations require.
     *
     * @param id unique identifier of the promotion
     * @param name name of the promotion
     * @param startDate first date on which the promotion is valid
     * @param endDate last date on which the promotion is valid
     */
    protected PercentageBasedPromotion(String id, String name, LocalDate startDate,
            LocalDate endDate) {
        super(id, name, startDate, endDate);
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
     * Applies the discount percentage to the given amount.
     *
     * @param amount the amount the percentage is applied to
     * @return the discount for that amount
     */
    protected double applyPercentage(double amount) {
        return amount * percentage / 100;
    }
}