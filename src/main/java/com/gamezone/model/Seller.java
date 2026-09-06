package com.gamezone.model;

public class Seller extends Person{
    private String employeeCode;
    private String workShift;

    public Seller(String name, String phone, String identification, String employeeCode, String workShift) {
        super(name, phone, identification);
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
