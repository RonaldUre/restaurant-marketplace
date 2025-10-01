// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/customer/infrastructure/web/CustomerController.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.ports.in.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.application.view.CustomerView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.mapper.CustomerWebMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.web.dto.request.ChangeCustomerPasswordRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.web.dto.request.RegisterCustomerRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.web.dto.request.UpdateCustomerProfileRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.web.dto.response.CustomerMeResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.customer.infrastructure.web.dto.response.CustomerRegisteredResponse;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@Validated
@RestController
@RequestMapping
public class CustomerController {

    private final RegisterCustomerUseCase registerCustomer;
    private final GetCustomerMeQuery getCustomerMe;
    private final UpdateCustomerProfileUseCase updateCustomerProfile;
    private final ChangeCustomerPasswordUseCase changeCustomerPassword;
    private final CustomerWebMapper web;

    public CustomerController(RegisterCustomerUseCase registerCustomer,
                              GetCustomerMeQuery getCustomerMe,
                              UpdateCustomerProfileUseCase updateCustomerProfile,
                              ChangeCustomerPasswordUseCase changeCustomerPassword,
                              CustomerWebMapper web) {
        this.registerCustomer = registerCustomer;
        this.getCustomerMe = getCustomerMe;
        this.updateCustomerProfile = updateCustomerProfile;
        this.changeCustomerPassword = changeCustomerPassword;
        this.web = web;
    }

    // Registro público de customers → 201 Created
    @PostMapping("/public/customers")
    public ResponseEntity<CustomerRegisteredResponse> register(@RequestBody @Valid RegisterCustomerRequest body) {
        var cmd = web.toCommand(body);
        CustomerView view = registerCustomer.register(cmd);
        CustomerRegisteredResponse resp = web.toRegisteredResponse(view);

        // sugerimos /customers/me como location para el propio cliente
        return ResponseEntity.created(URI.create("/customers/me")).body(resp);
    }

    // Datos del customer autenticado → 200 OK
    @GetMapping("/customers/me")
    public ResponseEntity<CustomerMeResponse> me() {
        CustomerView view = getCustomerMe.get();
        return ResponseEntity.ok(web.toMeResponse(view));
    }

    // Actualizar perfil del customer autenticado → 200 OK
    @PutMapping("/customers/me")
    public ResponseEntity<CustomerMeResponse> updateProfile(@RequestBody @Valid UpdateCustomerProfileRequest body) {
        var cmd = web.toCommand(body);
        CustomerView view = updateCustomerProfile.update(cmd);
        return ResponseEntity.ok(web.toMeResponse(view));
    }

    // Cambiar contraseña del customer autenticado → 200 OK
    @PostMapping("/customers/me/password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody @Valid ChangeCustomerPasswordRequest body) {
        var cmd = web.toCommand(body);
        String result = changeCustomerPassword.change(cmd); // "success"
        return ResponseEntity.ok(Map.of("status", result));
    }
}
