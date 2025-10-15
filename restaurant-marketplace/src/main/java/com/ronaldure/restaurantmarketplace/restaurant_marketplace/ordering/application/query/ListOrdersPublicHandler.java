package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.query;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.in.ListOrdersPublicQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.PublicOrderQuery;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.view.OrderCardView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageRequest;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.query.PageResponse;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.AccessControl;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.CurrentUserProvider;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.application.security.Roles;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListOrdersPublicHandler implements ListOrdersPublicQuery {

    private final PublicOrderQuery publicOrderQuery;
    private final CurrentUserProvider userProvider;
    private final AccessControl accessControl;

    public ListOrdersPublicHandler(PublicOrderQuery publicOrderQuery,
                                   CurrentUserProvider userProvider,
                                   AccessControl accessControl) {
        this.publicOrderQuery = publicOrderQuery;
        this.userProvider = userProvider;
        this.accessControl = accessControl;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderCardView> list(ListOrdersPublicQueryParams params) {
        accessControl.requireRole(Roles.CUSTOMER);
        UserId owner = userProvider.requireAuthenticated().userId();
        PageRequest page = new PageRequest(params.page(), params.size());
        return publicOrderQuery.list(owner, params, page);
    }
}
