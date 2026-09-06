package com.gamezone.model;

/**
 * Represents a game console product. Extends {@link Product} adding the
 * characteristics specific to this type of product: brand, model and
 * generation.
 */
public class Console extends Product {

    private String brand;
    private String model;
    private int generation;

    public Console(String productId, String title, double price, int stockQuantity,
            String brand, String model, int generation) {
        super(productId, title, price, stockQuantity);
        this.brand = brand;
        this.model = model;
        this.generation = generation;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }
    @Override
    public String getFullDescription() {
        return String.format(
                "[Console] %s | Brand: %s | Model: %s | Generation: %d | Price: $%.2f | Stock: %d",
                getTitle(), brand, model, generation, getPrice(), getStockQuantity());
    }
}
