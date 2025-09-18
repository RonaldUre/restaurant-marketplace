// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/application/factory/ProductFactory.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.factory;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.command.CreateProductCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.command.UpdateProductCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.Product;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.domain.model.vo.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Money;
import org.springframework.stereotype.Component;

@Component
public class ProductFactory {

    // Create aggregate from command (admin context provides TenantId)
    public Product createNew(TenantId tenantId, CreateProductCommand cmd) {
        return Product.create(
                tenantId,
                Sku.of(cmd.sku()),
                ProductName.of(cmd.name()),
                mapDescription(cmd.description()), // see helper below
                Category.of(cmd.category()),
                Money.of(cmd.priceAmount(), cmd.priceCurrency())
        );
    }

    // Build strongly-typed payload for updates
    public UpdatePayload toUpdatePayload(UpdateProductCommand cmd) {
        return new UpdatePayload(
                ProductId.of(cmd.productId()),
                ProductName.of(cmd.name()),
                mapDescription(cmd.description()),
                Category.of(cmd.category()),
                Money.of(cmd.priceAmount(), cmd.priceCurrency())
        );
    }

    // ----- Helpers -----

    /**
     * Prefer ProductDescription.ofNullable(raw) if your VO provides it.
     * If not, this helper maps null/blank -> ProductDescription.empty(), else ProductDescription.of(raw).
     */
    private ProductDescription mapDescription(String raw) {
        try {
            // If your VO exposes ofNullable, use it:
            // return ProductDescription.ofNullable(raw);

            // Fallback if you only have of(...) and empty():
            if (raw == null || raw.isBlank()) {
                return ProductDescription.empty();
            }
            return ProductDescription.of(raw);
        } catch (NoSuchMethodError | RuntimeException ignore) {
            // In case of classpath differences during refactor, keep a safe default:
            if (raw == null || raw.isBlank()) {
                return ProductDescription.empty();
            }
            return ProductDescription.of(raw);
        }
    }

    // Small immutable carrier for update flows
    public record UpdatePayload(
            ProductId id,
            ProductName name,
            ProductDescription description,
            Category category,
            Money price
    ) { }
}
