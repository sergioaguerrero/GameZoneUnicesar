package com.gamezone.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a sale transaction in the GameZone system.
 */
public class Sale {
    private LocalDate date;
    private Customer customer;
    private Seller seller;
    private List<SaleItem> items;

    /**
     * Constructs a new Sale transaction.
     *
     * @param date     the date the sale was made
     * @param customer the customer making the purchase
     * @param seller   the seller attending the sale
     */
    public Sale(LocalDate date, Customer customer, Seller seller) {
        this.date = date;
        this.customer = customer;
        this.seller = seller;
        this.items = new ArrayList<>();
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

    /**
     * Adds a new item to the sale's item list.
     *
     * @param item the SaleItem to be added
     */
    public void addItem(SaleItem item) {
        this.items.add(item);
    }

    /**
     * Calculates the total amount of the sale by summing up the subtotals of all items.
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
     * Registers the sale and validates that it contains at least one product.
     *
     * @throws IllegalStateException if the sale has no items
     */
    public void register() {
        if (this.items == null || this.items.isEmpty()) {
            throw new IllegalStateException("A sale must contain at least one product to be registered.");
        }
    }
}