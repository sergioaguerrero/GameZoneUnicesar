package com.gamezone.model;

/**
 * Represents an item within a sale, containing a specific product and its purchased quantity.
 */
public class SaleItem {

    private Product product;
    private int quantity;

    /**
     * Constructs a new SaleItem with the specified product and quantity.
     *
     * @param product  the product being purchased
     * @param quantity the amount of the product being purchased
     */
    public SaleItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Calculates the subtotal for this specific item.
     *
     * @return the subtotal calculated as product price multiplied by quantity
     */
    public double calculateSubtotal() {
        return this.product.getPrice() * this.quantity;
    }
}