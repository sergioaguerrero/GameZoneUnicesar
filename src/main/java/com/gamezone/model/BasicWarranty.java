package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents a basic warranty granted by GameZone Unicesar.
 *
 * A basic warranty covers only factory defects, lasts 6 months from the sale
 * date and has no additional cost for the customer. It is generated
 * automatically for every console included in a sale.
 */
public class BasicWarranty extends Warranty {

    private static final int DURATION_IN_MONTHS = 6;

    /**
     * Creates a new basic warranty.
     *
     * @param warrantyId unique identifier of the warranty
     * @param product product covered by the warranty
     * @param sale sale in which the product was purchased
     * @param startDate date on which the warranty starts, normally the sale
     * date
     * @throws IllegalArgumentException if the identifier is null or blank, or
     * if the product, the sale or the start date are null
     */
    public BasicWarranty(String warrantyId, Product product, Sale sale, LocalDate startDate) {
        super(warrantyId, product, sale, startDate);
    }

    /**
     * Returns the duration of the basic warranty.
     *
     * @return 6 months
     */
    @Override
    public int getDurationInMonths() {
        return DURATION_IN_MONTHS;
    }

    /**
     * Returns the name of this type of warranty.
     *
     * @return Garantía Básica
     */
    @Override
    public String getWarrantyType() {
        return "Garantía Básica";
    }

    /**
     * Returns the additional cost of the basic warranty, which is always zero.
     *
     * @return 0.0
     */
    @Override
    public double getAdditionalCost() {
        return 0.0;
    }
}
