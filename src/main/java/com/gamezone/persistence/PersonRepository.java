package com.gamezone.persistence;

import com.gamezone.model.Customer;
import com.gamezone.model.Seller;

import java.util.ArrayList;
import java.util.List;
import java.io.*;
/**
 * Handles persistence operations for managing Customer and Seller data using CSV files.
 *
 * @author Jhonatan David Galindo Gómez
 */
public class PersonRepository {
    private static final String customerCSV = "data/customers.csv";
    private static final String sellerCSV = "data/sellers.csv";
    /**
     * Converts a Customer object into a CSV-formatted string.
     *
     * @param c The customer object to convert.
     * @return A comma-separated string containing name, phone, id, and mail.
     */
    public String customerLine(Customer c){
        return c.getName()+","+c.getPhone()+","+c.getId()+","+c.getMail();
    }
    /**
     * Converts a Seller object into a CSV-formatted string.
     *
     * @param s The seller object to convert.
     * @return A comma-separated string containing name, phone, id, employee code, and work shift.
     */
    public String sellerLine(Seller s) {
        return s.getName()+","+s.getPhone()+","+s.getId()+","+s.getEmployeeCode()+","+s.getWorkShift();
    }
    /**
     * Saves a list of customers to the CSV file.
     *
     * @param customers The list of customers to save.
     */
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
    /**
     * Loads a list of customers from the CSV file.
     *
     * @return A list containing the loaded customers.
     */
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
    /**
     * Saves a list of sellers to the CSV file.
     *
     * @param sellers The list of sellers to save.
     */
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
    /**
     * Loads a list of sellers from the CSV file.
     *
     * @return A list containing the loaded sellers.
     */
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
    /**
     * Deletes a seller matching the given ID from the list and updates the storage.
     *
     * @param sellers The current list of sellers.
     * @param id      The identification of the seller to delete.
     */
    public void deleteSeller(List<Seller> sellers,String id){
        for (Seller s:sellers){
            sellers.remove(s);
            saveSeller(sellers);
            break;
        }
    }
    /**
     * Deletes a customer matching the given ID from the list and updates the storage.
     *
     * @param customers The current list of customers.
     * @param id        The identification of the customer to delete.
     */
    public void deleteCustomer(List<Customer> customers,String id){
        for(Customer c:customers){
            customers.remove(c);
            saveCustomer(customers);
        }
    }
}

