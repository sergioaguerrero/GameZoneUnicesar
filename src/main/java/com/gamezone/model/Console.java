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

    /**
     * Creates a new console.
     *
     * @param productId     unique identifier of the product
     * @param title         title/name of the console
     * @param price         unit price
     * @param stockQuantity quantity available in inventory
     * @param brand         brand of the console (e.g. Sony, Microsoft)
     * @param model         model of the console (e.g. PlayStation 5)
     * @param generation    generation number of the console
     */
    public Console(String productId, String title, double price, int stockQuantity,
                    String brand, String model, int generation) {
        super(productId, title, price, stockQuantity);
        this.brand = brand;
        this.model = model;
        this.generation = generation;
    }

    /**
     * Returns the brand of the console.
     *
     * @return the brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * Sets the brand of the console.
     *
     * @param brand the new brand
     */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Returns the model of the console.
     *
     * @return the model
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the model of the console.
     *
     * @param model the new model
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Returns the generation number of the console.
     *
     * @return the generation
     */
    public int getGeneration() {
        return generation;
    }

    /**
     * Sets the generation number of the console.
     *
     * @param generation the new generation
     */
    public void setGeneration(int generation) {
        this.generation = generation;
    }

    /**
     * Builds a full description of the console combining the common
     * product attributes with its brand, model and generation.
     *
     * @return the full description of the console
     */
    @Override
    public String getFullDescription() {
        return String.format(
                "[Console] %s | Brand: %s | Model: %s | Generation: %d | Price: $%.2f | Stock: %d",
                getTitle(), brand, model, generation, getPrice(), getStockQuantity());
    }
}