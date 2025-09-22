// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/infrastructure/web/RestaurantPublicController.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.CheckSlugAvailabilityQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.GetRestaurantPublicQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.ListRestaurantsPublicQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query.GetRestaurantPublicQueryParams;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query.ListRestaurantsPublicQueryParams;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

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

    @GetMapping
    public PageResponse<RestaurantCardResponse> list(@Valid @ModelAttribute ListRestaurantsPublicRequest req) {
        var params = new ListRestaurantsPublicQueryParams(req.page(), req.size(), req.city());
        var result = listQuery.list(params);

        var items = result.items().stream()
                .map(webMapper::toResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(items, result.totalElements(), result.totalPages());
    }

    @GetMapping("/{id}")
    public RestaurantPublicResponse getById(@PathVariable @Min(1) Long id) {
        var params = new GetRestaurantPublicQueryParams(id, null);
        return webMapper.toResponse(getQuery.get(params));
    }

    @GetMapping("/slug/{slug}")
    public RestaurantPublicResponse getBySlug(
            @PathVariable @Size(min = 1, max = 140) @Pattern(regexp = com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.validation.Patterns.SLUG) String slug) {
        var params = new GetRestaurantPublicQueryParams(null, slug);
        return webMapper.toResponse(getQuery.get(params));
    }

    @GetMapping("/slug/check")
    public SlugAvailabilityResponse check(@RequestParam("value") String value) {
        var r = checkSlug.check(value);
        return new SlugAvailabilityResponse(r.value(), r.normalized(), r.available());
    }
}
