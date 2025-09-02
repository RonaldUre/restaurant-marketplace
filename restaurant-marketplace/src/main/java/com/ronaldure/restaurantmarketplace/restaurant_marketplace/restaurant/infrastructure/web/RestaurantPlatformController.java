// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/infrastructure/web/RestaurantPlatformController.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.RegisterRestaurantCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.SuspendRestaurantCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.RegisterRestaurantUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.SuspendRestaurantUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.RestaurantPublicResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.mapper.RestaurantWebMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Platform REST API — operaciones a nivel plataforma (requiere SUPER_ADMIN).
 * - Registrar restaurantes (tenants).
 * - Suspender restaurantes (por id o por slug).
 * 
 * La autorización real se valida en la capa application (AccessControl).
 */
@RestController
@RequestMapping("/platform/restaurants")
@Validated
public class RestaurantPlatformController {

    private final RegisterRestaurantUseCase registerUseCase;
    private final SuspendRestaurantUseCase suspendUseCase;
    private final RestaurantWebMapper webMapper;

    public RestaurantPlatformController(RegisterRestaurantUseCase registerUseCase,
                                        SuspendRestaurantUseCase suspendUseCase,
                                        RestaurantWebMapper webMapper) {
        this.registerUseCase = registerUseCase;
        this.suspendUseCase = suspendUseCase;
        this.webMapper = webMapper;
    }

    /**
     * POST /platform/restaurants
     * Crea un nuevo restaurante (tenant). Por defecto queda en estado CLOSED.
     */
    @PostMapping
    public RestaurantPublicResponse register(@RequestBody @Valid RegisterRestaurantCommand body) {
        var view = registerUseCase.register(body);
        return webMapper.toResponse(view);
    }

    /**
     * POST /platform/restaurants/{id}/suspend?reason=
     * Suspende un restaurante por ID. Idempotente (si ya está SUSPENDED, no cambia estado).
     */
    @PostMapping("/{id}/suspend")
    public RestaurantPublicResponse suspendById(
            @PathVariable @Min(1) Long id,
            @RequestParam(required = false) @Size(max = 255) String reason
    ) {
        var cmd = new SuspendRestaurantCommand(id, null, reason);
        var view = suspendUseCase.suspend(cmd);
        return webMapper.toResponse(view);
    }

    /**
     * POST /platform/restaurants/slug/{slug}/suspend?reason=
     * Suspende un restaurante por slug (kebab-case).
     */
    @PostMapping("/slug/{slug}/suspend")
    public RestaurantPublicResponse suspendBySlug(
            @PathVariable
            @Size(min = 1, max = 140)
            @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
            String slug,
            @RequestParam(required = false) @Size(max = 255) String reason
    ) {
        var cmd = new SuspendRestaurantCommand(null, slug, reason);
        var view = suspendUseCase.suspend(cmd);
        return webMapper.toResponse(view);
    }
}
