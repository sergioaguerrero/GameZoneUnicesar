package com.gamezone.persistence;

import com.gamezone.model.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles file-based persistence of {@link Promotion} instances as CSV records,
 * using a discriminator column to distinguish between bulk purchase discounts,
 * category discounts, and flat percentage discounts.
 * Contains no business rules; those belong to the service layer.
 */
public class PromotionRepository {
    private static final String FILE_PATH= "data/promotions.csv";
    private static final String BULK_TYPE = "BULK";
    private static final String CATEGORY_TYPE = "CATEGORY";
    private static final String PERCENTAGE_TYPE = "PERCENTAGE";

    /**
     * Overwrites the CSV file with the given list of promotions.
     *
     * @param promotions the complete list of promotions to persist
     */
    public void saveAll(List<Promotion> promotions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Promotion p : promotions) {
                writer.write(toCsvLine(p));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save promotions to " + FILE_PATH, e);
        }
    }

    /**
     * Reads the CSV file and reconstructs the list of promotions, choosing the
     * concrete subclass based on the discriminator column.
     *
     * @return the list of promotions found in the file, or an empty list if the
     * file does not exist
     */
    public List<Promotion> loadAll() {
        List<Promotion> promotions = new ArrayList<>();
        Path path = Path.of(FILE_PATH);
        if (!Files.exists(path)) {
            return promotions;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                promotions.add(fromCsvLine(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load promotions from " + FILE_PATH, e);
        }
        return promotions;
    }

    /**
     * Converts a single promotion into its CSV line representation, using a
     * discriminator column ("BULK", "CATEGORY", or "PERCENTAGE") followed by the
     * promotion's attributes.
     *
     * @param promotion the promotion to convert
     * @return the CSV line representing the promotion
     * @throws IllegalArgumentException if the promotion's concrete type is not
     * supported
     */
    private String toCsvLine(Promotion promotion) {
        if (promotion instanceof BulkPurchaseDiscount bulk) {
            return String.join(",",
                    BULK_TYPE,
                    bulk.getId(),
                    bulk.getName(),
                    String.valueOf(bulk.getStartDate()),
                    String.valueOf(bulk.getEndDate()),
                    String.valueOf(bulk.getMinimumQuantity()),
                    String.valueOf(bulk.getPercentage())
            );
        }
        if (promotion instanceof CategoryDiscount category) {
            return String.join(",",
                    CATEGORY_TYPE,
                    category.getId(),
                    category.getName(),
                    String.valueOf(category.getStartDate()),
                    String.valueOf(category.getEndDate()),
                    String.valueOf(category.getPercentage()),
                    category.getTargetCategory()
            );
        }
        if (promotion instanceof PercentageDiscount percentage){
            return String.join(",",
                    PERCENTAGE_TYPE,
                    percentage.getId(),
                    percentage.getName(),
                    String.valueOf(percentage.getStartDate()),
                    String.valueOf(percentage.getEndDate()),
                    String.valueOf(percentage.getPercentage())
            );
        }
        throw new IllegalArgumentException("Unsupported promotion type: " + promotion.getClass());
    }

    /**
     * Parses a single CSV line and reconstructs the corresponding concrete
     * {@link Promotion} subclass based on its discriminator column.
     *
     * @param line the CSV line to parse
     * @return the reconstructed promotion
     * @throws IllegalArgumentException if the discriminator column does not
     * match a known promotion type
     */
    private Promotion fromCsvLine(String line) {
        String[] fields = line.split(",", -1);
        String type = fields[0];
        String id = fields[1];
        String name = fields[2];
        LocalDate startDate = LocalDate.parse(fields[3]);
        LocalDate endDate = LocalDate.parse(fields[4]);

        if (BULK_TYPE.equals(type)) {
            return new BulkPurchaseDiscount(id, name, startDate,endDate,
                    Integer.parseInt(fields[5]),Double.parseDouble(fields[6]));
        }
        if (CATEGORY_TYPE.equals(type)) {
            return new CategoryDiscount(id, name, startDate, endDate,
                    Double.parseDouble(fields[5]), fields[6]);
        }
        if (PERCENTAGE_TYPE.equals(type)){
            return new PercentageDiscount(id, name, startDate, endDate,
                    Double.parseDouble(fields[5]));
        }
        throw new IllegalArgumentException("Unknown promotion type in CSV: " + type);
    }
}