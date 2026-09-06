package com.gamezone.service;

import com.gamezone.model.*;
import com.gamezone.persistence.SaleRepository;

import java.time.LocalDate;
import java.util.List;

public class SaleService {
    private final SaleRepository saleRepository;
    private final PersonService personService;
    private final ProductService productService;

    // Initialize constructor to inject the sales repository
    public SaleService(SaleRepository saleRepository, PersonService personService, ProductService productService) {
        this.saleRepository = saleRepository;
        this.personService = personService;
        this.productService = productService;
    }

    public boolean registerSale(String customerId, String sellerId, List<SaleItem> items) {
        Customer customer = personService.findCustomer(customerId);
        Seller seller = personService.findSeller(sellerId);

        // Validations to see if customer or seller exist
        if (customer == null || seller == null) {
            System.err.println("Customer or seller don't exist");
            return false;
        }

        // Validation to see the available stock
        for (SaleItem item : items) {
            String productId = item.getProduct().getProductId();
            if (!productService.hasEnoughStock(productId, item.getQuantity())) {
                System.err.println("Insufficient stock for product with ID: " + productId);
                return false;
            }
        }

        // Create the sale
        Sale newSale = new Sale(LocalDate.now(), customer, seller);
        for (SaleItem item : items) {
            newSale.addItem(item);
        }

        // Business rules validations
        try {
            newSale.register();
        } catch (IllegalArgumentException e) {
            System.err.println("Error registering: " + e.getMessage());
            return false;
        }

        // Getting the lists form PersonService and ProductService
        List<Customer> customers = personService.listCustomer();
        List<Seller> sellers = personService.listSeller();
        List<Product> products = productService.listAllProducts();

        // Load history add the new sale and upload the csv
        List<Sale> existingSales = saleRepository.loadSales(customers, sellers, products);
        existingSales.add(newSale);
        saleRepository.saveSales(existingSales);

        // Persistently discount the stock using the ProductService
        for (SaleItem item : items) {
            productService.updateStock(item.getProduct().getProductId(), item.getQuantity());
        }

        System.out.println("Sale registered successfully");
        return true;
    }
}
