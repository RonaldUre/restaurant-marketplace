// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/errors/RestaurantNotFoundException.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors;

/**
 * Thrown when a Restaurant cannot be found by the given identifier.
 * Maps to HTTP 404 in the web layer.
 */
public class RestaurantNotFoundException extends RuntimeException {

    private final Long id;
    private final String slug;

    public static RestaurantNotFoundException byId(Long id) {
        return new RestaurantNotFoundException(id, null, "Restaurant not found with id=" + id);
    }

    public static RestaurantNotFoundException bySlug(String slug) {
        return new RestaurantNotFoundException(null, slug, "Restaurant not found with slug=" + slug);
    }

    private RestaurantNotFoundException(Long id, String slug, String message) {
        super(message);
        this.id = id;
        this.slug = slug;
    }

    public Long getId() { return id; }
    public String getSlug() { return slug; }
}
