package com.gamezone.service;

import com.gamezone.model.*;
import com.gamezone.persistence.AccessoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Coordinates the business rules for managing {@link Accessory} instances
 * (cables, controllers, and memory accessories), delegating persistence to
 * an {@link AccessoryRepository}.
 */
public class AccessoryService {
    private final AccessoryRepository accessoryRepository;
    private List<Accessory> accessories;

    /**
     * Creates a service backed by the given repository and initial list of
     * accessories.
     *
     * @param accessoryRepository the repository used to persist changes
     * @param accessories the initial in-memory list of accessories
     */
    public AccessoryService(AccessoryRepository accessoryRepository, List<Accessory> accessories) {
        this.accessoryRepository = accessoryRepository;
        this.accessories = accessories;
    }

    /**
     * Registers a new cable accessory and persists the updated list.
     *
     * @param productId the unique identifier for the new cable
     * @param title the display name of the cable
     * @param price the sale price of the cable
     * @param stockQuantity the initial stock quantity
     * @param length the cable's length
     * @param connectorType the type of connector the cable uses
     * @return the newly created {@link Cable}
     * @throws IllegalArgumentException if an accessory with the given id
     * already exists
     */
    public Cable registerCable(String productId, String title, double price,
                               int stockQuantity, double length, String connectorType) {
        validateNewAccessoryId(productId);
        Cable cable = new Cable(productId, title, price, stockQuantity,
                length,connectorType);
        accessories.add(cable);
        persist();
        return cable;
    }



    /**
     * Registers a new controller accessory and persists the updated list.
     *
     * @param productId the unique identifier for the new controller
     * @param title the display name of the controller
     * @param price the sale price of the controller
     * @param stockQuantity the initial stock quantity
     * @param connectionType the type of connection the controller uses
     * @return the newly created {@link Controller}
     * @throws IllegalArgumentException if an accessory with the given id
     * already exists
     */
    public Controller registerController(String productId, String title, double price,
                               int stockQuantity,String connectionType) {
        validateNewAccessoryId(productId);
        Controller controller = new Controller(productId, title, price, stockQuantity,
                connectionType);
        accessories.add(controller);
        persist();
        return controller;
    }

    /**
     * Registers a new memory accessory and persists the updated list.
     *
     * @param productId the unique identifier for the new memory accessory
     * @param title the display name of the memory accessory
     * @param price the sale price of the memory accessory
     * @param stockQuantity the initial stock quantity
     * @param capacityGB the storage capacity in gigabytes
     * @param memoryType the type of memory (e.g. SD, SSD)
     * @return the newly created {@link Memory}
     * @throws IllegalArgumentException if an accessory with the given id
     * already exists
     */
    public Memory registerMemory(String productId, String title, double price,
                                 int stockQuantity, int capacityGB, String memoryType) {
        validateNewAccessoryId(productId);
        Memory memory = new Memory(productId, title, price, stockQuantity,
                capacityGB,memoryType);
        accessories.add(memory);
        persist();
        return memory;
    }

    /**
     * Returns a new list containing every registered accessory.
     *
     * @return a copy of the list of all accessories
     */
    public List<Accessory> listAllAccessories() {
        return new ArrayList<>(accessories);
    }

    /**
     * Returns every accessory matching the given accessory type.
     *
     * @param TYPE the accessory type to filter by (e.g. "CABLE", "CONTROLLER", "MEMORY")
     * @return the list of accessories whose type matches {@code TYPE}
     */
    public List<Accessory> listAccessoriesByType(String TYPE){
        List<Accessory> listAccessoriesByType = new ArrayList<>();
        for(Accessory a : accessories){
            if (a.getAccessoryType().equals(TYPE)){
                listAccessoriesByType.add(a);
            }
        }
        return listAccessoriesByType;
    }

    /**
     * Returns every accessory compatible with the given console.
     *
     * @param ConsoleId the identifier of the console to check compatibility against
     * @return the list of compatible accessories
     */
    public List<Accessory> findAccessoriesCompatibleWith(String ConsoleId){
        List<Accessory> compatibleWith = new ArrayList<>();
        for(Accessory a : accessories){
            if (a.getAccessoryType().equals(ConsoleId)){
                compatibleWith.add(a);
            }
        }
        return compatibleWith;
    }

    /**
     * Persists the current in-memory list of accessories via the repository.
     */
    private void persist() {
        accessoryRepository.saveAll(accessories);
    }

    /**
     * Validates that no existing accessory already uses the given id.
     *
     * @param accessoryId the id to validate
     * @throws IllegalArgumentException if an accessory with this id already exists
     */
    private void validateNewAccessoryId(String accessoryId) {
        if (findById(accessoryId).isPresent()) {
            throw new IllegalArgumentException(
                    "A product with id " + accessoryId + " already exists");
        }
    }

    /**
     * Looks up an accessory by its id.
     *
     * @param accessoryId the id to search for
     * @return an {@link Optional} containing the matching accessory, or empty
     * if none was found
     */
    public Optional<Accessory> findById(String accessoryId) {
        return accessories.stream()
                .filter(p -> p.getProductId().equals(accessoryId))
                .findFirst();
    }

    /**
     * Decreases the stock quantity of the given accessory and persists the change.
     *
     * @param accessoryId the id of the accessory to update
     * @param quantity the quantity to subtract from current stock
     * @throws IllegalArgumentException if no accessory with the given id exists
     */
    public void updateStock(String accessoryId, int quantity) {
        Accessory a = findById(accessoryId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Accessory not found: " + accessoryId));
        a.decreaseStock(quantity);
        persist();
    }
}
