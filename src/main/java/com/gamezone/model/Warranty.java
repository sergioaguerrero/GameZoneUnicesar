package com.gamezone.model;

import java.time.LocalDate;

/**
 * Abstract base class that represents a warranty granted for a product sold by
 * GameZone Unicesar.
 *
 * Warranty holds the attributes and behavior common to every warranty type:
 * identifier, covered product, associated sale and validity period. Each
 * concrete subclass defines its own duration, type name and additional cost, so
 * the rest of the system can work with any warranty without knowing its
 * concrete type.
 *
 * The end date is calculated automatically in the constructor from the start
 * date and the duration declared by the subclass.
 */
public abstract class Warranty {

    private String warrantyId;
    private Product product;
    private Sale sale;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Creates a new warranty and calculates its end date.
     *
     * The end date is the start date plus the number of months returned by
     * {@link #getDurationInMonths()}. Subclasses must implement that method so
     * that it returns a constant, since it is invoked while this constructor is
     * still running.
     *
     * @param warrantyId unique identifier of the warranty
     * @param product product covered by the warranty
     * @param sale sale in which the product was purchased
     * @param startDate date on which the warranty starts, normally the sale
     * date
     * @throws IllegalArgumentException if the identifier is null or blank, or
     * if the product, the sale or the start date are null
     */
    public Warranty(String warrantyId, Product product, Sale sale, LocalDate startDate) {
        if (warrantyId == null || warrantyId.isBlank()) {
            throw new IllegalArgumentException("Warranty id must not be null or blank");
        }
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null");
        }
        if (sale == null) {
            throw new IllegalArgumentException("Sale must not be null");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start date must not be null");
        }
        this.warrantyId = warrantyId.trim();
        this.product = product;
        this.sale = sale;
        this.startDate = startDate;
        this.endDate = startDate.plusMonths(getDurationInMonths());
    }

    /**
     * Returns the unique identifier of the warranty.
     *
     * @return the warranty id
     */
    public String getWarrantyId() {
        return warrantyId;
    }

    /**
     * Returns the product covered by the warranty.
     *
     * @return the covered product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Returns the sale in which the covered product was purchased.
     *
     * @return the associated sale
     */
    public Sale getSale() {
        return sale;
    }

    /**
     * Returns the date on which the warranty starts.
     *
     * @return the start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the date on which the warranty ends.
     *
     * @return the end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Returns the duration of this type of warranty, in months.
     *
     * @return the duration in months
     */
    public abstract int getDurationInMonths();

    /**
     * Returns the name of this type of warranty.
     *
     * @return the warranty type name
     */
    public abstract String getWarrantyType();

    /**
     * Returns the additional cost that this warranty adds to the sale.
     *
     * @return the additional cost in pesos
     */
    public abstract double getAdditionalCost();
}
