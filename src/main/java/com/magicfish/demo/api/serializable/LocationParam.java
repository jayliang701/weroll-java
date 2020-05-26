package com.magicfish.demo.api.serializable;

import java.io.Serializable;

public class LocationParam implements Serializable {

    private String city = "";

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    private String country = "";

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
