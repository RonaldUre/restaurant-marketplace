// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/errors/SlugAlreadyInUseException.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors;

/**
 * Thrown when attempting to use a slug that is already taken.
 * Maps to HTTP 409 in the web layer.
 */
public class SlugAlreadyInUseException extends RuntimeException {

    private final String slug;

    public SlugAlreadyInUseException(String slug) {
        super("Slug already in use: " + slug);
        this.slug = slug;
    }

    public String getSlug() { return slug; }
}
