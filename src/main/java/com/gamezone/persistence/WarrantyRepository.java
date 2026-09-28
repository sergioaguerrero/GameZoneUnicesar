package com.gamezone.persistence;

import com.gamezone.model.*;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class WarrantyRepository {
    private static final String FILE_PATH= "data/warranties.csv";
    private static final String EXTENDED_TYPE = "EXTENDED";
    private static final String BASIC_TYPE = "BASIC";
    private ProductService productService;
    private SaleService saleService;

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

    private Warranty fromCsvLine(String line) {
        String[] fields = line.split(",", -1);
        String type = fields[0];
        String id = fields[1];
        String productId = fields[2];
        String saleId = fields[3];
        LocalDate startDate = LocalDate.parse(fields[4]);

        Product product=null;
        Sale sale=null;

        for (Product p:productService.listAllProducts()){
            if (p.getProductId().equals(productId)){
                product=p;
            }
        }
        for (Sale s:saleService.listAllSales()){
            if (s.getId().equals(saleId)){
                sale=s;
            }
        }

        if (EXTENDED_TYPE.equals(type)) {
            return new ExtendedWarranty(id,product,sale,startDate);
        }
        if (BASIC_TYPE.equals(type)) {
            return new BasicWarranty(id,product,sale,startDate);
        }
        throw new IllegalArgumentException("Unknown warranty type in CSV: " + type);
    }

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
