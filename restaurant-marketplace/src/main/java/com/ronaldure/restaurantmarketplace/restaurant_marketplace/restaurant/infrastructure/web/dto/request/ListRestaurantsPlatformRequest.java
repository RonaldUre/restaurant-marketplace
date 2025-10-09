package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.util.List;

/** Request para listado de plataforma (SUPER_ADMIN) con filtros y orden. */
public record ListRestaurantsPlatformRequest(
        @Min(0) Integer page,
        @Min(1) Integer size,
        List<String> statuses,                 // acepta "OPEN,CLOSED" o múltiples
        @Size(min = 1, max = 120) String city,
        @Size(min = 1, max = 120) String q,
        String sortBy,                         // "createdAt" | "name" | "status"
        String sortDir,                        // "asc" | "desc"
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdFrom,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdTo
) {}
