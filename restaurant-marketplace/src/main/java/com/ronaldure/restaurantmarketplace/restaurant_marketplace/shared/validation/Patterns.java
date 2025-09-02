package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.validation;

public final class Patterns {
    private Patterns() {}

    // kebab-case: letters/numbers separados por guiones
    public static final String SLUG = "^[a-z0-9]+(?:-[a-z0-9]+)*$";

    // email simple (fail-fast). El VO seguirá validando también.
    public static final String EMAIL = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
}