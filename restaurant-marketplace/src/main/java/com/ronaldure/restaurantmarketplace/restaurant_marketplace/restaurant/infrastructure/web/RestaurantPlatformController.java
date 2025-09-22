// src/main/java/.../infrastructure/web/RestaurantPlatformController.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.GetRestaurantPlatformQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.ListRestaurantsPlatformQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.RegisterRestaurantUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.SuspendRestaurantUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.UnsuspendRestaurantUseCase;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping
    public PageResponse<PlatformRestaurantCardResponse> list(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            // Spring permite ?statuses=OPEN&statuses=CLOSED o ?statuses=OPEN,CLOSED
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) @Size(min = 1, max = 120) String city,
            @RequestParam(required = false) @Size(min = 1, max = 120) String q,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdTo) {

        // DTO web (valida aquí y queda desacoplado de application)
        var req = new ListRestaurantsPlatformRequest(
                page, size, normalizeStatuses(statuses), city, q, sortBy, sortDir, createdFrom, createdTo);

        // Mapper web -> params de aplicación
        var params = webMapper.toQueryParams(req);

        var result = listPlatformQuery.list(params);
        var items = result.items().stream()
                .map(webMapper::toResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(items, result.totalElements(), result.totalPages());
    }

    @GetMapping("/{id}")
    public RestaurantPublicResponse getById(@PathVariable @Min(1) Long id) {
        var view = getPlatformQuery.getById(id);
        return webMapper.toResponse(view);
    }

    /**
     * POST /platform/restaurants
     * Crea un nuevo restaurante (tenant). Por defecto queda en estado CLOSED.
     */
    @PostMapping
    public RestaurantPublicResponse register(@RequestBody @Valid RegisterRestaurantRequest body) {
        var cmd = webMapper.toCommand(body); // web -> command (application)
        var view = registerUseCase.register(cmd);
        return webMapper.toResponse(view);
    }

    /**
     * POST /platform/restaurants/{id}/suspend?reason=
     * Suspende un restaurante por ID. Idempotente (si ya está SUSPENDED, no cambia estado).
     */
    @PostMapping("/{id}/suspend")
    public RestaurantPublicResponse suspendById(
            @PathVariable @Min(1) Long id,
            @RequestParam(required = false) @Size(max = 255) String reason) {
        var cmd = webMapper.toSuspendByIdCommand(id, reason); // delega construcción del command
        var view = suspendUseCase.suspend(cmd);
        return webMapper.toResponse(view);
    }

    @PostMapping("/{id}/unsuspend")
    public RestaurantPublicResponse unsuspendById(@PathVariable @Min(1) Long id) {
        var cmd = webMapper.toUnsuspendByIdCommand(id); // delega construcción del command
        var view = unsuspendUseCase.unsuspend(cmd);
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
            @Pattern(regexp = com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.validation.Patterns.SLUG)
            String slug,
            @RequestParam(required = false) @Size(max = 255) String reason) {
        var cmd = webMapper.toSuspendBySlugCommand(slug, reason); // delega construcción del command
        var view = suspendUseCase.suspend(cmd);
        return webMapper.toResponse(view);
    }

    // helpers

    // Normaliza "OPEN,CLOSED" -> ["OPEN","CLOSED"]; deja null si vacío.
    private List<String> normalizeStatuses(List<String> raw) {
        if (raw == null || raw.isEmpty()) return null;
        return raw.stream()
                .flatMap(s -> List.of(s.split(",")).stream())
                .map(String::trim)
                .filter(v -> !v.isEmpty())
                .toList();
    }
}
