package com.ronaldure.restaurantmarketplace.restaurant_marketplace.inventory.application.errors;

public class InventoryOperationNotAllowedException extends RuntimeException {
    public InventoryOperationNotAllowedException(String message) {
        super(message);
    }
    public static InventoryOperationNotAllowedException cannotAdjustUnlimited(Long productId) {
        return new InventoryOperationNotAllowedException(
            "Cannot adjust unlimited stock: productId=" + productId
        );
    }
    public static InventoryOperationNotAllowedException cannotSwitchToUnlimitedWithReservations(Long productId) {
        return new InventoryOperationNotAllowedException(
            "Cannot switch to unlimited while reserved > 0: productId=" + productId
        );
    }
}
