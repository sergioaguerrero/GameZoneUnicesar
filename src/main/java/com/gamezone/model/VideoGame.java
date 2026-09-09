package com.gamezone.model;

/**
 * Represents a video game product. Extends {@link Product} adding the
 * characteristics specific to this type of product: platform, genre and
 * recommended age rating.
 */
public class VideoGame extends Product {

    private String platform;
    private String genre;
    private String ageRating;

    /**
     * Creates a new video game.
     *
     * @param productId     unique identifier of the product
     * @param title         title of the video game
     * @param price         unit price
     * @param stockQuantity quantity available in inventory
     * @param platform      platform the game was developed for (e.g. PS5, PC)
     * @param genre         genre of the game (e.g. Action, RPG)
     * @param ageRating     recommended age rating (e.g. E, T, M)
     */
    public VideoGame(String productId, String title, double price, int stockQuantity,
                      String platform, String genre, String ageRating) {
        super(productId, title, price, stockQuantity);
        this.platform = platform;
        this.genre = genre;
        this.ageRating = ageRating;
    }

    /**
     * Returns the platform the game was developed for.
     *
     * @return the platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Sets the platform the game was developed for.
     *
     * @param platform the new platform
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * Returns the genre of the game.
     *
     * @return the genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Sets the genre of the game.
     *
     * @param genre the new genre
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Returns the recommended age rating of the game.
     *
     * @return the age rating
     */
    public String getAgeRating() {
        return ageRating;
    }

    /**
     * Sets the recommended age rating of the game.
     *
     * @param ageRating the new age rating
     */
    public void setAgeRating(String ageRating) {
        this.ageRating = ageRating;
    }

    /**
     * Builds a full description of the video game combining the common
     * product attributes with its platform, genre and age rating.
     *
     * @return the full description of the video game
     */
    @Override
    public String getFullDescription() {
        return String.format(
                "[Video Game] %s | Platform: %s | Genre: %s | Age Rating: %s | Price: $%.2f | Stock: %d",
                getTitle(), platform, genre, ageRating, getPrice(), getStockQuantity());
    }
}