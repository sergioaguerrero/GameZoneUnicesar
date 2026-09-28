package com.gamezone.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sale transaction in the GameZone system.
 */
public class Sale {

    private String id;
    private LocalDate date;
    private Customer customer;
    private Seller seller;
    private List<SaleItem> items;
    private String appliedPromotionName;
    private double discountAmount = 0.0;
    private double extendedWarrantyCost = 0.0;

    /**
     * Constructs a new Sale transaction.
     *
     * @param id the unique identifier of the sale
     * @param date the date the sale was made
     * @param customer the customer making the purchase
     * @param seller the seller attending the sale
     */
    public Sale(String id, LocalDate date, Customer customer, Seller seller) {
        this.id = id;
        this.date = date;
        this.customer = customer;
        this.seller = seller;
        this.items = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public void setItems(List<SaleItem> items) {
        this.items = items;
    }

    public String getAppliedPromotionName() {
        return appliedPromotionName;
    }

    public void setAppliedPromotionName(String appliedPromotionName) {
        this.appliedPromotionName = appliedPromotionName;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getExtendedWarrantyCost() {
        return extendedWarrantyCost;
    }

    public void setExtendedWarrantyCost(double extendedWarrantyCost) {
        this.extendedWarrantyCost = extendedWarrantyCost;
    }

    /**
     * Adds a new item to the sale's item list.
     *
     * @param item the SaleItem to be added
     */
    public void addItem(SaleItem item) {
        this.items.add(item);
    }

    /**
     * Calculates the total amount of the sale by summing up the subtotals of
     * all items.
     *
     * @return the total cost of the sale
     */
    public double calculateTotal() {
        double total = 0.0;
        for (SaleItem item : items) {
            total += item.calculateSubtotal();
        }
        return total;
    }

    /**
     * Calculates the final amount the customer has to pay: the items
     * subtotal, minus the applied promotion discount, plus the additional
     * cost of the extended warranties selected for this sale.
     *
     * @return the final total of the sale
     */
    public double calculateFinalTotal() {
        return calculateTotal() - discountAmount + extendedWarrantyCost;
    }

    /**
     * Registers the sale and validates that it contains at least one product.
     *
     * @throws IllegalStateException if the sale has no items
     */
    public void register() {
        if (this.items == null || this.items.isEmpty()) {
            throw new IllegalStateException("A sale must contain at least one product to be registered.");
        }
    }

    /**
     * Generates a detailed receipt for the sale, including the sale date,
     * customer, seller, purchased items, subtotal, applied discount, and final
     * total.
     *
     * @return a formatted string containing the detailed sale receipt
     */
    public String generateReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("Venta ").append(id).append(" [").append(date).append("] ")
                .append("Cliente: ").append(customer.getName())
                .append(" | Vendedor: ").append(seller.getName())
                .append("\n");

        for (SaleItem item : items) {
            sb.append("     - ").append(item.getProduct().getTitle())
                    .append(" x").append(item.getQuantity())
                    .append(" = $").append(String.format("%.2f", item.calculateSubtotal()))
                    .append("\n");
        }

        double subtotal = calculateTotal();
        sb.append("     Subtotal: $").append(String.format("%.2f", subtotal)).append("\n");

        if (discountAmount > 0 && appliedPromotionName != null) {
            sb.append("     Descuento (").append(appliedPromotionName).append("): -$")
                    .append(String.format("%.2f", discountAmount)).append("\n");
        }

        if (extendedWarrantyCost > 0) {
            sb.append("     Garantía extendida: +$")
                    .append(String.format("%.2f", extendedWarrantyCost)).append("\n");
        }

        sb.append("     Total Final: $").append(String.format("%.2f", calculateFinalTotal()));

        return sb.toString();
    }

    /**
     * Checks whether this sale is still within the return period.
     *
     * A sale can be returned only while the current date is within 30 calendar
     * days of the sale date, inclusive.
     *
     * @return true if the sale is still within the 30-day return window, false
     * otherwise
     */
    public boolean canBeReturned() {
        long daysSinceSale = java.time.temporal.ChronoUnit.DAYS.between(this.date, LocalDate.now());
        return daysSinceSale >= 0 && daysSinceSale <= 30;
    }
}