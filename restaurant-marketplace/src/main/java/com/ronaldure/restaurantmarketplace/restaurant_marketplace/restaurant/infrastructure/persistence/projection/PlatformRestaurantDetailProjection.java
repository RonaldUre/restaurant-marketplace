package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.projection;

/** Detalle para plataforma (sin filtro por estado). */
public interface PlatformRestaurantDetailProjection {
    Long getId();
    String getName();
    String getSlug();
    String getStatus();

    String getEmail();
    String getPhone();

    String getAddressLine1();
    String getAddressLine2();
    String getCity();
    String getCountry();
    String getPostalCode();

    String getOpeningHoursJson();
}
