package com.gamezone.persistence;

import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;

/**
 * CSV mapper for {@link VideoGame}: platform, genre and age rating.
 */
public class VideoGameCsvMapper implements ProductCsvMapper {

    /**
     * Returns the discriminator of video games.
     *
     * @return {@link Product#CATEGORY_VIDEOGAME}
     */
    @Override
    public String getType() {
        return Product.CATEGORY_VIDEOGAME;
    }

    /**
     * Returns platform, genre and age rating.
     *
     * @param product the video game to convert
     * @return the video game specific column values
     */
    @Override
    public String[] toSpecificFields(Product product) {
        VideoGame videoGame = (VideoGame) product;
        return new String[]{
            videoGame.getPlatform(),
            videoGame.getGenre(),
            videoGame.getAgeRating()
        };
    }

    /**
     * Builds a video game reading platform, genre and age rating from columns
     * 5, 6 and 7.
     *
     * @param productId the product identifier
     * @param title the product title
     * @param price the product price
     * @param stockQuantity the stock quantity
     * @param fields all the fields of the CSV line
     * @return the reconstructed video game
     */
    @Override
    public Product fromCsvFields(String productId, String title, double price, int stockQuantity,
            String[] fields) {
        return new VideoGame(productId, title, price, stockQuantity,
                fields[5], fields[6], fields[7]);
    }
}
