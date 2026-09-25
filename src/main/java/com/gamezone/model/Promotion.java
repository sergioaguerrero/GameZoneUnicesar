package com.gamezone.model;

import java.time.LocalDate;

/**
 * Abstract base class that represents a promotional discount campaign
 * commercialized by GameZone Unicesar.
 *
 * Promotion holds the attributes and behavior common to every promotion
 * type: identifier, name and the validity period. Each promotion is only
 * applicable while the current date falls within that period.
 *
 * Concrete subclasses must implement {@link #calculateDiscount(Sale)} with
 * their own calculation rule, so the rest of the system can compute the
 * discount of any promotion without knowing its concrete type.
 */
public abstract class Promotion {

    private String id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Creates a new promotion.
     *
     * @param id        unique identifier of the promotion
     * @param name      name of the promotion
     * @param startDate first date on which the promotion is valid
     * @param endDate   last date on which the promotion is valid
     * @throws IllegalArgumentException if the identifier or the name are
     *                                  null or blank, if either date is null,
     *                                  or if the start date is after the end date
     */
    public Promotion(String id, String name, LocalDate startDate, LocalDate endDate) {
        setId(id);
        setName(name);
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date must not be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must not be after end date");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the unique identifier of the promotion.
     *
     * @return the promotion id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the promotion.
     *
     * @param id the new promotion id
     * @throws IllegalArgumentException if the id is null or blank
     */
    public void setId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Promotion id must not be null or blank");
        }
        this.id = id.trim();
    }

    /**
     * Returns the name of the promotion.
     *
     * @return the promotion name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the promotion.
     *
     * @param name the new promotion name
     * @throws IllegalArgumentException if the name is null or blank
     */
    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Promotion name must not be null or blank");
        }
        this.name = name.trim();
    }

    /**
     * Returns the first date on which the promotion is valid.
     *
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Sets the first date on which the promotion is valid.
     *
     * @param startDate the new start date
     * @throws IllegalArgumentException if the date is null or is after the end date
     */
    public void setStartDate(LocalDate startDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date must not be null");
        }
        if (endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must not be after end date");
        }
        this.startDate = startDate;
    }

    /**
     * Returns the last date on which the promotion is valid.
     *
     * @return the end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Sets the last date on which the promotion is valid.
     *
     * @param endDate the new end date
     * @throws IllegalArgumentException if the date is null or is before the start date
     */
    public void setEndDate(LocalDate endDate) {
        if (endDate == null) {
            throw new IllegalArgumentException("End date must not be null");
        }
        if (startDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must not be before start date");
        }
        this.endDate = endDate;
    }

    /**
     * Checks whether this promotion is valid on the given date.
     *
     * @param date the date to check
     * @return true if the date falls within the validity period (inclusive), false otherwise
     */
    public boolean isActive(LocalDate date) {
        return date != null && !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Calculates the discount, in pesos, that this promotion would grant to
     * the given sale. Each concrete promotion type applies its own rule.
     *
     * @param sale the sale to evaluate
     * @return the discount amount in pesos, never negative
     */
    public abstract double calculateDiscount(Sale sale);
}
