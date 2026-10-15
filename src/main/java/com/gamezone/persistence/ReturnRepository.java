package com.gamezone.persistence;

import com.gamezone.model.*;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.SaleService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles file-based persistence for Return objects using a CSV format[cite: 12].
 */
public class ReturnRepository {
    private static final String FILE_PATH = "data/returns.csv";

    private final SaleService saleService;
    private final AccessoryService accessoryService;

    /**
     * Initializes the ReturnRepository with the services required to resolve
     * sale and accessory references while loading[cite: 12].
     *
     * @param saleService the service handling and listing sales
     * @param accessoryService the service managing accessories, used to resolve
     * accessory references on load
     */
    public ReturnRepository(SaleService saleService, AccessoryService accessoryService) {
        this.saleService = saleService;
        this.accessoryService = accessoryService;
    }

    /**
     * Saves a list of returns into the CSV file[cite: 12].
     *
     * @param returns the list of returns to be saved
     * @throws RuntimeException if an I/O error occurs while writing to the file
     */
    public void saveAll(List<Return> returns) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Return r : returns) {
                writer.write(toCsvLine(r));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save promotions to " + FILE_PATH, e);
        }
    }

    /**
     * Serializes a Return object into a CSV formatted text line[cite: 12].
     *
     * @param returnn the return to serialize
     * @return the serialized string representing the return
     */
    private String toCsvLine(Return returnn) {
        return String.join(",",
                returnn.getReturnId(),
                String.valueOf(returnn.getReturnDate()),
                returnn.getReason(),
                returnn.getOriginalSale().getId()
        );
    }

    /**
     * Loads a return from a CSV line, resolves the original sale, and reconstructs the object.
     * Accessories found in the sale are resolved against {@link AccessoryService}, so the
     * return references the accessory instances managed by the inventory[cite: 12].
     *
     * @param line the text line stored in the CSV file
     * @return a reconstructed Return instance, or null if the original sale is not found
     */
    public Return fromCsvLine(String line) {
        String[] fields = line.split(",", -1);
        String returnId = fields[0];
        LocalDate returnDate = LocalDate.parse(fields[1]);
        String reason = fields[2];
        String saleId = fields[3];

        List<Sale> sales = saleService.listAllSales();

        Sale targetSale = null;
        List<Product> products = new ArrayList<>();
        for (Sale sale : sales) {
            if (saleId.equals(sale.getId())) {
                targetSale = sale;
                for (SaleItem items : sale.getItems()){
                    products.add(resolveProduct(items.getProduct()));
                }
                break;
            }
        }

        if (targetSale != null) {
            return new Return(returnId, returnDate, targetSale, products, reason);
        }

        return null;
    }

    /**
     * Resolves a product found in a sale item. Accessories are replaced by the
     * instance managed by {@link AccessoryService}; any other product, or an
     * accessory that is no longer registered, is returned unchanged.
     *
     * @param product the product taken from a sale item
     * @return the resolved product
     */
    private Product resolveProduct(Product product) {
        if (product instanceof Accessory) {
            Optional<Accessory> managed = accessoryService.findById(product.getProductId());
            if (managed.isPresent()) {
                return managed.get();
            }
        }
        return product;
    }

    /**
     * Loads all returns from the CSV file.
     *
     * @return a list containing all the persisted returns
     */
    public List<Return> loadAll() {
        List<Return> returns = new ArrayList<>();
        java.io.File file = new java.io.File(FILE_PATH);
        if (!file.exists()) {
            return returns;
        }

        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Return ret = fromCsvLine(line);
                if (ret != null) {
                    returns.add(ret);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load returns from " + FILE_PATH + ": " + e.getMessage());
        }
        return returns;
    }

}