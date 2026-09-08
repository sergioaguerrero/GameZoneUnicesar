package com.gamezone.service;

import com.gamezone.model.Customer;
import com.gamezone.model.Seller;
import com.gamezone.persistence.PersonRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PersonService{
    private final PersonRepository repository;
    private final List<Customer> customers;
    private final List<Seller> sellers;

    public PersonService(PersonRepository repository, List<Customer> customers, List<Seller> sellers) {
        this.repository = repository;
        this.customers = new ArrayList<>(repository.loadCustomer());
        this.sellers = new ArrayList<>(repository.loadSeller());
    }

    public void registerCustomer(String name, String phone,String id,String mail){
        Customer customer = new Customer(name, phone, id, mail);
        customers.add(customer);
        repository.saveCustomer(customers);
    }

    public void registerSeller(String name,String phone,String id,
                               String employeeCode,String workShift){
        Seller seller = new Seller(name,phone,id,employeeCode,workShift);
        sellers.add(seller);
        repository.saveSeller(sellers);
    }

    public List<Seller> listSeller(){
        return Collections.unmodifiableList(sellers); //Inspirado en Freddy
    }

    public List<Customer> listCustomer(){
        return Collections.unmodifiableList(customers); //Inspirado en Freddy
    }

    public Seller findSeller(String id){
        for (Seller s:sellers){
            if (s.getId().equals(id)){
                return s;
            }
        }
        return null;
    }

    public Customer findCustomer(String id){
        for (Customer c:customers){
            if(c.getId().equals(id)){
                return c;
            }
        }
        return null;
    }
}
