package com.example.aplikasiparkirpayment.model;

public class TransactionRequest {
    private int parker_id;
    private int vehicle_id;
    private String license_plate;

    public int getParkerId() {
        return parker_id;
    }

    public void setParkerId(int parker_id) {
        this.parker_id = parker_id;
    }

    public int getVehicleId() {
        return vehicle_id;
    }

    public void setVehicleId(int vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getLicensePlate() {
        return license_plate;
    }

    public void setLicensePlate(String license_plate) {
        this.license_plate = license_plate;
    }
}
