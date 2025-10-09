package com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.adapter;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.PaymentsPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.application.service.PaymentsService;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo.Money;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Component
@Profile("!dev")
public class PayPalAdapter implements PaymentsPort {

    private final PaymentsService payments;
    private final PayPalHttpClient payPalClient;

    public PayPalAdapter(PaymentsService payments, PayPalHttpClient payPalClient) {
        this.payments = payments;
        this.payPalClient = payPalClient;
    }
    
    @Override
    public CreatePaymentResult createPayment(CreatePaymentRequest request) {
        payments.recordInitiated(OrderId.of(request.orderId()), request.tenantId(), request.amount(), "PAYPAL");

        OrdersCreateRequest createRequest = new OrdersCreateRequest();
        createRequest.prefer("return=representation");
        createRequest.requestBody(buildRequestBody(request.amount()));

        try {
            HttpResponse<Order> response = payPalClient.execute(createRequest);
            Order order = response.result();

            if (response.statusCode() == 201) {
                String approvalUrl = order.links().stream()
                        .filter(link -> "approve".equals(link.rel()))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException("Approval URL not found in PayPal response"))
                        .href();
                
                return new CreatePaymentResult(order.id(), approvalUrl);
            } else {
                throw new IOException("PayPal create order failed with status: " + response.statusCode());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CapturePaymentResult capturePayment(CapturePaymentRequest request) {
        OrdersCaptureRequest captureRequest = new OrdersCaptureRequest(request.paymentProviderOrderId());
        captureRequest.requestBody(new OrderRequest());

        try {
            HttpResponse<Order> response = payPalClient.execute(captureRequest);
            Order capturedOrder = response.result();

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                String captureId = capturedOrder.purchaseUnits().get(0).payments().captures().get(0).id();
                
                payments.recordApproved(
                    OrderId.of(request.orderId()),
                    request.tenantId(),
                    request.amount(),
                    "PAYPAL",
                    captureId
                );
                return new CapturePaymentResult(true, captureId, null);
            } else {
                String reason = "PayPal capture failed with status: " + response.statusCode();
                payments.recordDeclined(
                    OrderId.of(request.orderId()),
                    request.tenantId(),
                    request.amount(),
                    "PAYPAL",
                    reason
                );
                return new CapturePaymentResult(false, null, reason);
            }
        } catch (IOException e) {
            String reason = e.getMessage();
            payments.recordDeclined(
                OrderId.of(request.orderId()),
                request.tenantId(),
                request.amount(),
                "PAYPAL",
                reason
            );
            return new CapturePaymentResult(false, null, reason);
        }
    }

    private OrderRequest buildRequestBody(Money amount) {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        AmountWithBreakdown amountBreakdown = new AmountWithBreakdown()
                .currencyCode(amount.currency())
                .value(String.format("%.2f", amount.amount()));

        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest()
                .amountWithBreakdown(amountBreakdown);

        orderRequest.purchaseUnits(List.of(purchaseUnitRequest));
        return orderRequest;
    }
}