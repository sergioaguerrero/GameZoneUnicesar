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

/**
 * Manages the business logic related to product returns, validation, stock restoration,
 * and financial balance calculations[cite: 23].
 */
public class ReturnService {
    private final ReturnRepository returnRepository;
    private final SaleService saleService;
    private final ProductService productService;
    private List<Return> returns;

    /**
     * Initializes the ReturnService with the required repository, services, and initial returns list[cite: 23].
     *
     * @param returnRepository the repository handling return persistence
     * @param saleService the service handling sales
     * @param productService the service handling products and inventory
     * @param returns the initial in-memory list of returns
     */
    public ReturnService(ReturnRepository returnRepository, SaleService saleService, ProductService productService, List<Return> returns) {
        this.returnRepository = returnRepository;
        this.saleService = saleService;
        this.productService = productService;
        this.returns = returns != null ? returns : new ArrayList<>();
    }

    /**
     * Registers a new product return by validating the sale existence, the 30-day return window,
     * verifying that products belong to the sale, restoring inventory stock, and calculating the refund amount[cite: 23].
     *
     * @param saleId the unique identifier of the original sale
     * @param productIds the list of product IDs being returned
     * @param reason the reason for the return
     * @return the newly created Return instance
     * @throws IllegalArgumentException if any validation fails
     */
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

    /**
     * Returns a list of all registered returns[cite: 23].
     *
     * @return the list containing all returns
     */
    public List<Return> viewAllReturns(){
        return returns;
    }

    /**
     * Filters and returns all returns associated with a specific customer[cite: 23].
     *
     * @param customerId the ID of the customer whose returns are requested
     * @return the list of returns matching the customer ID
     */
    public List<Return> viewReturnsByCustomer(String customerId){
        List<Return> returnByCustomer = new ArrayList<>();
        for (Return r : returns){
            if (r.getOriginalSale().getCustomer().equals(customerId)){
                returnByCustomer.add(r);
            }
        }
        return returnByCustomer;
    }

    /**
     * Filters and returns all returns associated with a specific sale[cite: 23].
     *
     * @param saleId the ID of the sale whose returns are requested
     * @return the list of returns matching the sale ID
     */
    public List<Return> viewReturnsBySale(String saleId){
        List<Return> returnBySale = new ArrayList<>();
        for (Return r : returns){
            if (r.getOriginalSale().getId().equals(saleId)){
                returnBySale.add(r);
            }
        }
        return returnBySale;
    }

    /**
     * Calculates the net financial balance for a specific month and year by subtracting
     * total refunds from total sales in that period[cite: 23].
     *
     * @param month the month to evaluate (1-12)
     * @param year the year to evaluate
     * @return the net balance (total sales minus total refunds)
     */
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