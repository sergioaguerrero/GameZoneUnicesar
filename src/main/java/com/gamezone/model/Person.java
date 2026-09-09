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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
