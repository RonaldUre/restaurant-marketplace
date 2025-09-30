package com.ronaldure.restaurantmarketplace.restaurant_marketplace.catalog.infrastructure.persistence.adapter;

import java.util.List;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.CatalogPricingPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;

public class CatalogPricingPortJpaAdapter implements CatalogPricingPort{

    @Override
    public PricedCatalog priceAndValidate(TenantId tenantId, List<Item> items) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'priceAndValidate'");
    }


    
}
