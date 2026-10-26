package com.gamezone.model;
 
import java.time.LocalDate;
 
/**
 * Represents a category discount promotion commercialized by GameZone Unicesar.
 *
 * A category discount applies a percentage discount only to the products of a
 * specific target category ("VIDEOGAME", "CONSOLE" or "ACCESSORY") included in
 * the sale. Products of any other category are not affected.
 */
public class CategoryDiscount extends PercentageBasedPromotion {
 
    /**
     * Category eligible for a video game discount.
     */
    public static final String CATEGORY_VIDEOGAME = Product.CATEGORY_VIDEOGAME;
 
    /**
     * Category eligible for a console discount.
     */
    public static final String CATEGORY_CONSOLE = Product.CATEGORY_CONSOLE;
 
    /**
     * Category eligible for an accessory discount.
     */
    public static final String CATEGORY_ACCESSORY = Product.CATEGORY_ACCESSORY;
 
    private String targetCategory;
 
    /**
     * Creates a new category discount promotion.
     *
     * @param id unique identifier of the promotion
     * @param name name of the promotion
     * @param startDate first date on which the promotion is valid
     * @param endDate last date on which the promotion is valid
     * @param percentage discount percentage, between 0 and 100
     * @param targetCategory category the discount applies to ("VIDEOGAME",
     * "CONSOLE" or "ACCESSORY")
     * @throws IllegalArgumentException if the percentage is not between 0 and
     * 100, or if the category is not one of the allowed values
     */
    public CategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
            double percentage, String targetCategory) {
        super(id, name, startDate, endDate);
        setPercentage(percentage);
        setTargetCategory(targetCategory);
    }
 
 
 
    /**
     * Returns the category this discount applies to.
     *
     * @return the target category, "VIDEOGAME", "CONSOLE" or "ACCESSORY"
     */
    public String getTargetCategory() {
        return targetCategory;
    }
 
    /**
     * Sets the category this discount applies to.
     *
     * @param targetCategory the new target category, "VIDEOGAME", "CONSOLE" or
     * "ACCESSORY"
     * @throws IllegalArgumentException if the category is not one of the
     * allowed values
     */
    public void setTargetCategory(String targetCategory) {
        if (!CATEGORY_VIDEOGAME.equals(targetCategory)
                && !CATEGORY_CONSOLE.equals(targetCategory)
                && !CATEGORY_ACCESSORY.equals(targetCategory)) {
            throw new IllegalArgumentException(
                    "Target category must be " + CATEGORY_VIDEOGAME + ", " + CATEGORY_CONSOLE
                    + " or " + CATEGORY_ACCESSORY);
        }
        this.targetCategory = targetCategory;
    }
 
    /**
     * Calculates the discount as the configured percentage applied only to the
     * subtotal of the products that belong to the target category.
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
        return applyPercentage(categorySubtotal);
    }
 
    /**
     * Checks whether the given product belongs to this promotion's target
     * category.
     *
     * @param product the product to check
     * @return true if the product belongs to the target category, false
     * otherwise
     */
    private boolean belongsToTargetCategory(Product product) {
        return targetCategory.equals(product.getCategory());
    }
}