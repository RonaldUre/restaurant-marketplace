// .../customer/application/command/RegisterCustomerCommand.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.command;

/**
 * Command: RegisterCustomer
 * - password: contraseña en texto plano (se hashea en la capa application/service).
 * - phone: opcional; puede venir null o vacío.
 */
public record RegisterCustomerCommand(
        String email,
        String name,
        String phone,
        String password
) { }
