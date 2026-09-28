package com.gamezone.persistence;

import com.gamezone.model.*;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository responsible for managing warranty data persistence using a CSV file.
 * Handles saving and mapping operations between Warranty objects and CSV lines.
 *
 * @version 1.0
 * @since 2026-09-28
 */
public class WarrantyRepository {
    private static final String FILE_PATH = "data/warranties.csv";
    private static final String EXTENDED_TYPE = "EXTENDED";
    private static final String BASIC_TYPE = "BASIC";
    private final ProductService productService;
    private final SaleService saleService;

    /**
     * Creates the repository with the services needed to resolve references.
     *
     * @param productService service used to resolve products while loading
     * @param saleService    service used to resolve sales while loading
     */
    public WarrantyRepository(ProductService productService, SaleService saleService) {
        this.productService = productService;
        this.saleService = saleService;
    }

    /**
     * Loads all warranties from the CSV file.
     *
     * @return the loaded warranties; empty list if the file does not exist
     */
    public List<Warranty> loadAll() {
        List<Warranty> result = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return result;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                Warranty w = fromCsvLine(line);
                if (w != null) {
                    result.add(w);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load warranties from " + FILE_PATH, e);
        }
        return result;
    }

    /**
     * Saves a list of warranties to the CSV file.
     *
     * @param warranties the list of Warranty objects to be saved
     * @throws RuntimeException if an I/O error occurs while writing to the file
     */
    public void saveAll(List<Warranty> warranties) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Warranty w : warranties) {
                writer.write(toCsvLine(w));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save warranties to " + FILE_PATH, e);
        }
    }

    /**
     * Converts a CSV line into a corresponding Warranty object (Extended or Basic).
     *
     * @param line the CSV formatted string representing a warranty
     * @return the constructed Warranty object
     * @throws IllegalArgumentException if the warranty type in the CSV is unknown
     */
    private Warranty fromCsvLine(String line) {
        String[] fields = line.split(",", -1);
        String type = fields[0];
        String id = fields[1];
        String productId = fields[2];
        String saleId = fields[3];
        LocalDate startDate = LocalDate.parse(fields[4]);

        Product product = null;
        Sale sale = null;

        for (Product p : productService.listAllProducts()) {
            if (p.getProductId().equals(productId)) {
                product = p;
            }
        }
        for (Sale s : saleService.listAllSales()) {
            if (s.getId().equals(saleId)) {
                sale = s;
            }
        }

        if (product == null || sale == null) {
            return null;
        }
        if (EXTENDED_TYPE.equals(type)) {
            return new ExtendedWarranty(id, product, sale, startDate);
        }
        if (BASIC_TYPE.equals(type)) {
            return new BasicWarranty(id, product, sale, startDate);
        }
        throw new IllegalArgumentException("Unknown warranty type in CSV: " + type);
    }

    /**
     * Converts a Warranty object into its corresponding CSV line format.
     *
     * @param warranty the Warranty object to convert
     * @return a comma-separated string representing the warranty data
     * @throws IllegalArgumentException if the warranty type/class is unsupported
     */
    private String toCsvLine(Warranty warranty) {
        if (warranty instanceof ExtendedWarranty extended) {
            return String.join(",",
                    EXTENDED_TYPE,
                    extended.getWarrantyId(),
                    String.valueOf(extended.getProduct().getProductId()),
                    String.valueOf(extended.getSale().getId()),
                    String.valueOf(extended.getStartDate())
            );
        }
        if (warranty instanceof BasicWarranty basic) {
            return String.join(",",
                    BASIC_TYPE,
                    basic.getWarrantyId(),
                    String.valueOf(basic.getProduct().getProductId()),
                    String.valueOf(basic.getSale().getId()),
                    String.valueOf(basic.getStartDate())
            );
        }
        throw new IllegalArgumentException("Unsupported promotion type: " + warranty.getClass());
    }
}