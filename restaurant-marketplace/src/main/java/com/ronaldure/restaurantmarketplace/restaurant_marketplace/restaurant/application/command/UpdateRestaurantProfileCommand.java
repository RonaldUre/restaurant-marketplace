package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateRestaurantProfileCommand(

        // Opcional: nombre
        @Size(min = 1, max = 120)
        String name,

        // Opcional: slug en kebab-case
        @Size(min = 1, max = 140)
        @Pattern(regexp = com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.validation.Patterns.SLUG)
        String slug,

        // Opcional: contacto
        @Email @Size(max = 255)
        String email,

        @Size(max = 30)
        String phone,

        // Dirección anidada (opcional). Null => "no cambiar".
        @Valid
        AddressPayload address,

        // Horarios (JSON) opcional. Null => "no cambiar".
        String openingHoursJson

) {
    public record AddressPayload(
            @Size(max = 255) String line1,
            @Size(max = 255) String line2,
            @Size(max = 120) String city,
            @Size(max = 2)   String country,
            @Size(max = 20)  String postalCode
    ) {}
}
