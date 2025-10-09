// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/restaurant/application/factory/RestaurantFactory.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.factory;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.RegisterRestaurantCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.application.command.UpdateRestaurantProfileCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.Restaurant;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo.*;
import org.springframework.stereotype.Component;

/**
 * Application-level factory:
 * - Builds domain Value Objects from application commands.
 * - Creates new Restaurant aggregates for the register flow.
 * - Produces partial VO payloads for update flows (null means "no change").
 *
 * No JPA / web dependencies here. Pure assembly/validation via VOs.
 */
@Component
public class RestaurantFactory {

    // ---------- Register flow ----------

    /**
     * Build a new Restaurant aggregate from RegisterRestaurantCommand.
     * Defaults to CLOSED status (handled by Restaurant.create).
     */
    public Restaurant newFrom(RegisterRestaurantCommand c) {
        // Commands are pre-validated (JSR-380), VOs enforce invariants again.
        Name name = Name.of(c.name());
        Slug slug = Slug.of(c.slug());

        Email email = c.email() == null ? null : Email.of(c.email());
        Phone phone = c.phone() == null ? null : Phone.of(c.phone());

        Address address = Address.of(
                c.addressLine1(),
                c.addressLine2(),
                c.city(),
                c.country(),
                c.postalCode()
        );

        OpeningHours openingHours = c.openingHoursJson() == null ? null : OpeningHours.of(c.openingHoursJson());

        return Restaurant.create(name, slug, email, phone, emptyToNull(address), openingHours);
    }

    // ---------- Update profile flow ----------

    /**
     * Produce a partial payload of VOs from UpdateRestaurantProfileCommand.
     * Null fields mean "no change" and should be passed as null to the aggregate.
     * Address is now nested; if provided parcialmente, el agregado hace merge campo a campo.
     */
    public UpdatePayload from(UpdateRestaurantProfileCommand c) {
        Name name = c.name() == null ? null : Name.of(c.name());
        Slug slug = c.slug() == null ? null : Slug.of(c.slug());
        Email email = c.email() == null ? null : Email.of(c.email());
        Phone phone = c.phone() == null ? null : Phone.of(c.phone());

        Address address = null;
        if (anyAddressFieldPresent(c.address())) {
            var a = c.address();
            address = Address.of(
                    a.line1(),
                    a.line2(),
                    a.city(),
                    a.country(),
                    a.postalCode()
            );
        }

        OpeningHours openingHours = c.openingHoursJson() == null ? null : OpeningHours.of(c.openingHoursJson());

        // Important: if an Address.of(...) came out "empty", treat it as null (no change)
        address = emptyToNull(address);

        return new UpdatePayload(name, slug, email, phone, address, openingHours);
    }

    // ---------- Helpers ----------

    private boolean anyAddressFieldPresent(UpdateRestaurantProfileCommand.AddressPayload a) {
        if (a == null) return false;
        return a.line1() != null
                || a.line2() != null
                || a.city() != null
                || a.country() != null
                || a.postalCode() != null;
    }

    /**
     * If Address is the "all-null" instance (allowed by VO), return null to
     * represent "no change" at the aggregate level during updates.
     */
    private Address emptyToNull(Address a) {
        if (a == null) return null;
        boolean allNull = a.line1() == null
                && a.line2() == null
                && a.city() == null
                && a.country() == null
                && a.postalCode() == null;
        return allNull ? null : a;
    }

    /**
     * Partial VO set for updateProfile(...). All fields nullable: null = "no change".
     */
    public record UpdatePayload(
            Name name,
            Slug slug,
            Email email,
            Phone phone,
            Address address,
            OpeningHours openingHours
    ) {}
}
