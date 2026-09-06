package com.gamezone.persistence;

import com.gamezone.model.Product;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles file-based persistence for {@link Product} objects. Responsible only
 * for saving and loading the list of products to/from disk. Contains no
 * business rules; those belong to the service layer.
 *
 * Uses Java serialization so that both {@code VideoGame} and {@code Console}
 * instances (polymorphic subtypes of {@code Product}) can be stored and
 * restored transparently in a single file.
 */
public class ProductRepository {

    private final String filePath;

    public ProductRepository(String filePath) {
        this.filePath = filePath;
    }

    public void saveAll(List<Product> products) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(new ArrayList<>(products));
        } catch (IOException e) {
            throw new RuntimeException("Error saving products to file: " + filePath, e);
        }
    }

    @SuppressWarnings("unchecked")
    public List<Product> loadAll() {
        File file = new File(filePath);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object data = in.readObject();
            return (List<Product>) data;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error loading products from file: " + filePath, e);
        }
    }
}
