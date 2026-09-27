package com.gamezone.model;

/**
 * Represents a cable accessory commercialized by GameZone Unicesar.
 *
 * A cable is characterized by its length in meters and its connector type.
 */
public class Cable extends Accessory {

    private double length;
    private String connectorType;

    /**
     * Creates a new cable.
     *
     * @param productId     unique identifier of the cable
     * @param title         title of the cable
     * @param price         unit price of the cable
     * @param stockQuantity quantity available in inventory
     * @param length        cable length in meters, greater than zero
     * @param connectorType type of connector used by the cable (HDMI, USB, optical, etc.)
     * @throws IllegalArgumentException if the length is not greater than zero
     *                                  or the connector type is null or blank
     */
    public Cable(String productId, String title, double price, int stockQuantity,
                 double length, String connectorType) {
        super(productId, title, price, stockQuantity);
        setLength(length);
        setConnectorType(connectorType);
    }

    /**
     * Returns the length of the cable in meters.
     *
     * @return the cable length
     */
    public double getLength() {
        return length;
    }

    /**
     * Sets the cable length in meters.
     *
     * @param length the new cable length, greater than zero
     * @throws IllegalArgumentException if the length is not greater than zero
     */
    public void setLength(double length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Cable length must be greater than zero");
        }
        this.length = length;
    }

    /**
     * Returns the connector type of the cable.
     *
     * @return the connector type
     */
    public String getConnectorType() {
        return connectorType;
    }

    /**
     * Sets the connector type of the cable.
     *
     * @param connectorType the new connector type
     * @throws IllegalArgumentException if the connector type is null or blank
     */
    public void setConnectorType(String connectorType) {
        this.connectorType = validateText(connectorType, "Connector type");
    }

    /**
     * Returns the type of this accessory.
     *
     * @return Cable
     */
    @Override
    public String getAccessoryType() {
        return "Cable";
    }

    /**
     * Builds a full description of the cable combining the common
     * product attributes with its specific characteristics.
     *
     * @return the full description of the cable
     */
    @Override
    public String getFullDescription() {
        return String.format(
                "[Cable] %s | Length: %.2f m | Connector: %s | Price: $%.2f | Stock: %d",
                getTitle(),
                length,
                connectorType,
                getPrice(),
                getStockQuantity());
    }
}
