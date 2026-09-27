package com.gamezone.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a product return commercialized by GameZone Unicesar.
 *
 * A return references an existing {@link Sale} and includes one or more
 * products from that sale that the customer gives back. It is not necessarily a
 * full return of the original sale: the customer may keep some of the purchased
 * products.
 *
 * The relationship with the original sale is a plain reference and is immutable
 * once the return is created, so there is no setter for it.
 */
public class Return {

    private String returnId;
    private LocalDate returnDate;
    private Sale originalSale;
    private List<Product> returnedProducts;
    private String reason;
    private double refundAmount;

    /**
     * Creates a new return.
     *
     * @param returnId unique identifier of the return
     * @param returnDate date on which the return is registered
     * @param originalSale sale the returned products belong to
     * @param returnedProducts products being returned, at least one
     * @param reason reason given for the return
     * @throws IllegalArgumentException if the identifier or the reason are null
     * or blank, if the date or the original sale are null, or if the list of
     * returned products is null or empty
     */
    public Return(String returnId, LocalDate returnDate, Sale originalSale,
            List<Product> returnedProducts, String reason) {
        if (returnId == null || returnId.isBlank()) {
            throw new IllegalArgumentException("Return id must not be null or blank");
        }
        if (returnDate == null) {
            throw new IllegalArgumentException("Return date must not be null");
        }
        if (originalSale == null) {
            throw new IllegalArgumentException("Original sale must not be null");
        }
        if (returnedProducts == null || returnedProducts.isEmpty()) {
            throw new IllegalArgumentException("Returned products must not be null or empty");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Return reason must not be null or blank");
        }
        this.returnId = returnId.trim();
        this.returnDate = returnDate;
        this.originalSale = originalSale;
        this.returnedProducts = new ArrayList<>(returnedProducts);
        this.reason = reason.trim();
        this.refundAmount = 0.0;
    }

    /**
     * Returns the unique identifier of the return.
     *
     * @return the return id
     */
    public String getReturnId() {
        return returnId;
    }

    /**
     * Returns the date on which the return was registered.
     *
     * @return the return date
     */
    public LocalDate getReturnDate() {
        return returnDate;
    }

    /**
     * Returns the original sale this return references.
     *
     * @return the original sale
     */
    public Sale getOriginalSale() {
        return originalSale;
    }

    /**
     * Returns the products being returned.
     *
     * The returned list is read-only, since the set of returned products does
     * not change after the return is created.
     *
     * @return the returned products
     */
    public List<Product> getReturnedProducts() {
        return Collections.unmodifiableList(returnedProducts);
    }

    /**
     * Returns the reason given for the return.
     *
     * @return the return reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Returns the amount refunded to the customer.
     *
     * This value is zero until {@link #calculateRefundAmount()} is invoked.
     *
     * @return the refund amount
     */
    public double getRefundAmount() {
        return refundAmount;
    }

    /**
     * Calculates the refund amount as the sum of the prices of the returned
     * products, and stores it in this return.
     *
     * @return the calculated refund amount
     */
    public double calculateRefundAmount() {
        double total = 0.0;
        for (Product product : returnedProducts) {
            total += product.getPrice();
        }
        this.refundAmount = total;
        return this.refundAmount;
    }

    /**
     * Builds a formatted receipt, in Spanish, with the detail of this return:
     * identifier, date, reference to the original sale, returned products with
     * their prices, reason and refunded amount.
     *
     * @return the return receipt
     */
    public String generateReturnReceipt() {
        StringBuilder receipt = new StringBuilder();
        receipt.append("=== Recibo de Devolución ===\n");
        receipt.append("Identificador: ").append(returnId).append("\n");
        receipt.append("Fecha: ").append(returnDate).append("\n");
        receipt.append("Venta original: ").append(originalSale.getDate()).append("\n");
        receipt.append("Productos devueltos:\n");
        for (Product product : returnedProducts) {
            receipt.append(String.format("  - %s | $%.2f%n", product.getTitle(), product.getPrice()));
        }
        receipt.append("Motivo: ").append(reason).append("\n");
        receipt.append(String.format("Monto reembolsado: $%.2f%n", refundAmount));
        return receipt.toString();
    }
}
