package com.gamezone.model;

import java.time.LocalDate;
import java.util.ArrayList;

public class Sale {
    private LocalDate date;
    private Customer customer;
    private Seller seller;
    private List<SaleItem> items;

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
}
