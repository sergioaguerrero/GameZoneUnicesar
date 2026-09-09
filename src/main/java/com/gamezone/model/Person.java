package com.gamezone.model;
/**
 * Abstract class representing a person within the GameZone system.
 *
 * @author Jhonatan David Galindo Gómez
 */
abstract public class Person {
    private String name;
    private String id;
    private String phone;

    /**
     * Constructs a new Person instance with their basic information.
     *
     * @param name  The name of the person.
     * @param phone The phone of the person.
     * @param id    The identification of the person.
     */
    public Person(String name, String phone, String id) {
        this.name = name;
        this.phone = phone;
        this.id = id;
    }
    /**
     * Gets the identification number of the person.
     *
     * @return The identification number.
     */
    public String getId() {
        return id;
    }
    /**
     * Sets or updates the identification number of the person.
     *
     * @param id The new identification number.
     */
    public void setId(String id) {
        this.id = id;
    }
    /**
     * Gets the phone number of the person.
     *
     * @return The phone number.
     */
    public String getPhone() {
        return phone;
    }
    /**
     * Sets or updates the phone number of the person.
     *
     * @param phone The new phone number.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    /**
     * Gets the name of the person.
     *
     * @return The name of the person.
     */
    public String getName() {
        return name;
    }
    /**
     * Sets or updates the full name of the person.
     *
     * @param name The new name of the person.
     */
    public void setName(String name) {
        this.name = name;
    }
}
