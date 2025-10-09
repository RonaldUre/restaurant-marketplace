// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/service/CloseRestaurantService.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors.RestaurantNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.mapper.RestaurantApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.CloseRestaurantUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.RestaurantRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.Restaurant;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CloseRestaurantService implements CloseRestaurantUseCase {


    private final AccessControl accessControl;
    private final CurrentTenantProvider currentTenantProvider;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantApplicationMapper mapper;

    public CloseRestaurantService(AccessControl accessControl,
                                  CurrentTenantProvider currentTenantProvider,
                                  RestaurantRepository restaurantRepository,
                                  RestaurantApplicationMapper mapper) {
        this.accessControl = accessControl;
        this.currentTenantProvider = currentTenantProvider;
        this.restaurantRepository = restaurantRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public RestaurantView close() {
        // 1) Authorization
        accessControl.requireRole(Roles.RESTAURANT_ADMIN);

        // 2) Tenant context required
        Long tenantId = currentTenantProvider.requireCurrent().value();
   

        // 3) Load aggregate
        Restaurant restaurant = restaurantRepository.findById(tenantId)
                .orElseThrow(() -> RestaurantNotFoundException.byId(tenantId));

        // 4) Domain behavior (idempotent)
        restaurant.close();

        // 5) Persist & map
        return mapper.toView(restaurantRepository.save(restaurant));
    }
}
