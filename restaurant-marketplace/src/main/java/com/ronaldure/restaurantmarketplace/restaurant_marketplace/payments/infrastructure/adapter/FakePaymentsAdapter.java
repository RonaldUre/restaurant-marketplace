// payments/infrastructure/adapter/FakePaymentsAdapter.java
package com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.infrastructure.adapter;

import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.application.ports.out.PaymentsPort;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.ordering.domain.model.vo.OrderId;
import com.ronaldure.restaurantmarketplace.restaurant_marketplace.payments.application.service.PaymentsService;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
public class FakePaymentsAdapter implements PaymentsPort {

  private final PaymentsService payments;

  public FakePaymentsAdapter(PaymentsService payments) {
    this.payments = payments;
  }

  @Override
  public ChargeResult charge(ChargeRequest req) {
    // 1) INITIATED
    payments.recordInitiated(OrderId.of(req.orderId()), req.tenantId(), req.amount(), req.method());

    // 2) Simular latencia mínima
    try { Thread.sleep(150); } catch (InterruptedException ignored) {}

    // 3) Reglas deterministas del fake
    String m = req.method() == null ? "FAKE" : req.method().toUpperCase(Locale.ROOT);
    boolean approved = switch (m) {
      case "FAKE_FAIL", "DECLINE", "REJECT" -> false;
      default -> true; // FAKE, CARD, CASH...
    };

    if (approved) {
      String txId = UUID.randomUUID().toString();
      payments.recordApproved(OrderId.of(req.orderId()), req.tenantId(), req.amount(), req.method(), txId);
      return new ChargeResult(true, txId, null);
    } else {
      String reason = "declined_by_fake_gateway";
      payments.recordDeclined(OrderId.of(req.orderId()), req.tenantId(), req.amount(), req.method(), reason);
      return new ChargeResult(false, null, reason);
    }
  }
}
