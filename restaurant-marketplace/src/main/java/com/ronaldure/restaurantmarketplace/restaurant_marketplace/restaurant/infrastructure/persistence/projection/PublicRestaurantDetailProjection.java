package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.persistence.projection;

/** Detailed projection for public view (no datos privados). */
public interface PublicRestaurantDetailProjection {
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
