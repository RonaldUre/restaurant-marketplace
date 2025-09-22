package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command;

public record UpdateRestaurantProfileCommand(

        String name,

        // Opcional: slug en kebab-case
        String slug,

        // Opcional: contacto
        String email,

        String phone,

        // Dirección anidada (opcional). Null => "no cambiar".
        AddressPayload address,

        // Horarios (JSON) opcional. Null => "no cambiar".
        String openingHoursJson

) {
    public record AddressPayload(
            String line1,
            String line2,
            String city,
            String country,
            String postalCode
    ) {}
}
