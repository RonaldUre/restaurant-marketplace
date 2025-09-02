// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/infrastructure/web/RestaurantAdminController.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.UpdateRestaurantProfileCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.CloseRestaurantUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.OpenRestaurantUseCase;
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
    private final RestaurantWebMapper webMapper;

    public RestaurantAdminController(UpdateRestaurantProfileUseCase updateProfileUseCase,
                                     OpenRestaurantUseCase openRestaurantUseCase,
                                     CloseRestaurantUseCase closeRestaurantUseCase,
                                     RestaurantWebMapper webMapper) {
        this.updateProfileUseCase = updateProfileUseCase;
        this.openRestaurantUseCase = openRestaurantUseCase;
        this.closeRestaurantUseCase = closeRestaurantUseCase;
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
}
