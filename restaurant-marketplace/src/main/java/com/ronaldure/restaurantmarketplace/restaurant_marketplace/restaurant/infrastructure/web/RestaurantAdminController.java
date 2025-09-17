// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/infrastructure/web/RestaurantAdminController.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.UpdateOpeningHoursCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.UpdateRestaurantProfileCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.CloseRestaurantUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.GetMyRestaurantDetailQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.OpenRestaurantUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.UpdateOpeningHoursUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.UpdateRestaurantProfileUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.RestaurantPublicResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.mapper.RestaurantWebMapper;
import jakarta.validation.Valid;
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

    @PutMapping("/profile")
    public RestaurantPublicResponse updateProfile(@RequestBody @Valid UpdateRestaurantProfileCommand body) {
        return webMapper.toResponse(updateProfileUseCase.update(body));
    }

    @PostMapping("/open")
    public RestaurantPublicResponse open() {
        return webMapper.toResponse(openRestaurantUseCase.open());
    }

    @PostMapping("/close")
    public RestaurantPublicResponse close() {
        return webMapper.toResponse(closeRestaurantUseCase.close());
    }

    @GetMapping("/me")
    public RestaurantPublicResponse me() {
        return webMapper.toResponse(getMyRestaurantDetailQuery.get());
    }

    @PutMapping("/opening-hours")
    public RestaurantPublicResponse updateOpeningHours(@RequestBody @Valid UpdateOpeningHoursCommand body) {
        return webMapper.toResponse(updateOpeningHoursUseCase.update(body));
    }
}
