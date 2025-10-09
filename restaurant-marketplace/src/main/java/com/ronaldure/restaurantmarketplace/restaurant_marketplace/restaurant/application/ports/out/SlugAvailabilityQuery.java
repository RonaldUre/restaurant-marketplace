package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out;

/** OUT port para consultas de disponibilidad de slug (lectura pública/ligera). */
public interface SlugAvailabilityQuery {
    /** @return true si el slug YA EXISTE (está tomado) */
    boolean existsBySlug(String normalizedSlug);
}
