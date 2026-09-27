package com.gamezone;

import com.gamezone.persistence.*;
import com.gamezone.service.*;
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
            AccessoryRepository accessoryRepository = new AccessoryRepository();
            PromotionRepository promotionRepository = new PromotionRepository(); // Nuevo

            ProductService productService = new ProductService(productRepository);
            PersonService personService = new PersonService(personRepository,
                    Collections.emptyList(), Collections.emptyList());
            AccessoryService accessoryService = new AccessoryService(accessoryRepository,
                    accessoryRepository.loadAll());
            PromotionService promotionService = new PromotionService(promotionRepository,
                    promotionRepository.loadAll());

            SaleService saleService = new SaleService(saleRepository, personService,
                    productService, accessoryService, promotionService);

            ConsoleMenu consoleMenu = new ConsoleMenu(productService, personService,
                    saleService, accessoryService, promotionService);
            consoleMenu.start();
        } catch (RuntimeException e) {
            System.err.println("Fatal error: " + e.getMessage());
            System.exit(1);
        }
    }
}