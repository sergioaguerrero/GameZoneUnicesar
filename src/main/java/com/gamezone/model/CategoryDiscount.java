package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents a category discount promotion commercialized by GameZone Unicesar.
 *
 * A category discount applies a percentage discount only to the products of
 * a specific target category ("VIDEOGAME" or "CONSOLE") included in the sale.
 * Products of any other category are not affected.
 */
public class CategoryDiscount extends Promotion {

    /**
     * Category eligible for a video game discount.
     */
    public static final String CATEGORY_VIDEOGAME = "VIDEOGAME";

    /**
     * Category eligible for a console discount.
     */
    public static final String CATEGORY_CONSOLE = "CONSOLE";

    private double percentage;
    private String targetCategory;

    /**
     * Creates a new category discount promotion.
     *
     * @param id             unique identifier of the promotion
     * @param name           name of the promotion
     * @param startDate      first date on which the promotion is valid
     * @param endDate        last date on which the promotion is valid
     * @param percentage     discount percentage, between 0 and 100
     * @param targetCategory category the discount applies to ("VIDEOGAME" or "CONSOLE")
     * @throws IllegalArgumentException if the percentage is not between 0 and 100,
     *                                  or if the category is not "VIDEOGAME" or "CONSOLE"
     */
    public CategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                            double percentage, String targetCategory) {
        super(id, name, startDate, endDate);
        setPercentage(percentage);
        setTargetCategory(targetCategory);
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
     * Returns the category this discount applies to.
     *
     * @return the target category, "VIDEOGAME" or "CONSOLE"
     */
    public String getTargetCategory() {
        return targetCategory;
    }

    /**
     * Sets the category this discount applies to.
     *
     * @param targetCategory the new target category, "VIDEOGAME" or "CONSOLE"
     * @throws IllegalArgumentException if the category is not "VIDEOGAME" or "CONSOLE"
     */
    public void setTargetCategory(String targetCategory) {
        if (!CATEGORY_VIDEOGAME.equals(targetCategory) && !CATEGORY_CONSOLE.equals(targetCategory)) {
            throw new IllegalArgumentException(
                    "Target category must be either " + CATEGORY_VIDEOGAME + " or " + CATEGORY_CONSOLE);
        }
        this.targetCategory = targetCategory;
    }

    /**
     * Calculates the discount as the configured percentage applied only to
     * the subtotal of the products that belong to the target category.
     *
     * @param sale the sale to evaluate
     * @return the discount amount in pesos
     */
    @Override
    public double calculateDiscount(Sale sale) {
        double categorySubtotal = 0.0;
        for (SaleItem item : sale.getItems()) {
            if (belongsToTargetCategory(item.getProduct())) {
                categorySubtotal += item.calculateSubtotal();
            }
        }
        return categorySubtotal * percentage / 100;
    }

    /**
     * Checks whether the given product belongs to this promotion's target category.
     *
     * @param product the product to check
     * @return true if the product belongs to the target category, false otherwise
     */
    private boolean belongsToTargetCategory(Product product) {
        if (CATEGORY_VIDEOGAME.equals(targetCategory)) {
            return product instanceof VideoGame;
        }
        return product instanceof Console;
    }
}
