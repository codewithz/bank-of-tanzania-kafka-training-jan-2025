package com.codewithz.model;

public class Transaction {
    private String txId;
    private String bankName;
    private String accountHolder;
    private double amount;
    private double balanceAmount;
    private String country;
    private String state;
    private String timestamp;

    private String txTypeCode; // Add this field

    // Getters and Setters for all fields, including txTypeCode
    public String getTxTypeCode() {
        return txTypeCode;
    }

    public void setTxTypeCode(String txTypeCode) {
        this.txTypeCode = txTypeCode;
    }

    // Getters and Setters
    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(double balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


}

