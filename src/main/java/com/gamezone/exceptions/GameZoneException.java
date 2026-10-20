package com.gamezone.exceptions;

/**
 * Root of the GameZone custom exception hierarchy.
 * <p>
 * It is unchecked (extends {@link RuntimeException}) so that service and
 * repository method signatures do not need {@code throws} clauses, and it is
 * abstract because only its specialized subclasses describe a concrete error
 * category. Each subclass carries an error code that identifies its category.
 */
public abstract class GameZoneException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String errorCode;

    /**
     * Creates an exception with a message and no error code.
     *
     * @param message the detail message, in Spanish, shown to the user
     */
    protected GameZoneException(String message) {
        super(message);
        this.errorCode = null;
    }

    /**
     * Creates an exception with a message and an error code.
     *
     * @param message the detail message, in Spanish, shown to the user
     * @param errorCode the code that identifies the error category
     */
    protected GameZoneException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Returns the code that identifies the error category.
     *
     * @return the error code, or {@code null} if none was provided
     */
    public String getErrorCode() {
        return errorCode;
    }
}
