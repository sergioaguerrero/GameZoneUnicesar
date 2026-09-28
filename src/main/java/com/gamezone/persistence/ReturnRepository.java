package com.gamezone.persistence;

import com.gamezone.model.*;
import com.gamezone.service.SaleService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReturnRepository {
    private static final String FILE_PATH = "data/returns.csv";

    private final SaleService saleService;

    public ReturnRepository(SaleService saleService) {
        this.saleService = saleService;
    }

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

    private String toCsvLine(Return returnn) {
        return String.join(",",
                returnn.getReturnId(),
                String.valueOf(returnn.getReturnDate()),
                returnn.getReason(),
                returnn.getOriginalSale().getId()
                );
    }

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
                    products.add(items.getProduct());
                }
                break;
            }
        }

        if (targetSale != null) {
            return new Return(returnId, returnDate, targetSale, products, reason);
        }

        return null;
    }

}