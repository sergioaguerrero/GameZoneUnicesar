package com.gamezone.model;
/**
 * Represents a customer within the GameZone system, extending the Person class.
 *
 * @author Jhonatan David Galindo Gómez
 */
public class Customer extends Person{
    private String mail;
    /**
     * Constructs a new Customer with complete information including purchase history.
     *
     * @param name            The full name of the customer.
     * @param phone           The contact phone number.
     * @param id              The unique identification number.
     * @param mail            The email address.
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
}
