package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.query;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors.RestaurantNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.mapper.RestaurantApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.GetMyRestaurantDetailQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.RestaurantRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.Restaurant;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetMyRestaurantDetailHandler implements GetMyRestaurantDetailQuery {

    private final AccessControl accessControl;
    private final CurrentTenantProvider currentTenantProvider;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantApplicationMapper mapper;

    public GetMyRestaurantDetailHandler(AccessControl accessControl,
                                        CurrentTenantProvider currentTenantProvider,
                                        RestaurantRepository restaurantRepository,
                                        RestaurantApplicationMapper mapper) {
        this.accessControl = accessControl;
        this.currentTenantProvider = currentTenantProvider;
        this.restaurantRepository = restaurantRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public RestaurantView get() {
        // 1) Auth
        accessControl.requireRole(Roles.RESTAURANT_ADMIN);

        // 2) Tenant requerido
        Long tenantId = currentTenantProvider.requireCurrent().value();
              

        // 3) Cargar agregado
        Restaurant restaurant = restaurantRepository.findById(tenantId)
                .orElseThrow(() -> RestaurantNotFoundException.byId(tenantId));

        // 4) Mapear a vista de aplicación
        return mapper.toView(restaurant);
    }
}
