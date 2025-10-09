// src/main/java/.../infrastructure/web/RestaurantAdminController.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.UpdateOpeningHoursCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.UpdateRestaurantProfileCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.CloseRestaurantUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.GetMyRestaurantDetailQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.OpenRestaurantUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.UpdateOpeningHoursUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.UpdateRestaurantProfileUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.mapper.RestaurantWebMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.request.UpdateOpeningHoursRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.request.UpdateRestaurantProfileRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.response.RestaurantPublicResponse;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/restaurants")
@Validated
public class RestaurantAdminController {

    private final UpdateRestaurantProfileUseCase updateProfileUseCase;
    private final OpenRestaurantUseCase openRestaurantUseCase;
    private final CloseRestaurantUseCase closeRestaurantUseCase;
    private final GetMyRestaurantDetailQuery getMyRestaurantDetailQuery;
    private final UpdateOpeningHoursUseCase updateOpeningHoursUseCase;
    private final RestaurantWebMapper webMapper;

    public RestaurantAdminController(UpdateRestaurantProfileUseCase updateProfileUseCase,
                                     OpenRestaurantUseCase openRestaurantUseCase,
                                     CloseRestaurantUseCase closeRestaurantUseCase,
                                     GetMyRestaurantDetailQuery getMyRestaurantDetailQuery,
                                     UpdateOpeningHoursUseCase updateOpeningHoursUseCase,
                                     RestaurantWebMapper webMapper) {
        this.updateProfileUseCase = updateProfileUseCase;
        this.openRestaurantUseCase = openRestaurantUseCase;
        this.closeRestaurantUseCase = closeRestaurantUseCase;
        this.getMyRestaurantDetailQuery = getMyRestaurantDetailQuery;
        this.updateOpeningHoursUseCase = updateOpeningHoursUseCase;
        this.webMapper = webMapper;
    }

    // Update profile → 200 OK (devuelve representación actualizada)
    @PutMapping("/profile")
    public ResponseEntity<RestaurantPublicResponse> updateProfile(@RequestBody @Valid UpdateRestaurantProfileRequest body) {
        UpdateRestaurantProfileCommand cmd = webMapper.toCommand(body);
        RestaurantPublicResponse resp = webMapper.toResponse(updateProfileUseCase.update(cmd));
        return ResponseEntity.ok(resp); // 200
    }

    // Open → 200 OK (acción con body resultante)
    @PostMapping("/open")
    public ResponseEntity<RestaurantPublicResponse> open() {
        return ResponseEntity.ok(webMapper.toResponse(openRestaurantUseCase.open())); // 200
    }

    // Close → 200 OK (acción con body resultante)
    @PostMapping("/close")
    public ResponseEntity<RestaurantPublicResponse> close() {
        return ResponseEntity.ok(webMapper.toResponse(closeRestaurantUseCase.close())); // 200
    }

    // Me (detalle del tenant actual) → 200 OK
    @GetMapping("/me")
    public ResponseEntity<RestaurantPublicResponse> me() {
        return ResponseEntity.ok(webMapper.toResponse(getMyRestaurantDetailQuery.get())); // 200
    }

    // Update opening hours → 200 OK
    @PutMapping("/opening-hours")
    public ResponseEntity<RestaurantPublicResponse> updateOpeningHours(@RequestBody @Valid UpdateOpeningHoursRequest body) {
        UpdateOpeningHoursCommand cmd = webMapper.toCommand(body);
        RestaurantPublicResponse resp = webMapper.toResponse(updateOpeningHoursUseCase.update(cmd));
        return ResponseEntity.ok(resp); // 200
    }
}