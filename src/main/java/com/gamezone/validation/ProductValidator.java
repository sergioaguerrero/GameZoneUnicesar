package com.gamezone.validation;
 
import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.exceptions.ResourceType;
import com.gamezone.model.Product;
 
/**
 * Validation rules for the product module. Services call these methods before
 * running any business operation; each method throws the matching custom
 * exception when a rule is not met.
 */
public final class ProductValidator {
 
    /**
     * Prevents instantiation: this class only exposes static methods.
     */
    private ProductValidator() {
    }
 
    /**
     * Validates the basic data of a product.
     *
     * @param id the product identifier, must not be null or blank
     * @param title the product title, must not be null or blank
     * @param price the product price, must be greater than zero
     * @param stock the stock quantity, must not be negative
     * @throws InvalidDataException if any value is invalid
     */
    public static void validateProductData(String id, String title, double price, int stock) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("identificador", "no puede estar vacío");
        }
        if (title == null || title.isBlank()) {
            throw new InvalidDataException("título", "no puede estar vacío");
        }
        if (price <= 0) {
            throw new InvalidDataException("precio", "debe ser mayor que cero");
        }
        if (stock < 0) {
            throw new InvalidDataException("stock", "no puede ser negativo");
        }
    }
 
    /**
     * Validates that a product has enough stock for the requested quantity.
     *
     * @param product the product to check, must not be null
     * @param requestedQuantity the quantity requested
     * @throws BusinessRuleException if the available stock is insufficient
     */
    public static void validateStockAvailability(Product product, int requestedQuantity) {
        if (!product.hasEnoughStock(requestedQuantity)) {
            throw new BusinessRuleException(String.format(
                    "Stock insuficiente para el producto %s (disponible: %d, solicitado: %d)",
                    product.getTitle(), product.getStockQuantity(), requestedQuantity));
        }
    }
 
    /**
     * Validates that a product found by its identifier exists.
     *
     * @param product the product returned by the search, possibly null
     * @param productId the identifier that was searched
     * @throws ResourceNotFoundException if the product is null
     */
    public static void validateProductExists(Product product, String productId) {
        if (product == null) {
            throw new ResourceNotFoundException(ResourceType.PRODUCT, productId);
        }
    }
}