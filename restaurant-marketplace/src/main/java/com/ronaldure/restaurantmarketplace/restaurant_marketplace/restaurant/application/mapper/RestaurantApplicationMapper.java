// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/mapper/RestaurantApplicationMapper.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.mapper;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.view.RestaurantView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.Restaurant;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo.Address;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo.OpeningHours;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo.Email;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo.Phone;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo.RestaurantId;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RestaurantApplicationMapper {

    /**
     * Maps a Restaurant aggregate to a stable application DTO (RestaurantView).
     * No JPA/web concerns here. Pure transformation.
     */
    public RestaurantView toView(Restaurant restaurant) {
        Objects.requireNonNull(restaurant, "restaurant is required");

        Long id = unwrap(restaurant.id());
        String name = restaurant.name() != null ? restaurant.name().value() : null;
        String slug = restaurant.slug() != null ? restaurant.slug().value() : null;
        String status = restaurant.status() != null ? restaurant.status().name() : null;
        String email = unwrap(restaurant.email());
        String phone = unwrap(restaurant.phone());
        RestaurantView.AddressView address = toAddressView(restaurant.address());
        String openingHoursJson = unwrap(restaurant.openingHours());

        return new RestaurantView(
                id, name, slug, status, email, phone, address, openingHoursJson);
    }

    // ---------- Helpers (VO unwrapping) ----------

    private Long unwrap(RestaurantId id) {
        return id == null ? null : id.value();
    }

    private String unwrap(Email email) {
        return email == null ? null : email.value();
    }

    private String unwrap(Phone phone) {
        return phone == null ? null : phone.value();
    }

    private String unwrap(OpeningHours openingHours) {
        return openingHours == null ? null : openingHours.json();
    }

    private RestaurantView.AddressView toAddressView(Address a) {
        if (a == null)
            return null;
        boolean empty = a.line1() == null
                && a.line2() == null
                && a.city() == null
                && a.country() == null
                && a.postalCode() == null;
        if (empty)
            return null;
        return new RestaurantView.AddressView(
                a.line1(),
                a.line2(),
                a.city(),
                a.country(),
                a.postalCode());
    }
}
