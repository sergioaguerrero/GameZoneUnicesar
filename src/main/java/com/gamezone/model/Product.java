package com.gamezone.model;

/**
 * Abstract base class that represents a general product commercialized by
 * GameZone Unicesar. Holds the attributes and behavior common to every product
 * type (video games, consoles, etc.).
 *
 * This class cannot be instantiated directly since it represents a generic
 * category. Concrete subclasses must provide their own implementation of
 * {@link #getFullDescription()}.
 */
public abstract class Product {

    private String productId;
    private String title;
    private double price;
    private int stockQuantity;

    /**
     * Creates a new product with the given common attributes.
     *
     * @param productId unique identifier of the product
     * @param title product title
     * @param price unit price of the product
     * @param stockQuantity quantity available in inventory
     */
    public Product(String productId, String title, double price, int stockQuantity) {
        this.productId = productId;
        this.title = title;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    /**
     * Returns the unique identifier of the product.
     *
     * @return the product id
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Sets the unique identifier of the product.
     *
     * @param productId the new product id
     */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * Returns the title of the product.
     *
     * @return the product title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the product.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the unit price of the product.
     *
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the unit price of the product.
     *
     * @param price the new price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Returns the quantity currently available in inventory.
     *
     * @return the stock quantity
     */
    public int getStockQuantity() {
        return stockQuantity;
    }

    /**
     * Sets the quantity available in inventory.
     *
     * @param stockQuantity the new stock quantity
     */
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    /**
     * Checks whether there is enough stock available for the given quantity.
     *
     * @param quantity the quantity to check
     * @return true if there is enough stock, false otherwise
     */
    public boolean hasEnoughStock(int quantity) {
        return quantity > 0 && this.stockQuantity >= quantity;
    }

    /**
     * Decreases the available stock by the given quantity. Should only be
     * called after verifying with {@link #hasEnoughStock(int)} that enough
     * stock is available.
     *
     * @param quantity the amount to subtract from stock
     * @throws IllegalArgumentException if there is not enough stock available
     */
    public void decreaseStock(int quantity) {
        if (!hasEnoughStock(quantity)) {
            throw new IllegalArgumentException(
                    "Insufficient stock for product " + productId);
        }
        this.stockQuantity -= quantity;
    }

    /**
     * Builds a complete description of the product that integrates the common
     * attributes with the specific characteristics of each concrete subclass.
     * Every subclass must implement this method on its own, since the
     * description depends on its own attributes.
     *
     * @return the full description of the product
     */
    public abstract String getFullDescription();
}
