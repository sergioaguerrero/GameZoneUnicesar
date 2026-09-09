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
    /**
     * Gets the unique employee code assigned to the seller.
     *
     * @return The employee code.
     */
    public String getEmployeeCode() {
        return employeeCode;
    }
    /**
     * Sets or updates the employee code for the seller.
     *
     * @param employeeCode The new employee code.
     */
    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }
    /**
     * Gets the work shift schedule assigned to the seller.
     *
     * @return The work shift.
     */
    public String getWorkShift() {
        return workShift;
    }
    /**
     * Sets or updates the work shift schedule for the seller.
     *
     * @param workShift The new work shift.
     */
    public void setWorkShift(String workShift) {
        this.workShift = workShift;
    }


}
