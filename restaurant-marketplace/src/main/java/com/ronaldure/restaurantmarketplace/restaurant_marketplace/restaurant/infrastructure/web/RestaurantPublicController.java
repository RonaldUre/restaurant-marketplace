// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/infrastructure/web/RestaurantPublicController.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.CheckSlugAvailabilityQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.GetRestaurantPublicQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.ListRestaurantsPublicQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query.GetRestaurantPublicQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query.ListRestaurantsPublicQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.request.ListRestaurantsPublicRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.response.RestaurantCardResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.response.RestaurantPublicResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.response.SlugAvailabilityResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.mapper.RestaurantWebMapper;
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
@RequestMapping("/restaurants")
@Validated
public class RestaurantPublicController {

    private final ListRestaurantsPublicQuery listQuery;
    private final GetRestaurantPublicQuery getQuery;
    private final CheckSlugAvailabilityQuery checkSlug;
    private final RestaurantWebMapper webMapper;

    public RestaurantPublicController(ListRestaurantsPublicQuery listQuery,
                                      GetRestaurantPublicQuery getQuery,
                                      CheckSlugAvailabilityQuery checkSlug,
                                      RestaurantWebMapper webMapper) {
        this.listQuery = listQuery;
        this.getQuery = getQuery;
        this.checkSlug = checkSlug;
        this.webMapper = webMapper;
    }

    // List → 200 OK
    @GetMapping
    public ResponseEntity<PageResponse<RestaurantCardResponse>> list(
            @Valid @ModelAttribute ListRestaurantsPublicRequest req) {

        ListRestaurantsPublicQueryParams params = webMapper.toQueryParams(req);

        PageResponse<RestaurantCardView> result = listQuery.list(params);

        List<RestaurantCardResponse> items = result.items().stream()
                .map(webMapper::toResponse)
                .toList();

        PageResponse<RestaurantCardResponse> body =
                new PageResponse<>(items, result.totalElements(), result.totalPages());

        return ResponseEntity.ok(body);
    }

    // Detail by id → 200 OK
    @GetMapping("/{id}")
    public ResponseEntity<RestaurantPublicResponse> getById(@PathVariable @Min(1) Long id) {
        GetRestaurantPublicQueryParams params = new GetRestaurantPublicQueryParams(id, null);
        RestaurantPublicResponse body = webMapper.toResponse(getQuery.get(params));
        return ResponseEntity.ok(body);
    }

    // Detail by slug → 200 OK
    @GetMapping("/slug/{slug}")
    public ResponseEntity<RestaurantPublicResponse> getBySlug(
            @PathVariable
            @Size(min = 1, max = 140)
            @Pattern(regexp = com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.validation.Patterns.SLUG)
            String slug) {

        GetRestaurantPublicQueryParams params = new GetRestaurantPublicQueryParams(null, slug);
        RestaurantPublicResponse body = webMapper.toResponse(getQuery.get(params));
        return ResponseEntity.ok(body);
    }

    // Slug availability check → 200 OK
    @GetMapping("/slug/check")
    public ResponseEntity<SlugAvailabilityResponse> check(@RequestParam("value") String value) {
        var r = checkSlug.check(value);
        SlugAvailabilityResponse body = new SlugAvailabilityResponse(r.value(), r.normalized(), r.available());
        return ResponseEntity.ok(body);
    }
}
