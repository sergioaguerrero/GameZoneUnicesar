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
     * Serializes a Sale object into a CSV formatted text line.
     *
     * @param sale the sale to serialize
     * @return the serialized string representing the sale
     */
    public String saleLine(Sale sale){
        StringBuilder sb = new StringBuilder();

        sb.append(sale.getDate().toString()).append(",");
        sb.append(sale.getCustomer().getId()).append(",");
        sb.append(sale.getSeller().getId()).append(",");

        List<String> itemsList = new ArrayList<>();
        for (SaleItem item : sale.getItems()){
            itemsList.add(item.getProduct().getProductId() + ":" + item.getQuantity());
        }
        sb.append(String.join(";", itemsList));

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
     * @param allCustomers the master list of customers
     * @param allSellers   the master list of sellers
     * @param allProducts  the master list of products
     * @return a list of reconstructed Sale objects
     */
    public List<Sale> loadSales(List<Customer> allCustomers, List<Seller> allSellers, List<Product> allProducts) {
        List<Sale> sales = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(saleCSV))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] splits = line.split(",");

                if (splits.length >= 4) {
                    LocalDate date = LocalDate.parse(splits[0]);
                    String customerId = splits[1];
                    String sellerId = splits[2];
                    String itemsData = splits[3];

                    Customer customer = findCustomerById(allCustomers, customerId);
                    Seller seller = findSellerById(allSellers, sellerId);

                    if (customer != null && seller != null) {
                        Sale sale = new Sale(date, customer, seller);

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