package com.example.aplikasiparkirpayment.model;

public class DetailTransactionResponse {
    private boolean status;
    private int id;
    private String location;
    private String vehicle_name;
    private int vehicle_price;
    private String license_plate;
    private int in_time;
    private int out_time;

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getVehicleName() {
        return vehicle_name;
    }

    public void setVehicleName(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public int getVehiclePrice() {
        return vehicle_price;
    }

    public void setVehiclePrice(int vehicle_price) {
        this.vehicle_price = vehicle_price;
    }

    public String getLicensePlate() {
        return license_plate;
    }

    public void setLicensePlate(String license_plate) {
        this.license_plate = license_plate;
    }

    public int getInTime() {
        return in_time;
    }

    public void setInTime(int in_time) {
        this.in_time = in_time;
    }

    public int getOutTime() {
        return out_time;
    }

    public void setOutTime(int out_time) {
        this.out_time = out_time;
    }
}
