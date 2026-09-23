package com.gamezone.service;

import com.gamezone.model.*;
import com.gamezone.persistence.AccessoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccessoryService {
    private final AccessoryRepository accessoryRepository;
    private List<Accessory> accessories;

    public AccessoryService(AccessoryRepository accessoryRepository, List<Accessory> accessories) {
        this.accessoryRepository = accessoryRepository;
        this.accessories = accessories;
    }

    public Cable registerCable(String productId, String title, double price,
                               int stockQuantity, double length, String connectorType) {
        validateNewAccessoryId(productId);
        Cable cable = new Cable(productId, title, price, stockQuantity,
                length,connectorType);
        accessories.add(cable);
        persist();
        return cable;
    }

    public Controller registerController(String productId, String title, double price,
                               int stockQuantity,String connectionType) {
        validateNewAccessoryId(productId);
        Controller controller = new Controller(productId, title, price, stockQuantity,
                connectionType);
        accessories.add(controller);
        persist();
        return controller;
    }

    public Memory registerMemory(String productId, String title, double price,
                                 int stockQuantity, int capacityGB, String memoryType) {
        validateNewAccessoryId(productId);
        Memory memory = new Memory(productId, title, price, stockQuantity,
                capacityGB,memoryType);
        accessories.add(memory);
        persist();
        return memory;
    }

    public List<Accessory> listAllAccessories() {
        return new ArrayList<>(accessories);
    }

    public List<Accessory> listAccessoriesByType(String TYPE){
        List<Accessory> listAccessoriesByType = new ArrayList<>();
        for(Accessory a : accessories){
            if (a.getAccessoryType().equals(TYPE)){
                listAccessoriesByType.add(a);
            }
        }
        return listAccessoriesByType;
    }

    public List<Accessory> findAccessoriesCompatibleWith(String ConsoleId){
        List<Accessory> compatibleWith = new ArrayList<>();
        for(Accessory a : accessories){
            if (a.getAccessoryType().equals(ConsoleId)){
                compatibleWith.add(a);
            }
        }
        return compatibleWith;
    }

    private void persist() {
        accessoryRepository.saveAll(accessories);
    }

    private void validateNewAccessoryId(String accessoryId) {
        if (findById(accessoryId).isPresent()) {
            throw new IllegalArgumentException(
                    "A product with id " + accessoryId + " already exists");
        }
    }

    public Optional<Accessory> findById(String accessoryId) {
        return accessories.stream()
                .filter(p -> p.getProductId().equals(accessoryId))
                .findFirst();
    }

    public void updateStock(String accessoryId, int quantity) {
        Accessory a = findById(accessoryId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Accessory not found: " + accessoryId));
        a.decreaseStock(quantity);
        persist();
    }
}
