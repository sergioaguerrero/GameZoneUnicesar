package com.gamezone.service;

import com.gamezone.model.*;
import com.gamezone.persistence.WarrantyRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Contains the business rules related to warranty management, including
 * basic and extended warranties creation, filtering, and retrieval.
 *
 * @version 1.2
 * @since 2026-09-28
 */
public class WarrantyService {
    private final WarrantyRepository warrantyRepository;
    private List<Warranty> warranties;

    /**
     * Initializes the WarrantyService with the required repository and initial warranties list.
     *
     * @param warrantyRepository the repository handling warranty persistence
     */
    public WarrantyService(WarrantyRepository warrantyRepository) {
        this.warrantyRepository = warrantyRepository;
        this.warranties = warrantyRepository.loadAll();
    }

    /**
     * Assigns and creates a new basic warranty for a specific product and sale.
     *
     * @param product   the product associated with the warranty
     * @param sale      the sale associated with the warranty
     * @param startdate the start date of the warranty
     * @return the created BasicWarranty object
     */
    public BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startdate){
        String Id = "WAT-" + UUID.randomUUID().toString().substring(0, 8);
        BasicWarranty warranty = new BasicWarranty(Id, product, sale, startdate);
        warranties.add(warranty);
        persist();
        return warranty;
    }

    /**
     * Assigns and creates a new extended warranty for a specific product and sale.
     *
     * @param product   the product associated with the warranty
     * @param sale      the sale associated with the warranty
     * @param startdate the start date of the warranty
     * @return the created ExtendedWarranty object
     */
    public ExtendedWarranty assignExtendedWarranty(Product product, Sale sale, LocalDate startdate){
        String Id = "WAT-" + UUID.randomUUID().toString().substring(0, 8);
        ExtendedWarranty warranty = new ExtendedWarranty(Id, product, sale, startdate);
        warranties.add(warranty);
        persist();
        return warranty;
    }

    /**
     * Finds a warranty by its unique ID.
     *
     * @param id the warranty ID to search for
     * @return an Optional containing the found Warranty, or empty if not found
     */
    public Optional<Warranty> findById(String id) {
        return warranties.stream()
                .filter(p -> p.getWarrantyId().equals(id))
                .findFirst();
    }

    /**
     * Finds the warranty associated with a specific product in a given sale,
     * or returns null if it does not exist.
     *
     * @param productId the ID of the product
     * @param saleId    the ID of the sale
     * @return the matching Warranty object, or null if none is found
     */
    public Warranty findWarrantyByProduct(String productId, String saleId) {
        for (Warranty warranty : warranties) {
            boolean matchesProduct = warranty.getProduct() != null &&
                    warranty.getProduct().getProductId().equals(productId);
            boolean matchesSale = warranty.getSale() != null &&
                    warranty.getSale().getId().equals(saleId);
            if (matchesProduct && matchesSale) {
                return warranty;
            }
        }
        return null;
    }

    /**
     * Returns all registered warranties.
     *
     * @return a list containing all Warranty objects
     */
    public List<Warranty> listAllWarranties() {
        return new ArrayList<>(warranties);
    }

    /**
     * Returns all warranties that are currently active/valid on the current date.
     *
     * @return a list of currently active warranties
     */
    public List<Warranty> listActiveWarranties() {
        LocalDate today = LocalDate.now();
        List<Warranty> activeWarranties = new ArrayList<>();

        for (Warranty warranty : warranties) {
            if (warranty.isActive(today)) {
                activeWarranties.add(warranty);
            }
        }
        return activeWarranties;
    }

    /**
     * Returns warranties whose end date falls within the next "daysAhead" days.
     *
     * @param daysAhead the number of days ahead to check for expiration
     * @return a list of warranties expiring soon
     */
    public List<Warranty> listWarrantiesExpiringSoon(int daysAhead) {
        LocalDate today = LocalDate.now();
        LocalDate limitDate = today.plusDays(daysAhead);
        List<Warranty> expiringSoon = new ArrayList<>();

        for (Warranty warranty : warranties) {
            LocalDate endDate = warranty.getEndDate();
            if (!endDate.isBefore(today) && !endDate.isAfter(limitDate)) {
                expiringSoon.add(warranty);
            }
        }
        return expiringSoon;
    }

    /**
     * Persists the current list of warranties using the repository.
     */
    private void persist() {
        warrantyRepository.saveAll(warranties);
    }
}