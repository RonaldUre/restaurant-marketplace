package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command;

/** Input para UnsuspendRestaurantUseCase. Exactamente un target: id XOR slug. */
public record UnsuspendRestaurantCommand(

        Long id,

        String slug

) {
    public boolean hasExactlyOneTarget() {
        boolean hasId = id != null;
        boolean hasSlug = slug != null && !slug.isBlank();
        return hasId ^ hasSlug; // XOR
    }
}
