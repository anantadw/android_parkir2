package com.example.aplikasiparkirpayment.model;

public class LoginRequest {
    private String member_number;
    private String password;

    public String getMember_number() {
        return member_number;
    }

    public void setMember_number(String member_number) {
        this.member_number = member_number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
