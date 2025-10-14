package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.mapper;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query.GetRestaurantPublicQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query.ListRestaurantsPlatformQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query.ListRestaurantsPublicQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.PlatformRestaurantCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantForSelectView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.request.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.response.PlatformRestaurantCardResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.response.RestaurantCardResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.response.RestaurantForSelectResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.response.RestaurantPublicResponse;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class RestaurantWebMapper {

    // ===========================
    // Views -> Responses (YA EXISTENTE)
    // ===========================
    public RestaurantCardResponse toResponse(RestaurantCardView v) {
        return new RestaurantCardResponse(
                v.id(), v.name(), v.slug(), v.status(), v.city());
    }

    public RestaurantPublicResponse toResponse(RestaurantView v) {
        RestaurantPublicResponse.AddressResponse addr = null;
        if (v.address() != null) {
            addr = new RestaurantPublicResponse.AddressResponse(
                    v.address().line1(),
                    v.address().line2(),
                    v.address().city(),
                    v.address().country(),
                    v.address().postalCode());
        }
        return new RestaurantPublicResponse(
                v.id(), v.name(), v.slug(), v.status(), v.email(), v.phone(), addr, v.openingHoursJson());
    }

    public PlatformRestaurantCardResponse toResponse(PlatformRestaurantCardView v) {
        return new PlatformRestaurantCardResponse(
                v.id(), v.name(), v.slug(), v.status(), v.city(), v.createdAt());
    }

    public RestaurantForSelectResponse toResponse(RestaurantForSelectView v) { // ← NUEVO
        return new RestaurantForSelectResponse(v.id(), v.name());
    }

    /** NEW: map list */
    public List<RestaurantForSelectResponse> toResponseList(List<RestaurantForSelectView> views) { // ← NUEVO
        return views.stream().map(this::toResponse).toList();
    }

    // ===========================
    // Requests -> Commands / Query Params (NUEVO)
    // ===========================

    // --- Platform: Register ---
    public RegisterRestaurantCommand toCommand(RegisterRestaurantRequest dto) {
        Objects.requireNonNull(dto, "request is required");
        String line1 = dto.address() != null ? dto.address().line1() : null;
        String line2 = dto.address() != null ? dto.address().line2() : null;
        String city = dto.address() != null ? dto.address().city() : null;
        String country = dto.address() != null ? dto.address().country() : null;
        String postal = dto.address() != null ? dto.address().postalCode() : null;

        return new RegisterRestaurantCommand(
                dto.name(),
                dto.slug(),
                dto.email(),
                dto.phone(),
                line1, line2, city, country, postal,
                dto.openingHoursJson(),
                dto.adminEmail(),
                dto.adminPassword());
    }

    // --- Admin: Update profile (tenant actual) ---
    public UpdateRestaurantProfileCommand toCommand(UpdateRestaurantProfileRequest dto) {
        Objects.requireNonNull(dto, "request is required");
        UpdateRestaurantProfileCommand.AddressPayload addr = null;
        if (dto.address() != null) {
            addr = new UpdateRestaurantProfileCommand.AddressPayload(
                    dto.address().line1(),
                    dto.address().line2(),
                    dto.address().city(),
                    dto.address().country(),
                    dto.address().postalCode());
        }
        return new UpdateRestaurantProfileCommand(
                dto.name(),
                dto.slug(),
                dto.email(),
                dto.phone(),
                addr,
                dto.openingHoursJson());
    }

    // --- Admin: Update only opening hours ---
    public UpdateOpeningHoursCommand toCommand(UpdateOpeningHoursRequest dto) {
        Objects.requireNonNull(dto, "request is required");
        return new UpdateOpeningHoursCommand(dto.openingHoursJson());
    }

    // --- Platform: Suspend by id OR slug ---
    public SuspendRestaurantCommand toCommand(SuspendRestaurantRequest dto) {
        Objects.requireNonNull(dto, "request is required");
        // La validación XOR ya viene en el DTO; aquí solo mapeamos.
        return new SuspendRestaurantCommand(dto.id(), dto.slug(), dto.reason());
    }

    // --- Platform: Unsuspend by id OR slug ---
    public UnsuspendRestaurantCommand toCommand(UnsuspendRestaurantRequest dto) {
        Objects.requireNonNull(dto, "request is required");
        return new UnsuspendRestaurantCommand(dto.id(), dto.slug());
    }

    // --- Public listing (paginación + ciudad opcional) ---
    public ListRestaurantsPublicQueryParams toQueryParams(ListRestaurantsPublicRequest dto) {
        Objects.requireNonNull(dto, "request is required");
        int page = (dto.page() == null) ? 0 : Math.max(dto.page(), 0);
        int size = (dto.size() == null) ? 10 : Math.max(dto.size(), 1);
        return new ListRestaurantsPublicQueryParams(
                page,
                size,
                safeTrimOrNull(dto.city()));
    }

    // --- Platform listing (filtros + orden + rango de fechas) ---
    public ListRestaurantsPlatformQueryParams toQueryParams(ListRestaurantsPlatformRequest dto) {
        Objects.requireNonNull(dto, "request is required");

        int page = (dto.page() == null) ? 0 : Math.max(dto.page(), 0);
        int size = (dto.size() == null) ? 10 : Math.max(dto.size(), 1);

        return new ListRestaurantsPlatformQueryParams(
                page,
                size,
                normalizeStatuses(dto.statuses()),
                safeTrimOrNull(dto.city()),
                safeTrimOrNull(dto.q()),
                safeSortBy(dto.sortBy()),
                safeSortDir(dto.sortDir()),
                dto.createdFrom(),
                dto.createdTo());
    }

    public UnsuspendRestaurantCommand toUnsuspendByIdCommand(Long id) {
        return new UnsuspendRestaurantCommand(id, null);
    }

    /** NUEVO: construir command para suspender por ID. */
    public SuspendRestaurantCommand toSuspendByIdCommand(Long id, String reason) {
        return new SuspendRestaurantCommand(id, null, reason);
    }

    /** NUEVO: construir command para suspender por slug. */
    public SuspendRestaurantCommand toSuspendBySlugCommand(String slug, String reason) {
        return new SuspendRestaurantCommand(null, slug, reason);
    }

    // --- Public detail by id OR slug ---
    public GetRestaurantPublicQueryParams toQueryParamsById(Long id) {
        return new GetRestaurantPublicQueryParams(id, null);
    }

    public GetRestaurantPublicQueryParams toQueryParamsBySlug(String slug) {
        return new GetRestaurantPublicQueryParams(null, slug);
    }

    // ===========================
    // Helpers
    // ===========================
    private String safeTrimOrNull(String s) {
        if (s == null)
            return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    // Acepta ["OPEN,CLOSED","SUSPENDED"] o ["OPEN","CLOSED"] y normaliza
    private List<String> normalizeStatuses(List<String> raw) {
        if (raw == null || raw.isEmpty())
            return null;
        List<String> flattened = raw.stream()
                .flatMap(s -> List.of(s.split(",")).stream())
                .map(String::trim)
                .filter(v -> !v.isEmpty())
                .collect(Collectors.toList());
        return flattened.isEmpty() ? null : flattened;
    }

    private String safeSortBy(String sortBy) {
        if (sortBy == null)
            return "createdAt";
        return switch (sortBy) {
            case "name", "status", "createdAt" -> sortBy;
            default -> "createdAt";
        };
    }

    private String safeSortDir(String sortDir) {
        return "asc".equalsIgnoreCase(sortDir) ? "asc" : "desc";
    }
}
