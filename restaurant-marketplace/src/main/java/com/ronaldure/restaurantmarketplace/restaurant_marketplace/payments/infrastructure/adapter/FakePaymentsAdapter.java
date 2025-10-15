// payments/infrastructure/adapter/FakePaymentsAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.PaymentsPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.application.service.PaymentsService;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.application.view.PaymentTransactionView;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.config.PaymentsRedirectProperties;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Component
@Profile("dev")
public class FakePaymentsAdapter implements PaymentsPort {

    private final PaymentsService payments;
    private final PaymentsRedirectProperties redirects;

    public FakePaymentsAdapter(PaymentsService payments, PaymentsRedirectProperties redirects) {
        this.payments = payments;
        this.redirects = redirects;
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

        // Construimos la URL de aprobación del "simulador" incluyendo success/cancel
        String success = urlEncode(redirects.success());
        String cancel  = urlEncode(redirects.cancel());
        String token   = urlEncode(fakePayPalOrderId);

        // Puedes mantener el host/puerto del back según tu dev setup
        String fakeApprovalUrl = "http://localhost:8080/fake-paypal-approval"
                + "?token=" + token
                + "&success=" + success
                + "&cancel=" + cancel;

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
            // El guardado approved/declined lo hace el módulo 'ordering' después de capturar.
            return new CapturePaymentResult(true, fakeTxId, null);
        } else {
            String reason = "declined_by_fake_gateway";
            return new CapturePaymentResult(false, null, reason);
        }
    }

    private static String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
