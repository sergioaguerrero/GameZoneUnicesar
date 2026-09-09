package com.gamezone;

import com.gamezone.persistence.PersonRepository;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;
import com.gamezone.ui.ConsoleMenu;

import java.util.Collections;

/**
 * Application entry point for GameZone Unicesar. Wires the persistence,
 * service, and UI layers together and launches the console menu.
 */
public class Main {

    /**
     * Loads all repositories and services, then starts the console menu.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            ProductRepository productRepository = new ProductRepository();
            PersonRepository personRepository = new PersonRepository();
            SaleRepository saleRepository = new SaleRepository();

            ProductService productService = new ProductService(productRepository);
            PersonService personService = new PersonService(personRepository,
                    Collections.emptyList(), Collections.emptyList());
            SaleService saleService = new SaleService(saleRepository, personService, productService);

            ConsoleMenu consoleMenu = new ConsoleMenu(productService, personService, saleService);
            consoleMenu.start();
        } catch (RuntimeException e) {
            System.err.println("Fatal error: " + e.getMessage());
            System.exit(1);
        }
    }
}