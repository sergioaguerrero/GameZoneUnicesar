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

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.products = productRepository.loadAll();
    }

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

    public List<Product> listAllProducts() {
        return new ArrayList<>(products);
    }

    public Optional<Product> findById(String productId) {
        return products.stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst();
    }

    public boolean hasEnoughStock(String productId, int quantity) {
        return findById(productId)
                .map(p -> p.hasEnoughStock(quantity))
                .orElse(false);
    }
        public void updateStock(String productId, int quantity) {
        Product product = findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found: " + productId));
        product.decreaseStock(quantity);
        persist();
    }
            private void validateNewProductId(String productId) {
        if (findById(productId).isPresent()) {
            throw new IllegalArgumentException(
                    "A product with id " + productId + " already exists");
        }
    }

    private void persist() {
        productRepository.saveAll(products);
    }
}
