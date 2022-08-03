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

    public boolean isStatus() {
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

    public String getVehicle_name() {
        return vehicle_name;
    }

    public void setVehicle_name(String vehicle_name) {
        this.vehicle_name = vehicle_name;
    }

    public int getVehicle_price() {
        return vehicle_price;
    }

    public void setVehicle_price(int vehicle_price) {
        this.vehicle_price = vehicle_price;
    }

    public String getLicense_plate() {
        return license_plate;
    }

    public void setLicense_plate(String license_plate) {
        this.license_plate = license_plate;
    }

    public int getIn_time() {
        return in_time;
    }

    public void setIn_time(int in_time) {
        this.in_time = in_time;
    }

    public int getOut_time() {
        return out_time;
    }

    public void setOut_time(int out_time) {
        this.out_time = out_time;
    }
}
