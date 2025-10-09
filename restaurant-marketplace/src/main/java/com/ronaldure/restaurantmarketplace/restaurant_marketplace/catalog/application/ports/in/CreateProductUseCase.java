// src/main/java/com/ronaldure/restaurantmarketplace/restaurant_marketplace/catalog/application/ports/in/CreateProductUseCase.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.ports.in;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.command.CreateProductCommand;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.application.view.ProductAdminDetailView;

public interface CreateProductUseCase {
    ProductAdminDetailView create(CreateProductCommand command);
}
