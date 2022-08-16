package com.example.aplikasiparkirpayment.model;

import java.util.List;

public class TransactionResponse {
    private boolean status;
    private List<TransactionData> data;

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<TransactionData> getData() {
        return data;
    }

    public void setData(List<TransactionData> data) {
        this.data = data;
    }
}
