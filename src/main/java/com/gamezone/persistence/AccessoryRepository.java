package com.gamezone.persistence;

import com.gamezone.model.*;

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
 * Handles file-based persistence of {@link Accessory} instances as CSV records,
 * using a discriminator column to distinguish between cables, controllers, and
 * memory accessories.
 * Contains no business rules; those belong to the service layer.
 */
public class AccessoryRepository {
    private static final String FILE_PATH = "dat/accesories.csv";
    private static final String CABLE_TYPE = "CABLE";
    private static final String CONTROLLER_TYPE = "CONTROLLER";
    private static final String MEMORY_TYPE = "MEMORY";

    /**
     * Overwrites the CSV file with the given list of accessories.
     *
     * @param accessories the complete list of accessories to persist
     */
    public void saveAll(List<Accessory> accessories) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Accessory a: accessories) {
                writer.write(toCsvLine(a));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save accessories to " + FILE_PATH, e);
        }
    }

    /**
     * Reads the CSV file and reconstructs the list of accessories, choosing the
     * concrete subclass based on the discriminator column.
     *
     * @return the list of accessories found in the file, or an empty list if the
     * file does not exist
     */
    public List<Accessory> loadAll() {
        List<Accessory> accessories = new ArrayList<>();
        Path path = Path.of(FILE_PATH);
        if (!Files.exists(path)) {
            return accessories;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                accessories.add(fromCsvLine(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load accessories from " + FILE_PATH, e);
        }
        return accessories;
    }

    /**
     * Converts a single accessory into its CSV line representation, using a
     * discriminator column ("CABLE", "CONTROLLER", or "MEMORY") followed by the
     * accessory's attributes.
     *
     * @param a the accessory to convert
     * @return the CSV line representing the accessory
     * @throws IllegalArgumentException if the accessory's concrete type is not
     * supported
     */
    private String toCsvLine(Accessory a) {
        if (a instanceof Cable cable) {
            return String.join(",",
                    CABLE_TYPE,
                    cable.getProductId(),
                    cable.getTitle(),
                    String.valueOf(cable.getPrice()),
                    String.valueOf(cable.getStockQuantity()),
                    String.valueOf(cable.getLength()),
                    cable.getConnectorType()
            );
        }
        if (a instanceof Controller controller) {
            return String.join(",",
                    CONTROLLER_TYPE,
                    controller.getProductId(),
                    controller.getTitle(),
                    String.valueOf(controller.getPrice()),
                    String.valueOf(controller.getStockQuantity()),
                    controller.getConnectionType()
            );
        }
        if (a instanceof Memory memory){
            return String.join(
                    ",",
                    MEMORY_TYPE,
                    memory.getProductId(),
                    memory.getTitle(),
                    String.valueOf(memory.getPrice()),
                    String.valueOf(memory.getStockQuantity()),
                    String.valueOf(memory.getCapacityGB()),
                    memory.getMemoryType()
            );
        }
        throw new IllegalArgumentException("Unsupported product type: " + a.getClass());
    }

    /**
     * Parses a single CSV line and reconstructs the corresponding concrete
     * {@link Accessory} subclass based on its discriminator column.
     *
     * @param line the CSV line to parse
     * @return the reconstructed accessory
     * @throws IllegalArgumentException if the discriminator column does not
     * match a known accessory type
     */
    private Accessory fromCsvLine(String line) {
        String[] fields = line.split(",", -1);
        String type = fields[0];
        String productId = fields[1];
        String title = fields[2];
        double price = Double.parseDouble(fields[3]);
        int stockQuantity = Integer.parseInt(fields[4]);

        if (CABLE_TYPE.equals(type)) {
            return new Cable(productId, title, price, stockQuantity,
                    Double.parseDouble(fields[5]), fields[6]);
        }
        if (CONTROLLER_TYPE.equals(type)) {
            return new Controller(productId, title, price, stockQuantity,
                    fields[5]);
        }
        if (MEMORY_TYPE.equals(type)){
            return new Memory(productId,title,price,stockQuantity,
                    Integer.parseInt(fields[5]),fields[6]);
        }
        throw new IllegalArgumentException("Unknown product type in CSV: " + type);
    }
}

