// ordering/application/ports/out/PaymentsPort.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.security.TenantId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Money;

public interface PaymentsPort {

    CreatePaymentResult createPayment(CreatePaymentRequest request);

    CapturePaymentResult capturePayment(CapturePaymentRequest request);

    // ---- Tipos auxiliares ----
    record CreatePaymentRequest(long orderId, TenantId tenantId, Money amount, String method) {}

    record CreatePaymentResult(String paymentProviderOrderId, String approvalUrl) {}

    record CapturePaymentRequest( String paymentProviderOrderId, 
        long orderId, 
        TenantId tenantId, 
        Money amount) {}

    record CapturePaymentResult(boolean approved, String txId, String reason) {}
}