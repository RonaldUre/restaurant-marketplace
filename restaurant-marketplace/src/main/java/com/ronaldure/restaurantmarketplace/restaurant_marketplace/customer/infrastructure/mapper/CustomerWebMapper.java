// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/customer/infrastructure/mapper/CustomerWebMapper.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.mapper;

import org.springframework.stereotype.Component;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.command.ChangeCustomerPasswordCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.command.RegisterCustomerCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.command.UpdateCustomerProfileCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.view.CustomerView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.web.dto.request.ChangeCustomerPasswordRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.web.dto.request.RegisterCustomerRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.web.dto.request.UpdateCustomerProfileRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.web.dto.response.CustomerMeResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.web.dto.response.CustomerRegisteredResponse;

@Component
public class CustomerWebMapper {

    // -------- Web -> Commands --------

    public RegisterCustomerCommand toCommand(RegisterCustomerRequest req) {
        // phone: puede venir null (sin teléfono) o string (validado por DTO)
        return new RegisterCustomerCommand(
                req.email(),
                req.name(),
                req.phone(),    // null => Phone.empty() en la factory
                req.password()
        );
    }

    public UpdateCustomerProfileCommand toCommand(UpdateCustomerProfileRequest req) {
        // Semántica:
        //  - name siempre requerido (DTO @NotBlank)
        //  - phone:
        //      null  => no cambiar
        //      ""    => quitar teléfono
        //      texto => actualizar
        return new UpdateCustomerProfileCommand(
                req.name(),
                req.phone()
        );
    }

    public ChangeCustomerPasswordCommand toCommand(ChangeCustomerPasswordRequest req) {
        return new ChangeCustomerPasswordCommand(
                req.currentPassword(),
                req.newPassword()
        );
    }

    // -------- Views -> Responses --------

    public CustomerRegisteredResponse toRegisteredResponse(CustomerView view) {
        return new CustomerRegisteredResponse(
                view.id(),
                view.email(),
                view.name()
        );
    }

    public CustomerMeResponse toMeResponse(CustomerView view) {
        return new CustomerMeResponse(
                view.id(),
                view.email(),
                view.name(),
                view.phone(),       // puede ser null si no hay teléfono
                view.createdAt(),
                view.updatedAt()
        );
    }
}
