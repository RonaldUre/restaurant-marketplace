package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.query.ListOrdersAdminQueryParams;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;

/**
 * Request para listar pedidos en el backoffice por tenant.
 * Se bindea desde query params con @ModelAttribute y valida con @Valid.
 *
 * Ejemplo de uso en controller:
 *   @GetMapping("/admin/orders")
 *   public PageResponse<OrderCardResponse> list(@Valid @ModelAttribute ListOrdersAdminRequest req) { ... }
 */
public record ListOrdersAdminRequest(

        // Filtros
        @Pattern(regexp = "PENDING|PAID|CANCELLED", message = "status must be CREATED, PAID or CANCELLED")
        String status,                         // opcional

        @Positive(message = "customerId must be > 0")
        Long customerId,                       // opcional

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant createdFrom,                   // opcional

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        Instant createdTo,                     // opcional

        // Paginación / orden
        @Min(value = 0, message = "page must be >= 0")
        Integer page,                          // default 0

        @Min(value = 1, message = "size must be >= 1")
        @Max(value = 200, message = "size must be <= 200")
        Integer size,                          // default 20

        String sortBy,                         // default "createdAt"
        @Pattern(regexp = "asc|desc", flags = Pattern.Flag.CASE_INSENSITIVE,
                 message = "sortDir must be 'asc' or 'desc'")
        String sortDir                         // default "desc"
) {

    /**
     * Defaults y normalización segura.
     * Al ser record, podemos ajustar los componentes en el constructor compacto.
     */
    public ListOrdersAdminRequest {
        // defaults de paginación
        page = (page == null || page < 0) ? 0 : page;
        size = (size == null || size < 1 || size > 200) ? 20 : size;

        // defaults de orden
        sortBy = (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy;
        sortDir = (sortDir == null || sortDir.isBlank()) ? "desc" : sortDir;

        // normalizar sortDir a minúsculas
        sortDir = sortDir.toLowerCase();
    }

    /** Mapeo directo al record de aplicación. */
    public ListOrdersAdminQueryParams toQueryParams() {
        return new ListOrdersAdminQueryParams(
                status,
                customerId,
                createdFrom,
                createdTo,
                page,
                size,
                sortBy,
                sortDir
        );
    }
}
