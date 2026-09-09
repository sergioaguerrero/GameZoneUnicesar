package com.gamezone.service;

import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
import com.gamezone.persistence.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Contains the business rules related to product management: registering new
 * products, listing the inventory and updating stock levels. This is the only
 * class in the product module authorized to invoke the
 * {@link ProductRepository}.
 */
public class ProductService {

    private final ProductRepository productRepository;
    private List<Product> products;

    /**
     * Creates a product service backed by the given repository, loading any
     * previously persisted products immediately.
     *
     * @param productRepository the repository used for persistence
     */
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.products = productRepository.loadAll();
    }

    /**
     * Registers a new video game in the inventory and persists the change.
     *
     * @param productId unique identifier of the product
     * @param title title of the video game
     * @param price unit price
     * @param stockQuantity initial quantity available
     * @param platform platform the game was developed for
     * @param genre genre of the game
     * @param ageRating recommended age rating
     * @return the newly created video game
     * @throws IllegalArgumentException if a product with the same id already
     * exists
     */
    public VideoGame registerVideoGame(String productId, String title, double price,
            int stockQuantity, String platform, String genre,
            String ageRating) {
        validateNewProductId(productId);
        VideoGame videoGame = new VideoGame(productId, title, price, stockQuantity,
                platform, genre, ageRating);
        products.add(videoGame);
        persist();
        return videoGame;
    }

    /**
     * Registers a new console in the inventory and persists the change.
     *
     * @param productId unique identifier of the product
     * @param title title/name of the console
     * @param price unit price
     * @param stockQuantity initial quantity available
     * @param brand brand of the console
     * @param model model of the console
     * @param generation generation number of the console
     * @return the newly created console
     * @throws IllegalArgumentException if a product with the same id already
     * exists
     */
    public Console registerConsole(String productId, String title, double price,
            int stockQuantity, String brand, String model,
            int generation) {
        validateNewProductId(productId);
        Console console = new Console(productId, title, price, stockQuantity,
                brand, model, generation);
        products.add(console);
        persist();
        return console;
    }

    /**
     * Returns the full list of products currently available in inventory.
     *
     * @return a copy of the list of products
     */
    public List<Product> listAllProducts() {
        return new ArrayList<>(products);
    }

    /**
     * Finds a product by its unique identifier.
     *
     * @param productId the identifier to search for
     * @return an Optional containing the product if found, or empty otherwise
     */
    public Optional<Product> findById(String productId) {
        return products.stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst();
    }

    /**
     * Checks whether a given product has enough stock for the requested
     * quantity. Used by other modules (such as sales) before confirming an
     * operation that consumes inventory.
     *
     * @param productId the id of the product to check
     * @param quantity the quantity requested
     * @return true if enough stock is available, false otherwise
     */
    public boolean hasEnoughStock(String productId, int quantity) {
        return findById(productId)
                .map(p -> p.hasEnoughStock(quantity))
                .orElse(false);
    }

    /**
     * Decreases the stock of a given product by the specified quantity and
     * persists the change. Intended to be called when a sale is registered.
     *
     * @param productId the id of the product whose stock will be updated
     * @param quantity the quantity to subtract from stock
     * @throws IllegalArgumentException if the product does not exist or there
     * is not enough stock available
     */
    public void updateStock(String productId, int quantity) {
        Product product = findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                "Product not found: " + productId));
        product.decreaseStock(quantity);
        persist();
    }

    /**
     * Updates the common attributes of an existing product (title, price and
     * stock quantity). Does not modify type-specific attributes.
     *
     * @param productId the id of the product to update
     * @param title the new title
     * @param price the new price
     * @param stockQuantity the new stock quantity
     * @return true if the product was found and updated, false otherwise
     */
    public boolean updateProduct(String productId, String title, double price, int stockQuantity) {
        Optional<Product> found = findById(productId);
        if (found.isEmpty()) {
            return false;
        }
        Product product = found.get();
        product.setTitle(title);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        persist();
        return true;
    }

    /**
     * Removes a product from the inventory by its id.
     *
     * @param productId the id of the product to remove
     * @return true if a product was found and removed, false otherwise
     */
    public boolean deleteProduct(String productId) {
        boolean removed = products.removeIf(p -> p.getProductId().equals(productId));
        if (removed) {
            persist();
        }
        return removed;
    }

    /**
     * Ensures the given product id is not already in use.
     *
     * @param productId the id to validate
     * @throws IllegalArgumentException if a product with the same id already
     * exists
     */
    private void validateNewProductId(String productId) {
        if (findById(productId).isPresent()) {
            throw new IllegalArgumentException(
                    "A product with id " + productId + " already exists");
        }
    }

    /**
     * Persists the current in-memory list of products through the repository.
     */
    private void persist() {
        productRepository.saveAll(products);
    }
}
