package com.gamezone.service;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleItem;
import com.gamezone.persistence.ReturnRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReturnService {
    private final ReturnRepository returnRepository;
    private final SaleService saleService;
    private final ProductService productService;
    private List<Return> returns;

    public ReturnService(ReturnRepository returnRepository, SaleService saleService, ProductService productService, List<Return> returns) {
        this.returnRepository = returnRepository;
        this.saleService = saleService;
        this.productService = productService;
        this.returns = returns != null ? returns : new ArrayList<>();
    }

    public Return registerReturn(String saleId, List<String> productIds, String reason) {
        if (saleId == null || saleId.isBlank()) {
            throw new IllegalArgumentException("El identificador de la venta no puede ser nulo o vacío.");
        }
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Debe indicar al menos un producto para la devolución.");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("El motivo de la devolución es obligatorio.");
        }

        Sale originalSale = null;
        for (Sale s : saleService.listAllSales()) {
            if (s.getId().equals(saleId)) {
                originalSale = s;
                break;
            }
        }

        if (originalSale == null) {
            throw new IllegalArgumentException("La venta original con ID " + saleId + " no existe.");
        }

        if (!originalSale.canBeReturned()) {
            throw new IllegalArgumentException("La venta ha superado el plazo límite de 30 días para devoluciones.");
        }

        List<Product> productsToReturn = new ArrayList<>();
        for (String id : productIds) {
            Product matchedProduct = null;
            int quantityToRestore = 0;

            for (SaleItem item : originalSale.getItems()) {
                if (item.getProduct().getProductId().equals(id)) {
                    matchedProduct = item.getProduct();
                    quantityToRestore = item.getQuantity();
                    break;
                }
            }

            if (matchedProduct == null) {
                throw new IllegalArgumentException("El producto con ID " + id + " no pertenece a la venta original.");
            }

            productsToReturn.add(matchedProduct);

            productService.restoreStock(matchedProduct.getProductId(), quantityToRestore);
        }

        String returnId = "RET-" + UUID.randomUUID().toString().substring(0, 8);
        Return newReturn = new Return(returnId, LocalDate.now(), originalSale, productsToReturn, reason);
        newReturn.calculateRefundAmount();
        returns.add(newReturn);
        System.out.println("Devolución registrada exitosamente.");
        return newReturn;
    }

    public List<Return> viewAllReturns(){
        return returns;
    }

    public List<Return> viewReturnsByCustomer(String customerId){
        List<Return>returnByCustomer = new ArrayList<>();
        for (Return r:returns){
            if (r.getOriginalSale().getCustomer().equals(customerId)){
                returnByCustomer.add(r);
            }
        }
        return returnByCustomer;
    }

    public List<Return> viewReturnsBySale(String saleId){
        List<Return> returnBySale = new ArrayList<>();
        for (Return r:returns){
            if (r.getOriginalSale().getId().equals(saleId)){
                returnBySale.add(r);
            }
        }
        return returnBySale;
    }

    public double generateMonthlyBalance(int month, int year) {
        double totalSales = 0.0;
        double totalReturns = 0.0;

        for (Sale sale : saleService.listAllSales()) {
            LocalDate saleDate = sale.getDate();
            if (saleDate != null && saleDate.getMonthValue() == month && saleDate.getYear() == year) {
                totalSales += sale.calculateTotal();
            }
        }
        for (Return ret : returns) {
            LocalDate returnDate = ret.getReturnDate();
            if (returnDate != null && returnDate.getMonthValue() == month && returnDate.getYear() == year) {
                totalReturns += ret.getRefundAmount();
            }
        }
        return totalSales - totalReturns;
    }
}