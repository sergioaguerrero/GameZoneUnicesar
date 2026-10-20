package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Person;
import java.util.List;

/**
 * Validation rules for the person module (customers and sellers). Services call
 * these methods before running any business operation; each method throws the
 * matching custom exception when a rule is not met.
 */
public final class PersonValidator {

    /**
     * Prevents instantiation: this class only exposes static methods.
     */
    private PersonValidator() {
    }

    /**
     * Validates the basic data of a person.
     * <p>
     * Sellers have no email, so a {@code null} email is not validated. Any
     * non-null email must not be blank and must have a reasonable format: text
     * before an "@" and a "." in the domain part.
     *
     * @param id the identifier, must not be null or blank
     * @param name the name, must not be null or blank
     * @param email the email, or {@code null} when the person has none
     * @param phone the phone, must not be null or blank
     * @throws InvalidDataException if any value is invalid
     */
    public static void validatePersonData(String id, String name, String email, String phone) {
        if (id == null || id.isBlank()) {
            throw new InvalidDataException("identificador", "no puede estar vacío");
        }
        if (name == null || name.isBlank()) {
            throw new InvalidDataException("nombre", "no puede estar vacío");
        }
        if (phone == null || phone.isBlank()) {
            throw new InvalidDataException("teléfono", "no puede estar vacío");
        }
        if (email != null && !isReasonableEmail(email)) {
            throw new InvalidDataException("correo", "debe tener un formato válido (ejemplo: nombre@dominio.com)");
        }
    }

    /**
     * Validates that a person found by its identifier exists.
     *
     * @param person the person returned by the search, possibly null
     * @param personId the identifier that was searched
     * @throws ResourceNotFoundException if the person is null
     */
    public static void validatePersonExists(Person person, String personId) {
        if (person == null) {
            throw new ResourceNotFoundException("persona", personId);
        }
    }

    /**
     * Validates that no registered person already uses the given identifier.
     * <p>
     * The parameter is declared as {@code List<? extends Person>} so it accepts
     * both {@code List<Customer>} and {@code List<Seller>}.
     *
     * @param existing the persons already registered
     * @param newId the identifier of the person to register
     * @throws BusinessRuleException if the identifier is already in use
     */
    public static void validateUniquePerson(List<? extends Person> existing, String newId) {
        for (Person person : existing) {
            if (person.getId().equals(newId)) {
                throw new BusinessRuleException(
                        "Ya existe una persona registrada con el identificador " + newId);
            }
        }
    }

    /**
     * Checks that an email is not blank, has text before the "@", and has a "."
     * in the domain part that is neither its first nor its last character.
     */
    private static boolean isReasonableEmail(String email) {
        String value = email.trim();
        int at = value.indexOf('@');
        if (at <= 0 || at != value.lastIndexOf('@')) {
            return false;
        }
        String domain = value.substring(at + 1);
        int dot = domain.lastIndexOf('.');
        return dot > 0 && dot < domain.length() - 1;
    }
}
