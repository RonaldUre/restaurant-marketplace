package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.response;

public record RestaurantPublicResponse(
        Long id,
        String name,
        String slug,
        String status,
        String email,
        String phone,
        AddressResponse address,
        String openingHoursJson
) {
    public record AddressResponse(
            String line1,
            String line2,
            String city,
            String country,
            String postalCode
    ) {}
}
