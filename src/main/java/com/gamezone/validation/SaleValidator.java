package com.gamezone.validation;
 
import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.exceptions.ResourceType;
import com.gamezone.model.Customer;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import java.util.List;
 
/**
 * Validation rules for the sale module. Services call these methods before
 * running any business operation; each method throws the matching custom
 * exception when a rule is not met.
 */
public final class SaleValidator {
 
    /**
     * Prevents instantiation: this class only exposes static methods.
     */
    private SaleValidator() {
    }
 
    /**
     * Validates the data required to register a sale.
     * <p>
     * The products parameter is declared as {@code List<? extends Product>} so
     * it also accepts lists of a specific product type, such as accessories.
     *
     * @param customer the customer of the sale, must not be null
     * @param seller the seller of the sale, must not be null
     * @param products the products of the sale, must contain at least one
     * @throws InvalidDataException if the customer or the seller is missing
     * @throws BusinessRuleException if the sale has no products
     */
    public static void validateSaleData(Customer customer, Seller seller, List<? extends Product> products) {
        if (customer == null) {
            throw new InvalidDataException("cliente", "la venta debe tener un cliente");
        }
        if (seller == null) {
            throw new InvalidDataException("vendedor", "la venta debe tener un vendedor");
        }
        if (products == null || products.isEmpty()) {
            throw new BusinessRuleException("La venta debe contener al menos un producto");
        }
    }
 
    /**
     * Validates that a sale found by its identifier exists.
     *
     * @param sale the sale returned by the search, possibly null
     * @param saleId the identifier that was searched
     * @throws ResourceNotFoundException if the sale is null
     */
    public static void validateSaleExists(Sale sale, String saleId) {
        if (sale == null) {
            throw new ResourceNotFoundException(ResourceType.SALE, saleId);
        }
    }
}