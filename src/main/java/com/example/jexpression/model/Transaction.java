package com.example.jexpression.model;

/**
 * Transaction DTO for FEEL rule evaluation.
 */
public class Transaction {
    private String country;
    private String channel;
    private String transactionDate;
    private Double amount;
    private String messageType;
    private String beneficiaryIban;
    private String purposeCode;
    private String requestedExecutionDate;
    private String currency;

    // Getters and Setters
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getBeneficiaryIban() {
        return beneficiaryIban;
    }

    public void setBeneficiaryIban(String beneficiaryIban) {
        this.beneficiaryIban = beneficiaryIban;
    }

    public String getPurposeCode() {
        return purposeCode;
    }

    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    public String getRequestedExecutionDate() {
        return requestedExecutionDate;
    }

    public void setRequestedExecutionDate(String requestedExecutionDate) {
        this.requestedExecutionDate = requestedExecutionDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
