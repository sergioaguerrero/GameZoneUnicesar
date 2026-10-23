package com.gamezone.service;

import com.gamezone.model.Customer;
import com.gamezone.model.Seller;
import com.gamezone.persistence.PersonRepository;
import com.gamezone.validation.PersonValidator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Service class that manages the business logic for Customer and Seller entities.
 *
 * @author Jhonatan David Galindo Gómez
 */
public class PersonService {
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
     * Registers a new customer after validating its data and identifier.
     *
     * @throws com.gamezone.exceptions.InvalidDataException if any field is invalid
     * @throws com.gamezone.exceptions.BusinessRuleException if the id is already in use
     */
    public void registerCustomer(String name, String phone, String id, String mail) {
        PersonValidator.validatePersonData(id, name, mail, phone);
        PersonValidator.validateUniquePerson(customers, id);

        Customer customer = new Customer(name, phone, id, mail);
        customers.add(customer);
        repository.saveCustomer(customers);
    }
    /**
     * Registers a new seller after validating its data and identifier.
     * Sellers have no email, so {@code null} is passed for that parameter.
     *
     * @throws com.gamezone.exceptions.InvalidDataException if any field is invalid
     * @throws com.gamezone.exceptions.BusinessRuleException if the id is already in use
     */
    public void registerSeller(String name, String phone, String id,
                               String employeeCode, String workShift) {
        PersonValidator.validatePersonData(id, name, null, phone);
        PersonValidator.validateUniquePerson(sellers, id);

        Seller seller = new Seller(name, phone, id, employeeCode, workShift);
        sellers.add(seller);
        repository.saveSeller(sellers);
    }

    public List<Seller> listSeller() {
        return Collections.unmodifiableList(sellers);
    }

    public List<Customer> listCustomer() {
        return Collections.unmodifiableList(customers);
    }

    /** Returns the seller, or null if not found (kept for callers that check null). */
    public Seller findSeller(String id) {
        for (Seller s : sellers) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    /** Returns the customer, or null if not found (kept for callers that check null). */
    public Customer findCustomer(String id) {
        for (Customer c : customers) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Returns the seller with the given id.
     *
     * @throws com.gamezone.exceptions.ResourceNotFoundException if it does not exist
     */
    public Seller getSellerOrThrow(String id) {
        Seller seller = findSeller(id);
        PersonValidator.validatePersonExists(seller, id);
        return seller;
    }

    /**
     * Returns the customer with the given id.
     *
     * @throws com.gamezone.exceptions.ResourceNotFoundException if it does not exist
     */
    public Customer getCustomerOrThrow(String id) {
        Customer customer = findCustomer(id);
        PersonValidator.validatePersonExists(customer, id);
        return customer;
    }
}