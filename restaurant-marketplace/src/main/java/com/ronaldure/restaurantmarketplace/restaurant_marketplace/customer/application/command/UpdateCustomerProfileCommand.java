// .../customer/application/command/UpdateCustomerProfileCommand.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.command;

/**
 * Command: UpdateCustomerProfile
 * - customerId: id técnico del customer.
 * - phone: opcional; null/"" significa “sin teléfono”.
 */
public record UpdateCustomerProfileCommand(
        String name,
        String phone
) { }
