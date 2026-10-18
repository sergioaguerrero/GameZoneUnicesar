package com.gamezone.exceptions;

/**
 * Thrown when reading or writing a data file fails: corrupted file, permission
 * denied, full disk, among others.
 * <p>
 * The original exception is kept as the cause so the technical trace is not
 * lost, while the user interface shows only a generic message.
 */
public final class PersistenceException extends GameZoneException {

    private static final long serialVersionUID = 1L;

    /**
     * Error code for this category.
     */
    public static final String ERROR_CODE = "PERSISTENCE";

    /**
     * Creates the exception with a contextual message and its original cause.
     *
     * @param message the message, including the file and the operation (read or
     * write) that failed
     * @param cause the original exception, usually an
     * {@link java.io.IOException}
     */
    public PersistenceException(String message, Throwable cause) {
        super(message, ERROR_CODE);
        initCause(cause);
    }
}
