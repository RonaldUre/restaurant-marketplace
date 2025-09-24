// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/service/SuspendRestaurantService.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.SuspendRestaurantCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors.RestaurantNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.mapper.RestaurantApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.SuspendRestaurantUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.RestaurantRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.Restaurant;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SuspendRestaurantService implements SuspendRestaurantUseCase {

    private final AccessControl accessControl;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantApplicationMapper mapper;

    public SuspendRestaurantService(AccessControl accessControl,
                                    RestaurantRepository restaurantRepository,
                                    RestaurantApplicationMapper mapper) {
        this.accessControl = accessControl;
        this.restaurantRepository = restaurantRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public RestaurantView suspend(SuspendRestaurantCommand command) {
        // 1) Authorization (defense in depth)
        accessControl.requireRole(Roles.SUPER_ADMIN);

        // 2) Validate XOR target (exactly one of id or slug)
        if (command == null || !command.hasExactlyOneTarget()) {
            throw new IllegalArgumentException("Exactly one target is required: id or slug");
        }

        // 3) Resolve target and load aggregate
        Restaurant restaurant = (command.id() != null)
                ? restaurantRepository.findById(command.id())
                    .orElseThrow(() -> RestaurantNotFoundException.byId(command.id()))
                : restaurantRepository.findBySlug(command.slug())
                    .orElseThrow(() -> RestaurantNotFoundException.bySlug(command.slug()));

        // 4) Domain behavior (idempotent: multiple calls keep SUSPENDED)
        restaurant.suspend();

        // 5) Persist & map
        return mapper.toView(restaurantRepository.save(restaurant));
    }
}
