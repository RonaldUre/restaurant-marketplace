package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.UpdateOpeningHoursCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors.RestaurantNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.mapper.RestaurantApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.UpdateOpeningHoursUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.RestaurantRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.Restaurant;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo.OpeningHours;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateOpeningHoursService implements UpdateOpeningHoursUseCase {

    

    private final AccessControl accessControl;
    private final CurrentTenantProvider currentTenantProvider;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantApplicationMapper mapper;

    public UpdateOpeningHoursService(AccessControl accessControl,
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
    public RestaurantView update(UpdateOpeningHoursCommand command) {
        // 1) Auth
        accessControl.requireRole(Roles.RESTAURANT_ADMIN);

        // 2) Tenant obligatorio
        Long tenantId = currentTenantProvider.requireCurrent().value();

        // 3) Cargar agregado
        Restaurant restaurant = restaurantRepository.findById(tenantId)
                .orElseThrow(() -> RestaurantNotFoundException.byId(tenantId));

        // 4) Aplicar solo opening hours (los demás campos null = "no cambio")
        OpeningHours oh = OpeningHours.of(command.openingHoursJson());
        restaurant.updateProfile(
                /* name */ null,
                /* slug */ null,
                /* email */ null,
                /* phone */ null,
                /* address */ null,
                /* openingHours */ oh
        );

        // 5) Guardar y mapear
        return mapper.toView(restaurantRepository.save(restaurant));
    }
}
