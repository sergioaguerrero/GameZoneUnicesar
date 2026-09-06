package com.gamezone.model;

import java.util.List;

public class Customer extends Person{
    private String mail;
    private List<Sale> purchaseHistory;

    public Customer(String name, String phone, String id, String mail, List<Sale> purchaseHistory) {
        super(name, phone, id);
        this.mail = mail;
        this.purchaseHistory = purchaseHistory;
    }
    public Customer(String name, String phone, String id, String mail) {
        super(name, phone, id);
        this.mail = mail;
    }
    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public List<Sale> getPurchaseHistory() {
        return purchaseHistory;
    }

    public void addPurchaseHistory(Sale sale) {
        purchaseHistory.add(sale);
    }
}
