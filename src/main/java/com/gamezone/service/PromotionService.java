package com.gamezone.service;

import com.gamezone.model.*;
import com.gamezone.persistence.PromotionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PromotionService {
    private final PromotionRepository promotionRepository;
    private List<Promotion> promotions;

    public PromotionService(PromotionRepository promotionRepository, List<Promotion> promotions) {
        this.promotionRepository = promotionRepository;
        this.promotions = promotions;
    }

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

    public List<Promotion> listAllPromotions() {
        return new ArrayList<>(promotions);
    }

    public Optional<Promotion> findById(String id) {
        return promotions.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst();
    }

    public boolean deletePromotion(String id) {
        boolean removed = promotions.removeIf(p -> p.getId().equals(id));
        if (removed) {
            persist();
        }
        return removed;
    }

    private void validateNewPromotionId(String id) {
        if (findById(id).isPresent()) {
            throw new IllegalArgumentException(
                    "A promotion with id " + id + " already exists");
        }
    }

    private void persist() {
        promotionRepository.saveAll(promotions);
    }

}
