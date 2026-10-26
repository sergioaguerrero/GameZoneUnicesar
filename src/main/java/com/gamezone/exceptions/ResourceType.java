package com.gamezone.exceptions;
 
/**
 * Types of resource that can be searched by identifier. Each type declares
 * the Spanish noun with its article and the participle that agrees with its
 * grammatical gender, so {@link ResourceNotFoundException} builds correct
 * messages without guessing the gender.
 */
public enum ResourceType {
 
    /** A video game, console or accessory. */
    PRODUCT("El producto", "encontrado"),
    /** A customer or seller. */
    PERSON("La persona", "encontrada"),
    /** A registered sale. */
    SALE("La venta", "encontrada"),
    /** A promotional campaign. */
    PROMOTION("La promoción", "encontrada"),
    /** A product return. */
    RETURN("La devolución", "encontrada"),
    /** A basic or extended warranty. */
    WARRANTY("La garantía", "encontrada");
 
    private final String nounWithArticle;
    private final String participle;
 
    /**
     * Creates a resource type.
     *
     * @param nounWithArticle the Spanish noun preceded by its article
     * @param participle the participle "encontrado" or "encontrada"
     */
    ResourceType(String nounWithArticle, String participle) {
        this.nounWithArticle = nounWithArticle;
        this.participle = participle;
    }
 
    /**
     * Returns the Spanish noun preceded by its article, e.g. "La venta".
     *
     * @return the noun with its article
     */
    public String getNounWithArticle() {
        return nounWithArticle;
    }
 
    /**
     * Returns the participle that agrees with the noun, e.g. "encontrada".
     *
     * @return the participle
     */
    public String getParticiple() {
        return participle;
    }
}