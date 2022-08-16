package com.example.aplikasiparkirpayment.model;

public class LoginRequest {
    private String member_number;
    private String password;

    public String getMemberNumber() {
        return member_number;
    }

    public void setMemberNumber(String member_number) {
        this.member_number = member_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
