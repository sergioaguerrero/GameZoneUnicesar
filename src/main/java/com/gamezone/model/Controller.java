package com.gamezone.model;

/**
 * Represents a controller accessory commercialized by GameZone Unicesar.
 *
 * A controller is characterized by its connection type. The consoles with
 * which it is compatible are managed by {@link Accessory}.
 */
public class Controller extends Accessory {

    private String connectionType;

    /**
     * Creates a new controller.
     *
     * @param productId      unique identifier of the controller
     * @param title          title of the controller
     * @param price          unit price of the controller
     * @param stockQuantity  quantity available in inventory
     * @param connectionType connection type of the controller (wireless or wired)
     * @throws IllegalArgumentException if the connection type is null or blank
     */
    public Controller(String productId, String title, double price, int stockQuantity,
                      String connectionType) {
        super(productId, title, price, stockQuantity);
        setConnectionType(connectionType);
    }

    /**
     * Returns the connection type of the controller.
     *
     * @return the connection type
     */
    public String getConnectionType() {
        return connectionType;
    }

    /**
     * Sets the connection type of the controller.
     *
     * @param connectionType the new connection type
     * @throws IllegalArgumentException if the connection type is null or blank
     */
    public void setConnectionType(String connectionType) {
        this.connectionType = validateText(connectionType, "Connection type");
    }

    /**
     * Returns the type of this accessory.
     *
     * @return Controller
     */
    @Override
    public String getAccessoryType() {
        return "Controller";
    }

    /**
     * Builds a full description of the controller combining the common
     * product attributes with its specific characteristics.
     *
     * @return the full description of the controller
     */
    @Override
    public String getFullDescription() {
        return String.format(
                "[Controller] %s | Connection: %s | Price: $%.2f | Stock: %d | Compatible Consoles: %d",
                getTitle(),
                connectionType,
                getPrice(),
                getStockQuantity(),
                getCompatibleConsoles().size());
    }
}