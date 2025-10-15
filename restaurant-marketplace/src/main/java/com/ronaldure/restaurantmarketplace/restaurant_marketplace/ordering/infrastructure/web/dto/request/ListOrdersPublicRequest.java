package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.infrastructure.web.dto.request;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.query.ListOrdersPublicQueryParams;
import jakarta.validation.constraints.*;

public record ListOrdersPublicRequest(
        @Pattern(regexp = "PENDING|PAID|CANCELLED", message = "status must be PENDING, PAID or CANCELLED")
        String status,
        @Min(0) Integer page,
        @Min(1) @Max(200) Integer size,
        String sortBy,
        @Pattern(regexp = "asc|desc", flags = Pattern.Flag.CASE_INSENSITIVE,
                 message = "sortDir must be 'asc' or 'desc'")
        String sortDir
) {
    public ListOrdersPublicRequest {
        page = (page == null || page < 0) ? 0 : page;
        size = (size == null || size < 1 || size > 200) ? 20 : size;
        sortBy = (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy;
        sortDir = (sortDir == null || sortDir.isBlank()) ? "desc" : sortDir.toLowerCase();
    }
    public ListOrdersPublicQueryParams toQueryParams() {
        return new ListOrdersPublicQueryParams(status, page, size, sortBy, sortDir);
    }
}
