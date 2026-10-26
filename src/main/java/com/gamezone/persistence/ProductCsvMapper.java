package com.gamezone.persistence;

import com.gamezone.model.Product;

/**
 * Converts one concrete product type to and from the type-specific columns of a
 * line in {@code data/products.csv}. {@link ProductRepository} keeps one mapper
 * per product category, so adding a product type only requires a new mapper
 * instead of new conditionals in the repository.
 */
public interface ProductCsvMapper {

    /**
     * Returns the discriminator written in the first CSV column for this
     * product type. It matches {@link Product#getCategory()}.
     *
     * @return the discriminator of the product type
     */
    String getType();

    /**
     * Returns the columns specific to this product type, in the order they are
     * written after the common columns.
     *
     * @param product the product to convert; its category matches
     * {@link #getType()}
     * @return the type-specific column values
     */
    String[] toSpecificFields(Product product);

    /**
     * Builds the product from the common values and the full list of CSV
     * fields, reading the type-specific columns from index 5.
     *
     * @param productId the product identifier
     * @param title the product title
     * @param price the product price
     * @param stockQuantity the stock quantity
     * @param fields all the fields of the CSV line
     * @return the reconstructed product
     */
    Product fromCsvFields(String productId, String title, double price, int stockQuantity,
            String[] fields);
}
