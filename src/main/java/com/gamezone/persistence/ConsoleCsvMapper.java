package com.gamezone.persistence;

import com.gamezone.model.Console;
import com.gamezone.model.Product;

/**
 * CSV mapper for {@link Console}: brand, model and generation.
 */
public class ConsoleCsvMapper implements ProductCsvMapper {

    /**
     * Returns the discriminator of consoles.
     *
     * @return {@link Product#CATEGORY_CONSOLE}
     */
    @Override
    public String getType() {
        return Product.CATEGORY_CONSOLE;
    }

    /**
     * Returns brand, model and generation.
     *
     * @param product the console to convert
     * @return the console specific column values
     */
    @Override
    public String[] toSpecificFields(Product product) {
        Console console = (Console) product;
        return new String[]{
            console.getBrand(),
            console.getModel(),
            String.valueOf(console.getGeneration())
        };
    }

    /**
     * Builds a console reading brand, model and generation from columns 5, 6
     * and 7.
     *
     * @param productId the product identifier
     * @param title the product title
     * @param price the product price
     * @param stockQuantity the stock quantity
     * @param fields all the fields of the CSV line
     * @return the reconstructed console
     */
    @Override
    public Product fromCsvFields(String productId, String title, double price, int stockQuantity,
            String[] fields) {
        return new Console(productId, title, price, stockQuantity,
                fields[5], fields[6], Integer.parseInt(fields[7]));
    }
}
