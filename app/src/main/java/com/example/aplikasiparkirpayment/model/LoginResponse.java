package com.example.aplikasiparkirpayment.model;

public class LoginResponse {
    private boolean status;
    private String message;
    private int parker_id;
    private String parker_name;
    private String token;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getParker_id() {
        return parker_id;
    }

    public void setParker_id(int parker_id) {
        this.parker_id = parker_id;
    }

    public String getParker_name() {
        return parker_name;
    }

    public void setParker_name(String parker_name) {
        this.parker_name = parker_name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
