package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Money;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Quantity;

import java.util.Objects;

/** Snapshot de producto al momento del pedido. */
public final class OrderLine {
    private final long productId;           // FK técnica
    private final String productName;       // snapshot
    private final Money unitPrice;          // snapshot
    private final Quantity qty;
    private final Money lineTotal;          // snapshot (unitPrice * qty)

    private OrderLine(long productId, String productName, Money unitPrice, Quantity qty, Money lineTotal) {
        if (productId <= 0) throw new IllegalArgumentException("productId must be > 0");
        if (productName == null || productName.trim().isEmpty()) throw new IllegalArgumentException("name required");
        this.productId = productId;
        this.productName = productName.trim();
        this.unitPrice = Objects.requireNonNull(unitPrice, "unitPrice required");
        this.qty = Objects.requireNonNull(qty, "qty required");
        this.lineTotal = Objects.requireNonNull(lineTotal, "lineTotal required");
        if (qty.value() <= 0) throw new IllegalArgumentException("qty must be > 0");
    }

    public static OrderLine of(long productId, String productName, Money unitPrice, Quantity qty, Money lineTotal){
        return new OrderLine(productId, productName, unitPrice, qty, lineTotal);
    }

    public long productId(){ return productId; }
    public String productName(){ return productName; }
    public Money unitPrice(){ return unitPrice; }
    public Quantity qty(){ return qty; }
    public Money lineTotal(){ return lineTotal; }
}
