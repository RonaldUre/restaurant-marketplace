// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/service/UpdateRestaurantProfileService.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.service;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.UpdateRestaurantProfileCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors.RestaurantNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.errors.SlugAlreadyInUseException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.factory.RestaurantFactory;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.factory.RestaurantFactory.UpdatePayload;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.mapper.RestaurantApplicationMapper;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.in.UpdateRestaurantProfileUseCase;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.ports.out.RestaurantRepository;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.Restaurant;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentTenantProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateRestaurantProfileService implements UpdateRestaurantProfileUseCase {

    private final AccessControl accessControl;
    private final CurrentTenantProvider currentTenantProvider;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantFactory restaurantFactory;
    private final RestaurantApplicationMapper mapper;

    public UpdateRestaurantProfileService(AccessControl accessControl,
                                          CurrentTenantProvider currentTenantProvider,
                                          RestaurantRepository restaurantRepository,
                                          RestaurantFactory restaurantFactory,
                                          RestaurantApplicationMapper mapper) {
        this.accessControl = accessControl;
        this.currentTenantProvider = currentTenantProvider;
        this.restaurantRepository = restaurantRepository;
        this.restaurantFactory = restaurantFactory;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public RestaurantView update(UpdateRestaurantProfileCommand command) {
        // 1) Autorización (defensa en profundidad)
        accessControl.requireRole(Roles.RESTAURANT_ADMIN);

        // 2) Tenant en contexto (obligatorio para admins)
        Long tenantId = currentTenantProvider.requireCurrent().value();

        // 3) Cargar el agregado por id = tenantId
        Restaurant restaurant = restaurantRepository.findById(tenantId)
                .orElseThrow(() -> RestaurantNotFoundException.byId(tenantId));

        // 4) Preparar VOs opcionales desde el command
        UpdatePayload payload = restaurantFactory.from(command);

        // 5) Si viene un slug distinto, verificar unicidad antes de tocar dominio
        if (payload.slug() != null) {
            String newSlug = payload.slug().value();
            String currentSlug = restaurant.slug() != null ? restaurant.slug().value() : null;

            if (!newSlug.equals(currentSlug) && restaurantRepository.existsBySlug(newSlug)) {
                throw new SlugAlreadyInUseException(newSlug);
            }
        }

        // 6) Aplicar cambios al agregado (null = no cambio)
        restaurant.updateProfile(
                payload.name(),
                payload.slug(),
                payload.email(),
                payload.phone(),
                payload.address(),
                payload.openingHours()
        );

        // 7) Guardar y devolver vista
        Restaurant saved = restaurantRepository.save(restaurant);
        return mapper.toView(saved);
    }
}
