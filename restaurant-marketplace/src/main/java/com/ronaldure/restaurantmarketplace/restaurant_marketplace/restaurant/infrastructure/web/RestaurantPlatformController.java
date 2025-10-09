// src/main/java/.../infrastructure/web/RestaurantPlatformController.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.RegisterRestaurantCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.SuspendRestaurantCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.UnsuspendRestaurantCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.GetRestaurantPlatformQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.ListRestaurantsPlatformQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.RegisterRestaurantUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.SuspendRestaurantUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.UnsuspendRestaurantUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query.ListRestaurantsPlatformQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.PlatformRestaurantCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.mapper.RestaurantWebMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.request.ListRestaurantsPlatformRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.request.RegisterRestaurantRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.response.PlatformRestaurantCardResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.response.RestaurantPublicResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/platform/restaurants")
@Validated
public class RestaurantPlatformController {

    private final RegisterRestaurantUseCase registerUseCase;
    private final SuspendRestaurantUseCase suspendUseCase;
    private final ListRestaurantsPlatformQuery listPlatformQuery;
    private final GetRestaurantPlatformQuery getPlatformQuery;
    private final UnsuspendRestaurantUseCase unsuspendUseCase;
    private final RestaurantWebMapper webMapper;

    public RestaurantPlatformController(RegisterRestaurantUseCase registerUseCase,
            SuspendRestaurantUseCase suspendUseCase,
            ListRestaurantsPlatformQuery listPlatformQuery,
            GetRestaurantPlatformQuery getPlatformQuery,
            UnsuspendRestaurantUseCase unsuspendUseCase,
            RestaurantWebMapper webMapper) {
        this.registerUseCase = registerUseCase;
        this.suspendUseCase = suspendUseCase;
        this.listPlatformQuery = listPlatformQuery;
        this.getPlatformQuery = getPlatformQuery;
        this.unsuspendUseCase = unsuspendUseCase;
        this.webMapper = webMapper;
    }

    // List → 200 OK
    @GetMapping
    public ResponseEntity<PageResponse<PlatformRestaurantCardResponse>> list(
            @Valid @ModelAttribute ListRestaurantsPlatformRequest req) {

        ListRestaurantsPlatformQueryParams params = webMapper.toQueryParams(req);

        PageResponse<PlatformRestaurantCardView> result = listPlatformQuery.list(params);

        List<PlatformRestaurantCardResponse> items = result.items().stream()
                .map(webMapper::toResponse)
                .toList();

        PageResponse<PlatformRestaurantCardResponse> body = new PageResponse<>(items, result.totalElements(),
                result.totalPages());

        return ResponseEntity.ok(body); // 200
    }

    // Detail by id → 200 OK
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantPublicResponse> getById(@PathVariable @Min(1) Long id) {
        RestaurantView view = getPlatformQuery.getById(id);
        RestaurantPublicResponse body = webMapper.toResponse(view);
        return ResponseEntity.ok(body); // 200
    }

    /**
     * POST /platform/restaurants
     * Crea un nuevo restaurante (tenant). Por defecto queda en estado CLOSED.
     * → 201 Created + Location
     */
    @PostMapping
    public ResponseEntity<RestaurantPublicResponse> register(@RequestBody @Valid RegisterRestaurantRequest body) {
        RegisterRestaurantCommand cmd = webMapper.toCommand(body);
        RestaurantView view = registerUseCase.register(cmd);
        RestaurantPublicResponse resp = webMapper.toResponse(view);

        java.net.URI location = java.net.URI.create("/platform/restaurants/" + resp.id());
        return ResponseEntity.created(location).body(resp); // 201
    }

    /**
     * POST /platform/restaurants/{id}/suspend?reason=
     * Suspende un restaurante por ID (idempotente).
     * → 200 OK devolviendo la representación actualizada
     */
    @PostMapping("/{id}/suspend")
    public ResponseEntity<RestaurantPublicResponse> suspendById(
            @PathVariable @Min(1) Long id,
            @RequestParam(required = false) @Size(max = 255) String reason) {

        SuspendRestaurantCommand cmd = webMapper.toSuspendByIdCommand(id, reason);
        RestaurantView view = suspendUseCase.suspend(cmd);
        return ResponseEntity.ok(webMapper.toResponse(view)); // 200
    }

    // Unsuspend by id → 200 OK
    @PostMapping("/{id}/unsuspend")
    public ResponseEntity<RestaurantPublicResponse> unsuspendById(@PathVariable @Min(1) Long id) {
        UnsuspendRestaurantCommand cmd = webMapper.toUnsuspendByIdCommand(id);
        RestaurantView view = unsuspendUseCase.unsuspend(cmd);
        return ResponseEntity.ok(webMapper.toResponse(view)); // 200
    }

    /**
     * POST /platform/restaurants/slug/{slug}/suspend?reason=
     * Suspende por slug.
     * → 200 OK devolviendo la representación actualizada
     */
    @PostMapping("/slug/{slug}/suspend")
    public ResponseEntity<RestaurantPublicResponse> suspendBySlug(
            @PathVariable @Size(min = 1, max = 140) @Pattern(regexp = com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.validation.Patterns.SLUG) String slug,
            @RequestParam(required = false) @Size(max = 255) String reason) {

        SuspendRestaurantCommand cmd = webMapper.toSuspendBySlugCommand(slug, reason);
        RestaurantView view = suspendUseCase.suspend(cmd);
        return ResponseEntity.ok(webMapper.toResponse(view)); // 200
    }
}
