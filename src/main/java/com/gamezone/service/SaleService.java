package com.gamezone.service;

import com.gamezone.model.*;
import com.gamezone.persistence.SaleRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Contains the business rules related to sales management.
 */
public class SaleService {
    private final SaleRepository saleRepository;
    private final PersonService personService;
    private final ProductService productService;

    /**
     * Initializes the SaleService with the required repositories and services.
     *
     * @param saleRepository the repository handling sale persistence
     * @param personService  the service handling customers and sellers
     * @param productService the service handling products and inventory
     */
    public SaleService(SaleRepository saleRepository, PersonService personService, ProductService productService) {
        this.saleRepository = saleRepository;
        this.personService = personService;
        this.productService = productService;
    }

    /**
     * Registers a new sale by validating the actors, verifying product stock,
     * creating the transaction, decreasing stock, and saving it to the repository.
     *
     * @param customerId the ID of the customer making the purchase
     * @param sellerId   the ID of the seller handling the transaction
     * @param items      the list of items to be purchased
     * @return true if the sale was successfully registered, false otherwise
     */
    public boolean registerSale(String customerId, String sellerId, List<SaleItem> items) {
        Customer customer = personService.findCustomer(customerId);
        Seller seller = personService.findSeller(sellerId);

        if (customer == null || seller == null) {
            System.err.println("Customer or seller don't exist");
            return false;
        }

        for (SaleItem item : items) {
            String productId = item.getProduct().getProductId();
            if (!productService.hasEnoughStock(productId, item.getQuantity())) {
                System.err.println("Insufficient stock for product with ID: " + productId);
                return false;
            }
        }

        Sale newSale = new Sale(LocalDate.now(), customer, seller);
        for (SaleItem item : items) {
            newSale.addItem(item);
        }

        try {
            newSale.register();
        } catch (IllegalArgumentException e) {
            System.err.println("Error registering: " + e.getMessage());
            return false;
        }

        List<Customer> customers = personService.listCustomer();
        List<Seller> sellers = personService.listSeller();
        List<Product> products = productService.listAllProducts();

        List<Sale> existingSales = saleRepository.loadSales(customers, sellers, products);
        existingSales.add(newSale);
        saleRepository.saveSales(existingSales);

        for (SaleItem item : items) {
            productService.updateStock(item.getProduct().getProductId(), item.getQuantity());
        }

        System.out.println("Sale registered successfully");
        return true;
    }
}