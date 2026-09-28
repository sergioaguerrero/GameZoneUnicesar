package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents an extended warranty offered by GameZone Unicesar.
 *
 * An extended warranty covers factory defects and accidental damage, lasts 12
 * months from the sale date and has an additional cost equal to 10% of the
 * price of the covered product. It is only granted when the seller requests it
 * at the moment of the sale.
 */
public class ExtendedWarranty extends Warranty {

    private static final int DURATION_IN_MONTHS = 12;
    private static final double COST_PERCENTAGE = 0.10;

    /**
     * Creates a new extended warranty.
     *
     * @param warrantyId unique identifier of the warranty
     * @param product product covered by the warranty
     * @param sale sale in which the product was purchased
     * @param startDate date on which the warranty starts, normally the sale
     * date
     * @throws IllegalArgumentException if the identifier is null or blank, or
     * if the product, the sale or the start date are null
     */
    public ExtendedWarranty(String warrantyId, Product product, Sale sale, LocalDate startDate) {
        super(warrantyId, product, sale, startDate);
    }

    /**
     * Returns the duration of the extended warranty.
     *
     * @return 12 months
     */
    @Override
    public int getDurationInMonths() {
        return DURATION_IN_MONTHS;
    }

    /**
     * Returns the name of this type of warranty.
     *
     * @return Garantía Extendida
     */
    @Override
    public String getWarrantyType() {
        return "Garantía Extendida";
    }

    /**
     * Returns the additional cost of the extended warranty, which is 10% of the
     * price of the covered product.
     *
     * @return the additional cost in pesos
     */
    @Override
    public double getAdditionalCost() {
        return getProduct().getPrice() * COST_PERCENTAGE;
    }
}
