package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.UnsuspendRestaurantCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors.RestaurantNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.mapper.RestaurantApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.UnsuspendRestaurantUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.RestaurantRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.Restaurant;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UnsuspendRestaurantService implements UnsuspendRestaurantUseCase {

    

    private final AccessControl accessControl;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantApplicationMapper mapper;

    public UnsuspendRestaurantService(AccessControl accessControl,
                                      RestaurantRepository restaurantRepository,
                                      RestaurantApplicationMapper mapper) {
        this.accessControl = accessControl;
        this.restaurantRepository = restaurantRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public RestaurantView unsuspend(UnsuspendRestaurantCommand command) {
        // 1) Autorización
        accessControl.requireRole(Roles.SUPER_ADMIN);

        // 2) Validación XOR
        if (command == null || !command.hasExactlyOneTarget()) {
            throw new IllegalArgumentException("Exactly one target is required: id or slug");
        }

        // 3) Cargar agregado
        Restaurant restaurant = (command.id() != null)
                ? restaurantRepository.findById(command.id())
                    .orElseThrow(() -> RestaurantNotFoundException.byId(command.id()))
                : restaurantRepository.findBySlug(command.slug())
                    .orElseThrow(() -> RestaurantNotFoundException.bySlug(command.slug()));

        // 4) Dominio: SUSPENDED -> CLOSED (idempotente)
        restaurant.unsuspend();

        // 5) Persistir & mapear
        return mapper.toView(restaurantRepository.save(restaurant));
    }
}
