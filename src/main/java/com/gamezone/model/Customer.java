package com.gamezone.model;

import java.util.List;
/**
 * Represents a customer within the GameZone system, extending the Person class.
 *
 * @author Jhonatan David Galindo Gómez
 */
public class Customer extends Person{
    private String mail;
    private List<Sale> purchaseHistory;
    /**
     * Constructs a new Customer with complete information including purchase history.
     *
     * @param name            The full name of the customer.
     * @param phone           The contact phone number.
     * @param id              The unique identification number.
     * @param mail            The email address.
     * @param purchaseHistory The initial list of past sales.
     */
    public Customer(String name, String phone, String id, String mail, List<Sale> purchaseHistory) {
        super(name, phone, id);
        this.mail = mail;
        this.purchaseHistory = purchaseHistory;
    }
    /**
     * Constructs a new Customer with basic information, without an initial purchase history.
     *
     * @param name  The full name of the customer.
     * @param phone The contact phone number.
     * @param id    The unique identification number.
     * @param mail  The email address.
     */
    public Customer(String name, String phone, String id, String mail) {
        super(name, phone, id);
        this.mail = mail;
    }
    /**
     * Gets the email address of the person.
     *
     * @return The email address.
     */
    public String getMail() {
        return mail;
    }
    /**
     * Sets or updates the email address of the customer.
     *
     * @param mail The new email address.
     */
    public void setMail(String mail) {
        this.mail = mail;
    }
    /**
     * Gets the list of sales representing the customer's purchase history.
     *
     * @return The list of past sales.
     */
    public List<Sale> getPurchaseHistory() {
        return purchaseHistory;
    }
    /**
     * Adds a new sale to the customer's purchase history.
     *
     * @param sale The sale object to add to the history.
     */
    public void addPurchaseHistory(Sale sale) {
        purchaseHistory.add(sale);
    }
}
