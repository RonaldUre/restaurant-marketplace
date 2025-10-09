package com.ronaldure.restaurantmarketplace.restaurant_marketplace.restaurant.domain.model.vo;

import java.util.Locale;

/** Simple immutable address; keep nullables out by optional usage at aggregate level */
public final class Address {
    private final String line1;
    private final String line2; // optional
    private final String city;  // optional
    private final String country; // optional ISO-3166-1 alpha-2
    private final String postalCode; // optional

    private Address(String line1, String line2, String city, String country, String postalCode) {
        this.line1 = line1 == null ? null : line1.trim();
        this.line2 = line2 == null ? null : line2.trim();
        this.city = city == null ? null : city.trim();
        this.country = country == null ? null : country.trim().toUpperCase(Locale.ROOT);
        this.postalCode = postalCode == null ? null : postalCode.trim();

        if (this.line1 != null && this.line1.length() > 255) throw new IllegalArgumentException("line1 too long");
        if (this.line2 != null && this.line2.length() > 255) throw new IllegalArgumentException("line2 too long");
        if (this.city != null && this.city.length() > 120) throw new IllegalArgumentException("city too long");
        if (this.country != null && this.country.length() != 2) throw new IllegalArgumentException("country must be ISO-2");
        if (this.postalCode != null && this.postalCode.length() > 20) throw new IllegalArgumentException("postalCode too long");
    }

    public static Address of(String line1, String line2, String city, String country, String postalCode) {
        if (line1 == null && line2 == null && city == null && country == null && postalCode == null) {
            return new Address(null, null, null, null, null); // empty address object allowed
        }
        return new Address(line1, line2, city, country, postalCode);
    }

    public String line1(){ return line1; }
    public String line2(){ return line2; }
    public String city(){ return city; }
    public String country(){ return country; }
    public String postalCode(){ return postalCode; }
}
