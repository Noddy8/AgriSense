package com.ninjaTurtles.agrisense.models;

public class AccountBootstrapRequest {
    public String fullName;
    public String farmName;
    public String mobileNumber;

    public AccountBootstrapRequest(String fullName, String farmName, String mobileNumber) {
        this.fullName = fullName;
        this.farmName = farmName;
        this.mobileNumber = mobileNumber;
    }
}
