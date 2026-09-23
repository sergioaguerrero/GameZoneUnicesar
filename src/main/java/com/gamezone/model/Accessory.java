package com.gamezone.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class that represents an accessory commercialized by
 * GameZone Unicesar.
 *
 * Accessory extends {@link Product} because accessories share the common
 * commercial attributes already defined by the product hierarchy, such as
 * product identifier, title, price and stock quantity. It also keeps the list
 * of consoles with which the accessory is compatible, so every accessory type
 * can be queried in the same way.
 *
 * Concrete accessory subclasses must provide their own accessory type and
 * full description.
 */
public abstract class Accessory extends Product {

    private List<Console> compatibleConsoles;

    /**
     * Creates a new accessory with the given common product attributes.
     *
     * @param productId     unique identifier of the accessory
     * @param title         title of the accessory
     * @param price         unit price of the accessory
     * @param stockQuantity quantity available in inventory
     */
    public Accessory(String productId, String title, double price, int stockQuantity) {
        super(productId, title, price, stockQuantity);
        this.compatibleConsoles = new ArrayList<>();
    }

    /**
     * Returns the list of consoles compatible with this accessory.
     *
     * The returned list is read-only. Use {@link #addCompatibleConsole(Console)}
     * and {@link #removeCompatibleConsole(Console)} to modify it.
     *
     * @return the compatible consoles
     */
    public List<Console> getCompatibleConsoles() {
        return Collections.unmodifiableList(compatibleConsoles);
    }

    /**
     * Replaces the list of consoles compatible with this accessory.
     *
     * The given list is copied, so later changes to it do not affect the
     * accessory. Null and duplicated consoles are ignored.
     *
     * @param compatibleConsoles the new list of compatible consoles
     */
    public void setCompatibleConsoles(List<Console> compatibleConsoles) {
        this.compatibleConsoles = new ArrayList<>();
        if (compatibleConsoles != null) {
            for (Console console : compatibleConsoles) {
                addCompatibleConsole(console);
            }
        }
    }

    /**
     * Adds a console to the list of compatible consoles.
     *
     * A null console or a console that is already registered will not
     * be added. Consoles are compared by their product identifier.
     *
     * @param console console to add
     */
    public void addCompatibleConsole(Console console) {
        if (console != null && !containsConsole(console)) {
            compatibleConsoles.add(console);
        }
    }

    /**
     * Removes a console from the list of compatible consoles.
     *
     * @param console console to remove
     * @return true if the console was removed, false otherwise
     */
    public boolean removeCompatibleConsole(Console console) {
        if (console == null) {
            return false;
        }
        return compatibleConsoles.removeIf(
                registered -> registered.getProductId().equals(console.getProductId()));
    }

    /**
     * Checks whether this accessory is compatible with the given console.
     *
     * @param console console to check
     * @return true if the console is registered as compatible, false otherwise
     */
    public boolean isCompatibleWith(Console console) {
        return console != null && containsConsole(console);
    }

    /**
     * Returns the specific type of this accessory.
     *
     * @return the accessory type
     */
    public abstract String getAccessoryType();

    /**
     * Validates that a text attribute is neither null nor blank.
     *
     * @param value     text to validate
     * @param fieldName name of the attribute, used in the error message
     * @return the text without leading and trailing spaces
     * @throws IllegalArgumentException if the text is null or blank
     */
    protected static String validateText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be null or blank");
        }
        return value.trim();
    }

    /**
     * Checks whether a console with the same product identifier is already
     * registered as compatible.
     *
     * @param console console to look for
     * @return true if a console with the same identifier is registered
     */
    private boolean containsConsole(Console console) {
        for (Console registered : compatibleConsoles) {
            if (registered.getProductId().equals(console.getProductId())) {
                return true;
            }
        }
        return false;
    }
}