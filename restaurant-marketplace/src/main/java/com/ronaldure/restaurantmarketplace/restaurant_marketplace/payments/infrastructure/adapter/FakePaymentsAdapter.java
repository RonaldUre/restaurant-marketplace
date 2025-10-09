// payments/infrastructure/adapter/FakePaymentsAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.PaymentsPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.application.service.PaymentsService;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.application.view.PaymentTransactionView;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
@Profile("dev")
public class FakePaymentsAdapter implements PaymentsPort {

    private final PaymentsService payments;

    public FakePaymentsAdapter(PaymentsService payments) {
        this.payments = payments;
    }

    @Override
    public CreatePaymentResult createPayment(CreatePaymentRequest request) {
        payments.recordInitiated(
            OrderId.of(request.orderId()),
            request.tenantId(),
            request.amount(),
            "FAKE"
        );

        // Adjuntamos el ID de la orden real al final para recuperarlo en la captura
        String fakePayPalOrderId = "FAKE-" + UUID.randomUUID() + "-" + request.orderId();
        String fakeApprovalUrl = "http://localhost:8080/fake-paypal-approval?token=" + fakePayPalOrderId;
        
        System.out.println("FAKE PAYMENTS: Generated approval link: " + fakeApprovalUrl);

        return new CreatePaymentResult(fakePayPalOrderId, fakeApprovalUrl);
    }

    @Override
    public CapturePaymentResult capturePayment(CapturePaymentRequest request) {
        boolean approved = !request.paymentProviderOrderId().contains("FAIL");

        // Extraemos el ID de la orden real del ID de pago falso
        String[] parts = request.paymentProviderOrderId().split("-");
        Long orderId = Long.parseLong(parts[parts.length - 1]);

        // ✅ Usamos el nuevo método público, no el repositorio directamente
        Optional<PaymentTransactionView> txView = payments.findByOrderId(orderId);

        // Ejemplo de lógica determinista: pagos por más de 1000 fallan
        if (txView.isPresent() && txView.get().amount().doubleValue() > 1000) {
            approved = false;
        }

        if (approved) {
            String fakeTxId = "TX-" + UUID.randomUUID();
            // Nota: El recordApproved/Declined lo llama el módulo 'ordering',
            // así que aquí solo devolvemos el resultado.
            return new CapturePaymentResult(true, fakeTxId, null);
        } else {
            String reason = "declined_by_fake_gateway";
            return new CapturePaymentResult(false, null, reason);
        }
    }
}