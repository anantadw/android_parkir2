package com.example.aplikasiparkirpayment.model;

public class TransactionData {
    private int id;
    private String license_plate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return license_plate;
    }

    public void setLicensePlate(String license_plate) {
        this.license_plate = license_plate;
    }
}
