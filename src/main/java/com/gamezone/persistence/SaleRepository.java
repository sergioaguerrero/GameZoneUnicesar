package com.gamezone.persistence;

import com.gamezone.model.Sale;
import com.gamezone.model.SaleItem;

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
}
