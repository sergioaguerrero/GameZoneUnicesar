package com.gamezone.service;

import com.gamezone.model.*;
import com.gamezone.persistence.SaleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains the business rules related to sales management.
 *
 * Accessories were added to the inventory alongside video games and
 * consoles. Since {@link Accessory} extends {@link Product}, every
 * pre-existing rule for a sale (minimum one item, stock validation,
 * automatic inventory update, total calculation) continues to work
 * unchanged for accessories: the only additive change was delegating stock
 * updates to {@link AccessoryService} when the sold item is an accessory,
 * and combining products and accessories into a single master list when
 * resolving persisted sales.
 *
 * The warranty module was integrated additively: registering a sale now
 * automatically generates a basic warranty for every console sold and,
 * when requested, an extended warranty whose additional cost is added to the
 * sale total. All warranty rules live in {@link WarrantyService}; this class
 * only decides when to invoke them.
 */
public class SaleService {
    private final SaleRepository saleRepository;
    private final PersonService personService;
    private final ProductService productService;
    private final AccessoryService accessoryService;
    private final PromotionService promotionService;
    private WarrantyService warrantyService;

    /**
     * Initializes the SaleService with the required repositories and services.
     *
     * @param saleRepository   the repository handling sale persistence
     * @param personService    the service handling customers and sellers
     * @param productService   the service handling products and inventory
     * @param accessoryService the service handling accessories and their inventory
     * @param promotionService the service handling promotions and their inventory
     */
    public SaleService(SaleRepository saleRepository, PersonService personService,
                       ProductService productService, AccessoryService accessoryService,
                       PromotionService promotionService) {
        this.saleRepository = saleRepository;
        this.personService = personService;
        this.productService = productService;
        this.accessoryService = accessoryService;
        this.promotionService = promotionService;
    }

    /**
     * Injects the warranty service. It is set after construction (setter
     * injection) because {@code WarrantyRepository} needs this service to
     * resolve the sales referenced by persisted warranties, so a constructor
     * parameter would create a circular dependency.
     *
     * @param warrantyService the service handling warranties
     */
    public void setWarrantyService(WarrantyService warrantyService) {
        this.warrantyService = warrantyService;
    }

    /**
     * Registers a new sale without any extended warranty. Kept so existing
     * callers keep working; equivalent to calling
     * {@link #registerSale(String, String, List, List)} with no extended
     * warranty requested.
     *
     * @param customerId the ID of the customer making the purchase
     * @param sellerId   the ID of the seller handling the transaction
     * @param items      the list of items to be purchased
     * @return true if the sale was successfully registered, false otherwise
     */
    public boolean registerSale(String customerId, String sellerId, List<SaleItem> items) {
        return registerSale(customerId, sellerId, items, null);
    }

    /**
     * Registers a new sale by validating the actors, verifying product stock,
     * creating the transaction, generating the warranties, decreasing stock,
     * and saving it to the repository.
     *
     * A basic warranty (free) is generated automatically for every
     * {@link Console} in the sale. For each console whose id is listed in
     * {@code productIdsWithExtendedWarranty}, an extended warranty is also
     * generated and its additional cost is added to the sale total. Video
     * games and accessories never receive a warranty.
     *
     * @param customerId                     the ID of the customer making the purchase
     * @param sellerId                       the ID of the seller handling the transaction
     * @param items                          the list of items to be purchased
     * @param productIdsWithExtendedWarranty ids of the consoles that must get an
     *                                       extended warranty; {@code null} or empty
     *                                       means none
     * @return true if the sale was successfully registered, false otherwise
     */
    public boolean registerSale(String customerId, String sellerId, List<SaleItem> items,
                                List<String> productIdsWithExtendedWarranty) {
        Customer customer = personService.findCustomer(customerId);
        Seller seller = personService.findSeller(sellerId);

        if (customer == null || seller == null) {
            System.err.println("Customer or seller don't exist");
            return false;
        }

        for (SaleItem item : items) {
            String productId = item.getProduct().getProductId();
            boolean enoughStock;
            if (item.getProduct() instanceof Accessory) {
                Accessory accessory = accessoryService.findById(productId).orElse(null);
                enoughStock = accessory != null && accessory.hasEnoughStock(item.getQuantity());
            } else {
                enoughStock = productService.hasEnoughStock(productId, item.getQuantity());
            }
            if (!enoughStock) {
                System.err.println("Insufficient stock for product with ID: " + productId);
                return false;
            }
        }

        List<String> extendedIds = productIdsWithExtendedWarranty != null
                ? productIdsWithExtendedWarranty : new ArrayList<>();
        for (String extendedId : extendedIds) {
            if (!isConsoleInItems(extendedId, items)) {
                System.err.println("Extended warranty is only available for consoles included in the sale: "
                        + extendedId);
                return false;
            }
        }

        List<Sale> existingSales = listAllSales();
        Sale newSale = new Sale(nextSaleId(existingSales), LocalDate.now(), customer, seller);
        for (SaleItem item : items) {
            newSale.addItem(item);
        }

        Promotion bestPromotion = promotionService.findBestPromotionFor(newSale);
        if (bestPromotion != null) {
            double discount = bestPromotion.calculateDiscount(newSale);
            newSale.setAppliedPromotionName(bestPromotion.getName());
            newSale.setDiscountAmount(discount);
        }

        try {
            newSale.register();
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Error registering: " + e.getMessage());
            return false;
        }

        applyWarranties(newSale, extendedIds);

        existingSales.add(newSale);
        saleRepository.saveSales(existingSales);

        for (SaleItem item : items) {
            String productId = item.getProduct().getProductId();
            if (item.getProduct() instanceof Accessory) {
                accessoryService.updateStock(productId, item.getQuantity());
            } else {
                productService.updateStock(productId, item.getQuantity());
            }
        }

        System.out.println("Sale registered successfully");
        return true;
    }

