package com.gamezone.persistence;

import com.gamezone.model.Customer;
import com.gamezone.model.Seller;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class PersonRepository {
    private static final String customerCSV = "data/customers.csv";
    private static final String sellerCSV = "data/sellers.csv";

    public String customerLine(Customer c){
        return c.getName()+","+c.getPhone()+","+c.getId()+","+c.getMail();
    }

    public String sellerLine(Seller s) {
        return s.getName()+","+s.getPhone()+","+s.getId()+","+s.getEmployeeCode()+","+s.getWorkShift();
    }

    public void saveCustomer(List<Customer> customers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(customerCSV))) {
            for (Customer c : customers) {
                writer.write(customerLine(c));
                writer.newLine();
            }
            System.out.println("Customers saved successfully in " + customerCSV);
        } catch (IOException e) {
            System.err.println("Error saving the CSV file: " + e.getMessage());
        }
    }

    public List<Customer> loadCustomer() {
        List<Customer> customers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(customerCSV))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length == 4) {
                    String name = parts[0];
                    String phone = parts[1];
                    String id = parts[2];
                    String mail = parts[3];
                    Customer customer = new Customer(name,phone,id,mail);
                    customers.add(customer);
                }
            }
            System.out.println("Data successfully loaded from " + customerCSV);
        } catch (IOException e) {
            System.err.println("The file could not be read: " + e.getMessage());
        }

        return customers;
    }

    public void saveSeller(List<Seller> sellers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(sellerCSV))) {
            for (Seller s : sellers) {
                writer.write(sellerLine(s));
                writer.newLine();
            }
            System.out.println("Sellers saved successfully in " + sellerCSV);
        } catch (IOException e) {
            System.err.println("Error saving the CSV file: " + e.getMessage());
        }
    }

    public List<Seller> loadSeller() {
        List<Seller> sellers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(sellerCSV))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length == 5) {
                    String name = parts[0];
                    String phone = parts[1];
                    String id = parts[2];
                    String employeeCode = parts[3];
                    String workShift = parts[4];
                    Seller seller = new Seller(name,phone,id,employeeCode,workShift);
                    sellers.add(seller);
                }
            }
            System.out.println("Data successfully loaded from " + sellerCSV);
        } catch (IOException e) {
            System.err.println("The file could not be read: " + e.getMessage());
        }

        return sellers;
    }
}

