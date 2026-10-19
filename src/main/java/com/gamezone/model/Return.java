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

    private final String returnId;
    private final LocalDate returnDate;
    private final Sale originalSale;
    private final List<Product> returnedProducts;
    private final String reason;
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
     * This value is zero until {@link #calculateRefundAmount()} is invoked.
     *
     * @return the refund amount
     */
    public double getRefundAmount() {
        return refundAmount;
    }

    /**
     * Calculates the refund amount as the sum of the proportional value of each
     * returned product.
     * Each product's refund is its list price reduced by the same discount
     * ratio that applied to the whole original sale: price × (1 − discount /
     * subtotal). This way, if the original sale had a promotion applied, the
     * customer is not refunded more than what they actually paid.
     *
     * @return the calculated refund amount
     */
    public double calculateRefundAmount() {
        double total = 0.0;
        for (Product product : returnedProducts) {
            total += calculateItemRefund(product);
        }
        this.refundAmount = total;
        return this.refundAmount;
    }

    /**
     * Calculates the refund amount as the sum of the proportional value of each
     * returned product plus the refundable cost of the cancelled warranties.
     *
     * @param warrantyRefund the refundable cost of the warranties cancelled with
     * this return, zero if there is none
     * @return the calculated refund amount
     * @throws IllegalArgumentException if the warranty refund is negative
     */
    public double calculateRefundAmount(double warrantyRefund) {
        if (warrantyRefund < 0) {
            throw new IllegalArgumentException("Warranty refund must not be negative");
        }
        calculateRefundAmount();
        this.refundAmount += warrantyRefund;
        return this.refundAmount;
    }

    /**
     * Calculates the proportional refund for a single returned product,
     * applying the same discount ratio that applied to the original sale.
     *
     * @param product the product to calculate the refund for
     * @return the proportional refund amount for the product
     */
    private double calculateItemRefund(Product product) {
        double subtotal = originalSale.getSubtotal();
        double discountRatio = subtotal > 0 ? originalSale.getDiscountAmount() / subtotal : 0.0;
        return product.getPrice() * (1 - discountRatio);
    }

    /**
     * Builds a formatted receipt, in Spanish, with the detail of this return:
     * identifier, date, reference to the original sale, and for each returned
     * product its list price, proportional discount and refunded amount,
     * followed by the reason and the total refunded amount.
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
            double listPrice = product.getPrice();
            double itemRefund = calculateItemRefund(product);
            double proportionalDiscount = listPrice - itemRefund;
            receipt.append(String.format("  - %s%n", product.getTitle()));
            receipt.append(String.format("      Precio de lista: $%.2f%n", listPrice));
            receipt.append(String.format("      Descuento proporcional: -$%.2f%n", proportionalDiscount));
            receipt.append(String.format("      Reembolso: $%.2f%n", itemRefund));
        }
        receipt.append("Motivo: ").append(reason).append("\n");
        receipt.append(String.format("Monto reembolsado: $%.2f%n", refundAmount));
        return receipt.toString();
    }
}