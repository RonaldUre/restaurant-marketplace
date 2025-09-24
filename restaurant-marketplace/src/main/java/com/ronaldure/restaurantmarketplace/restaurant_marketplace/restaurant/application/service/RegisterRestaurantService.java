// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/service/RegisterRestaurantService.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.RegisterRestaurantCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors.SlugAlreadyInUseException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.mapper.RestaurantApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.RegisterRestaurantUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.AdminAccountGateway;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.RestaurantRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.factory.RestaurantFactory;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.Restaurant;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for registering a new Restaurant (tenant).
 * Responsibilities:
 * - Authorization: require SUPER_ADMIN.
 * - Pre-check uniqueness for slug (fast path) and rely on DB constraint as last
 * resort.
 * - Build domain aggregate from command (via Factory).
 * - Persist via Repository port.
 * - Map aggregate to application view (Mapper).
 */
@Service
public class RegisterRestaurantService implements RegisterRestaurantUseCase {

    private final AccessControl accessControl;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantFactory restaurantFactory;
    private final RestaurantApplicationMapper mapper;
    private final AdminAccountGateway adminAccounts;

    public RegisterRestaurantService(AccessControl accessControl,
            RestaurantRepository restaurantRepository,
            RestaurantFactory restaurantFactory,
            RestaurantApplicationMapper mapper,
            AdminAccountGateway adminAccounts) {
        this.accessControl = accessControl;
        this.restaurantRepository = restaurantRepository;
        this.restaurantFactory = restaurantFactory;
        this.mapper = mapper;
        this.adminAccounts = adminAccounts;
    }

    @Override
    @Transactional // write transaction boundary
    public RestaurantView register(RegisterRestaurantCommand command) {
        // 1) Authorization
        accessControl.requireRole(Roles.SUPER_ADMIN);

        // 2) Fast-path uniqueness check for slug (domain handles format; repo checks
        // existence)
        if (restaurantRepository.existsBySlug(command.slug())) {
            throw new SlugAlreadyInUseException(command.slug());
        }

        // 3) Build domain aggregate from command (VOs inside the factory re-validate
        // invariants)
        Restaurant aggregate = restaurantFactory.newFrom(command);

        // 4) Persist aggregate (adapter assigns ID for new aggregates)
        Restaurant saved = restaurantRepository.save(aggregate);

        adminAccounts.createTenantAdmin(
                saved.id().value(),
                command.adminEmail(),
                command.adminPassword());

        // 5) Map to application view
        return mapper.toView(saved);
    }
}
