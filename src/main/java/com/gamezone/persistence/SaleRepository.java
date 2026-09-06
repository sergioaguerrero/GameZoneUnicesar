package com.gamezone.persistence;

import com.gamezone.model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SaleRepository {
    private static final String saleCSV = "data/sales.csv";

    // This method serializes the sale into a text line.
    public String saleLine(Sale sale){
        StringBuilder sb = new StringBuilder();

        // To save date with ID Costumer and ID Seller
        sb.append(sale.getDate().toString()).append(",");
        sb.append(sale.getCustomer().getId()).append(",");
        sb.append(sale.getSeller().getId()).append(",");

        // To serialize with format (idProduct:quantity;)
        List<String> itemsList = new ArrayList<>();
        for (SaleItem item : sale.getItems()){
            itemsList.add(item.getProduct().getProductId() + ":" + item.getQuantity());
        }
        sb.append(String.join(";", itemsList));

        return sb.toString();
    }

    // To save the sales into the csv

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

                    // To search objects by id
                    Customer customer = findCustomerById(allCustomers, customerId);
                    Seller seller = findSellerById(allSellers, sellerId);

                    if (customer != null && seller != null) {
                        Sale sale = new Sale(date, customer, seller);

                        // To load item list separating by ";"
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

    // Another aux methods to loadSales

    private Customer findCustomerById(List<Customer> customers, String id) {
        for (Customer c : customers) {
            if (c.getId().equals(id)) return c;
        }
        return null;
    }

    private Seller findSellerById(List<Seller> sellers, String id) {
        for (Seller s : sellers) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }

    private Product findProductById(List<Product> products, String id) {
        for (Product p : products) {
            if (p.getProductId().equals(id)) return p;
        }
        return null;
    }
}
