package com.gamezone.exceptions;

/**
 * Thrown when an input value does not meet format, range or type criteria: a
 * negative price, a negative quantity, an empty text where content is expected,
 * among others.
 */
public class InvalidDataException extends GameZoneException {

    private static final long serialVersionUID = 1L;

    /**
     * Error code for this category.
     */
    public static final String ERROR_CODE = "INVALID_DATA";

    private final String fieldName;

    /**
     * Creates the exception with a descriptive Spanish message, for example "El
     * campo 'precio' no es válido: debe ser mayor que cero".
     *
     * @param fieldName the name of the invalid field, in Spanish
     * @param description the description of the problem, in Spanish
     */
    public InvalidDataException(String fieldName, String description) {
        super(String.format("El campo '%s' no es válido: %s", fieldName, description), ERROR_CODE);
        this.fieldName = fieldName;
    }

    /**
     * Returns the name of the invalid field.
     *
     * @return the field name
     */
    public String getFieldName() {
        return fieldName;
    }
}
