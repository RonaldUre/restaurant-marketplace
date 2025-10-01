// .../customer/application/command/ChangeCustomerPasswordCommand.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.command;

/**
 * Command: ChangeCustomerPassword
 * - currentPassword: verificación de la contraseña actual.
 * - newPassword: nueva contraseña en texto plano (se hashea en service).
 */
public record ChangeCustomerPasswordCommand(
        Long customerId,
        String currentPassword,
        String newPassword
) { }
