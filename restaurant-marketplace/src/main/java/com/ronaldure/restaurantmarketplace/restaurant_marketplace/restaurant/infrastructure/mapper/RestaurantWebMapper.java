package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.mapper;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.PlatformRestaurantCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.PlatformRestaurantCardResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.RestaurantCardResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.infrastructure.web.dto.RestaurantPublicResponse;
import org.springframework.stereotype.Component;

@Component
public class RestaurantWebMapper {

    public RestaurantCardResponse toResponse(RestaurantCardView v) {
        return new RestaurantCardResponse(
                v.id(), v.name(), v.slug(), v.status(), v.city());
    }

    public RestaurantPublicResponse toResponse(RestaurantView v) {
        RestaurantPublicResponse.AddressResponse addr = null;
        if (v.address() != null) {
            addr = new RestaurantPublicResponse.AddressResponse(
                    v.address().line1(),
                    v.address().line2(),
                    v.address().city(),
                    v.address().country(),
                    v.address().postalCode());
        }
        return new RestaurantPublicResponse(
                v.id(), v.name(), v.slug(), v.status(), v.email(), v.phone(), addr, v.openingHoursJson());
    }

    public PlatformRestaurantCardResponse toResponse(PlatformRestaurantCardView v) {
        return new PlatformRestaurantCardResponse(
                v.id(), v.name(), v.slug(), v.status(), v.city(), v.createdAt());
    }
}
