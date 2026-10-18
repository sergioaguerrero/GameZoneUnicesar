package com.gamezone.exceptions;

/**
 * Thrown when an operation violates a business rule: insufficient stock,
 * duplicated customer, a sale without products, among others.
 */
public class BusinessRuleException extends GameZoneException {

    private static final long serialVersionUID = 1L;

    /** Error code for this category. */
    public static final String ERROR_CODE = "BUSINESS_RULE";

    /**
     * Creates the exception describing the violated rule.
     *
     * @param message the description of the violated rule, in Spanish
     */
    public BusinessRuleException(String message) {
        super(message, ERROR_CODE);
    }
}