    /**
     * Generates the warranties of a sale. Every console gets a free basic
     * warranty; consoles whose id is in {@code extendedIds} also get an
     * extended warranty, whose additional cost is added to the sale.
     * The type check uses {@code instanceof} because only the real type of
     * the product (Console vs VideoGame vs Accessory) decides the rule.
     *
     * @param sale        the sale that was just created
     * @param extendedIds ids of the consoles that must get an extended warranty
     */
    private void applyWarranties(Sale sale, List<String> extendedIds) {
        if (warrantyService == null) {
            throw new IllegalStateException("WarrantyService has not been configured in SaleService");
        }
        double extendedCost = 0.0;
        for (SaleItem item : sale.getItems()) {
            Product product = item.getProduct();
            if (product instanceof Console) {
                warrantyService.assignBasicWarranty(product, sale, sale.getDate());
                if (extendedIds.contains(product.getProductId())) {
                    ExtendedWarranty extended =
                            warrantyService.assignExtendedWarranty(product, sale, sale.getDate());
                    extendedCost += extended.getAdditionalCost();
                }
            }
        }
        sale.setExtendedWarrantyCost(extendedCost);
    }

    /**
     * Checks whether the given product id belongs to a console included in
     * the given items.
     *
     * @param productId the product id to look for
     * @param items     the items of the sale
     * @return true if a console with that id is among the items
     */
    private boolean isConsoleInItems(String productId, List<SaleItem> items) {
        for (SaleItem item : items) {
            if (item.getProduct() instanceof Console
                    && item.getProduct().getProductId().equals(productId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generates the identifier of the next sale, following the pattern
     * {@code SALE-0001}, one above the highest existing sequence number.
     *
     * @param existingSales the sales already registered
     * @return a new unique sale id
     */
    private String nextSaleId(List<Sale> existingSales) {
        int max = 0;
        for (Sale sale : existingSales) {
            String id = sale.getId();
            if (id != null && id.startsWith("SALE-")) {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(5)));
                } catch (NumberFormatException ignored) {
                    // ids with a custom format do not affect the sequence
                }
            }
        }
        return String.format("SALE-%04d", max + 1);
    }

    /**
     * Builds the combined master list of every sellable item (traditional
     * products and accessories), used to resolve the items referenced by a
     * persisted sale. Accessories can be included here because
     * {@link Accessory} extends {@link Product}.
     *
     * @return the combined list of products and accessories
     */
    private List<Product> allSellableItems() {
        List<Product> items = new ArrayList<>(productService.listAllProducts());
        items.addAll(accessoryService.listAllAccessories());
        return items;
    }

    /**
     * Loads and returns the complete history of registered sales, resolving
     * each sale's customer, seller and products against the current
     * in-memory data held by {@link PersonService} and {@link ProductService}.
     *
     * @return the list of all sales currently persisted
     */
    public List<Sale> listAllSales() {
        List<Customer> customers = personService.listCustomer();
        List<Seller> sellers = personService.listSeller();
        List<Product> products = allSellableItems();
        return saleRepository.loadSales(customers, sellers, products);
    }

    /**
     * Filters the complete sales history to the sales made by a specific
     * customer.
     *
     * @param customerId the id of the customer whose sales are requested
     * @return the list of sales associated with the given customer; empty if
     * none are found
     */
    public List<Sale> listSalesByCustomer(String customerId) {
        List<Sale> result = new ArrayList<>();
        for (Sale sale : listAllSales()) {
            if (sale.getCustomer().getId().equals(customerId)) {
                result.add(sale);
            }
        }
        return result;
    }

    /**
     * Filters the complete sales history to the sales handled by a specific
     * seller.
     *
     * @param sellerId the id of the seller whose sales are requested
     * @return the list of sales associated with the given seller; empty if
     * none are found
     */
    public List<Sale> listSalesBySeller(String sellerId) {
        List<Sale> result = new ArrayList<>();
        for (Sale sale : listAllSales()) {
            if (sale.getSeller().getId().equals(sellerId)) {
                result.add(sale);
            }
        }
        return result;
    }
}