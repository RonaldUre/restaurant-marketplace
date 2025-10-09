package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view;

/**
 * Application-level DTO for Restaurant data exposure.
 * Keep it free from JPA/domain types and annotations.
 */
public record RestaurantView(
        Long id,
        String name,
        String slug,
        String status,          // "OPEN" | "CLOSED" | "SUSPENDED"
        String email,
        String phone,
        AddressView address,
        String openingHoursJson  // JSON string for UI/clients; app does not enforce schema here
) {
    public record AddressView(
            String line1,
            String line2,
            String city,
            String country,   // ISO-3166-1 alpha-2
            String postalCode
    ) {}
}
