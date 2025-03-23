package hu.opau.voltvault.models;

import java.util.Objects;

public class BillingAddress {
    private String name;
    private String phone;
    private boolean primary;
    private int postalCode;
    private String country;
    private String city;
    private String address;

    public BillingAddress() {}

    public BillingAddress(String name, String phone, boolean isPrimary, int postalCode, String country, String city, String address) {
        this.name = name;
        this.phone = phone;
        this.primary = isPrimary;
        this.postalCode = postalCode;
        this.country = country;
        this.city = city;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isPrimary() {
        return primary;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillingAddress that = (BillingAddress) o;
        return primary == that.primary && postalCode == that.postalCode && Objects.equals(name, that.name) && Objects.equals(phone, that.phone) && Objects.equals(country, that.country) && Objects.equals(city, that.city) && Objects.equals(address, that.address);
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }
}
