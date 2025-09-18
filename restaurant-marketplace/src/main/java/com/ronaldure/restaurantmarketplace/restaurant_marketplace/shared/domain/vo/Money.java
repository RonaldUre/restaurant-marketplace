package com.ronaldure.restaurantmarketplace.restaurant_marketplace.shared.domain.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object: Money (immutable)
 *
 * - Represents monetary value with amount (BigDecimal) and ISO-4217 currency (e.g., "USD", "EUR").
 * - Enforces scale=2 by default (typical for fiat currencies).
 * - Operations require same currency; otherwise throws IllegalArgumentException.
 * - Amount is validated to be >= 0 for product pricing in Catalog (no negative prices).
 *
 * Domain Notes:
 * - Use factory methods: of(amount, currency), zero(currency).
 * - Prefer BigDecimal string ctor to avoid FP precision artifacts.
 * - Placed in shared/domain/vo because it is reused by multiple modules (Catalog, Ordering, Payments).
 */
public final class Money implements Comparable<Money> {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    private static final Pattern ISO4217 = Pattern.compile("^[A-Z]{3}$");

    private final BigDecimal amount;
    private final String currency;

    private Money(BigDecimal amount, String currency) {
        this.amount = amount.setScale(SCALE, ROUNDING);
        this.currency = currency;
    }

    public static Money of(BigDecimal amount, String currency) {
        Objects.requireNonNull(amount, "amount is required");
        Objects.requireNonNull(currency, "currency is required");
        validateCurrency(currency);
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Money amount must be >= 0");
        }
        return new Money(amount, currency);
    }

    public static Money of(String amount, String currency) {
        return of(new BigDecimal(Objects.requireNonNull(amount, "amount is required")), currency);
    }

    public static Money zero(String currency) {
        return of(BigDecimal.ZERO, currency);
    }

    public BigDecimal amount() { return amount; }
    public String currency() { return currency; }

    public Money add(Money other) {
        assertSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        assertSameCurrency(other);
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Resulting money cannot be negative");
        }
        return new Money(result, this.currency);
    }

    public Money multiply(int factor) {
        if (factor < 0) {
            throw new IllegalArgumentException("Factor must be >= 0");
        }
        return new Money(this.amount.multiply(BigDecimal.valueOf(factor)), this.currency);
    }

    public boolean isZero() { return amount.compareTo(BigDecimal.ZERO) == 0; }
    public boolean isPositive() { return amount.compareTo(BigDecimal.ZERO) > 0; }

    private void assertSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch: " + this.currency + " vs " + other.currency);
        }
    }

    private static void validateCurrency(String currency) {
        if (!ISO4217.matcher(currency).matches()) {
            throw new IllegalArgumentException("Currency must be ISO-4217 (e.g., USD, EUR)");
        }
    }

    @Override
    public int compareTo(Money o) {
        assertSameCurrency(o);
        return this.amount.compareTo(o.amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount.stripTrailingZeros(), currency);
    }

    @Override
    public String toString() {
        return amount.toPlainString() + " " + currency;
    }
}
