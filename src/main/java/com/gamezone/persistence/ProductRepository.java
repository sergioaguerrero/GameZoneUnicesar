package com.gamezone.persistence;

import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles file-based persistence of {@link Product} instances as CSV records,
 * using a discriminator column to distinguish between video games and consoles.
 * Contains no business rules; those belong to the service layer.
 */
public class ProductRepository {

    private static final String FILE_PATH = "data/products.csv";
    private static final String VIDEOGAME_TYPE = "VIDEOGAME";
    private static final String CONSOLE_TYPE = "CONSOLE";

    /**
     * Overwrites the CSV file with the given list of products.
     *
     * @param products the complete list of products to persist
     */
    public void saveAll(List<Product> products) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Product product : products) {
                writer.write(toCsvLine(product));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save products to " + FILE_PATH, e);
        }
    }

    /**
     * Reads the CSV file and reconstructs the list of products, choosing the
     * concrete subclass based on the discriminator column.
     *
     * @return the list of products found in the file, or an empty list if the
     * file does not exist
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
            throw new RuntimeException("Failed to load products from " + FILE_PATH, e);
        }
        return products;
    }

    /**
     * Converts a single product into its CSV line representation, using a
     * discriminator column ("VIDEOGAME" or "CONSOLE") followed by the product's
     * attributes.
     *
     * @param product the product to convert
     * @return the CSV line representing the product
     * @throws IllegalArgumentException if the product's concrete type is not
     * supported
     */
    private String toCsvLine(Product product) {
        if (product instanceof VideoGame videoGame) {
            return String.join(",",
                    VIDEOGAME_TYPE,
                    videoGame.getProductId(),
                    videoGame.getTitle(),
                    String.valueOf(videoGame.getPrice()),
                    String.valueOf(videoGame.getStockQuantity()),
                    videoGame.getPlatform(),
                    videoGame.getGenre(),
                    videoGame.getAgeRating());
        }
        if (product instanceof Console console) {
            return String.join(",",
                    CONSOLE_TYPE,
                    console.getProductId(),
                    console.getTitle(),
                    String.valueOf(console.getPrice()),
                    String.valueOf(console.getStockQuantity()),
                    console.getBrand(),
                    console.getModel(),
                    String.valueOf(console.getGeneration()));
        }
        throw new IllegalArgumentException("Unsupported product type: " + product.getClass());
    }

    /**
     * Parses a single CSV line and reconstructs the corresponding concrete
     * {@link Product} subclass based on its discriminator column.
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

        if (VIDEOGAME_TYPE.equals(type)) {
            return new VideoGame(productId, title, price, stockQuantity,
                    fields[5], fields[6], fields[7]);
        }
        if (CONSOLE_TYPE.equals(type)) {
            return new Console(productId, title, price, stockQuantity,
                    fields[5], fields[6], Integer.parseInt(fields[7]));
        }
        throw new IllegalArgumentException("Unknown product type in CSV: " + type);
    }
}
