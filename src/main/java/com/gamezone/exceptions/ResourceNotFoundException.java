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
 
    private final ResourceType resourceType;
    private final String resourceId;
 
    /**
     * Creates the exception with a descriptive Spanish message, for example "El
     * producto con identificador P01 no fue encontrado".
     *
     * @param resourceType the type of the resource that was searched
     * @param resourceId the identifier that was searched
     */
    public ResourceNotFoundException(ResourceType resourceType, String resourceId) {
        super(buildMessage(resourceType, resourceId), ERROR_CODE);
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
 
    /**
     * Returns the type of the resource that was not found.
     *
     * @return the resource type
     */
    public ResourceType getResourceType() {
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
     * Builds the Spanish message with the article and participle declared by
     * the resource type.
     */
    private static String buildMessage(ResourceType resourceType, String resourceId) {
        return String.format("%s con identificador %s no fue %s",
                resourceType.getNounWithArticle(), resourceId, resourceType.getParticiple());
    }
}