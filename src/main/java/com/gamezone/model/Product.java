
package com.gamezone.model;

import java.io.Serializable;

/**
 * Abstract base class that represents a general product commercialized by
 * GameZone Unicesar. Holds the attributes and behavior common to every product
 * type (video games, consoles, etc.).
 *
 * This class cannot be instantiated directly since it represents a generic
 * category. Concrete subclasses must provide their own implementation of
 * {@link #getFullDescription()}.
 */
public abstract class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    private String productId;
    private String title;
    private double price;
    private int stockQuantity;

    public Product(String productId, String title, double price, int stockQuantity) {
        this.productId = productId;
        this.title = title;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public boolean hasEnoughStock(int quantity) {
        return quantity > 0 && this.stockQuantity >= quantity;
    }

    public void decreaseStock(int quantity) {
        if (!hasEnoughStock(quantity)) {
            throw new IllegalArgumentException(
                    "Insufficient stock for product " + productId);
        }
        this.stockQuantity -= quantity;
    }

    public abstract String getFullDescription();
}
