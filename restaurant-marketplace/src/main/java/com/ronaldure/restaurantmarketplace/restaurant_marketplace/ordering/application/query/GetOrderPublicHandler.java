package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.query;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.errors.OrderNotFoundException;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.GetOrderPublicQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.PublicOrderDetailQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderDetailView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentUserProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetOrderPublicHandler implements GetOrderPublicQuery {

    private final PublicOrderDetailQuery publicDetailQuery;
    private final CurrentUserProvider userProvider;
    private final AccessControl accessControl;

    public GetOrderPublicHandler(PublicOrderDetailQuery publicDetailQuery,
                                 CurrentUserProvider userProvider,
                                 AccessControl accessControl) {
        this.publicDetailQuery = publicDetailQuery;
        this.userProvider = userProvider;
        this.accessControl = accessControl;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailView get(Long orderId) {
        accessControl.requireRole(Roles.CUSTOMER);
        UserId owner = userProvider.requireAuthenticated().userId();

        return publicDetailQuery
                .findOwned(orderId, owner)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
