package com.gamezone.service;

import com.gamezone.model.Customer;
import com.gamezone.model.Seller;
import com.gamezone.persistence.PersonRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Service class that manages the business logic for Customer and Seller entities.
 *
 * @author Jhonatan David Galindo Gómez
 */
public class PersonService{
    private final PersonRepository repository;
    private final List<Customer> customers;
    private final List<Seller> sellers;
    /**
     * Constructs a new PersonService instance, initializing the repository
     * and loading existing customer and seller records from storage.
     *
     * @param repository The repository responsible for data persistence.
     * @param customers  Initial list of customers (overridden by loaded data).
     * @param sellers    Initial list of sellers (overridden by loaded data).
     */
    public PersonService(PersonRepository repository, List<Customer> customers, List<Seller> sellers) {
        this.repository = repository;
        this.customers = new ArrayList<>(repository.loadCustomer());
        this.sellers = new ArrayList<>(repository.loadSeller());
    }
    /**
     * Registers a new customer, adds them to the active list, and saves the updated records.
     *
     * @param name  The full name of the customer.
     * @param phone The contact phone number.
     * @param id    The unique identification number.
     * @param mail  The email address.
     */
    public void registerCustomer(String name, String phone,String id,String mail){
        Customer customer = new Customer(name, phone, id, mail);
        customers.add(customer);
        repository.saveCustomer(customers);
    }
    /**
     * Registers a new seller, adds them to the active list, and saves the updated records.
     *
     * @param name         The full name of the seller.
     * @param phone        The contact phone number.
     * @param id           The unique identification number.
     * @param employeeCode The unique employee code.
     * @param workShift    The work shift schedule.
     */
    public void registerSeller(String name,String phone,String id,
                               String employeeCode,String workShift){
        Seller seller = new Seller(name,phone,id,employeeCode,workShift);
        sellers.add(seller);
        repository.saveSeller(sellers);
    }
    /**
     * Returns an unmodifiable view of the registered sellers list.
     *
     * @return An unmodifiable list of sellers.
     */
    public List<Seller> listSeller(){
        return Collections.unmodifiableList(sellers); //Inspirado en Freddy
    }
    /**
     * Returns an unmodifiable view of the registered customers list.
     *
     * @return An unmodifiable list of customers.
     */
    public List<Customer> listCustomer(){
        return Collections.unmodifiableList(customers); //Inspirado en Freddy
    }
    /**
     * Searches for a seller by their unique identification number.
     *
     * @param id The identification number to search for.
     * @return The Seller object if found, or null otherwise.
     */
    public Seller findSeller(String id){
        for (Seller s:sellers){
            if (s.getId().equals(id)){
                return s;
            }
        }
        return null;
    }
    /**
     * Searches for a customer by their unique identification number.
     *
     * @param id The identification number to search for.
     * @return The Customer object if found, or null otherwise.
     */
    public Customer findCustomer(String id){
        for (Customer c:customers){
            if(c.getId().equals(id)){
                return c;
            }
        }
        return null;
    }
}
