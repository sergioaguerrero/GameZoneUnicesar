package com.gamezone.persistence;

import com.gamezone.model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles file-based persistence for Sale objects using a CSV format.
 */
public class SaleRepository {
    private static final String saleCSV = "data/sales.csv";

    /**
     * Serializes a Sale object into a CSV formatted text line with the format
     * {@code id,date,customerId,sellerId,items,extendedWarrantyCost}.
     *
     * @param sale the sale to serialize
     * @return the serialized string representing the sale
     */
    public String saleLine(Sale sale){
        StringBuilder sb = new StringBuilder();

        sb.append(sale.getId()).append(",");
        sb.append(sale.getDate().toString()).append(",");
        sb.append(sale.getCustomer().getId()).append(",");
        sb.append(sale.getSeller().getId()).append(",");

        List<String> itemsList = new ArrayList<>();
        for (SaleItem item : sale.getItems()){
            itemsList.add(item.getProduct().getProductId() + ":" + item.getQuantity());
        }
        sb.append(String.join(";", itemsList)).append(",");
        sb.append(sale.getExtendedWarrantyCost());

        return sb.toString();
    }

    /**
     * Saves a list of sales into the CSV file.
     *
     * @param sales the list of sales to be saved
     */
    public void saveSales(List<Sale> sales){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saleCSV))){
            for (Sale sale : sales){
                writer.write(saleLine(sale));
                writer.newLine();
            }
            System.out.println("Sales saved successfully in " + saleCSV);
        } catch (IOException e) {
            System.err.println("Error saving sales  in csv file." + e.getMessage());
        }
    }

    /**
     * Loads the sales from the CSV file and reconstructs the objects.
     *
     * The current format is {@code id,date,customerId,sellerId,items,extendedWarrantyCost}.
     * Legacy lines written before sales had an identifier
     * ({@code date,customerId,sellerId,items}) are still supported: they
     * receive a deterministic id based on their line number
     * ({@code SALE-0001}, {@code SALE-0002}, ...), which is persisted the next
     * time the sales are saved. If the file does not exist, an empty list is
     * returned.
     *
     * @param allCustomers the master list of customers
     * @param allSellers   the master list of sellers
     * @param allProducts  the master list of products
     * @return a list of reconstructed Sale objects
     */
    public List<Sale> loadSales(List<Customer> allCustomers, List<Seller> allSellers, List<Product> allProducts) {
        List<Sale> sales = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(saleCSV))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                String[] splits = line.split(",", -1);

                boolean legacy = splits[0].matches("\\d{4}-\\d{2}-\\d{2}");
                if (legacy ? splits.length < 4 : splits.length < 5) {
                    continue;
                }

                int offset = legacy ? 0 : 1;
                String id = legacy ? String.format("SALE-%04d", lineNumber) : splits[0];
                LocalDate date = LocalDate.parse(splits[offset]);
                String customerId = splits[offset + 1];
                String sellerId = splits[offset + 2];
                String itemsData = splits[offset + 3];
                double warrantyCost = 0.0;
                if (!legacy && splits.length > 5 && !splits[5].isBlank()) {
                    warrantyCost = Double.parseDouble(splits[5]);
                }

                Customer customer = findCustomerById(allCustomers, customerId);
                Seller seller = findSellerById(allSellers, sellerId);

                if (customer != null && seller != null) {
                    Sale sale = new Sale(id, date, customer, seller);
                    sale.setExtendedWarrantyCost(warrantyCost);

                    String[] itemsArray = itemsData.split(";");
                    for (String itemStr : itemsArray) {
                        String[] itemParts = itemStr.split(":");
                        String productId = itemParts[0];
                        int quantity = Integer.parseInt(itemParts[1]);

                        Product product = findProductById(allProducts, productId);
                        if (product != null) {
                            sale.addItem(new SaleItem(product, quantity));
                        }
                    }
                    sales.add(sale);
                }
            }
            System.out.println("Sales successfully loaded from " + saleCSV);
        } catch (IOException e) {
            System.err.println("The file could not be read: " + e.getMessage());
        }

        return sales;
    }

    /**
     * Finds a customer by their ID within a given list.
     *
     * @param customers the list of customers to search
     * @param id        the ID of the customer
     * @return the Customer object if found, or null otherwise
     */
    private Customer findCustomerById(List<Customer> customers, String id) {
        for (Customer c : customers) {
            if (c.getId().equals(id)) return c;
        }
        return null;
    }

    /**
     * Finds a seller by their ID within a given list.
     *
     * @param sellers the list of sellers to search
     * @param id      the ID of the seller
     * @return the Seller object if found, or null otherwise
     */
    private Seller findSellerById(List<Seller> sellers, String id) {
        for (Seller s : sellers) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }

    /**
     * Finds a product by its ID within a given list.
     *
     * @param products the list of products to search
     * @param id       the ID of the product
     * @return the Product object if found, or null otherwise
     */
    private Product findProductById(List<Product> products, String id) {
        for (Product p : products) {
            if (p.getProductId().equals(id)) return p;
        }
        return null;
    }
}