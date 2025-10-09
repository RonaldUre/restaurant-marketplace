// .../UpdateProductUseCase.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.command.UpdateProductCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminDetailView;

public interface UpdateProductUseCase {
    ProductAdminDetailView update(UpdateProductCommand command);
}
