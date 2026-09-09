package com.gamezone.model;
/**
 * Represents a seller or employee within the GameZone system, extending the Person class.
 *
 * @author Jhonatan David Galindo Gómez
 */
public class Seller extends Person{
    private String employeeCode;
    private String workShift;

    /**
     * Constructs a new Seller instance with their personal and employment information.
     *
     * @param name         The full name of the seller.
     * @param phone        The contact phone number.
     * @param id           The unique identification number.
     * @param employeeCode The unique employee code.
     * @param workShift    The work shift schedule.
     */
    public Seller(String name, String phone, String id, String employeeCode, String workShift) {
        super(name, phone, id);
        this.employeeCode = employeeCode;
        this.workShift = workShift;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getWorkShift() {
        return workShift;
    }

    public void setWorkShift(String workShift) {
        this.workShift = workShift;
    }


}
