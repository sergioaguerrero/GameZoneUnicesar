package com.gamezone.service;

import com.gamezone.model.*;
import com.gamezone.persistence.PromotionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Coordinates the business rules for managing {@link Promotion} instances
 * (bulk purchase discounts, category discounts, and percentage discounts),
 * delegating persistence to a {@link PromotionRepository}.
 */
public class PromotionService {
    private final PromotionRepository promotionRepository;
    private List<Promotion> promotions;

    /**
     * Creates a service backed by the given repository and initial list of
     * promotions.
     *
     * @param promotionRepository the repository used to persist changes
     * @param promotions the initial in-memory list of promotions
     */
    public PromotionService(PromotionRepository promotionRepository, List<Promotion> promotions) {
        this.promotionRepository = promotionRepository;
        this.promotions = promotions;
    }

    /**
     * Registers a new bulk purchase discount and persists the updated list.
     *
     * @param id the unique identifier for the new promotion
     * @param name the display name of the promotion
     * @param startDate the date the promotion becomes active
     * @param endDate the date the promotion expires
     * @param minimumQuantity the minimum purchase quantity required to qualify
     * @param percentage the discount percentage applied
     * @return the newly created {@link BulkPurchaseDiscount}
     * @throws IllegalArgumentException if a promotion with the given id
     * already exists
     */
    public BulkPurchaseDiscount registerBulk(String id, String name,
                                             LocalDate startDate, LocalDate endDate,
                                             int minimumQuantity, double percentage) {
        validateNewPromotionId(id);
        BulkPurchaseDiscount bulk = new BulkPurchaseDiscount(id,name,startDate,endDate,
                minimumQuantity,percentage);
        promotions.add(bulk);
        persist();
        return bulk;
    }

    /**
     * Registers a new category discount and persists the updated list.
     *
     * @param id the unique identifier for the new promotion
     * @param name the display name of the promotion
     * @param startDate the date the promotion becomes active
     * @param endDate the date the promotion expires
     * @param percentage the discount percentage applied
     * @param targetCategory the product category the discount applies to
     * @return the newly created {@link CategoryDiscount}
     * @throws IllegalArgumentException if a promotion with the given id
     * already exists
     */
    public CategoryDiscount registerCategory(String id, String name,
                                             LocalDate startDate, LocalDate endDate,
                                             double percentage,String targetCategory) {
        validateNewPromotionId(id);
        CategoryDiscount category = new CategoryDiscount(id,name,startDate,endDate,
                percentage,targetCategory);
        promotions.add(category);
        persist();
        return category;
    }

    /**
     * Registers a new flat percentage discount and persists the updated list.
     *
     * @param id the unique identifier for the new promotion
     * @param name the display name of the promotion
     * @param startDate the date the promotion becomes active
     * @param endDate the date the promotion expires
     * @param percentage the discount percentage applied
     * @return the newly created {@link PercentageDiscount}
     * @throws IllegalArgumentException if a promotion with the given id
     * already exists
     */
    public PercentageDiscount registerPercentage(String id, String name,
                                                 LocalDate startDate, LocalDate endDate,
                                                 double percentage) {
        validateNewPromotionId(id);
        PercentageDiscount p = new PercentageDiscount(id,name,startDate,endDate,
                percentage);
        promotions.add(p);
        persist();
        return p;
    }

    /**
     * Returns a new list containing every registered promotion.
     *
     * @return a copy of the list of all promotions
     */
    public List<Promotion> listAllPromotions() {
        return new ArrayList<>(promotions);
    }

    /**
     * Looks up a promotion by its id.
     *
     * @param id the id to search for
     * @return an {@link Optional} containing the matching promotion, or empty
     * if none was found
     */
    public Optional<Promotion> findById(String id) {
        return promotions.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    /**
     * Removes the promotion with the given id, persisting the change only if
     * a matching promotion was found.
     *
     * @param id the id of the promotion to delete
     * @return true if a promotion was found and removed, false otherwise
     */
    public boolean deletePromotion(String id) {
        boolean removed = promotions.removeIf(p -> p.getId().equals(id));
        if (removed) {
            persist();
        }
        return removed;
    }

    /**
     * Validates that no existing promotion already uses the given id.
     *
     * @param id the id to validate
     * @throws IllegalArgumentException if a promotion with this id already exists
     */
    private void validateNewPromotionId(String id) {
        if (findById(id).isPresent()) {
            throw new IllegalArgumentException(
                    "A promotion with id " + id + " already exists");
        }
    }

    /**
     * Persists the current in-memory list of promotions via the repository.
     */
    private void persist() {
        promotionRepository.saveAll(promotions);
    }

}