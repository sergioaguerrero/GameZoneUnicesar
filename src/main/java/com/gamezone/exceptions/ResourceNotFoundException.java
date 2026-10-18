package com.gamezone.exceptions;

/**
 * Thrown when a resource (product, customer, seller, sale, etc.) is searched by
 * its identifier and does not exist in the system.
 */
public class ResourceNotFoundException extends GameZoneException {

    private static final long serialVersionUID = 1L;

    /**
     * Error code for this category.
     */
    public static final String ERROR_CODE = "RESOURCE_NOT_FOUND";

    private final String resourceType;
    private final String resourceId;

    /**
     * Creates the exception with a descriptive Spanish message, for example "El
     * producto con identificador P01 no fue encontrado".
     *
     * @param resourceType the resource type in Spanish, without article (e.g.
     * "producto", "cliente", "vendedor", "venta")
     * @param resourceId the identifier that was searched
     */
    public ResourceNotFoundException(String resourceType, String resourceId) {
        super(buildMessage(resourceType, resourceId), ERROR_CODE);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /**
     * Returns the type of the resource that was not found.
     *
     * @return the resource type
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Returns the identifier that was searched.
     *
     * @return the resource identifier
     */
    public String getResourceId() {
        return resourceId;
    }

    /**
     * Builds the Spanish message using the grammatical gender of the resource
     * type: nouns ending in "a" (venta, persona, garantía) are feminine.
     */
    private static String buildMessage(String resourceType, String resourceId) {
        String type = resourceType == null ? "recurso" : resourceType.trim().toLowerCase();
        boolean feminine = type.endsWith("a");
        String article = feminine ? "La" : "El";
        String found = feminine ? "encontrada" : "encontrado";
        return String.format("%s %s con identificador %s no fue %s",
                article, type, resourceId, found);
    }
}
