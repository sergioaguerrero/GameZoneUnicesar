package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Product;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles file-based persistence of {@link Product} instances as CSV records,
 * using a discriminator column to distinguish between video games and consoles.
 * Contains no business rules; those belong to the service layer.
 */
public class ProductRepository {

    private static final String FILE_PATH = "data/products.csv";

    private final Map<String, ProductCsvMapper> mappers = new LinkedHashMap<>();

    /**
     * Creates the repository and registers one CSV mapper per supported product
     * type.
     */
    public ProductRepository() {
        register(new VideoGameCsvMapper());
        register(new ConsoleCsvMapper());
    }

    /**
     * Registers a mapper under its discriminator.
     *
     * @param mapper the mapper to register
     */
    private void register(ProductCsvMapper mapper) {
        mappers.put(mapper.getType(), mapper);
    }

    /**
     * Overwrites the CSV file with the given list of products.
     *
     * @param products the complete list of products to persist
     * @throws PersistenceException if the file cannot be written
     */
    public void saveAll(List<Product> products) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Product product : products) {
                writer.write(toCsvLine(product));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException(
                    "Error de escritura: no se pudieron guardar los productos en " + FILE_PATH, e);
        }
    }

    /**
     * Reads the CSV file and reconstructs the list of products, choosing the
     * concrete subclass based on the discriminator column.
     *
     * @return the list of products found in the file, or an empty list if the
     * file does not exist
     * @throws PersistenceException if the file exists but cannot be read
     */
    public List<Product> loadAll() {
        List<Product> products = new ArrayList<>();
        Path path = Path.of(FILE_PATH);
        if (!Files.exists(path)) {
            return products;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                products.add(fromCsvLine(line));
            }
        } catch (IOException e) {
            throw new PersistenceException(
                    "Error de lectura: no se pudieron cargar los productos desde " + FILE_PATH, e);
        }
        return products;
    }

    /**
     * Converts a single product into its CSV line representation: the
     * discriminator, the common columns and the columns provided by the mapper
     * of the product's category.
     *
     * @param product the product to convert
     * @return the CSV line representing the product
     * @throws IllegalArgumentException if no mapper is registered for the
     * product's category
     */
    private String toCsvLine(Product product) {
        ProductCsvMapper mapper = mappers.get(product.getCategory());
        if (mapper == null) {
            throw new IllegalArgumentException("Unsupported product type: " + product.getClass());
        }
        List<String> columns = new ArrayList<>(List.of(
                mapper.getType(),
                product.getProductId(),
                product.getTitle(),
                String.valueOf(product.getPrice()),
                String.valueOf(product.getStockQuantity())));
        columns.addAll(List.of(mapper.toSpecificFields(product)));
        return String.join(",", columns);
    }

    /**
     * Parses a single CSV line and reconstructs the corresponding concrete
     * {@link Product} through the mapper registered for its discriminator.
     *
     * @param line the CSV line to parse
     * @return the reconstructed product
     * @throws IllegalArgumentException if the discriminator column does not
     * match a known product type
     */
    private Product fromCsvLine(String line) {
        String[] fields = line.split(",", -1);
        String type = fields[0];
        String productId = fields[1];
        String title = fields[2];
        double price = Double.parseDouble(fields[3]);
        int stockQuantity = Integer.parseInt(fields[4]);

        ProductCsvMapper mapper = mappers.get(type);
        if (mapper == null) {
            throw new IllegalArgumentException("Unknown product type in CSV: " + type);
        }
        return mapper.fromCsvFields(productId, title, price, stockQuantity, fields);
    }
}
