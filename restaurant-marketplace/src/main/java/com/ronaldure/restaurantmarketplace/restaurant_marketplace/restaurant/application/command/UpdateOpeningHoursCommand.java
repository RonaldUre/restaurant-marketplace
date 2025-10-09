package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command;


/** Command dedicado para actualizar solo los horarios (JSON). */
public record UpdateOpeningHoursCommand(
        String openingHoursJson
) {}
