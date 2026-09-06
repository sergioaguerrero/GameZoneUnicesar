package com.gamezone.persistence;

import com.gamezone.model.Sale;
import com.gamezone.model.SaleItem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

}
