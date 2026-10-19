package com.gamezone.model;

/**
 * Represents a memory accessory commercialized by GameZone Unicesar.
 *
 * A memory is characterized by its storage capacity and memory type. When
 * applicable, the consoles with which it is compatible are managed by
 * {@link Accessory}.
 */
public class Memory extends Accessory {

    private int capacityGB;
    private String memoryType;

    /**
     * Creates a new memory accessory.
     *
     * @param productId     unique identifier of the memory
     * @param title         title of the memory
     * @param price         unit price of the memory
     * @param stockQuantity quantity available in inventory
     * @param capacityGB    storage capacity in gigabytes, greater than zero
     * @param memoryType    type of memory, such as SD, microSD or internal
     * @throws IllegalArgumentException if the capacity is not greater than zero
     *                                  or the memory type is null or blank
     */
    public Memory(String productId, String title, double price, int stockQuantity,
                  int capacityGB, String memoryType) {
        super(productId, title, price, stockQuantity);
        setCapacityGB(capacityGB);
        setMemoryType(memoryType);
    }

    /**
     * Returns the storage capacity in gigabytes.
     *
     * @return the storage capacity
     */
    public int getCapacityGB() {
        return capacityGB;
    }

    /**
     * Sets the storage capacity in gigabytes.
     *
     * @param capacityGB the new storage capacity, greater than zero
     * @throws IllegalArgumentException if the capacity is not greater than zero
     */
    public void setCapacityGB(int capacityGB) {
        if (capacityGB <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero");
        }
        this.capacityGB = capacityGB;
    }

    /**
     * Returns the type of memory.
     *
     * @return the memory type
     */
    public String getMemoryType() {
        return memoryType;
    }

    /**
     * Sets the type of memory.
     *
     * @param memoryType the new memory type
     * @throws IllegalArgumentException if the memory type is null or blank
     */
    public void setMemoryType(String memoryType) {
        this.memoryType = validateText(memoryType, "Memory type");
    }

    /**
     * Returns the type of this accessory.
     *
     * @return Memory
     */
    @Override
    public String getAccessoryType() {
        return "Memory";
    }

    /**
     * Builds a full description of the memory combining the common
     * product attributes with its specific characteristics.
     *
     * @return the full description of the memory
     */
    @Override
    public String getFullDescription() {
        return String.format(
                "[Memory] %s | Capacity: %d GB | Type: %s | Price: $%.2f | Stock: %d | Compatible Consoles: %d",
                getTitle(),
                capacityGB,
                memoryType,
                getPrice(),
                getStockQuantity(),
                getCompatibleConsoles().size());
    }
}
